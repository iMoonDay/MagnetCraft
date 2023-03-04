package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.items.*;
import com.imoonday.magnetcraft.common.items.armors.MagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.armors.NetheriteMagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.magnets.*;
import com.imoonday.magnetcraft.common.items.materials.MagneticIronArmorMaterial;
import com.imoonday.magnetcraft.common.items.materials.MagneticIronToolMaterial;
import com.imoonday.magnetcraft.common.items.materials.NetheriteMagneticIronArmorMaterial;
import com.imoonday.magnetcraft.common.items.materials.NetheriteMagneticIronToolMaterial;
import com.imoonday.magnetcraft.common.items.tools.CustomAxeItem;
import com.imoonday.magnetcraft.common.items.tools.CustomHoeItem;
import com.imoonday.magnetcraft.common.items.tools.CustomPickaxeItem;
import com.imoonday.magnetcraft.common.items.tools.CustomShovelItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.common.BlockRegistries.*;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ItemRegistries {

    public static final Item RAW_MAGNET_ITEM = new Item(new FabricItemSettings());
    public static final Item MAGNET_FRAGMENT_ITEM = new Item(new FabricItemSettings());
    public static final Item MAGNETIC_IRON_INGOT = new Item(new FabricItemSettings());
    public static final Item NETHERITE_MAGNETIC_IRON_INGOT = new Item(new FabricItemSettings().fireproof());
    public static final Item MAGNET_POWDER = new Item(new FabricItemSettings());
    public static final Item MAGNET_TEMPLATE_ITEM = new Item(new FabricItemSettings());

    public static final CraftingModuleItem EMPTY_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final CraftingModuleItem POLAR_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final CraftingModuleItem ELECTROMAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final CraftingModuleItem PERMANENT_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final CraftingModuleItem CREATURE_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final CraftingModuleItem MINERAL_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final CraftingModuleItem CROP_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));

    public static final Item RESTORE_MODULE_ITEM = new Item(new FabricItemSettings().maxCount(16));
    public static final Item FILTER_MODULE_ITEM = new Item(new FabricItemSettings().maxCount(16));

    public static final PolorMagnetItem POLAR_MAGNET_ITEM = new PolorMagnetItem(new FabricItemSettings().maxCount(1));
    public static final ElectroMagnetItem ELECTROMAGNET_ITEM = new ElectroMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(500));
    public static final PermanentMagnetItem PERMANENT_MAGNET_ITEM = new PermanentMagnetItem(new FabricItemSettings().maxCount(1));
    public static final CreatureMagnetItem CREATURE_MAGNET_ITEM = new CreatureMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(100));
    public static final MineralMagnetItem MINERAL_MAGNET_ITEM = new MineralMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(9 * 64));
    public static final CropMagnetItem CROP_MAGNET_ITEM = new CropMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(5 * 64));

    public static final MagnetControllerItem MAGNET_CONTROLLER_ITEM = new MagnetControllerItem(new FabricItemSettings().maxCount(1).maxDamage(100));

    public static SwordItem MAGNETIC_IRON_SWORD = new SwordItem(MagneticIronToolMaterial.INSTANCE, 3, -2.4f, new Item.Settings());
    public static CustomPickaxeItem MAGNETIC_IRON_PICKAXE = new CustomPickaxeItem(MagneticIronToolMaterial.INSTANCE, 1, -2.8f, new Item.Settings());
    public static CustomAxeItem MAGNETIC_IRON_AXE = new CustomAxeItem(MagneticIronToolMaterial.INSTANCE, 5.5f, -3.0f, new Item.Settings());
    public static CustomShovelItem MAGNETIC_IRON_SHOVEL = new CustomShovelItem(MagneticIronToolMaterial.INSTANCE, 1.5f, -3.0f, new Item.Settings());
    public static CustomHoeItem MAGNETIC_IRON_HOE = new CustomHoeItem(MagneticIronToolMaterial.INSTANCE, -2, -1.0f, new Item.Settings());

    public static SwordItem NETHERITE_MAGNETIC_IRON_SWORD = new SwordItem(NetheriteMagneticIronToolMaterial.INSTANCE, 4, -2.4f, new Item.Settings().fireproof());
    public static CustomPickaxeItem NETHERITE_MAGNETIC_IRON_PICKAXE = new CustomPickaxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2, -2.8f, new Item.Settings().fireproof());
    public static CustomAxeItem NETHERITE_MAGNETIC_IRON_AXE = new CustomAxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 6.0f, -3.0f, new Item.Settings().fireproof());
    public static CustomShovelItem NETHERITE_MAGNETIC_IRON_SHOVEL = new CustomShovelItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2.5f, -3.0f, new Item.Settings().fireproof());
    public static CustomHoeItem NETHERITE_MAGNETIC_IRON_HOE = new CustomHoeItem(NetheriteMagneticIronToolMaterial.INSTANCE, -3, 0.0f, new Item.Settings().fireproof());

    public static final MagneticIronArmorMaterial MAGNETIC_IRON_MATERIAL = new MagneticIronArmorMaterial();
    public static final MagneticIronArmorItem MAGNETIC_IRON_HELMET = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.HEAD, new Item.Settings());
    public static final MagneticIronArmorItem MAGNETIC_IRON_CHESTPLATE = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.CHEST, new Item.Settings());
    public static final MagneticIronArmorItem MAGNETIC_IRON_LEGGINGS = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.LEGS, new Item.Settings());
    public static final MagneticIronArmorItem MAGNETIC_IRON_BOOTS = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.FEET, new Item.Settings());

    public static final NetheriteMagneticIronArmorMaterial NETHERITE_MAGNETIC_IRON_MATERIAL = new NetheriteMagneticIronArmorMaterial();
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_HELMET = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.HEAD, new Item.Settings().fireproof());
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_CHESTPLATE = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.CHEST, new Item.Settings().fireproof());
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_LEGGINGS = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.LEGS, new Item.Settings().fireproof());
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_BOOTS = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.FEET, new Item.Settings().fireproof());

    public static final HorseArmorItem MAGNETIC_IRON_HORSE_ARMOR = new HorseArmorItem(9, "magnetic_iron", new Item.Settings().maxCount(1));

    public static void register() {
        Registry.register(Registries.ITEM, id("raw_magnet"), RAW_MAGNET_ITEM);
        Registry.register(Registries.ITEM, id("magnet_fragment"), MAGNET_FRAGMENT_ITEM);
        Registry.register(Registries.ITEM, id("magnetic_iron_ingot"), MAGNETIC_IRON_INGOT);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_ingot"), NETHERITE_MAGNETIC_IRON_INGOT);
        Registry.register(Registries.ITEM, id("magnet_powder"), MAGNET_POWDER);
        Registry.register(Registries.ITEM, id("magnet_template"), MAGNET_TEMPLATE_ITEM);
        Registry.register(Registries.ITEM, id("empty_crafting_module"), EMPTY_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, id("electromagnet_crafting_module"), ELECTROMAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, id("permanent_magnet_crafting_module"), PERMANENT_MAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, id("polar_magnet_crafting_module"), POLAR_MAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, id("creature_magnet_crafting_module"), CREATURE_MAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, id("mineral_magnet_crafting_module"), MINERAL_MAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, id("crop_magnet_crafting_module"), CROP_MAGNET_CRAFTING_MODULE_ITEM);

        Registry.register(Registries.ITEM, id("restore_module"), RESTORE_MODULE_ITEM);
        Registry.register(Registries.ITEM, id("filter_module"), FILTER_MODULE_ITEM);

        Registry.register(Registries.ITEM, id("polar_magnet"), POLAR_MAGNET_ITEM);
        Registry.register(Registries.ITEM, id("electromagnet"), ELECTROMAGNET_ITEM);
        Registry.register(Registries.ITEM, id("permanent_magnet"), PERMANENT_MAGNET_ITEM);
        Registry.register(Registries.ITEM, id("creature_magnet"), CREATURE_MAGNET_ITEM);
        Registry.register(Registries.ITEM, id("mineral_magnet"), MINERAL_MAGNET_ITEM);
        Registry.register(Registries.ITEM, id("crop_magnet"), CROP_MAGNET_ITEM);

        Registry.register(Registries.ITEM, id("magnet_controller"), MAGNET_CONTROLLER_ITEM);

        Registry.register(Registries.ITEM, id("magnetite"), new BlockItem(MAGNETITE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("deepslate_magnetite"), new BlockItem(DEEPSLATE_MAGNETITE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("magnet_block"), new BlockItem(MAGNET_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("netherite_magnet_block"), new BlockItem(NETHERITE_MAGNET_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("raw_magnet_block"), new BlockItem(RAW_MAGNET_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("lodestone"), new LodestoneBlockItem(LODESTONE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("maglev_rail"), new BlockItem(MAGLEV_RAIL_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("maglev_powered_rail"), new BlockItem(MAGLEV_POWERED_RAIL_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("maglev_detector_rail"), new BlockItem(MAGLEV_DETECTOR_RAIL_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("maglev_activator_rail"), new BlockItem(MAGLEV_ACTIVATOR_RAIL_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("maglev_lever"), new BlockItem(MAGLEV_LEVER_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("magnetic_pressure_plate"), new BlockItem(MAGNETIC_PRESSURE_PLATE, new FabricItemSettings()));

        Registry.register(Registries.ITEM, id("magnetic_iron_sword"), MAGNETIC_IRON_SWORD);
        Registry.register(Registries.ITEM, id("magnetic_iron_pickaxe"), MAGNETIC_IRON_PICKAXE);
        Registry.register(Registries.ITEM, id("magnetic_iron_axe"), MAGNETIC_IRON_AXE);
        Registry.register(Registries.ITEM, id("magnetic_iron_shovel"), MAGNETIC_IRON_SHOVEL);
        Registry.register(Registries.ITEM, id("magnetic_iron_hoe"), MAGNETIC_IRON_HOE);

        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_sword"), NETHERITE_MAGNETIC_IRON_SWORD);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_pickaxe"), NETHERITE_MAGNETIC_IRON_PICKAXE);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_axe"), NETHERITE_MAGNETIC_IRON_AXE);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_shovel"), NETHERITE_MAGNETIC_IRON_SHOVEL);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_hoe"), NETHERITE_MAGNETIC_IRON_HOE);

        Registry.register(Registries.ITEM, id("magnetic_iron_helmet"), MAGNETIC_IRON_HELMET);
        Registry.register(Registries.ITEM, id("magnetic_iron_chestplate"), MAGNETIC_IRON_CHESTPLATE);
        Registry.register(Registries.ITEM, id("magnetic_iron_leggings"), MAGNETIC_IRON_LEGGINGS);
        Registry.register(Registries.ITEM, id("magnetic_iron_boots"), MAGNETIC_IRON_BOOTS);

        Registry.register(Registries.ITEM, id("magnetic_iron_horse_armor"), MAGNETIC_IRON_HORSE_ARMOR);

        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_helmet"), NETHERITE_MAGNETIC_IRON_HELMET);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_chestplate"), NETHERITE_MAGNETIC_IRON_CHESTPLATE);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_leggings"), NETHERITE_MAGNETIC_IRON_LEGGINGS);
        Registry.register(Registries.ITEM, id("netherite_magnetic_iron_boots"), NETHERITE_MAGNETIC_IRON_BOOTS);
    }
}
