package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.tags.ItemTags;
import com.imoonday.magnetcraft.config.ModConfig;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin extends EntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkAttract(CallbackInfo info) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (entity != null) {
            World world = ((ItemEntity) (Object) this).world;
            if (world == null) return;
            ItemStack stack = entity.getStack();
            boolean isAttracting = stack.isIn(ItemTags.ATTRACTIVE_MAGNETS) && stack.getOrCreateNbt().getBoolean("Enable");
            int dis = ModConfig.getValue().droppedMagnetAttractDis;
            if (isAttracting && this.canAttract()) {
                this.setAttracting(true, dis);
            }
        }
    }
}
