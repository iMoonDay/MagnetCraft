package com.imoonday.magnetcraft.registries.special;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;
import static com.imoonday.magnetcraft.registries.common.BlockRegistries.*;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.*;

public class ItemGroupRegistries {

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(
                    MOD_ID, "magnet"))
            .displayName(Text.translatable("group.magnetcraft.magnet"))
            .icon(() -> new ItemStack(ELECTROMAGNET_ITEM))
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                entries.add(MAGNETITE_BLOCK);
                entries.add(DEEPSLATE_MAGNETITE_BLOCK);
                entries.add(RAW_MAGNET_BLOCK);
                entries.add(MAGNET_BLOCK);
                entries.add(NETHERITE_MAGNET_BLOCK);
                entries.add(RAW_MAGNET_ITEM);
                entries.add(MAGNET_FRAGMENT_ITEM);
                entries.add(MAGNETIC_IRON_INGOT);
                entries.add(NETHERITE_MAGNETIC_IRON_INGOT);
                entries.add(MAGNET_POWDER);
                entries.add(MAGNETIC_IRON_SWORD);
                entries.add(MAGNETIC_IRON_PICKAXE);
                entries.add(MAGNETIC_IRON_AXE);
                entries.add(MAGNETIC_IRON_SHOVEL);
                entries.add(MAGNETIC_IRON_HOE);
                entries.add(NETHERITE_MAGNETIC_IRON_SWORD);
                entries.add(NETHERITE_MAGNETIC_IRON_PICKAXE);
                entries.add(NETHERITE_MAGNETIC_IRON_AXE);
                entries.add(NETHERITE_MAGNETIC_IRON_SHOVEL);
                entries.add(NETHERITE_MAGNETIC_IRON_HOE);
                entries.add(MAGNETIC_IRON_HELMET);
                entries.add(MAGNETIC_IRON_CHESTPLATE);
                entries.add(MAGNETIC_IRON_LEGGINGS);
                entries.add(MAGNETIC_IRON_BOOTS);
                entries.add(NETHERITE_MAGNETIC_IRON_HELMET);
                entries.add(NETHERITE_MAGNETIC_IRON_CHESTPLATE);
                entries.add(NETHERITE_MAGNETIC_IRON_LEGGINGS);
                entries.add(NETHERITE_MAGNETIC_IRON_BOOTS);
                entries.add(EMPTY_CRAFTING_MODULE_ITEM);
                entries.add(POLAR_MAGNET_CRAFTING_MODULE_ITEM);
                entries.add(ELECTROMAGNET_CRAFTING_MODULE_ITEM);
                entries.add(PERMANENT_MAGNET_CRAFTING_MODULE_ITEM);
                entries.add(CREATURE_MAGNET_CRAFTING_MODULE_ITEM);
                entries.add(MAGNET_TEMPLATE_ITEM);
                entries.add(POLAR_MAGNET_ITEM);
                entries.add(ELECTROMAGNET_ITEM);
                entries.add(PERMANENT_MAGNET_ITEM);
                entries.add(CREATURE_MAGNET_ITEM);
                entries.add(MAGNET_CONTROLLER_ITEM);
            }).build();

    public static void register(){

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
            content.addAfter(Items.DEEPSLATE_IRON_ORE, MAGNETITE_BLOCK);
            content.addAfter(MAGNETITE_BLOCK, DEEPSLATE_MAGNETITE_BLOCK);
            content.addAfter(Items.RAW_IRON_BLOCK, RAW_MAGNET_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(Items.RAW_IRON, RAW_MAGNET_ITEM);
            content.addAfter(Items.IRON_NUGGET, MAGNET_FRAGMENT_ITEM);
            content.addAfter(Items.IRON_INGOT, MAGNETIC_IRON_INGOT);
            content.addAfter(Items.NETHERITE_INGOT, NETHERITE_MAGNETIC_IRON_INGOT);
            content.addAfter(Items.GUNPOWDER, MAGNET_POWDER);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.addAfter(Items.IRON_BLOCK, MAGNET_BLOCK);
            content.addAfter(Items.NETHERITE_BLOCK, NETHERITE_MAGNET_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.IRON_HOE, MAGNETIC_IRON_SHOVEL);
            content.addAfter(MAGNETIC_IRON_SHOVEL, MAGNETIC_IRON_PICKAXE);
            content.addAfter(MAGNETIC_IRON_PICKAXE, MAGNETIC_IRON_AXE);
            content.addAfter(MAGNETIC_IRON_AXE, MAGNETIC_IRON_SHOVEL);
            content.addAfter(MAGNETIC_IRON_SHOVEL, MAGNETIC_IRON_HOE);
            content.addAfter(Items.NETHERITE_HOE, NETHERITE_MAGNETIC_IRON_SHOVEL);
            content.addAfter(NETHERITE_MAGNETIC_IRON_SHOVEL, NETHERITE_MAGNETIC_IRON_PICKAXE);
            content.addAfter(NETHERITE_MAGNETIC_IRON_PICKAXE, NETHERITE_MAGNETIC_IRON_AXE);
            content.addAfter(NETHERITE_MAGNETIC_IRON_AXE, NETHERITE_MAGNETIC_IRON_SHOVEL);
            content.addAfter(NETHERITE_MAGNETIC_IRON_SHOVEL, NETHERITE_MAGNETIC_IRON_HOE);
            content.add(EMPTY_CRAFTING_MODULE_ITEM);
            content.add(POLAR_MAGNET_CRAFTING_MODULE_ITEM);
            content.add(ELECTROMAGNET_CRAFTING_MODULE_ITEM);
            content.add(PERMANENT_MAGNET_CRAFTING_MODULE_ITEM);
            content.add(CREATURE_MAGNET_CRAFTING_MODULE_ITEM);
            content.add(MAGNET_TEMPLATE_ITEM);
            content.add(POLAR_MAGNET_ITEM);
            content.add(ELECTROMAGNET_ITEM);
            content.add(PERMANENT_MAGNET_ITEM);
            content.add(CREATURE_MAGNET_ITEM);
            content.add(MAGNET_CONTROLLER_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.IRON_SWORD, MAGNETIC_IRON_SWORD);
            content.addAfter(Items.NETHERITE_SWORD, NETHERITE_MAGNETIC_IRON_SWORD);
            content.addAfter(Items.IRON_BOOTS, MAGNETIC_IRON_HELMET);
            content.addAfter(MAGNETIC_IRON_HELMET, MAGNETIC_IRON_CHESTPLATE);
            content.addAfter(MAGNETIC_IRON_CHESTPLATE, MAGNETIC_IRON_LEGGINGS);
            content.addAfter(MAGNETIC_IRON_LEGGINGS, MAGNETIC_IRON_BOOTS);
            content.addAfter(Items.NETHERITE_BOOTS, NETHERITE_MAGNETIC_IRON_HELMET);
            content.addAfter(NETHERITE_MAGNETIC_IRON_HELMET, NETHERITE_MAGNETIC_IRON_CHESTPLATE);
            content.addAfter(NETHERITE_MAGNETIC_IRON_CHESTPLATE, NETHERITE_MAGNETIC_IRON_LEGGINGS);
            content.addAfter(NETHERITE_MAGNETIC_IRON_LEGGINGS, NETHERITE_MAGNETIC_IRON_BOOTS);
        });

    }
}
