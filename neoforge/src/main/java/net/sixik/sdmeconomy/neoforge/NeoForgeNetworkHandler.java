package net.sixik.sdmeconomy.neoforge;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmeconomy.utils.BaseNetworkHandler;

import java.util.List;

public class NeoForgeNetworkHandler extends BaseNetworkHandler {

    @Override
    public void sendToAll(CustomPacketPayload.Type<?> type, MinecraftServer server, Packet<?> packet) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            sendTo(type, player, packet);
        }
    }

    @Override
    public void sendTo(CustomPacketPayload.Type<?> type, ServerPlayer player, Packet<?> packet) {
        if (player.connection.hasChannel(type)) {
            player.connection.send(packet);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T packet) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            sendTo(player, packet);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet) {
        if (player.connection.hasChannel(packet.type())) {
            NetworkManager.sendToPlayer(player, packet);
        }
    }
}
