package net.sixik.sdm_economy.forge;

import dev.architectury.platform.forge.EventBuses;
import net.sixik.sdm_economy.SDMEconomy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SDMEconomy.MOD_ID)
public class SDMEconomyForge {
    public SDMEconomyForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SDMEconomy.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SDMEconomy.init();
    }
}