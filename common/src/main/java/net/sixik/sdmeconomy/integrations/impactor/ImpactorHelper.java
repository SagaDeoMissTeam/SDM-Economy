package net.sixik.sdmeconomy.integrations.impactor;

import net.impactdev.impactor.api.economy.EconomyService;
import net.kyori.adventure.text.TextComponent;
import net.sixik.sdmeconomy.api.CustomCurrencies;
import net.sixik.sdmeconomy.economy.CurrencySymbol;

public class ImpactorHelper {

	public static void registerImpactorCurrencies() {
		EconomyService.instance().currencies()
				.registered()
				.forEach(currency -> {
					var key = currency.key();

					CustomCurrencies.CURRENCIES.put(key.namespace() + ":" + key.value(),
							() -> new ImpactorCurrency(key.namespace() + ":" + key.value(), new CurrencySymbol(((TextComponent) currency.symbol()).content()), currency.defaultAccountBalance().doubleValue()));
				});
	}
}
