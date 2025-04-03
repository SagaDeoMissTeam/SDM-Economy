package net.sixik.sdmeconomy.network.requests;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.utils.RequestsHelper;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

public class SendRequestInputC2S extends BaseC2SMessage {

    public CompoundTag nbt;

    public SendRequestInputC2S(String functionName, CompoundTag nbt) {
        this.nbt = new CompoundTag();
        this.nbt.putString("functionName", functionName);
        this.nbt.put("data", nbt);
    }

    public SendRequestInputC2S(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.SEND_REQUEST_INPUT;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        String functionName = nbt.getString("functionName");

        SDMRequest<?> function = RequestsHelper.FUNCTIONS.getOrDefault(functionName, null);
        if(function == null) return;

        new SendRequestOutputS2C(functionName, function.server.apply(context, nbt)).sendTo((ServerPlayer) context.getPlayer());

    }
}
