package net.sixik.sdmeconomy.network.requests.func;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmeconomy.economyData.CurrencyData;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;

import java.util.LinkedList;
import java.util.Optional;

public class GetPlayerCurrencyRequest {

    public static final String CURRENCY_NAME_KEY = "currency_name_key";

    public static CompoundTag server(NetworkManager.PacketContext context, CompoundTag nbt) {
        String curName = nbt.getString(CURRENCY_NAME_KEY);

        LinkedList<CurrencyPlayerData.PlayerCurrency> cur = CurrencyPlayerData.SERVER.getPlayersCurrency(context.getPlayer());

        Optional<CurrencyPlayerData.PlayerCurrency> t = cur.stream().filter(s -> s.currency.getName().equals(curName))
                .findFirst();

        if(t.isPresent()) {
            return t.get().serialize();
        }

        return new CompoundTag();
    }

    public static CurrencyPlayerData.PlayerCurrency client(NetworkManager.PacketContext context, CompoundTag nbt) {
        if(nbt.isEmpty()) return null;
        return CurrencyPlayerData.PlayerCurrency.deserialize(nbt);
    }

    public static String getName() {
        return "get_player_currency_data";
    }
}
