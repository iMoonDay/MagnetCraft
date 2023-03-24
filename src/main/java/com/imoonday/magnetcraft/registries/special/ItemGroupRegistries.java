package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.magnets.MineralMagnetItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import static com.imoonday.magnetcraft.registries.common.BlockRegistries.*;
import static com.imoonday.magnetcraft.registries.common.EntityRegistries.MAGNETIC_IRON_GOLEM_SPAWN_EGG;
import static com.imoonday.magnetcraft.registries.common.FluidRegistries.MAGNETIC_FLUID_BUCKET;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.*;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
@SuppressWarnings("CodeBlock2Expr")
public class ItemGroupRegistries {

    @SuppressWarnings("AlibabaMethodTooLong")
    public static void register() {

        FabricItemGroup.builder(id("magnet"))
                .displayName(Text.translatable("group.magnetcraft.magnet"))
                .icon(() -> new ItemStack(ELECTROMAGNET_ITEM))
                .entries((context, content) -> {
                    content.add(MAGNETITE_BLOCK);
                    content.add(DEEPSLATE_MAGNETITE_BLOCK);
                    content.add(RAW_MAGNET_BLOCK);
                    content.add(MAGNET_BLOCK);
                    content.add(NETHERITE_MAGNET_BLOCK);
                    content.add(LODESTONE_BLOCK);
                    content.add(DEMAGNETIZER_BLOCK);
                    content.add(ATTRACT_SENSOR_BLOCK);
                    content.add(ADVANCED_GRINDSTONE_BLOCK);
                    content.add(VERTICAL_REPEATER_BLOCK);
                    content.add(MAGLEV_LEVER_BLOCK);
                    content.add(MAGLEV_OAK_BUTTON_BLOCK);
                    content.add(MAGLEV_STONE_BUTTON_BLOCK);
                    content.add(MAGLEV_OAK_DOOR_BLOCK);
                    content.add(MAGLEV_IRON_DOOR_BLOCK);
                    content.add(MAGLEV_REPEATER_BLOCK);
                    content.add(CIRCULAR_REPEATER_BLOCK);
                    content.add(MAGLEV_COMPARATOR_BLOCK);
                    content.add(ELECTROMAGNETIC_RELAY_BLOCK);
                    content.add(MAGNETIC_PRESSURE_PLATE);
                    content.add(MAGLEV_RAIL_BLOCK);
                    content.add(MAGLEV_POWERED_RAIL_BLOCK);
                    content.add(MAGLEV_DETECTOR_RAIL_BLOCK);
                    content.add(MAGLEV_ACTIVATOR_RAIL_BLOCK);
                    content.add(RAW_MAGNET_ITEM);
                    content.add(MAGNET_FRAGMENT_ITEM);
                    content.add(MAGNETIC_SUSPENDED_POWDER_ITEM);
                    content.add(DEMAGNETIZED_POWDER_ITEM);
                    content.add(MAGNETIC_IRON_INGOT);
                    content.add(NETHERITE_MAGNETIC_IRON_INGOT);
                    content.add(MAGNET_POWDER);
                    content.add(MAGNETIC_IRON_SWORD);
                    content.add(MAGNETIC_IRON_PICKAXE);
                    content.add(MAGNETIC_IRON_AXE);
                    content.add(MAGNETIC_IRON_SHOVEL);
                    content.add(MAGNETIC_IRON_HOE);
                    content.add(NETHERITE_MAGNETIC_IRON_SWORD);
                    content.add(NETHERITE_MAGNETIC_IRON_PICKAXE);
                    content.add(NETHERITE_MAGNETIC_IRON_AXE);
                    content.add(NETHERITE_MAGNETIC_IRON_SHOVEL);
                    content.add(NETHERITE_MAGNETIC_IRON_HOE);
                    content.add(MAGNETIC_IRON_HELMET);
                    content.add(MAGNETIC_IRON_CHESTPLATE);
                    content.add(MAGNETIC_IRON_LEGGINGS);
                    content.add(MAGNETIC_IRON_BOOTS);
                    content.add(NETHERITE_MAGNETIC_IRON_HELMET);
                    content.add(NETHERITE_MAGNETIC_IRON_CHESTPLATE);
                    content.add(NETHERITE_MAGNETIC_IRON_LEGGINGS);
                    content.add(NETHERITE_MAGNETIC_IRON_BOOTS);
                    content.add(MAGNETIC_IRON_HORSE_ARMOR);
                    content.add(EMPTY_CRAFTING_MODULE_ITEM);
                    content.add(POLAR_MAGNET_CRAFTING_MODULE_ITEM);
                    content.add(ELECTROMAGNET_CRAFTING_MODULE_ITEM);
                    content.add(PERMANENT_MAGNET_CRAFTING_MODULE_ITEM);
                    content.add(CREATURE_MAGNET_CRAFTING_MODULE_ITEM);
                    content.add(MINERAL_MAGNET_CRAFTING_MODULE_ITEM);
                    content.add(CROP_MAGNET_CRAFTING_MODULE_ITEM);
                    content.add(ADSORPTION_MAGNET_CRAFTING_MODULE_ITEM);
                    content.add(RESTORE_MODULE_ITEM);
                    content.add(FILTER_MODULE_ITEM);
                    content.add(EXTRACTION_MODULE_ITEM);
                    content.add(DEMAGNETIZE_MODULE_ITEM);
                    content.add(MAGNET_TEMPLATE_ITEM);
                    content.add(POLAR_MAGNET_ITEM.getDefaultStack());
                    content.add(ELECTROMAGNET_ITEM.getDefaultStack());
                    content.add(PERMANENT_MAGNET_ITEM.getDefaultStack());
                    content.add(CREATURE_MAGNET_ITEM.getDefaultStack());
                    content.add(MINERAL_MAGNET_ITEM.getDefaultStack());
                    content.add(MineralMagnetItem.getAllCoresStack());
                    content.add(CROP_MAGNET_ITEM.getDefaultStack());
                    content.add(ADSORPTION_MAGNET_ITEM);
                    content.add(MAGNET_CONTROLLER_ITEM.getDefaultStack());
                    content.add(PORTABLE_DEMAGNETIZER_ITEM.getDefaultStack());
                    content.add(SMALL_MAGNETIC_SUCKER_ITEM);
                    content.add(LARGE_MAGNETIC_SUCKER_ITEM);
                    content.add(MAGNETIC_FLUID_BUCKET);
                    content.add(MAGNETIC_IRON_GOLEM_SPAWN_EGG);
                }).build();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
            content.addAfter(Items.DEEPSLATE_IRON_ORE, MAGNETITE_BLOCK);
            content.addAfter(MAGNETITE_BLOCK, DEEPSLATE_MAGNETITE_BLOCK);
            content.addAfter(Items.RAW_IRON_BLOCK, RAW_MAGNET_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(Items.RAW_IRON, RAW_MAGNET_ITEM);
            content.addAfter(Items.IRON_NUGGET, MAGNET_FRAGMENT_ITEM);
            content.addAfter(MAGNET_FRAGMENT_ITEM, MAGNETIC_SUSPENDED_POWDER_ITEM);
            content.addAfter(MAGNETIC_SUSPENDED_POWDER_ITEM, DEMAGNETIZED_POWDER_ITEM);
            content.addAfter(Items.IRON_INGOT, MAGNETIC_IRON_INGOT);
            content.addAfter(Items.NETHERITE_INGOT, NETHERITE_MAGNETIC_IRON_INGOT);
            content.addAfter(Items.GUNPOWDER, MAGNET_POWDER);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.addAfter(Items.IRON_BLOCK, MAGNET_BLOCK);
            content.addAfter(Items.NETHERITE_BLOCK, NETHERITE_MAGNET_BLOCK);
            content.addAfter(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, MAGNETIC_PRESSURE_PLATE);
            content.addAfter(Items.OAK_BUTTON, MAGLEV_OAK_BUTTON_BLOCK);
            content.addAfter(Items.STONE_BUTTON, MAGLEV_STONE_BUTTON_BLOCK);
            content.addAfter(Items.OAK_DOOR, MAGLEV_OAK_DOOR_BLOCK);
            content.addAfter(Items.IRON_DOOR, MAGLEV_IRON_DOOR_BLOCK);
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
            content.add(MINERAL_MAGNET_CRAFTING_MODULE_ITEM);
            content.add(CROP_MAGNET_CRAFTING_MODULE_ITEM);
            content.add(ADSORPTION_MAGNET_CRAFTING_MODULE_ITEM);
            content.add(RESTORE_MODULE_ITEM);
            content.add(FILTER_MODULE_ITEM);
            content.add(EXTRACTION_MODULE_ITEM);
            content.add(DEMAGNETIZE_MODULE_ITEM);
            content.add(MAGNET_TEMPLATE_ITEM);
            content.add(POLAR_MAGNET_ITEM.getDefaultStack());
            content.add(ELECTROMAGNET_ITEM.getDefaultStack());
            content.add(PERMANENT_MAGNET_ITEM.getDefaultStack());
            content.add(CREATURE_MAGNET_ITEM.getDefaultStack());
            content.add(MINERAL_MAGNET_ITEM.getDefaultStack());
            content.add(MineralMagnetItem.getAllCoresStack());
            content.add(CROP_MAGNET_ITEM.getDefaultStack());
            content.add(ADSORPTION_MAGNET_ITEM);
            content.add(MAGNET_CONTROLLER_ITEM.getDefaultStack());
            content.add(PORTABLE_DEMAGNETIZER_ITEM.getDefaultStack());
            content.add(SMALL_MAGNETIC_SUCKER_ITEM);
            content.add(LARGE_MAGNETIC_SUCKER_ITEM);
            content.addAfter(Items.ACTIVATOR_RAIL, MAGLEV_RAIL_BLOCK);
            content.addAfter(MAGLEV_RAIL_BLOCK, MAGLEV_POWERED_RAIL_BLOCK);
            content.addAfter(MAGLEV_POWERED_RAIL_BLOCK, MAGLEV_DETECTOR_RAIL_BLOCK);
            content.addAfter(MAGLEV_DETECTOR_RAIL_BLOCK, MAGLEV_ACTIVATOR_RAIL_BLOCK);
            content.addAfter(Items.WATER_BUCKET, MAGNETIC_FLUID_BUCKET);
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
            content.addAfter(Items.IRON_HORSE_ARMOR, MAGNETIC_IRON_HORSE_ARMOR);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.addAfter(Items.LODESTONE, LODESTONE_BLOCK);
            content.addAfter(LODESTONE_BLOCK, DEMAGNETIZER_BLOCK);
            content.addAfter(DEMAGNETIZER_BLOCK, ATTRACT_SENSOR_BLOCK);
            content.addAfter(ATTRACT_SENSOR_BLOCK, ADVANCED_GRINDSTONE_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.addAfter(Items.ACTIVATOR_RAIL, MAGLEV_RAIL_BLOCK);
            content.addAfter(MAGLEV_RAIL_BLOCK, MAGLEV_POWERED_RAIL_BLOCK);
            content.addAfter(MAGLEV_POWERED_RAIL_BLOCK, MAGLEV_DETECTOR_RAIL_BLOCK);
            content.addAfter(MAGLEV_DETECTOR_RAIL_BLOCK, MAGLEV_ACTIVATOR_RAIL_BLOCK);
            content.addAfter(Items.LEVER, MAGLEV_LEVER_BLOCK);
            content.addAfter(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, MAGNETIC_PRESSURE_PLATE);
            content.addAfter(Items.OAK_BUTTON, MAGLEV_OAK_BUTTON_BLOCK);
            content.addAfter(Items.STONE_BUTTON, MAGLEV_STONE_BUTTON_BLOCK);
            content.addAfter(Items.OAK_DOOR, MAGLEV_OAK_DOOR_BLOCK);
            content.addAfter(Items.IRON_DOOR, MAGLEV_IRON_DOOR_BLOCK);
            content.addAfter(Items.REPEATER, MAGLEV_REPEATER_BLOCK);
            content.addAfter(MAGLEV_REPEATER_BLOCK, CIRCULAR_REPEATER_BLOCK);
            content.addAfter(Items.COMPARATOR, MAGLEV_COMPARATOR_BLOCK);
            content.addAfter(MAGLEV_COMPARATOR_BLOCK, ELECTROMAGNETIC_RELAY_BLOCK);
            content.addAfter(ELECTROMAGNETIC_RELAY_BLOCK, VERTICAL_REPEATER_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(MAGNETIC_IRON_GOLEM_SPAWN_EGG);
        });
    }
}
