package net.sixik.sdm_economy.network.register;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixik.sdm_economy.common.currency.CurrencyRegister;

public record UpdateRegisterCurrenciesS2C(CompoundTag nbt) implements CustomPacketPayload {
    public static final Type<UpdateRegisterCurrenciesS2C> TYPE = new Type<>(ResourceLocation.tryBuild(SDMEconomy.MOD_ID, "update_custom_currencies"));

    public static final StreamCodec<FriendlyByteBuf, UpdateRegisterCurrenciesS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, UpdateRegisterCurrenciesS2C::nbt,
            UpdateRegisterCurrenciesS2C::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateRegisterCurrenciesS2C message, NetworkManager.PacketContext context) {
        context.queue(() -> {
           ListTag list = (ListTag) message.nbt().get("currencies");

            CurrencyRegister.CURRENCIES.clear();
            for (Tag tag : list) {
                AbstractCurrency currency = new AbstractCurrency(0) {
                    @Override
                    public String getID() {
                        return ((CompoundTag) tag).getString("currencyID");
                    }
                };
                currency.deserializeNBT((CompoundTag) tag);
                CurrencyRegister.register(() -> currency);
            }
        });
    }
}
