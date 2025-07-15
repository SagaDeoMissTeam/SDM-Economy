package net.sixik.sdmeconomy.network.packages.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

public class SendUpdatePlayerCurrencyS2C implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendUpdatePlayerCurrencyS2C> TYPE =
            new CustomPacketPayload.Type<>(SDMEconomyNetwork.nameOf("send_update_currency_player"));

    public static final StreamCodec<FriendlyByteBuf, SendUpdatePlayerCurrencyS2C> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, SendUpdatePlayerCurrencyS2C::nbt, SendUpdatePlayerCurrencyS2C::new);

    private final CompoundTag nbt;

    public SendUpdatePlayerCurrencyS2C(CurrencyPlayerData.PlayerCurrency currency) {
        this.nbt = currency.serialize();
    }

    public SendUpdatePlayerCurrencyS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendUpdatePlayerCurrencyS2C message, NetworkManager.PacketContext context) {
        CurrencyPlayerData.PlayerCurrency currency = CurrencyPlayerData.PlayerCurrency.deserialize(message.nbt);
        CurrencyHelper.getPlayerCurrencyClientData().updateCurrencyForce(currency);
    }

}
