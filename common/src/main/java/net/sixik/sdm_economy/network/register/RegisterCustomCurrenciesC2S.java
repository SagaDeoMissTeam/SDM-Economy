package net.sixik.sdm_economy.network.register;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.util.NetworkHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdm_economy.common.cap.MoneyData;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixik.sdm_economy.common.currency.CurrencyRegister;
import net.sixik.sdm_economy.network.adv.UpdateClientDataS2C;

public record RegisterCustomCurrenciesC2S(CompoundTag nbt) implements CustomPacketPayload {
    public static final Type<RegisterCustomCurrenciesC2S> TYPE = new Type<>(ResourceLocation.tryBuild(SDMEconomy.MOD_ID, "register_custom_currencies"));

    public static final StreamCodec<FriendlyByteBuf, RegisterCustomCurrenciesC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, RegisterCustomCurrenciesC2S::nbt,
            RegisterCustomCurrenciesC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RegisterCustomCurrenciesC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            AbstractCurrency currency = new AbstractCurrency(0) {
                @Override
                public String getID() {
                    return message.nbt.getString("currencyID");
                }
            };
            currency.deserializeNBT(message.nbt);
            AbstractCurrency.Constructor constructor = () -> currency;
            CurrencyRegister.register(constructor);
            CurrencyRegister.registerCustomCurrency(constructor);
            PlayerMoneyData.saveCustomCurrency(context.getPlayer().getServer());

            for (ServerPlayer player : context.getPlayer().getServer().getPlayerList().getPlayers()) {
                MoneyData data = CurrencyHelper.getPlayerData(player);
                data.loadAllCurrencies();
                NetworkHelper.sendTo(player, new UpdateClientDataS2C(data.serializeNBT()));
            }
        });
    }
}
