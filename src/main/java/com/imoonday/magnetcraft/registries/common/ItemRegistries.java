package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.items.*;
import com.imoonday.magnetcraft.common.materials.MagneticIronArmorMaterial;
import com.imoonday.magnetcraft.common.materials.MagneticIronToolMaterial;
import com.imoonday.magnetcraft.common.materials.NetheriteMagneticIronArmorMaterial;
import com.imoonday.magnetcraft.common.materials.NetheriteMagneticIronToolMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;
import static com.imoonday.magnetcraft.registries.common.BlockRegistries.*;

public class ItemRegistries {

    public static final Item RAW_MAGNET_ITEM = new Item(new FabricItemSettings());
    public static final Item MAGNET_FRAGMENT_ITEM = new Item(new FabricItemSettings());
    public static final Item MAGNETIC_IRON_INGOT = new Item(new FabricItemSettings());
    public static final Item NETHERITE_MAGNETIC_IRON_INGOT = new Item(new FabricItemSettings().fireproof());
    public static final Item MAGNET_POWDER = new Item(new FabricItemSettings());
    public static final Item MAGNET_TEMPLATE_ITEM = new Item(new FabricItemSettings());
    public static final Item EMPTY_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final Item POLAR_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final Item ELECTROMAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final Item PERMANENT_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));
    public static final Item CREATURE_MAGNET_CRAFTING_MODULE_ITEM = new CraftingModuleItem(new FabricItemSettings().maxCount(16));

    public static final Item POLAR_MAGNET_ITEM = new PolorMagnetItem(new FabricItemSettings().maxCount(1));
    public static final Item ELECTROMAGNET_ITEM = new ElectroMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(500));
    public static final Item PERMANENT_MAGNET_ITEM = new PermanentMagnetItem(new FabricItemSettings().maxCount(1));
    public static final Item CREATURE_MAGNET_ITEM = new CreatureMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(100));

    public static final Item MAGNET_CONTROLLER_ITEM = new MagnetControllerItem(new FabricItemSettings().maxCount(1).maxDamage(100));

    public static ToolItem MAGNETIC_IRON_SWORD = new SwordItem(MagneticIronToolMaterial.INSTANCE, 3, -2.4f, new Item.Settings());
    public static ToolItem MAGNETIC_IRON_PICKAXE = new CustomPickaxeItem(MagneticIronToolMaterial.INSTANCE, 1, -2.8f, new Item.Settings());
    public static ToolItem MAGNETIC_IRON_AXE = new CustomAxeItem(MagneticIronToolMaterial.INSTANCE, 5.5f, -3.0f, new Item.Settings());
    public static ToolItem MAGNETIC_IRON_SHOVEL = new ShovelItem(MagneticIronToolMaterial.INSTANCE, 1.5f, -3.0f, new Item.Settings());
    public static ToolItem MAGNETIC_IRON_HOE = new CustomHoeItem(MagneticIronToolMaterial.INSTANCE, -2, -1.0f, new Item.Settings());

    public static ToolItem NETHERITE_MAGNETIC_IRON_SWORD = new SwordItem(NetheriteMagneticIronToolMaterial.INSTANCE, 4, -2.4f, new Item.Settings().fireproof());
    public static ToolItem NETHERITE_MAGNETIC_IRON_PICKAXE = new CustomPickaxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2, -2.8f, new Item.Settings().fireproof());
    public static ToolItem NETHERITE_MAGNETIC_IRON_AXE = new CustomAxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 6.0f, -3.0f, new Item.Settings().fireproof());
    public static ToolItem NETHERITE_MAGNETIC_IRON_SHOVEL = new ShovelItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2.5f, -3.0f, new Item.Settings().fireproof());
    public static ToolItem NETHERITE_MAGNETIC_IRON_HOE = new CustomHoeItem(NetheriteMagneticIronToolMaterial.INSTANCE, -3, 0.0f, new Item.Settings().fireproof());

    public static final ArmorMaterial MAGNETIC_IRON_MATERIAL = new MagneticIronArmorMaterial();
    public static final Item MAGNETIC_IRON_HELMET = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.HEAD, new Item.Settings());
    public static final Item MAGNETIC_IRON_CHESTPLATE = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.CHEST, new Item.Settings());
    public static final Item MAGNETIC_IRON_LEGGINGS = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.LEGS, new Item.Settings());
    public static final Item MAGNETIC_IRON_BOOTS = new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.FEET, new Item.Settings());

    public static final ArmorMaterial NETHERITE_MAGNETIC_IRON_MATERIAL = new NetheriteMagneticIronArmorMaterial();
    public static final Item NETHERITE_MAGNETIC_IRON_HELMET = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.HEAD, new Item.Settings().fireproof());
    public static final Item NETHERITE_MAGNETIC_IRON_CHESTPLATE = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.CHEST, new Item.Settings().fireproof());
    public static final Item NETHERITE_MAGNETIC_IRON_LEGGINGS = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.LEGS, new Item.Settings().fireproof());
    public static final Item NETHERITE_MAGNETIC_IRON_BOOTS = new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.FEET, new Item.Settings().fireproof());

    public static void register() {

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "raw_magnet"), RAW_MAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_fragment"), MAGNET_FRAGMENT_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_ingot"), MAGNETIC_IRON_INGOT);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_ingot"), NETHERITE_MAGNETIC_IRON_INGOT);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_powder"), MAGNET_POWDER);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_template"), MAGNET_TEMPLATE_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "empty_crafting_module"), EMPTY_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "electromagnet_crafting_module"), ELECTROMAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "permanent_magnet_crafting_module"), PERMANENT_MAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "polar_magnet_crafting_module"), POLAR_MAGNET_CRAFTING_MODULE_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "creature_magnet_crafting_module"), CREATURE_MAGNET_CRAFTING_MODULE_ITEM);

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "polar_magnet"), POLAR_MAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "electromagnet"), ELECTROMAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "permanent_magnet"), PERMANENT_MAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "creature_magnet"), CREATURE_MAGNET_ITEM);

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_controller"), MAGNET_CONTROLLER_ITEM);

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetite"), new BlockItem(MAGNETITE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "deepslate_magnetite"), new BlockItem(DEEPSLATE_MAGNETITE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_block"), new BlockItem(MAGNET_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnet_block"), new BlockItem(NETHERITE_MAGNET_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "raw_magnet_block"), new BlockItem(RAW_MAGNET_BLOCK, new FabricItemSettings()));

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_sword"), MAGNETIC_IRON_SWORD);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_pickaxe"), MAGNETIC_IRON_PICKAXE);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_axe"), MAGNETIC_IRON_AXE);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_shovel"), MAGNETIC_IRON_SHOVEL);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_hoe"), MAGNETIC_IRON_HOE);

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_sword"), NETHERITE_MAGNETIC_IRON_SWORD);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_pickaxe"), NETHERITE_MAGNETIC_IRON_PICKAXE);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_axe"), NETHERITE_MAGNETIC_IRON_AXE);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_shovel"), NETHERITE_MAGNETIC_IRON_SHOVEL);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_hoe"), NETHERITE_MAGNETIC_IRON_HOE);

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_helmet"), MAGNETIC_IRON_HELMET);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_chestplate"), MAGNETIC_IRON_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_leggings"), MAGNETIC_IRON_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetic_iron_boots"), MAGNETIC_IRON_BOOTS);

        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_helmet"), NETHERITE_MAGNETIC_IRON_HELMET);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_chestplate"), NETHERITE_MAGNETIC_IRON_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_leggings"), NETHERITE_MAGNETIC_IRON_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "netherite_magnetic_iron_boots"), NETHERITE_MAGNETIC_IRON_BOOTS);

    }
}