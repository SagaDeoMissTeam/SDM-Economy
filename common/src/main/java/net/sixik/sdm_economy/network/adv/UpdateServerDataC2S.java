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

public record UpdateServerDataC2S(CompoundTag nbt) implements CustomPacketPayload {

    public static final Type<UpdateServerDataC2S> TYPE = new Type<>(ResourceLocation.tryBuild(SDMEconomy.MOD_ID, "update_server_data"));

    public static final StreamCodec<FriendlyByteBuf, UpdateServerDataC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, UpdateServerDataC2S::nbt,
            UpdateServerDataC2S::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



    public static void handle(UpdateServerDataC2S message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            PlayerMoneyData.SERVER.loadFromNBT(context.getPlayer(), message.nbt);
            PlayerMoneyData.savePlayer(context.getPlayer().getGameProfile().getId(), context.getPlayer().getServer());
        });
    }
}
