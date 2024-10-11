package net.sixik.sdm_economy.common.currency;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.api.CurrencyHelper;

import java.util.HashMap;

public interface CurrencyRegister {

    HashMap<String, AbstractCurrency.Constructor<?>> CUSTOM_CURRENCIES = new HashMap<>();
    HashMap<String, AbstractCurrency.Constructor<?>> CURRENCIES = new HashMap<>();


    static AbstractCurrency.Constructor<?> register(AbstractCurrency.Constructor<?> constructor){
        SDMEconomy.LOGGER.info("Starting to register a currency");
        AbstractCurrency c = constructor.createDefault();
        if(CURRENCIES.containsKey(c.getID())) {
            throw new RuntimeException("Currency with id " + c.getID() + " already registered !");
        } else {
            CURRENCIES.put(c.getID(), constructor);
        }
        SDMEconomy.LOGGER.info("Registered " + c.getID() + " currency!");
        return constructor;
    }

    static AbstractCurrency.Constructor<?> registerCustomCurrency(AbstractCurrency.Constructor<?> constructor){
        try {
            SDMEconomy.LOGGER.info("Starting to register a custom currency");
            AbstractCurrency c = constructor.createDefault();
            if (CUSTOM_CURRENCIES.containsKey(c.getID())) {
                throw new RuntimeException("Currency with id " + c.getID() + " already registered !");
            } else {
                CUSTOM_CURRENCIES.put(c.getID(), constructor);
            }
            SDMEconomy.LOGGER.info("Registered " + c.getID() + " currency!");
        } catch (Exception e){
            e.printStackTrace();
        }
        return constructor;
    }

    static CompoundTag getRegisteredCurrencies() {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = new ListTag();
        for (AbstractCurrency.Constructor<?> value : CURRENCIES.values()) {
            listTag.add(value.createDefault().serializeNBT());
        }
        nbt.put("currencies", listTag);
        return nbt;
    }

    static void init(){
        CurrencyHelper.registerBasicCurrency("basic_money", 0);
    }
}
