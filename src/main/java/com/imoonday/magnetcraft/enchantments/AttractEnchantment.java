package com.imoonday.magnetcraft.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AttractEnchantment extends Enchantment {

    public AttractEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{});
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return true;
    }

}