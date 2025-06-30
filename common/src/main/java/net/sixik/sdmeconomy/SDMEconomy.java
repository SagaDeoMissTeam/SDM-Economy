package net.sixik.sdmeconomy;

import com.mojang.logging.LogUtils;
import net.sixik.sdmeconomy.utils.RequestsHelper;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;
import net.sixik.sdmeconomy.network.requests.SDMRequest;
import net.sixik.sdmeconomy.network.requests.func.GetAllCurrencyDataRequest;
import net.sixik.sdmeconomy.network.requests.func.GetPlayerCurrencyRequest;
import net.sixik.sdmeconomy.network.requests.func.SyncDataRequest;
import org.slf4j.Logger;

public final class SDMEconomy {
    public static final String MODID = "sdmeconomy";
    public static Logger LOGGER = LogUtils.getLogger();

    public static void init() {

        SDMEconomyNetwork.init();

        SDMEconomyEvents.init();
        registerRequests();
    }

    private static void registerRequests() {
        RequestsHelper.GET_ALL_CURRENCY_DATA = RequestsHelper.registerRequest(
                new SDMRequest<>(GetAllCurrencyDataRequest.getName(), GetAllCurrencyDataRequest::server, GetAllCurrencyDataRequest::client)
        );
        RequestsHelper.GET_PLAYER_CURRENCY_DATA = RequestsHelper.registerRequest(
                new SDMRequest<>(GetPlayerCurrencyRequest.getName(), GetPlayerCurrencyRequest::server, GetPlayerCurrencyRequest::client)
        );
        RequestsHelper.SYNC_DATA = RequestsHelper.registerRequest(
                new SDMRequest<>(SyncDataRequest.getName(), SyncDataRequest::server, SyncDataRequest::client)
        );
    }

    public static void printStackTrace(String str, Throwable s){
        StringBuilder strBuilder = new StringBuilder(str + " " + s.getMessage());
        for (StackTraceElement stackTraceElement : s.getStackTrace()) {
            strBuilder.append("\t").append(" ").append("at").append(" ").append(stackTraceElement).append("\n");
        }
        str = strBuilder.toString();

        for (Throwable throwable : s.getSuppressed()) {
            printStackTrace(str, throwable);
        }



        Throwable ourCause = s.getCause();
        if(ourCause != null){
            printStackTrace(str, ourCause);
        }


        LOGGER.error(str);

    }
}
