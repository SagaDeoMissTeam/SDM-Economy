package net.sixik.sdmeconomy.network.packages.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.economyData.CurrenciesIO;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

import java.util.UUID;

public class SendPlayerCurrenciesS2C implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendPlayerCurrenciesS2C> TYPE =
            new CustomPacketPayload.Type<>(SDMEconomyNetwork.nameOf("send_currencies_player"));

    public static final StreamCodec<FriendlyByteBuf, SendPlayerCurrenciesS2C> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, SendPlayerCurrenciesS2C::nbt, SendPlayerCurrenciesS2C::new);

    private final CompoundTag nbt;

    public SendPlayerCurrenciesS2C(Player player) {
        this(CurrencyHelper.getPlayerUUID(player));
    }

    public SendPlayerCurrenciesS2C(UUID player) {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = new ListTag();
        for (CurrencyPlayerData.PlayerCurrency playerCurrency : CurrencyHelper.getPlayerCurrencyServerData().getPlayersCurrency(player)) {
            listTag.add(playerCurrency.serialize());
        }

        nbt.put(CurrenciesIO.Constants.CURRENCY_ARRAY_KEY, listTag);
        this.nbt = nbt;
    }

    public SendPlayerCurrenciesS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendPlayerCurrenciesS2C message, NetworkManager.PacketContext context) {
        CurrencyPlayerData.CLIENT = new CurrencyPlayerData.Client();
        CurrencyPlayerData.CLIENT.load(message.nbt);
    }

}
