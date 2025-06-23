package net.sixik.sdmeconomy.forge;

import net.sixik.sdmeconomy.SDMEconomy;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SDMEconomy.MOD_ID)
public final class SDMEconomyForge {
    public SDMEconomyForge() {
        EventBuses.registerModEventBus(SDMEconomy.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SDMEconomy.init();
    }
}
