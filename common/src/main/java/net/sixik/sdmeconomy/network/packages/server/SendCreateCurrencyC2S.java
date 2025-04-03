package net.sixik.sdmeconomy.network.packages.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmeconomy.network.packages.client.SendCurrenciesS2C;
import net.sixik.sdmeconomy.network.packages.client.SendPlayerCurrenciesS2C;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.economy.Currency;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

public class SendCreateCurrencyC2S extends BaseC2SMessage {

    private final CompoundTag nbt;

    public SendCreateCurrencyC2S(Currency currency) {
        this.nbt = currency.serialize();
    }

    public SendCreateCurrencyC2S(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.SEND_CREATE_CURRENCY;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if(!CurrencyHelper.isAdmin(context.getPlayer())) return;

        CurrencyHelper.createCurrencyOnServer(Currency.deserialize(nbt));

        CurrencyHelper.saveAll(context.getPlayer().getServer());

        for (ServerPlayer player : context.getPlayer().getServer().getPlayerList().getPlayers()) {
            CurrencyHelper.syncCurrencyData(player);
            CurrencyHelper.syncPlayer(player);
        }
    }
}
