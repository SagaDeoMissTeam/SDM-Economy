package net.sixik.sdm_economy.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.sixik.sdm_economy.SDMEconomy;
import net.sixik.sdm_economy.api.ICustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements ICustomData {

    private CompoundTag sdm_economy$customData;

    @Override
    public CompoundTag sdm$getCustomData() {
        if(sdm_economy$customData == null){
            sdm_economy$customData = new CompoundTag();
        }
        return sdm_economy$customData;
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    public void sdm$saveData(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
        if(sdm_economy$customData != null) compoundTag.put("customData", sdm_economy$customData.copy());
    }

    @Inject(method = "load", at = @At("HEAD"))
    public void sdm$loadData(CompoundTag compoundTag, CallbackInfo ci) {
        if(compoundTag.contains("customData")) sdm_economy$customData = compoundTag.getCompound("customData");
    }

}
