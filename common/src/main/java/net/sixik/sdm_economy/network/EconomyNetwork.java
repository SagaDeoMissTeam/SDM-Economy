package net.sixik.sdm_economy.network;


import dev.ftb.mods.ftblibrary.util.NetworkHelper;
import net.sixik.sdm_economy.network.adv.UpdateClientDataS2C;
import net.sixik.sdm_economy.network.adv.UpdateServerDataC2S;
import net.sixik.sdm_economy.network.register.RegisterCustomCurrenciesC2S;
import net.sixik.sdm_economy.network.register.UpdateRegisterCurrenciesS2C;

public class EconomyNetwork {
    public static void init() {
        NetworkHelper.registerS2C(UpdateClientDataS2C.TYPE, UpdateClientDataS2C.STREAM_CODEC, UpdateClientDataS2C::handle);
        NetworkHelper.registerS2C(UpdateRegisterCurrenciesS2C.TYPE, UpdateRegisterCurrenciesS2C.STREAM_CODEC, UpdateRegisterCurrenciesS2C::handle);
        NetworkHelper.registerC2S(UpdateServerDataC2S.TYPE, UpdateServerDataC2S.STREAM_CODEC, UpdateServerDataC2S::handle);
        NetworkHelper.registerC2S(RegisterCustomCurrenciesC2S.TYPE, RegisterCustomCurrenciesC2S.STREAM_CODEC, RegisterCustomCurrenciesC2S::handle);
    }
}
