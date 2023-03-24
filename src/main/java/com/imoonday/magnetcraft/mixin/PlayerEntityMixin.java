package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.enchantments.MagneticLevitationEnchantment;
import com.imoonday.magnetcraft.MagnetCraft.EnchantmentMethods;
import com.imoonday.magnetcraft.MagnetCraft;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.imoonday.magnetcraft.registries.common.EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT;

/**
 * @author iMoonDay
 */
@SuppressWarnings({"AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc", "AlibabaAbstractClassShouldStartWithAbstractNaming"})
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    private static final String USED_TICK = "UsedTick";

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    @Final
    private PlayerAbilities abilities;


    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player != null) {
            World world = player.world;
            if (world == null) {
                return;
            }
            MagneticLevitationEnchantment.tryLevitation(player);
            if ((player.isOnGround() || player.getAbilities().flying) && player.getLevitationTick() > 0) {
                player.setLevitationTick(player.getLevitationTick() - 3);
            }
            if (!player.isSpectator() && !player.getAbilities().creativeMode && player.hasNoGravity()) {
                player.setNoGravity(false);
            }
            if (!world.isClient && !player.isSpectator()) {
                ItemStack stack = player.getEquippedStack(EquipmentSlot.FEET);
                boolean hasTick = stack.getNbt() != null && stack.getNbt().contains(USED_TICK);
                int tick = hasTick ? stack.getNbt().getInt(USED_TICK) : 0;
                if (EnchantmentMethods.hasEnchantment(stack, MAGNETIC_LEVITATION_ENCHANTMENT) && player.getMagneticLevitationMode() && !player.getAbilities().creativeMode) {
                    stack.getOrCreateNbt().putInt(USED_TICK, ++tick);
                }
                int maxTick = 60 * 20;
                while (hasTick && stack.getNbt().getInt(USED_TICK) >= maxTick) {
                    stack.getOrCreateNbt().putInt(USED_TICK, stack.getOrCreateNbt().getInt(USED_TICK) - maxTick);
                    MagnetCraft.DamageMethods.addDamage(stack, player.getRandom(), 1, true);
                }
            }
            player.getInventory().markDirty();
        }
    }

}