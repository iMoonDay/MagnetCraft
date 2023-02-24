package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethods;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Shadow private ItemStack tridentStack;

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkAttract(CallbackInfo info) {
        TridentEntity entity = (TridentEntity) (Object) this;
        if (entity != null) {
            World world = ((TridentEntity) (Object) this).getWorld();
            if (world == null) return;
            ModConfig config = ModConfig.getConfig();
            ItemStack stack = this.tridentStack;
            int enchLvl = EnchantmentHelper.getLevel(EnchantmentRegistries.ATTRACT_ENCHANTMENT,stack);
            boolean isAttracting = enchLvl > 0;
            double enchDefaultDis = config.value.enchDefaultDis;
            double disPerLvl = config.value.disPerLvl;
            double enchMinDis = enchDefaultDis + disPerLvl;
            double dis = enchMinDis + (enchLvl - 1) * disPerLvl;
            if (isAttracting) {
                entity.addScoreboardTag("MagnetCraft.isAttracting");
                AttractMethods.attractItems(entity,dis);
            }
        }
    }
}
