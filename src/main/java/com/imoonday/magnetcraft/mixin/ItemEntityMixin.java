package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.tags.MagnetTags;
import com.imoonday.magnetcraft.methods.AttractMethod;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkAttract(CallbackInfo info) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (entity != null) {
            World world = ((ItemEntity) (Object) this).getWorld();
            if (world == null) return;
            ItemStack stack = entity.getStack();
            boolean isAttracting = stack.isIn(MagnetTags.ATTRACTING_MAGNETS) && stack.getOrCreateNbt().getBoolean("enabled");
            if (isAttracting) {
                AttractMethod.attractItems(entity, 10);
            }
        }
    }
}
