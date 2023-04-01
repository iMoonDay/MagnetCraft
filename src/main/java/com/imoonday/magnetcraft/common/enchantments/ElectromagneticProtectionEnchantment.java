package com.imoonday.magnetcraft.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;

public class ElectromagneticProtectionEnchantment extends Enchantment {

    public ElectromagneticProtectionEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public int getMinPower(int level) {
        return 8 + (level - 1) * 4;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 4;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        }
        return level * 2;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        if (other instanceof ProtectionEnchantment protectionEnchantment) {
            return protectionEnchantment.protectionType == ProtectionEnchantment.Type.FALL;
        }
        return super.canAccept(other);
    }

}
