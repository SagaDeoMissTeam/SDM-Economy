package net.sixik.sdm_economy.common.currency;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCurrency {

    public long moneys;
    public Map<String, Long> conversions = new HashMap<>();
    public String specialSymbol = "◎";

    public AbstractCurrency(long moneys) {
        this.moneys = moneys;
    }

    public AbstractCurrency addConversion(String currencyID, long exchangeRate) {
        conversions.put(currencyID, exchangeRate);
        return this;
    }

    public abstract String getID();

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("currencyID", getID());
        nbt.putLong("moneys", moneys);
        nbt.putString("specialSymbol", specialSymbol);
        return nbt;
    }


    public void deserializeNBT(CompoundTag nbt) {
        this.moneys = nbt.getLong("moneys");
        this.specialSymbol = nbt.getString("specialSymbol");
    }

    public interface Constructor<T extends AbstractCurrency> {
        T createDefault();
    }

    @Override
    public String toString() {
        return "AbstractCurrency{" +
                "moneys=" + moneys +
                ", conversions=" + conversions +
                '}';
    }
}
