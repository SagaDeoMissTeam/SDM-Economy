package net.sixik.sdm_economy.network.adv;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.network.EconomyNetwork;

public class UpdateServerDataC2S extends BaseC2SMessage {

    public final CompoundTag nbt;

    public UpdateServerDataC2S(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public UpdateServerDataC2S(FriendlyByteBuf buf) {
        this.nbt = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
//        return EconomyNetwork.UPDATE_SERVER_DATA;
        return null;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override

    public void handle(NetworkManager.PacketContext packetContext) {
        PlayerMoneyData.SERVER.loadFromNBT(packetContext.getPlayer(), nbt);
        PlayerMoneyData.savePlayer(packetContext.getPlayer().getGameProfile().getId(), packetContext.getPlayer().getServer());
    }
}
