package net.sixik.sdmeconomy.utils;

import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.CustomCurrencies;
import net.sixik.sdmeconomy.data.CustomPlayerData;
import net.sixik.sdmeconomy.economy.Currency;
import net.sixik.sdmeconomy.economyData.CurrenciesIO;
import net.sixik.sdmeconomy.economyData.CurrencyData;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;
import net.sixik.sdmeconomy.network.packages.client.SendCurrenciesS2C;
import net.sixik.sdmeconomy.network.packages.client.SendCustomDataS2C;
import net.sixik.sdmeconomy.network.packages.client.SendPlayerCurrenciesS2C;
import net.sixik.sdmeconomy.network.packages.server.SendCreateCurrencyC2S;
import net.sixik.sdmeconomy.network.packages.server.SendDeleteCurrencyC2S;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CurrencyHelper {

    public static Executor executor = Executors.newSingleThreadExecutor();

    public static ErrorCodeStruct<CurrencyData> getAllCurrency() {
        if(CurrencyData.SERVER != null) {
            return new ErrorCodeStruct<>(CurrencyData.SERVER, ErrorCodes.SUCCESS);
        }

        if(CurrencyData.CLIENT != null) {
            return new ErrorCodeStruct<>(CurrencyData.CLIENT, ErrorCodes.SUCCESS);
        }

        return new ErrorCodeStruct<>(null, ErrorCodes.FAIL);
    }

    public static ErrorCodes syncPlayer(ServerPlayer player) {
        try {
            syncCurrencyData(player);
            syncCustomData(player);
            new SendPlayerCurrenciesS2C(player).sendTo(player);
        } catch (Exception e) {
            SDMEconomy.printStackTrace("Error when try sync player currency", e);
            return ErrorCodes.FAIL;
        }
        return ErrorCodes.SUCCESS;
    }

    public static ErrorCodeStruct<CurrencyData> getCurrencyData(boolean isClient) {
        try {
            if (isClient) {
                if (CurrencyData.CLIENT == null) {
                    return new ErrorCodeStruct<>(null, ErrorCodes.FAIL);
                }
                return new ErrorCodeStruct<>(CurrencyData.CLIENT, ErrorCodes.FAIL);
            }

            if (CurrencyData.SERVER == null) {
                return new ErrorCodeStruct<>(null, ErrorCodes.FAIL);
            }
            return new ErrorCodeStruct<>(CurrencyData.SERVER, ErrorCodes.FAIL);
        } catch (Exception e) {
            SDMEconomy.printStackTrace("Error when try get currency data", e);
            return new ErrorCodeStruct<>(null, ErrorCodes.FAIL);
        }
    }

    public static ErrorCodeStruct<LinkedList<CurrencyPlayerData.PlayerCurrency>> getCurrencyPlayerData(Player player) {
        try {
            if (CurrencyPlayerData.SERVER != null)
                return new ErrorCodeStruct<>(CurrencyPlayerData.SERVER.getPlayersCurrency(player));

            if (CurrencyPlayerData.CLIENT != null) {
                return new ErrorCodeStruct<>(CurrencyPlayerData.CLIENT.currencies);
            }
        } catch (Exception e) {
            SDMEconomy.printStackTrace("Error when try get currency for player " + player.getGameProfile().getId(), e);
            return new ErrorCodeStruct<>(new LinkedList<>(), ErrorCodes.FAIL);
        }

        return new ErrorCodeStruct<>(new LinkedList<>(), ErrorCodes.NOT_FOUND);
    }

    public static CurrencyPlayerData.Server getPlayerCurrencyServerData() {
        return CurrencyPlayerData.SERVER;
    }

    public static CurrencyPlayerData.Client getPlayerCurrencyClientData() {
        return CurrencyPlayerData.CLIENT;
    }

    public static void syncCurrencyData(ServerPlayer player) {
        new SendCurrenciesS2C(CurrenciesIO.saveToNBT(CurrencyData.SERVER.currencies)).sendTo(player);
    }

    public static void syncCurrencyData(MinecraftServer server) {
        new SendCurrenciesS2C(CurrenciesIO.saveToNBT(CurrencyData.SERVER.currencies)).sendToAll(server);
    }

    public static void createCurrencyOnClient(Currency currency) {
        new SendCreateCurrencyC2S(currency).sendToServer();
    }

    public static ErrorCodes createCurrencyOnServer(Currency currency) {
        if(CurrencyData.SERVER == null)
            return ErrorCodes.FAIL;

        CurrencyData.SERVER.currencies.add(currency);
        for (Map.Entry<UUID, LinkedList<CurrencyPlayerData.PlayerCurrency>> entry :
                CurrencyPlayerData.SERVER.playersCurrencyMap.entrySet()) {
            entry.getValue().add(new CurrencyPlayerData.PlayerCurrency(currency.copy(), currency.getDefaultValue()));
        }

        saveAll(CurrencyData.SERVER.server);

        return ErrorCodes.SUCCESS;
    }

    public static void deleteCurrencyOnClient(Currency currency) {
        new SendDeleteCurrencyC2S(currency).sendToServer();
    }

    public static ErrorCodes deleteCurrencyOnServer(Currency currency) {
        if(CurrencyData.SERVER == null)
            return ErrorCodes.FAIL;

        CurrencyData.SERVER.currencies.removeIf(s -> s.getName().equals(currency.getName()));
        for (Map.Entry<UUID, LinkedList<CurrencyPlayerData.PlayerCurrency>> entry :
                CurrencyPlayerData.SERVER.playersCurrencyMap.entrySet()) {
            entry.getValue().removeIf(s -> s.currency.getName().equals(currency.getName()));
        }

        saveAll(CurrencyData.SERVER.server);

        return ErrorCodes.SUCCESS;
    }

    public static ErrorCodes syncCurrencyFromServerToClient(MinecraftServer server) {
        if(CurrencyData.SERVER == null)
            return ErrorCodes.FAIL;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            new SendPlayerCurrenciesS2C(player).sendTo(player);
        }

        return ErrorCodes.SUCCESS;
    }

    public static UUID getPlayerUUID(Player player) {
        return player.getGameProfile().getId();
    }

    public static boolean isAdmin(Player player) {
        return player.hasPermissions(3);
    }

    public static void saveAll(MinecraftServer server) {
        saveCurrencyData();
        savePlayerData(server);
        CustomPlayerData.SERVER.save(server.getWorldPath(LevelResource.ROOT));
    }

    public static void saveCurrencyData() {
        CurrenciesIO.save(Platform.getConfigFolder(), CurrencyData.SERVER.currencies);
    }

    public static void savePlayerData(MinecraftServer server) {
        CurrenciesIO.savePlayersData(server.getWorldPath(LevelResource.ROOT), CurrencyPlayerData.SERVER.playersCurrencyMap);
    }

    public static ErrorCodeStruct<Boolean> checkNewCurrency() {
        try {
            List<Currency> check = new ArrayList<>();
            List<Currency> notFounded = new ArrayList<>();


            for (Supplier<Currency> value : CustomCurrencies.CURRENCIES.values()) {
                check.add(value.get());
            }

            for (Currency currency1 : check) {
                if (CurrencyData.SERVER.currencies.stream().noneMatch(c -> c.getName().equals(currency1.getName()))) {
                    notFounded.add(currency1.copy());
                }
            }

            CurrencyData.SERVER.currencies.addAll(notFounded);

            for (Map.Entry<UUID, LinkedList<CurrencyPlayerData.PlayerCurrency>> entry : CurrencyPlayerData.SERVER.playersCurrencyMap.entrySet()) {
                notFounded = new ArrayList<>();
                for (Currency currency1 : check) {
                    if (entry.getValue().stream().noneMatch(c -> c.currency.getName().equals(currency1.getName()))) {
                        notFounded.add(currency1.copy());
                    }
                }

                if(notFounded.isEmpty()) continue;
                entry.getValue().addAll(
                        notFounded
                        .stream()
                        .map(s -> new CurrencyPlayerData.PlayerCurrency(s, s.getDefaultValue()))
                                .toList());
            }

            return new ErrorCodeStruct<>(true, ErrorCodes.SUCCESS);
        } catch (Exception e) {
            SDMEconomy.printStackTrace("Error when check new currency", e);
            return new ErrorCodeStruct<>(false, ErrorCodes.FAIL);
        }
    }

    public static CustomPlayerData.Server getCustomServerData() {
        return CustomPlayerData.SERVER;
    }

    public static CustomPlayerData.Client getCustomClientData() {
        return CustomPlayerData.CLIENT;
    }

    public static void syncCustomData(ServerPlayer player) {
        new SendCustomDataS2C(getPlayerUUID(player)).sendTo((ServerPlayer) player);
    }

    public static ErrorCodes updateCustomData(ServerPlayer player, Consumer<CompoundTag> nbt) {
       try {
           CustomPlayerData.Data data = getCustomServerData().getPlayerCustomData(player);
           nbt.accept(data.nbt);
           new SendCustomDataS2C(getPlayerUUID(player)).sendTo((ServerPlayer) player);
           return ErrorCodes.SUCCESS;
       } catch (Exception e) {
           SDMEconomy.printStackTrace("Error when update custom data", e);
           return ErrorCodes.FAIL;
       }
    }

    public static CompoundTag getCustomData(Player player) {
        if(player.isLocalPlayer())
            return getCustomClientData().data.nbt;
        return getCustomServerData().getPlayerCustomData(player).nbt;
    }
}
