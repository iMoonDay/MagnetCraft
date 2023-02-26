package com.imoonday.magnetcraft.common.items.armors;

import com.imoonday.magnetcraft.config.ModConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
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
        tooltip.add(Text.translatable("item.magnetcraft.magnetic_armor.tooltip", ModConfig.getConfig().value.magnetSetMultiplier)
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }
}
