package net.sixik.sdmeconomy.network.ASK.ASK_base;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.AbstractASKRequest;
import net.sixik.sdmeconomy.network.ASK.ASKHandler;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

import java.util.Optional;
import java.util.function.Function;

public class DataSyncASKC2S extends BaseC2SMessage {

    private final String id;
    private final CompoundTag nbt;

    public DataSyncASKC2S(String id, CompoundTag nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public DataSyncASKC2S(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readUtf();
        this.nbt = byteBuf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.ASK_TO_SERVER;
    }

    @Override
    public void write(RegistryFriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(id);
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        Optional<Function<Void, AbstractASKRequest>> opt = SDMEconomyNetwork.getRequest(id);
        if(opt.isEmpty()) {
            SDMEconomy.LOGGER.error("Request is null");
            return;
        }
        opt.get().apply(null).onServerTakeRequest(nbt, packetContext);
        ASKHandler.getInstance().getNextRequest(packetContext.getPlayer()).ifPresent(s -> s.waitRequest(false));
    }
}
