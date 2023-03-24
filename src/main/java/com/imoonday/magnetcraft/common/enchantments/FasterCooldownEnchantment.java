package com.imoonday.magnetcraft.common.enchantments;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.common.items.PortableDemagnetizerItem;
import com.imoonday.magnetcraft.common.items.magnets.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author iMoonDay
 */
public class FasterCooldownEnchantment extends Enchantment {
    public FasterCooldownEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{});
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

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof CreatureMagnetItem
                || item instanceof CropMagnetItem
                || item instanceof ElectromagnetItem
                || item instanceof MineralMagnetItem
                || item instanceof PermanentMagnetItem
                || item instanceof PolorMagnetItem
                || item instanceof MagnetControllerItem
                || item instanceof PortableDemagnetizerItem;
    }

}
