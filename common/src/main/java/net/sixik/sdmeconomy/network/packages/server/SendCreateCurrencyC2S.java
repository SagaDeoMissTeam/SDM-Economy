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

public class SendCreateCurrencyC2S implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendCreateCurrencyC2S> TYPE =
            new CustomPacketPayload.Type<>(SDMEconomyNetwork.nameOf("send_create_currency"));

    public static final StreamCodec<FriendlyByteBuf, SendCreateCurrencyC2S> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, SendCreateCurrencyC2S::nbt, SendCreateCurrencyC2S::new);

    private final CompoundTag nbt;

    public SendCreateCurrencyC2S(Currency currency) {
        this.nbt = currency.serialize();
    }

    public SendCreateCurrencyC2S(CompoundTag compoundTag) {
        this.nbt = compoundTag;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendCreateCurrencyC2S mesage, NetworkManager.PacketContext context) {
        if(!CurrencyHelper.isAdmin(context.getPlayer())) return;

        CurrencyHelper.createCurrencyOnServer(Currency.deserialize(mesage.nbt));

        CurrencyHelper.saveAll(context.getPlayer().getServer());

        for (ServerPlayer player : context.getPlayer().getServer().getPlayerList().getPlayers()) {
            CurrencyHelper.syncCurrencyData(player);
            CurrencyHelper.syncPlayer(player);
        }
    }
}
