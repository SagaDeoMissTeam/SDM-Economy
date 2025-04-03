package net.sixik.sdmeconomy.network.packages.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.sixik.sdmeconomy.economyData.CurrencyData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

public class SendCurrenciesS2C extends BaseS2CMessage {

    private final CompoundTag nbt;

    public SendCurrenciesS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }


    public SendCurrenciesS2C(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.SEND_CURRENCIES;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        CurrencyData.CLIENT.reloadCurrenciesFromNetwork(nbt);
    }
}
