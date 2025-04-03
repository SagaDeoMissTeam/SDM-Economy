package net.sixik.sdmeconomy.network.requests;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;

import java.util.function.BiFunction;

public class SDMRequest<T> {

    public final String name;
    public final BiFunction<NetworkManager.PacketContext, CompoundTag, CompoundTag> server;
    public final BiFunction<NetworkManager.PacketContext, CompoundTag, T> client;


    public SDMRequest(String name, BiFunction<NetworkManager.PacketContext, CompoundTag, CompoundTag> server, BiFunction<NetworkManager.PacketContext, CompoundTag, T> client) {
        this.name = name;
        this.server = server;
        this.client = client;
    }
}
