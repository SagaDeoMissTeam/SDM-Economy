package net.sixik.sdmeconomy.network.packages.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sixik.sdmeconomy.data.CustomPlayerData;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;
import net.sixik.sdmeconomy.utils.CurrencyHelper;

import java.util.UUID;

public class SendCustomDataS2C implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendCustomDataS2C> TYPE =
            new CustomPacketPayload.Type<>(SDMEconomyNetwork.nameOf("send_update_custom_data"));

    public static final StreamCodec<FriendlyByteBuf, SendCustomDataS2C> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, SendCustomDataS2C::nbt, SendCustomDataS2C::new);

    private final CompoundTag nbt;

    public SendCustomDataS2C(UUID player) {
        this.nbt = CurrencyHelper.getCustomServerData().getPlayerCustomData(player).nbt;
    }

    public SendCustomDataS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendCustomDataS2C message, NetworkManager.PacketContext context) {
        CustomPlayerData.CLIENT = new CustomPlayerData.Client(new CustomPlayerData.Data(message.nbt));
    }
}
