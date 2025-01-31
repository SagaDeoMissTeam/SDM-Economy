package net.sixik.sdm_economy.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.network.adv.UpdateClientDataS2C;
import net.sixik.sdm_economy.network.adv.UpdateServerDataC2S;

public interface EconomyNetwork {

    SimpleNetworkManager NET = SimpleNetworkManager.create(SDMEconomy.MOD_ID);
    MessageType UPDATE_CLIENT_DATA = NET.registerS2C("update_client_data", UpdateClientDataS2C::new);
//    MessageType UPDATE_SERVER_DATA = NET.registerC2S("update_server_data", UpdateServerDataC2S::new);

    static void init(){

    }
}
