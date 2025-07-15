package net.sixik.sdmeconomy.network.packages.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sixik.sdmeconomy.economyData.CurrencyData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

public record SendCurrenciesS2C(CompoundTag nbt) implements CustomPacketPayload {

    public static final Type<SendCurrenciesS2C> TYPE =
            new Type<>(SDMEconomyNetwork.nameOf("send_currencies"));

    public static final StreamCodec<FriendlyByteBuf, SendCurrenciesS2C> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, SendCurrenciesS2C::nbt, SendCurrenciesS2C::new);

    public static void handle(SendCurrenciesS2C message, NetworkManager.PacketContext context) {
        CurrencyData.CLIENT.reloadCurrenciesFromNetwork(message.nbt);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
