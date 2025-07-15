package net.sixik.sdmeconomy.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.AbstractASKRequest;
import net.sixik.sdmeconomy.network.ASK.ASK_base.DataSyncASKC2S;
import net.sixik.sdmeconomy.network.ASK.ASK_base.DataSyncASKS2C;
import net.sixik.sdmeconomy.network.packages.client.SendCurrenciesS2C;
import net.sixik.sdmeconomy.network.packages.client.SendCustomDataS2C;
import net.sixik.sdmeconomy.network.packages.client.SendPlayerCurrenciesS2C;
import net.sixik.sdmeconomy.network.packages.client.SendUpdatePlayerCurrencyS2C;
import net.sixik.sdmeconomy.network.packages.server.SendCreateCurrencyC2S;
import net.sixik.sdmeconomy.network.packages.server.SendDeleteCurrencyC2S;
import net.sixik.sdmeconomy.utils.BaseNetworkHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class SDMEconomyNetwork {

    private static BaseNetworkHandler Handler;

    protected static final Map<String, Function<Void, AbstractASKRequest>> REQUESTS = new HashMap<>();

    public static ResourceLocation nameOf(String name) {
        return ResourceLocation.tryBuild(SDMEconomy.MODID, name);
    }

    public static void init(BaseNetworkHandler handler) {
        SDMEconomyNetwork.Handler = handler;

        registerC2S(SendCreateCurrencyC2S.TYPE,       SendCreateCurrencyC2S.STREAM_CODEC,       SendCreateCurrencyC2S::handle);
        registerC2S(SendDeleteCurrencyC2S.TYPE,       SendDeleteCurrencyC2S.STREAM_CODEC,       SendDeleteCurrencyC2S::handle);
        registerC2S(DataSyncASKC2S.TYPE,              DataSyncASKC2S.STREAM_CODEC,              DataSyncASKC2S::handle);
        registerS2C(SendCustomDataS2C.TYPE,           SendCustomDataS2C.STREAM_CODEC,           SendCustomDataS2C::handle);
        registerS2C(SendUpdatePlayerCurrencyS2C.TYPE, SendUpdatePlayerCurrencyS2C.STREAM_CODEC, SendUpdatePlayerCurrencyS2C::handle);
        registerS2C(SendPlayerCurrenciesS2C.TYPE,     SendPlayerCurrenciesS2C.STREAM_CODEC,     SendPlayerCurrenciesS2C::handle);
        registerS2C(SendCurrenciesS2C.TYPE,           SendCurrenciesS2C.STREAM_CODEC,           SendCurrenciesS2C::handle);
        registerS2C(DataSyncASKS2C.TYPE,              DataSyncASKS2C.STREAM_CODEC,              DataSyncASKS2C::handle);
    }

    public static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> handler) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), type, codec, handler);
    }

    public static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> handler) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), type, codec, handler);
        } else {
            NetworkManager.registerS2CPayloadType(type, codec);
        }
    }

    public static void sendToAll(CustomPacketPayload.Type<?> type, MinecraftServer server, Packet<?> packet) {
        Handler.sendToAll(type, server, packet);
    }

    public static void sendTo(CustomPacketPayload.Type<?> type, ServerPlayer player, Packet<?> packet) {
        Handler.sendTo(type, player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T packet) {
        Handler.sendToAll(server, packet);
    }

    public static <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet) {
        Handler.sendTo(player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T payload) {
        NetworkManager.sendToServer(payload);
    }

    public static String registerRequest(String id, Function<Void, AbstractASKRequest> func) {
        if(REQUESTS.containsKey(id))
            throw new RuntimeException("Entry Type with " + id + " id already registered!");

        REQUESTS.put(id, func);
        SDMEconomy.LOGGER.info("Registered ASK request [{}]", id);
        return id;
    }

    public static Optional<Function<Void, AbstractASKRequest>> getRequest(String id) {
        return Optional.ofNullable(REQUESTS.getOrDefault(id, null));
    }
}
