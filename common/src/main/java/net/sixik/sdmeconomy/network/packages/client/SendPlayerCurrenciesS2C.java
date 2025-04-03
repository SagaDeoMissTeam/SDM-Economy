package net.sixik.sdmeconomy.network.packages.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.economyData.CurrenciesIO;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

import java.util.UUID;

public class SendPlayerCurrenciesS2C extends BaseS2CMessage {

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

    public SendPlayerCurrenciesS2C(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.SEND_CURRENCIES_PLAYER;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        CurrencyPlayerData.CLIENT = new CurrencyPlayerData.Client();
        CurrencyPlayerData.CLIENT.load(nbt);
    }
}
