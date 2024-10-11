package net.sixik.sdm_economy.neoforge;

import net.sixik.sdm_economy.SDMEconomy;
import net.neoforged.fml.common.Mod;

@Mod(SDMEconomy.MOD_ID)
public final class SDMEconomyNeoForge {
    public SDMEconomyNeoForge() {
        // Run our common setup.
        SDMEconomy.init();
    }
}
