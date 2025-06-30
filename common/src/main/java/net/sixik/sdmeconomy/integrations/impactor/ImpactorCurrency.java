package net.sixik.sdmeconomy.integrations.impactor;

import dev.architectury.platform.Platform;
import net.impactdev.impactor.api.economy.EconomyService;
import net.kyori.adventure.key.Key;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.IIntegrationCurrency;
import net.sixik.sdmeconomy.economy.Currency;
import net.sixik.sdmeconomy.economy.CurrencySymbol;

import java.math.BigDecimal;
import java.util.UUID;

public class ImpactorCurrency extends Currency implements IIntegrationCurrency {

    public static boolean isLoaded() {
        return Platform.isModLoaded("impactor");
    }

    public ImpactorCurrency(String currencyID,CurrencySymbol currencySymbol,double amount) {
        super(currencyID, currencySymbol, amount);
        canDelete(false);
    }

    @Deprecated
    public static String getCurrencyID() {
        return "impactor";
    }

    @Deprecated
    public ImpactorCurrency() {
        super("impactor", new CurrencySymbol("◎"), 0d);
        canDelete(false);
    }


    @Override
    public void addCurrency(UUID player, double amount) {
        EconomyService.instance()
                .account(EconomyService.instance()
                        .currencies()
                        .currency(Key.key(this.name))
                        .orElseThrow(), player)
                .thenAccept(account -> account.deposit(new BigDecimal(amount)));
    }

    @Override
    public void setCurrency(UUID player, double amount) {
        EconomyService.instance()
                .account(EconomyService.instance()
                        .currencies()
                        .currency(Key.key(this.name))
                        .orElseThrow(), player)
                .thenAccept(account -> account.set(new BigDecimal(amount)));
    }

    @Override
    public double getCurrency(UUID player) {
        try {
            var d1 = EconomyService.instance().account(EconomyService.instance().currencies().currency(Key.key(this.name)).orElseThrow(), player).join();
            return d1.balance().doubleValue();
        } catch (Exception e){
            SDMEconomy.printStackTrace("", e);
        }

        return 0d;
    }
}
