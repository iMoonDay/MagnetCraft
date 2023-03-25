package com.imoonday.magnetcraft.common.items.materials;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class NetheriteMagneticIronArmorMaterial implements ArmorMaterial {
    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[] {3, 6, 8, 3};
    public static final String NETHERITE_MAGNETIC_IRON = "netherite_magnetic_iron";

    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY[type.getEquipmentSlot().getEntitySlotId()] * 37;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return PROTECTION_VALUES[type.getEquipmentSlot().getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ItemRegistries.NETHERITE_MAGNETIC_IRON_INGOT);
    }

    @Override
    public String getName() {
        // Must be all lowercase
        return NETHERITE_MAGNETIC_IRON;
    }

    @Override
    public float getToughness() {
        return 3.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}