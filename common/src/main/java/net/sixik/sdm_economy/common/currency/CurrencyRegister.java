package net.sixik.sdm_economy.common.currency;

import net.sixik.sdm_economy.api.CurrencyHelper;

import java.util.HashMap;

public interface CurrencyRegister {

    HashMap<String, AbstractCurrency.Constructor<?>> CURRENCIES = new HashMap<>();


    static AbstractCurrency.Constructor<?> register(AbstractCurrency.Constructor<?> constructor){
        AbstractCurrency c = constructor.createDefault();
        if(CURRENCIES.containsKey(c.getID())) {
            throw new RuntimeException("Currency with id " + c.getID() + " already registered !");
        } else {
            CURRENCIES.put(c.getID(), constructor);
        }
        return constructor;
    }

    static void init(){
        CurrencyHelper.registerBasicCurrency("basic_money", 0);
    }
}
