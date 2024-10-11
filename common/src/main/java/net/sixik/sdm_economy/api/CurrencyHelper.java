package net.sixik.sdm_economy.api;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.util.NetworkHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.common.cap.MoneyData;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixik.sdm_economy.common.currency.CurrencyRegister;
import net.sixik.sdm_economy.network.adv.UpdateClientDataS2C;
import net.sixik.sdm_economy.network.adv.UpdateServerDataC2S;
import net.sixik.sdm_economy.network.register.RegisterCustomCurrenciesC2S;
import net.sixik.sdm_economy.network.register.UpdateRegisterCurrenciesS2C;

import java.util.List;
import java.util.Objects;

public class CurrencyHelper {

    public static void addMoney(Player player, String id, long amount) {
        setMoney(player,id,getMoney(player,id) + amount);
    }

    public static void setMoney(Player player, String id, long amount){
        if(player.isLocalPlayer()){
            PlayerMoneyData.CLIENT.CLIENT_MONET.setMoney(id, amount);
            NetworkManager.sendToServer(new UpdateServerDataC2S(PlayerMoneyData.CLIENT.CLIENT_MONET.serializeNBT()));
        } else {
            MoneyData data = PlayerMoneyData.from((ServerPlayer) player);
            data.setMoney(id, amount);
            NetworkHelper.sendTo((ServerPlayer) player, new UpdateClientDataS2C(data.serializeNBT()));
            PlayerMoneyData.savePlayer(player.getGameProfile().getId(), player.getServer());
        }
    }

    public static long getMoney(Player player, String id){
        for (AbstractCurrency currency : getPlayerData(player).currencies) {
            if(Objects.equals(currency.getID(), id)) {
                return currency.moneys;
            }
        }

        return 0;
    }

    public static MoneyData getPlayerData(Player player){
        if(player.isLocalPlayer()) {
            return PlayerMoneyData.CLIENT.CLIENT_MONET;
        }
        return PlayerMoneyData.from((ServerPlayer) player);
    }

    public static boolean convertMoney(Player player, String from, String to, long count) {
        for (AbstractCurrency currency : getPlayerData(player).getCurrencies()) {
            if(Objects.equals(currency.getID(), from)) {
                if(currency.conversions.containsKey(to)) {
                    long d = currency.conversions.get(to);
                    if(currency.moneys >= d * count) {
                        setMoney(player, from, currency.moneys - d * count);
                        setMoney(player, to, count);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static List<String> getAllCurrencyKeys(){
        return CurrencyRegister.CURRENCIES.keySet().stream().toList();
    }

    public static AbstractCurrency.Constructor<?> registerBasicCurrency(String id, long defaultValue){
        return CurrencyRegister.register(() -> new AbstractCurrency(defaultValue) {
            @Override
            public String getID() {
                return id;
            }
        });
    }

    public static void registerCustomCurrency(String id, long defaultValue, MinecraftServer server) {
        registerCustomCurrency(id,defaultValue, "◎", server);
    }

    public static void registerCustomCurrency(String id, long defaultValue, String symbol, MinecraftServer server){
        AbstractCurrency currency = new AbstractCurrency(defaultValue) {
            @Override
            public String getID() {
                return id;
            }

        };
        currency.specialSymbol = symbol;
        AbstractCurrency.Constructor<?> constructor = () -> currency;
        CurrencyRegister.registerCustomCurrency(constructor);
        CurrencyRegister.register(constructor);
        NetworkHelper.sendToAll(server, new UpdateRegisterCurrenciesS2C(CurrencyRegister.getRegisteredCurrencies()));
    }

    public static void registerCustomCurrency(String id, long defaultValue){
        registerCustomCurrency(id, defaultValue, "◎");
    }

    public static void registerCustomCurrency(String id, long defaultValue, String symbol){
        AbstractCurrency currency = new AbstractCurrency(defaultValue) {
            @Override
            public String getID() {
                return id;
            }
        };
        currency.specialSymbol = symbol;
        NetworkManager.sendToServer(new RegisterCustomCurrenciesC2S(currency.serializeNBT()));
    }

    public static class Basic {

        public static void addMoney(Player player, long count){
             CurrencyHelper.addMoney(player, "basic_money", count);
        }

        public static void setMoney(Player player, long count){
             CurrencyHelper.setMoney(player, "basic_money", count);
        }

        public static long getMoney(Player player){
            return CurrencyHelper.getMoney(player, "basic_money");
        }
    }
}
