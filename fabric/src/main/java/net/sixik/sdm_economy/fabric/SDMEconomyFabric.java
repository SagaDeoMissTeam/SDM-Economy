package net.sixik.sdm_economy.fabric;

import dev.architectury.platform.Platform;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdm_economy.SDMEconomy;
import net.fabricmc.api.ModInitializer;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.api.IOtherCurrency;
import net.sixik.sdm_economy.common.cap.MoneyData;

import java.math.BigDecimal;

public class SDMEconomyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SDMEconomy.init();
        integrationWithOtherEconomy();
    }


    public void integrationWithOtherEconomy() {
        PlayerMoneyData.isImpactorLoaded = Platform.isModLoaded("impactor");

        if(PlayerMoneyData.isImpactorLoaded) {
            PlayerMoneyData.OTHER_CURRENCY_MOD = new IOtherCurrency() {
                @Override
                public void addMoney(Player player, String id, long amount) {
                    EconomyService.instance().account(EconomyService.instance().currencies().primary(), player.getUUID()).thenAccept(account -> {
                        BigDecimal balance = account.balance();
                        account.set(balance.add(new BigDecimal(amount)));
                    });
                }

                @Override
                public void setMoney(Player player, String id, long amount) {
                    EconomyService.instance().account(EconomyService.instance().currencies().primary(), player.getUUID()).thenAccept(account -> {
                        account.set(new BigDecimal(amount));
                    });
                }

                @Override
                public long getMoney(Player player, String id) {
                    try {
                        var d1 = EconomyService.instance().account(EconomyService.instance().currencies().primary(), player.getUUID()).join();
                        return d1.balance().longValue();
                    } catch (Exception e){
                        SDMEconomy.printStackTrace("", e);
                    }

                    return 0;
                }

                @Override
                public void updateMoneyData(Player player, MoneyData data) {
                    data.currencies.clear();
                    try {
                        var d1 = EconomyService.instance().account(EconomyService.instance().currencies().primary(), player.getUUID()).join();
                        data.setMoney("basic_money", d1.balance().longValue());
                    } catch (Exception e){
                        SDMEconomy.printStackTrace("", e);
                    }
                }

                @Override
                public String getModID() {
                    return "impactor";
                }
            };
            return;
        }
    }
}