package net.sixik.sdmeconomy.utils;

import net.sixik.sdmeconomy.network.requests.SDMRequest;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RequestsHelper {

    public static Map<String, SDMRequest<?>> FUNCTIONS = new HashMap<>();

    public static String GET_ALL_CURRENCY_DATA;
    public static String GET_PLAYER_CURRENCY_DATA;
    public static String SYNC_DATA;

    public static String registerRequest(SDMRequest<?> request) {
        FUNCTIONS.put(request.name, request);
        return request.name;
    }

    @Nullable
    public static <T> SDMRequest<T> getRequest(String name) {
        var value = FUNCTIONS.get(name);
        return value instanceof SDMRequest<?>  ? (SDMRequest<T>) value : null;
    }
}
