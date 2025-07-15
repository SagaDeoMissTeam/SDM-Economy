package net.sixik.sdmeconomy.network.ASK.ASK_base;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sixik.sdmeconomy.api.AbstractASKRequest;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

import java.util.Optional;
import java.util.function.Function;

public class DataSyncASKS2C implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DataSyncASKS2C> TYPE =
            new CustomPacketPayload.Type<>(SDMEconomyNetwork.nameOf("ask_to_client"));

    public static final StreamCodec<FriendlyByteBuf, DataSyncASKS2C> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, DataSyncASKS2C::nbt, DataSyncASKS2C::new);

    private final CompoundTag nbt;

    public DataSyncASKS2C(String id, CompoundTag nbt) {
        CompoundTag data = new CompoundTag();
        data.putString("id", id);
        data.put("data", nbt);
        this.nbt = data;
    }

    public DataSyncASKS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    public static void handle(DataSyncASKS2C message, NetworkManager.PacketContext packetContext) {
        Optional<Function<Void, AbstractASKRequest>> opt = SDMEconomyNetwork.getRequest(message.nbt.getString("id"));
        if(opt.isEmpty()) return;
        opt.get().apply(null).onClientTakeRequest(message.nbt.getCompound("data"), packetContext);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}