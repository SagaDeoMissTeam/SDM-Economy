package net.sixik.sdm_economy.adv;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.util.NetworkHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.common.cap.MoneyData;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixik.sdm_economy.common.currency.CurrencyRegister;
import net.sixik.sdm_economy.network.adv.UpdateClientDataS2C;
import net.sixik.sdm_economy.network.register.UpdateRegisterCurrenciesS2C;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMoneyData {



    public static String folderName = "SDMEconomy";
    public static String customCurrenciesFile = "custom_currencies.data";

    public static PlayerMoneyData CLIENT = new PlayerMoneyData();
    public static PlayerMoneyData SERVER;

    public MoneyData CLIENT_MONET = new MoneyData();
    public Map<UUID, MoneyData> PLAYER_MONEY = new HashMap<>();

    public static MoneyData from(ServerPlayer player) {
        if(SERVER.PLAYER_MONEY.containsKey(player.getGameProfile().getId())) {
            return SERVER.PLAYER_MONEY.get(player.getGameProfile().getId());
        }

        MoneyData data = new MoneyData();
        data.loadAllCurrencies();
        SERVER.PLAYER_MONEY.put(player.getGameProfile().getId(), data);
        return SERVER.PLAYER_MONEY.get(player.getGameProfile().getId());
    }

    public static void onPlayerLoggedIn(ServerPlayer player) {
        from(player);

        NetworkHelper.sendTo(player, new UpdateRegisterCurrenciesS2C(CurrencyRegister.getRegisteredCurrencies()));
        NetworkHelper.sendTo(player, new UpdateClientDataS2C(PlayerMoneyData.SERVER.saveToNBT(player)));
        PlayerMoneyData.savePlayer(player.getGameProfile().getId(), player.server);
    }

    public static void onPlayerLoggedOut(ServerPlayer player) {
        if(SERVER.PLAYER_MONEY.containsKey(player.getGameProfile().getId())) {
            savePlayer(player.getGameProfile().getId(), player.server);
        }
    }

    public static void load(MinecraftServer server) {
        if(SERVER == null) {
            SERVER = new PlayerMoneyData();
        }

        Path path = server.getWorldPath(LevelResource.ROOT).resolve(folderName);
        if(!path.toFile().exists()) {
            path.toFile().mkdir();
            return;
        }

        loadCustomCurrency(server);

        try {
            SERVER.PLAYER_MONEY.clear();
            for (File file : path.toFile().listFiles()) {
                UUID uuid = UUID.fromString(FilenameUtils.removeExtension(file.getName()));
                CompoundTag nbt = NbtIo.read(file.toPath());
                if(nbt == null) continue;

                MoneyData moneyData = new MoneyData();
                moneyData.deserializeNBT(nbt);
                SERVER.PLAYER_MONEY.put(uuid, moneyData);
            }
        } catch (Exception e){
            SDMEconomy.printStackTrace(e.getMessage(), e);
        }
    }

    public static void save(MinecraftServer server) {
        if(SERVER == null) return;

        Path path = server.getWorldPath(LevelResource.ROOT).resolve(folderName);
        if(!path.toFile().exists()) {
            path.toFile().mkdir();
        }

        saveCustomCurrency(server);

        for (UUID uuid : SERVER.PLAYER_MONEY.keySet()) {
            savePlayer(uuid, path);
        }
    }

    public void replacePlayerData(UUID old, UUID newPlayer, MinecraftServer server) {
        MoneyData data = SERVER.PLAYER_MONEY.get(old);
        if(SERVER.PLAYER_MONEY.containsKey(old)) {
            SERVER.PLAYER_MONEY.remove(old);
            SERVER.PLAYER_MONEY.put(newPlayer, data);
            savePlayer(newPlayer, server);
        }
    }

    public void deleteOldFile(UUID uuid, MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT).resolve(folderName);
        if(!path.toFile().exists()) {
            path.toFile().mkdir();
        }

        if(SERVER.PLAYER_MONEY.containsKey(uuid)) {
            SERVER.PLAYER_MONEY.remove(uuid);
        }

        path = path.resolve(uuid.toString() +".data");
        if(path.toFile().exists()){
            path.toFile().delete();
        }
    }

    public static void savePlayer(UUID player, MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT).resolve(folderName);
        if(!path.toFile().exists()) {
            path.toFile().mkdir();
        }

        savePlayer(player, path);
    }

    public static void savePlayer(UUID player, Path path) {
        try {
            if(!SERVER.PLAYER_MONEY.containsKey(player)) return;

            Path f1 = path.resolve(player.toString() + ".data");
            if(!f1.toFile().exists())
                f1.toFile().createNewFile();
            NbtIo.write(SERVER.PLAYER_MONEY.get(player).serializeNBT(), f1);
        } catch (Exception e) {
            SDMEconomy.printStackTrace(e.getMessage(), e);
        }
    }

    public static void loadFromNBTClient(CompoundTag nbt) {
        PlayerMoneyData.CLIENT.CLIENT_MONET.deserializeNBT(nbt);
    }

    public void loadFromNBT(Player player, CompoundTag nbt) {
        if(!PLAYER_MONEY.containsKey(player.getGameProfile().getId())) {
            MoneyData data = new MoneyData();
            data.deserializeNBT(nbt);
            PLAYER_MONEY.put(player.getGameProfile().getId(), data);
        }

        PLAYER_MONEY.get(player.getGameProfile().getId()).deserializeNBT(nbt);
    }


    public CompoundTag saveToNBT(Player player) {
        if(!PLAYER_MONEY.containsKey(player.getGameProfile().getId())) {
            MoneyData data = new MoneyData();
            data.loadAllCurrencies();
            PLAYER_MONEY.put(player.getGameProfile().getId(), data);
        }

        return PLAYER_MONEY.get(player.getGameProfile().getId()).serializeNBT();
    }

    public static void saveCustomCurrency(MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT);

        CompoundTag nbt = new CompoundTag();
        ListTag currenciesList = new ListTag();
        for (AbstractCurrency.Constructor<?> value : CurrencyRegister.CUSTOM_CURRENCIES.values()) {
            currenciesList.add(value.createDefault().serializeNBT());
        }
        nbt.put("currenciesList", currenciesList);
        try {
            NbtIo.write(nbt, path.resolve(customCurrenciesFile));
        } catch (IOException e) {
            SDMEconomy.printStackTrace(e.getMessage(), e);
        }
    }

    public static void loadCustomCurrency(MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT);
        File customFile = path.resolve(customCurrenciesFile).toFile();
        if(!customFile.exists()) return;

        try {
            CompoundTag nbt = NbtIo.read(customFile.toPath());
            if(nbt == null) return;
            CurrencyRegister.CUSTOM_CURRENCIES.clear();
            ListTag currenciesList = (ListTag) nbt.get("currenciesList");
            for (Tag tag : currenciesList) {
                AbstractCurrency currency = new AbstractCurrency(0) {
                    @Override
                    public String getID() {
                        return ((CompoundTag)tag).getString("currencyID");
                    }
                };
                currency.deserializeNBT((CompoundTag) tag);
                AbstractCurrency.Constructor<?> constructor = () -> currency;
                CurrencyRegister.registerCustomCurrency(constructor);
                CurrencyRegister.register(constructor);
            }
        } catch (Exception e) {
            SDMEconomy.printStackTrace(e.getMessage(), e);
        }
    }
}
