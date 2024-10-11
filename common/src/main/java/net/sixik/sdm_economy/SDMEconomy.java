package net.sixik.sdm_economy;

import com.mojang.logging.LogUtils;
import net.sixik.sdm_economy.common.currency.CurrencyRegister;
import net.sixik.sdm_economy.events.ModEvents;
import net.sixik.sdm_economy.network.EconomyNetwork;
import org.slf4j.Logger;

public class SDMEconomy
{
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "sdm_economy";

	public static void init() {
		CurrencyRegister.init();
		EconomyNetwork.init();
		ModEvents.initialize();
	}

	public static void printStackTrace(String str, Throwable s){
		StringBuilder strBuilder = new StringBuilder(str);
		for (StackTraceElement stackTraceElement : s.getStackTrace()) {
			strBuilder.append("\t").append(" ").append("at").append(" ").append(stackTraceElement).append("\n");
		}
		str = strBuilder.toString();

		for (Throwable throwable : s.getSuppressed()) {
			printStackTrace(str, throwable);
		}

		Throwable ourCause = s.getCause();
		if(ourCause != null){
			printStackTrace(str, ourCause);
		}


		LOGGER.error(str);

	}
}
