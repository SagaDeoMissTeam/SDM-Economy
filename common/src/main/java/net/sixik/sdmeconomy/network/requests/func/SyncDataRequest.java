package net.sixik.sdmeconomy.network.requests.func;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.economyData.CurrenciesIO;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;

public class SyncDataRequest {


    public static CompoundTag server(NetworkManager.PacketContext context, CompoundTag f) {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = new ListTag();
        for (CurrencyPlayerData.PlayerCurrency playerCurrency : CurrencyHelper.getPlayerCurrencyServerData().getPlayersCurrency(context.getPlayer())) {
            listTag.add(playerCurrency.serialize());
        }
        nbt.put(CurrenciesIO.Constants.CURRENCY_ARRAY_KEY, listTag);

        return nbt;
    }

    public static Void client(NetworkManager.PacketContext context, CompoundTag nbt) {
        CurrencyPlayerData.CLIENT = new CurrencyPlayerData.Client();
        CurrencyPlayerData.CLIENT.load(nbt);
        return null;
    }

    public static String getName() {
        return "sync_data";
    }
}
