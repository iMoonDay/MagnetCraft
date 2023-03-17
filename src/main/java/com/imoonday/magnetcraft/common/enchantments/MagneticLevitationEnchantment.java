package com.imoonday.magnetcraft.common.enchantments;

import com.imoonday.magnetcraft.common.items.armors.MagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.armors.NetheriteMagneticIronArmorItem;
import com.imoonday.magnetcraft.methods.EnchantmentMethods;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;

import java.util.stream.DoubleStream;

import static com.imoonday.magnetcraft.registries.common.EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT;

public class MagneticLevitationEnchantment extends Enchantment {

    public MagneticLevitationEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    public static void tryLevitation(PlayerEntity player) {
        ItemStack stack = player.getEquippedStack(EquipmentSlot.FEET);
        if (player.isFallFlying() || player.getAbilities().flying || !EnchantmentMethods.hasEnchantment(stack, MAGNETIC_LEVITATION_ENCHANTMENT) || !player.getMagneticLevitationMode()) {
            return;
        }
        boolean level1 = stack.isOf(ItemRegistries.MAGNETIC_IRON_BOOTS);
        boolean level2 = stack.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_BOOTS);
        double multiplier = level2 ? 2.0 : level1 ? 1.0 : 0.5;
        int lvl = EnchantmentMethods.getEnchantmentLvl(player, EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT);
        int tick = player.getLevitationTick();
        int maxSec = (int) ((10 + lvl * 10) * multiplier);
        String displayTime = ((tick / 20) + (tick % 20 == 0 ? 0 : 1)) + " / " + maxSec;
        if (!player.isOnGround() && player.jumping) {
            double heightMultiplier = MagneticIronArmorItem.isInMagneticIronSuit(player) ? 1.5 : NetheriteMagneticIronArmorItem.isInNetheriteMagneticIronSuit(player) ? 2.0 : 1.0;
            double height = (1.5 + (double) lvl / 2.0) * heightMultiplier + 0.1;
            player.setNoGravity(true);
            boolean collide = DoubleStream.iterate(0, d -> d <= height, d -> d + 0.1).anyMatch(d -> (!player.doesNotCollide(0, -d, 0)));
            player.setVelocity(player.getVelocity().x, tick >= maxSec * 20 ? -0.1 : collide ? player.isInSneakingPose() ? 0 : 0.25 * multiplier : !player.doesNotCollide(0, -height - 0.1, 0) ? 0 : -0.1, player.getVelocity().z);
            if (player.world.isClient) {
                player.world.addParticle(ParticleTypes.FIREWORK, player.getX(), player.getY(), player.getZ(), player.getRandom().nextGaussian() * 0.05, -player.getVelocity().y * 0.5, player.getRandom().nextGaussian() * 0.05);
            }
            player.velocityDirty = true;
            player.setIgnoreFallDamage(true);
            if (!player.isSpectator() && !player.getAbilities().creativeMode) {
                player.setLevitationTick(tick + 1);
                player.addExhaustion(player.isSprinting() ? 0.2f / 20 : 0.05f / 20);
                if (!player.world.isClient) {
                    int usedTick = stack.getNbt() != null && stack.getNbt().contains("UsedTick") ? stack.getOrCreateNbt().getInt("UsedTick") : 0;
                    stack.getOrCreateNbt().putInt("UsedTick", ++usedTick);
                    player.sendMessage(Text.literal(displayTime), true);
                }
            }
            player.getInventory().markDirty();
        } else {
            player.setNoGravity(false);
            if (!player.world.isClient && !player.isSpectator() && !player.getAbilities().creativeMode && tick > 0) {
                player.sendMessage(Text.literal(displayTime), true);
            }
        }
    }

}
