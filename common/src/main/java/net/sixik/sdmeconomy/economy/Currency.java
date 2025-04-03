package net.sixik.sdmeconomy.economy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.api.CustomCurrencies;
import net.sixik.sdmeconomy.integrations.impactor.ImpactorCurrency;

import java.util.Objects;
import java.util.function.Supplier;

public class Currency {

    protected final String name;
    protected final double defaultValue;
    public CurrencySymbol symbol;
    public boolean canDelete = true;

    public Currency(String name) {
        this(name, new CurrencySymbol("◎"), 0.0);
    }

    public Currency(String name, CurrencySymbol symbol) {
        this(name, symbol, 0.0);
    }

    public Currency(String name, CurrencySymbol symbol, double defaultValue) {
        this.name = name;
        this.symbol = symbol;
        this.defaultValue = defaultValue;
    }

    public Currency canDelete(boolean value) {
        this.canDelete = value;
        return this;
    }

    public Currency copy() {
        return new Currency(name, symbol.copy(), defaultValue);
    }

    public String getName() {
        return name;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public Component getTranslation() {
        return Component.translatable("sdm.currency." + name);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putDouble("defaultValue", defaultValue);
        tag.put("symbol", symbol.serialize());
        return tag;
    }

    public static Currency deserialize(CompoundTag tag) {
        String name = tag.getString("name");
        double defaultValue = tag.getDouble("defaultValue");
        CurrencySymbol symbol = CurrencySymbol.deserialize(tag.getCompound("symbol"));


        return CustomCurrencies.CURRENCIES.getOrDefault(name, () -> new Currency(name, symbol, defaultValue)).get();
    }

}
