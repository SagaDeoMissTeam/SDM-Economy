package net.sixik.sdmeconomy.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class BaseNetworkHandler {

    public abstract void sendToAll(CustomPacketPayload.Type<?> type, MinecraftServer server, Packet<?> packet);

    public abstract void sendTo(CustomPacketPayload.Type<?> type, ServerPlayer player, Packet<?> packet);

    public abstract <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T packet);

    public abstract <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet);
}
