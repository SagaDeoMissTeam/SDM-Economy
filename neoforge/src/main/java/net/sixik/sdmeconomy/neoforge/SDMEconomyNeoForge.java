package net.sixik.sdmeconomy.neoforge;

import net.sixik.sdmeconomy.SDMEconomy;
import net.neoforged.fml.common.Mod;
import net.sixik.sdmeconomy.network.SDMEconomyNetwork;
import net.sixik.sdmeconomy.utils.BaseNetworkHandler;

@Mod(SDMEconomy.MODID)
public final class SDMEconomyNeoForge {
    public SDMEconomyNeoForge() {
        // Run our common setup.
        SDMEconomy.init(new NeoForgeNetworkHandler());


    }
}
