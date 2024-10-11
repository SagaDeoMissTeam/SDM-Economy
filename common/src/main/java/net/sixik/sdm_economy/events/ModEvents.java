package net.sixik.sdm_economy.events;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.sixik.sdm_economy.adv.PlayerMoneyData;
import net.sixik.sdm_economy.network.adv.UpdateClientDataS2C;

public class ModEvents {

    public static void initialize() {
        serverEvents();
        if (Platform.getEnvironment() == Env.CLIENT) {

        }

    }

    public static void serverEvents() {
        LifecycleEvent.SERVER_STARTED.register(PlayerMoneyData::load);
        LifecycleEvent.SERVER_STOPPED.register(PlayerMoneyData::save);

//        PlayerEvent.PLAYER_CLONE.register(((oldPlayer, newPlayer, wonGame) -> {
//            if(!wonGame) {
//                PlayerMoneyData.SERVER.replacePlayerData(oldPlayer.getUUID(), newPlayer.getUUID(), newPlayer.server);
//            }
//        }));

        PlayerEvent.PLAYER_JOIN.register(PlayerMoneyData::onPlayerLoggedIn);
        PlayerEvent.PLAYER_QUIT.register((PlayerMoneyData::onPlayerLoggedOut));
    }
}
