package net.sixik.sdmeconomy.network.packages.server;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.economy.Currency;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

public class SendDeleteCurrencyC2S implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendDeleteCurrencyC2S> TYPE =
            new CustomPacketPayload.Type<>(SDMEconomyNetwork.nameOf("send_delete_currency"));

    public static final StreamCodec<FriendlyByteBuf, SendDeleteCurrencyC2S> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, SendDeleteCurrencyC2S::nbt, SendDeleteCurrencyC2S::new);

    private final CompoundTag nbt;

    public SendDeleteCurrencyC2S(Currency currency) {
        this.nbt = currency.serialize();
    }

    public SendDeleteCurrencyC2S(CompoundTag compoundTag) {
        this.nbt = compoundTag;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendDeleteCurrencyC2S message, NetworkManager.PacketContext context) {
        if(!CurrencyHelper.isAdmin(context.getPlayer())) return;

        CurrencyHelper.deleteCurrencyOnServer(Currency.deserialize(message.nbt));

        CurrencyHelper.saveAll(context.getPlayer().getServer());

        for (ServerPlayer player : context.getPlayer().getServer().getPlayerList().getPlayers()) {
            CurrencyHelper.syncCurrencyData(player);
            CurrencyHelper.syncPlayer(player);
        }
    }
}
