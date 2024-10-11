package net.sixik.sdm_economy.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdm_economy.api.ICustomData;
import net.sixik.sdm_economy.common.currency.AbstractCurrency;
import net.sixik.sdm_economy.common.currency.CurrencyRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MoneyData {

    public long moneyBase = 0;
    public List<AbstractCurrency> currencies = new ArrayList<>();

    public List<AbstractCurrency> getCurrencies() {
        return currencies;
    }

    public static MoneyData from(Player player) {
        MoneyData data = new MoneyData();
        if(player instanceof ICustomData customData) {

            if(customData.sdm$getCustomData().contains("money_data") && !customData.sdm$getCustomData().getCompound("money_data").isEmpty()) {
                data.deserializeNBT(customData.sdm$getCustomData().getCompound("money_data"));
            } else {
                data.loadAllCurrencies();
                customData.sdm$getCustomData().put("money_data", data.serializeNBT());
            }
        }

        return data;
    }

    public void save(Player player) {
        ((ICustomData) player).sdm$getCustomData().put("money_data", serializeNBT());
    }

    public void copyFrom(MoneyData data){
        this.currencies = data.currencies;
        this.moneyBase = data.moneyBase;
    }

    public void loadAllCurrencies() {

        currencies.removeIf(currency -> !CurrencyRegister.CURRENCIES.containsKey(currency.getID()));
        List<String> c = currencies.stream().map(AbstractCurrency::getID).toList();

        for (Map.Entry<String, AbstractCurrency.Constructor<?>> stringConstructorEntry : CurrencyRegister.CURRENCIES.entrySet()) {
            if(!c.contains(stringConstructorEntry.getKey())) {
                currencies.add(stringConstructorEntry.getValue().createDefault());
            }
        }

    }

    public boolean addMoney(String id, long money) {
        for (AbstractCurrency currency : getCurrencies()) {
            if(Objects.equals(currency.getID(), id)) {
                currency.moneys += money;
                return true;
            }
        }
        return false;
    }

    public boolean addMoney(long money) {
        moneyBase += money;
        return true;
    }

    public boolean setMoney(String id, long money) {
        for (AbstractCurrency currency : getCurrencies()) {
            if(Objects.equals(currency.getID(), id)) {
                currency.moneys = money;
                return true;
            }
        }
        return false;
    }

    public boolean setMoney(long money) {
        moneyBase = money;
        return true;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag tags = new ListTag();
        for (AbstractCurrency currency : currencies) {
            tags.add(currency.serializeNBT());
        }
        nbt.put("currencies", tags);
        return nbt;
    }


    public void deserializeNBT(CompoundTag nbt) {
        ListTag tags = (ListTag) nbt.get("currencies");
        currencies.clear();

        assert tags != null;
        for (Tag tag : tags) {
            CompoundTag d1 = (CompoundTag) tag;
            if(d1.contains("currencyID")) {
                String currencyID = d1.getString("currencyID");
                AbstractCurrency currency = CurrencyRegister.CURRENCIES.get(currencyID).createDefault();
                currency.deserializeNBT(d1);
                currencies.add(currency);
            }
        }
    }
}
