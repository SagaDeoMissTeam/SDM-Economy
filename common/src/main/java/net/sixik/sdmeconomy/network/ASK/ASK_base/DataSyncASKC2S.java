package net.sixik.sdmeconomy.network.ASK.ASK_base;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.AbstractASKRequest;
import net.sixik.sdmeconomy.network.ASK.ASKHandler;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

import java.util.Optional;
import java.util.function.Function;

public class DataSyncASKC2S implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DataSyncASKC2S> TYPE =
            new CustomPacketPayload.Type<>(SDMEconomyNetwork.nameOf("ask_to_server"));

    public static final StreamCodec<FriendlyByteBuf, DataSyncASKC2S> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, DataSyncASKC2S::nbt, DataSyncASKC2S::new);

    private final CompoundTag nbt;

    public DataSyncASKC2S(String id, CompoundTag nbt) {
        CompoundTag data = new CompoundTag();
        data.putString("id", id);
        data.put("data", nbt);
        this.nbt = data;
    }

    public DataSyncASKC2S(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    public static void handle(DataSyncASKC2S message, NetworkManager.PacketContext packetContext) {
        String id = message.nbt.getString("id");


        Optional<Function<Void, AbstractASKRequest>> opt = SDMEconomyNetwork.getRequest(id);
        if(opt.isEmpty()) {
            SDMEconomy.LOGGER.error("Request is null");
            return;
        }
        opt.get().apply(null).onServerTakeRequest(message.nbt.getCompound("data"), packetContext);
        ASKHandler.getInstance().getNextRequest(packetContext.getPlayer()).ifPresent(s -> s.waitRequest(false));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
