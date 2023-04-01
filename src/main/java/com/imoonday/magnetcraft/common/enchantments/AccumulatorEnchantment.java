package com.imoonday.magnetcraft.common.enchantments;

import com.imoonday.magnetcraft.common.items.tools.MagneticWrenchItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class AccumulatorEnchantment extends Enchantment {

    public AccumulatorEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof MagneticWrenchItem;
    }
}
