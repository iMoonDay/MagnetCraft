package com.imoonday.magnetcraft.materials;

import com.imoonday.magnetcraft.registries.ItemRegistries;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class NetheriteMagneticIronToolMaterial implements ToolMaterial {

    public static final NetheriteMagneticIronToolMaterial INSTANCE = new NetheriteMagneticIronToolMaterial();
    @Override
    public int getDurability() {
        return 2843;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 9.0f;
    }

    @Override
    public float getAttackDamage() {
        return 4.0f;
    }

    @Override
    public int getMiningLevel() {
        return 4;
    }

    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ItemRegistries.NETHERITE_MAGNETIC_IRON_INGOT);
    }
}
