package net.sixik.sdmeconomy.currencies.compat;

import dev.architectury.platform.Platform;
import net.impactdev.impactor.api.economy.EconomyService;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.api.IntegrationCurrency;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.CurrencySymbol;

import java.math.BigDecimal;
import java.util.UUID;

public class ImpactorCurrency extends BaseCurrency implements IntegrationCurrency {

    public static boolean isLoaded() {
        return Platform.isModLoaded("impactor");
    }

    public static String getCurrencyID() {
        return "impactor";
    }

    public ImpactorCurrency() {
        super("impactor", new CurrencySymbol("◎"), 0d);
        canDelete(false);
    }

    @Override
    public void addCurrency(UUID player, double amount) {
        EconomyService.instance().account(EconomyService.instance().currencies().primary(), player).thenAccept(account -> {
            BigDecimal balance = account.balance();
            account.set(balance.add(new BigDecimal(amount)));
        });
    }

    @Override
    public void setCurrency(UUID player, double amount) {
        EconomyService.instance().account(EconomyService.instance().currencies().primary(), player).thenAccept(account -> {
            account.set(new BigDecimal(amount));
        });
    }

    @Override
    public double getCurrency(UUID player) {
        try {
            var d1 = EconomyService.instance().account(EconomyService.instance().currencies().primary(), player).join();
            return d1.balance().doubleValue();
        } catch (Exception e){
            SDMEconomy.printStackTrace("", e);
        }

        return 0d;
    }
}
