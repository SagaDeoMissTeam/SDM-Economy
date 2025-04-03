package net.sixik.sdmeconomy.network.requests;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.utils.RequestsHelper;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;

import java.util.concurrent.CompletableFuture;

public class SendRequestOutputS2C extends BaseS2CMessage {

    public CompoundTag nbt;

    public SendRequestOutputS2C(String functionName, CompoundTag nbt) {
        this.nbt = new CompoundTag();
        this.nbt.putString("functionName", functionName);
        this.nbt.put("data", nbt);
    }

    public SendRequestOutputS2C(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return SDMEconomyNetwork.SEND_REQUEST_OUTPUT;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        CurrencyHelper.executor.execute(() -> {
            String functionName = nbt.getString("functionName");

            SDMRequest<?> function = RequestsHelper.FUNCTIONS.getOrDefault(functionName, null);
            if(function == null) return;

            var req = SDMEconomyNetwork.Requests.futuresClient.getOrDefault(functionName, null);
            if(req == null) return;


            if(req instanceof CompletableFuture o) {
                try {
                    var data = function.client.apply(context, nbt.getCompound("data"));

                    o.complete(data);
                } catch (Exception e) {
                    o.complete(null);
                    SDMEconomy.printStackTrace("Error processing request: " + functionName, e);
                }
            }
        });
    }
}
