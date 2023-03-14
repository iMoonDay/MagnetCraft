package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.fluids.MagneticFluid;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.methods.AttractMethods;
import com.imoonday.magnetcraft.methods.EnchantmentMethods;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void tick(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity != null) {
            World world = ((LivingEntity) (Object) this).world;
            if (world == null) return;
            if (entity.isPlayer() && entity.isSpectator()) return;
            MagneticFluid.tick(entity);
            AttractMethods.tickCheck(entity);
            CreatureMagnetItem.followingCheck(entity);
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "drop")
    public void drop(DamageSource source, CallbackInfo ci) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            Entity sourceEntity = source.getSource();
            if (sourceEntity != null) {
                LivingEntity entity = (LivingEntity) (Object) this;
                World world = player.world;
                ItemStack stack;
                stack = sourceEntity instanceof TridentEntity tridentEntity ? tridentEntity.asItemStack() : player.getMainHandStack();
                boolean hasEnchantment = EnchantmentMethods.hasEnchantment(stack, EnchantmentRegistries.AUTOMATIC_LOOTING_ENCHANTMENT);
                if (hasEnchantment) {
                    world.getOtherEntities(null, entity.getBoundingBox(), targetEntity -> ((targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.age == 0)).forEach(targetEntity -> {
                        if (targetEntity instanceof ExperienceOrbEntity) {
                            int amount = ((ExperienceOrbEntity) targetEntity).getExperienceAmount();
                            player.addExperience(amount);
                        } else {
                            player.getInventory().offerOrDrop(((ItemEntity) targetEntity).getStack());
                        }
                        targetEntity.kill();
                    });
                }
            }
        }
    }

    @Inject(method = "onDeath", at = @At(value = "TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity) && entity.isAttracting() && entity.getEnable()) {
            entity.setAttracting(false);
            entity.setEnable(false);
        }
    }

    @Inject(method = "computeFallDamage", at = @At(value = "RETURN"), cancellable = true)
    protected void computeFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.ignoreFallDamage() && entity.isOnGround()) {
            entity.setIgnoreFallDamage(false);
            cir.setReturnValue(0);
        }
    }

}