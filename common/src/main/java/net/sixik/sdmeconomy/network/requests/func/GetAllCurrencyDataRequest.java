package net.sixik.sdmeconomy.network.requests.func;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmeconomy.economyData.CurrencyData;

public class GetAllCurrencyDataRequest {

    public static CompoundTag server(NetworkManager.PacketContext context, CompoundTag nbt) {
        return CurrencyData.SERVER.serialize();
    }

    public static CurrencyData client(NetworkManager.PacketContext context, CompoundTag nbt) {
        return CurrencyData.deserialize(nbt);
    }

    public static String getName() {
        return "get_all_currency_data";
    }
}
