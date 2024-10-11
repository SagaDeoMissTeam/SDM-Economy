package net.sixik.sdm_economy.fabric;

import net.sixik.sdm_economy.SDMEconomy;
import net.fabricmc.api.ModInitializer;

public class SDMEconomyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SDMEconomy.init();
    }
}