package net.sixik.sdm_economy.adv;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.api.IOtherCurrency;
import net.sixik.sdm_economy.common.cap.MoneyData;
import net.sixik.sdm_economy.network.adv.UpdateClientDataS2C;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMoneyData {

    public static boolean isImpactorLoaded = false;

    public static IOtherCurrency OTHER_CURRENCY_MOD = null;

    public static String folderName = "SDMEconomy";

    public static PlayerMoneyData CLIENT = new PlayerMoneyData();
    public static PlayerMoneyData SERVER;

    public MoneyData CLIENT_MONET = new MoneyData();
    public Map<UUID, MoneyData> PLAYER_MONEY = new HashMap<>();

    public static MoneyData from(ServerPlayer player) {
        if(SERVER.PLAYER_MONEY.containsKey(player.getGameProfile().getId())) {
            MoneyData data = SERVER.PLAYER_MONEY.get(player.getGameProfile().getId());
            if(data.isOtherMod) {
                if(OTHER_CURRENCY_MOD != null) {
                    data.otherModId = OTHER_CURRENCY_MOD.getModID();
                    OTHER_CURRENCY_MOD.updateMoneyData(player, data);
                } else {
                    data.isOtherMod = false;
                    data.loadAllCurrencies();
                }
            }

            return data;
        }

        MoneyData data = new MoneyData();
        if(OTHER_CURRENCY_MOD != null) {
            data.isOtherMod = true;
            data.otherModId = OTHER_CURRENCY_MOD.getModID();
            OTHER_CURRENCY_MOD.updateMoneyData(player, data);
        } else {
            data.loadAllCurrencies();
        }

        SERVER.PLAYER_MONEY.put(player.getGameProfile().getId(), data);
        return SERVER.PLAYER_MONEY.get(player.getGameProfile().getId());
    }

    public static void onPlayerLoggedIn(ServerPlayer player) {
        from(player);
        new UpdateClientDataS2C(PlayerMoneyData.SERVER.saveToNBT(player)).sendTo(player);
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

        try {
            SERVER.PLAYER_MONEY.clear();
            for (File file : path.toFile().listFiles()) {
                UUID uuid = UUID.fromString(FilenameUtils.removeExtension(file.getName()));
                CompoundTag nbt = NbtIo.read(file);
                if(nbt == null) continue;

                MoneyData moneyData = new MoneyData();
                moneyData.deserializeNBT(nbt);
                SERVER.PLAYER_MONEY.put(uuid, moneyData);
            }
        } catch (Exception e){
            SDMEconomy.printStackTrace("", e);
        }
    }

    public static void save(MinecraftServer server) {
        if(SERVER == null) return;

        Path path = server.getWorldPath(LevelResource.ROOT).resolve(folderName);
        if(!path.toFile().exists()) {
            path.toFile().mkdir();
        }

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
            NbtIo.write(SERVER.PLAYER_MONEY.get(player).serializeNBT(), f1.toFile());
        } catch (Exception e) {
            SDMEconomy.printStackTrace("", e);
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
}

