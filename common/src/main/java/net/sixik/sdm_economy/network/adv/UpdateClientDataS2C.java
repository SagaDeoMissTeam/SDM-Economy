package net.sixik.sdm_economy.network.adv;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.network.EconomyNetwork;

public class UpdateClientDataS2C extends BaseS2CMessage {

    public final CompoundTag nbt;

    public UpdateClientDataS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public UpdateClientDataS2C(FriendlyByteBuf buf) {
        this.nbt = buf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return EconomyNetwork.UPDATE_CLIENT_DATA;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handle(NetworkManager.PacketContext packetContext) {
        PlayerMoneyData.loadFromNBTClient(nbt);
    }
}
