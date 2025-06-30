package net.sixik.sdmeconomy.network.ASK.ASK_base;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.sixik.sdmeconomy.api.AbstractASKRequest;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

import java.util.Optional;
import java.util.function.Function;

public class DataSyncASKS2C extends BaseS2CMessage {

    private final String id;
    private final CompoundTag nbt;

    public DataSyncASKS2C(String id, CompoundTag nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public DataSyncASKS2C(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readUtf();
        this.nbt = byteBuf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.ASK_TO_CLIENT;
    }

    @Override
    public void write(RegistryFriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(id);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        Optional<Function<Void, AbstractASKRequest>> opt = SDMEconomyNetwork.getRequest(id);
        if(opt.isEmpty()) return;
        opt.get().apply(null).onClientTakeRequest(nbt, packetContext);
    }
}