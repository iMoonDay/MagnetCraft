package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.PotionRegistries;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public class ArrowEntityMixin extends EntityMixin {

    @Shadow
    private Potion potion;

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkAttract(CallbackInfo info) {
        ArrowEntity entity = (ArrowEntity) (Object) this;
        if (entity != null) {
            World world = ((ArrowEntity) (Object) this).world;
            if (world == null) {
                return;
            }
            boolean isAttracting = this.potion == PotionRegistries.ATTRACT_POTION;
            double dis = ModConfig.getValue().arrowAttractDis;
            if (isAttracting && this.canAttract()) {
                this.setAttracting(true, dis);
            }
        }
    }
}
