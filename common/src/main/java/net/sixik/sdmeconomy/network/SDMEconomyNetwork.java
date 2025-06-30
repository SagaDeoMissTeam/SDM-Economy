package net.sixik.sdmeconomy.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.AbstractASKRequest;
import net.sixik.sdmeconomy.network.ASK.ASK_base.DataSyncASKC2S;
import net.sixik.sdmeconomy.network.ASK.ASK_base.DataSyncASKS2C;
import net.sixik.sdmeconomy.network.packages.client.SendCurrenciesS2C;
import net.sixik.sdmeconomy.network.packages.client.SendCustomDataS2C;
import net.sixik.sdmeconomy.network.packages.client.SendPlayerCurrenciesS2C;
import net.sixik.sdmeconomy.network.packages.client.SendUpdatePlayerCurrencyS2C;
import net.sixik.sdmeconomy.network.packages.server.SendCreateCurrencyC2S;
import net.sixik.sdmeconomy.network.packages.server.SendDeleteCurrencyC2S;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class SDMEconomyNetwork {

    protected static final Map<String, Function<Void, AbstractASKRequest>> REQUESTS = new HashMap<>();


    private static final SimpleNetworkManager NET = SimpleNetworkManager.create(SDMEconomy.MODID);


    public static final MessageType SEND_CURRENCIES = NET.registerS2C("send_currencies", SendCurrenciesS2C::new);
    public static final MessageType SEND_CURRENCIES_PLAYER = NET.registerS2C("send_currencies_player", SendPlayerCurrenciesS2C::new);
    public static final MessageType SEND_UPDATE_CURRENCY_PLAYER = NET.registerS2C("send_update_currency_player", SendUpdatePlayerCurrencyS2C::new);

    public static final MessageType SEND_UPDATE_CUSTOM_DATA = NET.registerS2C("send_update_custom_data", SendCustomDataS2C::new);

    public static final MessageType SEND_CREATE_CURRENCY = NET.registerC2S("send_create_currency", SendCreateCurrencyC2S::new);
    public static final MessageType SEND_DELETE_CURRENCY = NET.registerC2S("send_delete_currency", SendDeleteCurrencyC2S::new);

    public static final MessageType ASK_TO_SERVER = NET.registerC2S("ask_to_server", DataSyncASKC2S::new);
    public static final MessageType ASK_TO_CLIENT = NET.registerS2C("ask_to_client", DataSyncASKS2C::new);


    public static void init() {}



    public static String registerRequest(String id, Function<Void, AbstractASKRequest> func) {
        if(REQUESTS.containsKey(id))
            throw new RuntimeException("Entry Type with " + id + " id already registered!");

        REQUESTS.put(id, func);
        SDMEconomy.LOGGER.info("Registered ASK request [{}]", id);
        return id;
    }

    public static Optional<Function<Void, AbstractASKRequest>> getRequest(String id) {
        return Optional.ofNullable(REQUESTS.getOrDefault(id, null));
    }
}
