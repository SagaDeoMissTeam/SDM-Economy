package net.sixik.sdmeconomy.api;

import net.sixik.sdmeconomy.economy.Currency;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CustomCurrencies {

    public static Map<String, Supplier<Currency>> CURRENCIES = new LinkedHashMap<>();
}
