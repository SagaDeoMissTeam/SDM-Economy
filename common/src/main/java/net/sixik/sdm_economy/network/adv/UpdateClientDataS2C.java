package net.sixik.sdm_economy.network.adv;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.adv.PlayerMoneyData;

public record UpdateClientDataS2C(CompoundTag nbt) implements CustomPacketPayload {

    public static final Type<UpdateClientDataS2C> TYPE = new Type<>(ResourceLocation.tryBuild(SDMEconomy.MOD_ID, "update_client_data"));

    public static final StreamCodec<FriendlyByteBuf, UpdateClientDataS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, UpdateClientDataS2C::nbt,
            UpdateClientDataS2C::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateClientDataS2C message, NetworkManager.PacketContext context) {
        context.queue(() -> PlayerMoneyData.loadFromNBTClient(message.nbt));
    }

}
