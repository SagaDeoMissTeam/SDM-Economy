package net.sixik.sdmeconomy.network.packages.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.sixik.sdmeconomy.data.CustomPlayerData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;
import net.sixik.sdmeconomy.utils.CurrencyHelper;

import java.util.UUID;

public class SendCustomDataS2C extends BaseS2CMessage {

    private final CompoundTag nbt;

    public SendCustomDataS2C(UUID player) {
        this.nbt = CurrencyHelper.getCustomServerData().getPlayerCustomData(player).nbt;
    }

    public SendCustomDataS2C(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.SEND_UPDATE_CUSTOM_DATA;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        CustomPlayerData.CLIENT = new CustomPlayerData.Client(new CustomPlayerData.Data(nbt));
    }
}
