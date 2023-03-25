package com.imoonday.magnetcraft.common.items.materials;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class MagneticIronToolMaterial implements ToolMaterial {

    public static final MagneticIronToolMaterial INSTANCE = new MagneticIronToolMaterial();
    @Override
    public int getDurability() {
        return 350;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 7.0f;
    }

    @Override
    public float getAttackDamage() {
        return 2.5f;
    }

    @Override
    public int getMiningLevel() {
        return 2;
    }

    @Override
    public int getEnchantability() {
        return 12;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ItemRegistries.MAGNETIC_IRON_INGOT);
    }
}
