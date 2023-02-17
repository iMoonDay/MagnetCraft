package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethod;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {

    @Shadow private ItemStack tridentStack;

    @Shadow protected abstract ItemStack asItemStack();

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkAttract(CallbackInfo info) {
        TridentEntity entity = (TridentEntity) (Object) this;
        if (entity != null) {
            World world = ((TridentEntity) (Object) this).getWorld();
            if (world == null) return;
            ModConfig config = ModConfig.getConfig();
            ItemStack stack = this.asItemStack();
            int enchLvl = NbtClassMethod.getEnchantmentLvl(stack, "magnetcraft:attract");
            boolean isAttracting = enchLvl > 0;
            double enchDefaultDis = config.value.enchDefaultDis;
            double disPerLvl = config.value.disPerLvl;
            double enchMinDis = enchDefaultDis + disPerLvl;
            double finalDis = enchMinDis + (enchLvl - 1) * disPerLvl;
            if (isAttracting) {
                entity.addScoreboardTag("MagnetCraft.isAttracting");
                AttractMethod.attractItems(entity,finalDis);
            }
        }
    }
}
