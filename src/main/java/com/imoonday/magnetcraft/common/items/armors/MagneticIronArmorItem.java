package com.imoonday.magnetcraft.common.items.armors;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagneticIronArmorItem extends ArmorItem {

    public MagneticIronArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.magnetcraft.magnetic_armor.tooltip", ModConfig.getValue().magnetSetMultiplier)
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        if (stack.getNbt() != null && stack.getNbt().contains("UsedTick")) {
            int usedTick = stack.getOrCreateNbt().getInt("UsedTick");
            tooltip.add(Text.translatable("enchantment.magnetcraft.magnetic_levitation.tooltop", usedTick / 12)
                    .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        }
    }

    public static boolean isInMagneticIronSuit(LivingEntity entity) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        boolean hasMagneticIronHelmet = head.isOf(ItemRegistries.MAGNETIC_IRON_HELMET);
        boolean hasMagneticIronChestcplate = chest.isOf(ItemRegistries.MAGNETIC_IRON_CHESTPLATE);
        boolean hasMagneticIronLeggings = legs.isOf(ItemRegistries.MAGNETIC_IRON_LEGGINGS);
        boolean hasMagneticIronBoots = feet.isOf(ItemRegistries.MAGNETIC_IRON_BOOTS);
        return hasMagneticIronHelmet && hasMagneticIronChestcplate && hasMagneticIronLeggings && hasMagneticIronBoots;
    }

}
