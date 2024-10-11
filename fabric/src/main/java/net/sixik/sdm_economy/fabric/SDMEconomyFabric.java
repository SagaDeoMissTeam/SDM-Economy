package net.sixik.sdm_economy.fabric;

import net.sixik.sdm_economy.SDMEconomy;
import net.fabricmc.api.ModInitializer;

public final class SDMEconomyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        SDMEconomy.init();
    }
}
