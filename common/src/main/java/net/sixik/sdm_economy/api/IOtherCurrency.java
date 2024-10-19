package net.sixik.sdm_economy.api;

import net.minecraft.world.entity.player.Player;
import net.sixik.sdm_economy.common.cap.MoneyData;

public interface IOtherCurrency {

    void addMoney(Player player, String id, long amount);
    void setMoney(Player player, String id, long amount);
    long getMoney(Player player, String id);
    void updateMoneyData(Player player, MoneyData data);
    String getModID();
}
