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

public class NetheriteMagneticIronArmorItem extends ArmorItem {
    public NetheriteMagneticIronArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.magnetcraft.netherite_magnetic_armor.tooltip", ModConfig.getValue().netheriteMagnetSetMultiplier)
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        if (stack.getNbt() != null && stack.getNbt().contains("UsedTick")) {
            int usedTick = stack.getOrCreateNbt().getInt("UsedTick");
            tooltip.add(Text.translatable("enchantment.magnetcraft.magnetic_levitation.tooltop", usedTick / 12)
                    .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        }
    }

    public static boolean isInNetheriteMagneticIronSuit(LivingEntity entity) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        boolean hasNetheriteMagneticIronHelmet = head.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_HELMET);
        boolean hasNetheriteMagneticIronChestcplate = chest.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_CHESTPLATE);
        boolean hasNetheriteMagneticIronLeggings = legs.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_LEGGINGS);
        boolean hasNetheriteMagneticIronBoots = feet.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_BOOTS);
        return hasNetheriteMagneticIronHelmet && hasNetheriteMagneticIronChestcplate && hasNetheriteMagneticIronLeggings && hasNetheriteMagneticIronBoots;
    }

}
