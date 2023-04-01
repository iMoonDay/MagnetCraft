package com.imoonday.magnetcraft.common.enchantments;

import com.imoonday.magnetcraft.common.items.tools.MagneticWrenchItem;
import com.imoonday.magnetcraft.common.items.weapons.ElectromagneticTransmitterItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

public class AutomaticLootingEnchantment extends Enchantment {

    public AutomaticLootingEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SwordItem
                || item instanceof AxeItem
                || item instanceof TridentItem
                || item instanceof BowItem
                || item instanceof CrossbowItem
                || item instanceof MagneticWrenchItem
                || item instanceof ElectromagneticTransmitterItem;
    }
}
