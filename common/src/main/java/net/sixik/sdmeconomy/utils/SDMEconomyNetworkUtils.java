package net.sixik.sdmeconomy.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SDMEconomyNetworkUtils {
    
    @ExpectPlatform
    public static void sendToAll(CustomPacketPayload.Type<?> type, MinecraftServer server, Packet<?> packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendTo(CustomPacketPayload.Type<?> type, ServerPlayer player, Packet<?> packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet) {
        throw new AssertionError();
    }

}
