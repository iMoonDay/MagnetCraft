package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.items.CraftingModuleItem;
import com.imoonday.magnetcraft.common.items.GlintItem;
import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.common.items.PortableDemagnetizerItem;
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
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ItemRegistries {

    public static final Item RAW_MAGNET_ITEM = register("raw_magnet", new Item(new FabricItemSettings()));
    public static final Item MAGNET_FRAGMENT_ITEM = register("magnet_fragment", new Item(new FabricItemSettings()));
    public static final Item MAGNETIC_IRON_INGOT = register("magnetic_iron_ingot", new Item(new FabricItemSettings()));
    public static final Item NETHERITE_MAGNETIC_IRON_INGOT = register("netherite_magnetic_iron_ingot", new Item(new FabricItemSettings().fireproof()));
    public static final Item MAGNET_POWDER = register("magnet_powder", new Item(new FabricItemSettings()));
    public static final Item MAGNET_TEMPLATE_ITEM = register("magnet_template", new Item(new FabricItemSettings()));
    public static final Item DEMAGNETIZED_POWDER_ITEM = register("demagnetized_powder", new Item(new FabricItemSettings()));

    public static final Item MAGNETIC_SUSPENDED_POWDER_ITEM = register("magnetic_suspended_powder", new GlintItem(new FabricItemSettings().recipeRemainder(DEMAGNETIZED_POWDER_ITEM)));

    public static final CraftingModuleItem EMPTY_CRAFTING_MODULE_ITEM = register("empty_crafting_module", new CraftingModuleItem(new FabricItemSettings().maxCount(16)));
    public static final CraftingModuleItem POLAR_MAGNET_CRAFTING_MODULE_ITEM = register("polar_magnet_crafting_module", new CraftingModuleItem(new FabricItemSettings().maxCount(16)));
    public static final CraftingModuleItem ELECTROMAGNET_CRAFTING_MODULE_ITEM = register("electromagnet_crafting_module", new CraftingModuleItem(new FabricItemSettings().maxCount(16)));
    public static final CraftingModuleItem PERMANENT_MAGNET_CRAFTING_MODULE_ITEM = register("permanent_magnet_crafting_module", new CraftingModuleItem(new FabricItemSettings().maxCount(16)));
    public static final CraftingModuleItem CREATURE_MAGNET_CRAFTING_MODULE_ITEM = register("creature_magnet_crafting_module", new CraftingModuleItem(new FabricItemSettings().maxCount(16)));
    public static final CraftingModuleItem MINERAL_MAGNET_CRAFTING_MODULE_ITEM = register("mineral_magnet_crafting_module", new CraftingModuleItem(new FabricItemSettings().maxCount(16)));
    public static final CraftingModuleItem CROP_MAGNET_CRAFTING_MODULE_ITEM = register("crop_magnet_crafting_module", new CraftingModuleItem(new FabricItemSettings().maxCount(16)));

    public static final Item RESTORE_MODULE_ITEM = register("restore_module", new Item(new FabricItemSettings().maxCount(16)));
    public static final Item FILTER_MODULE_ITEM = register("filter_module", new Item(new FabricItemSettings().maxCount(16)));
    public static final Item EXTRACTION_MODULE_ITEM = register("extraction_module", new Item(new FabricItemSettings().maxCount(16)));
    public static final Item DEMAGNETIZE_MODULE_ITEM = register("demagnetize_module", new Item(new FabricItemSettings().maxCount(16)));

    public static final PolorMagnetItem POLAR_MAGNET_ITEM = register("polar_magnet", new PolorMagnetItem(new FabricItemSettings().maxCount(1)));
    public static final ElectromagnetItem ELECTROMAGNET_ITEM = register("electromagnet", new ElectromagnetItem(new FabricItemSettings().maxCount(1).maxDamage(12 * 64)));
    public static final PermanentMagnetItem PERMANENT_MAGNET_ITEM = register("permanent_magnet", new PermanentMagnetItem(new FabricItemSettings().maxCount(1)));
    public static final CreatureMagnetItem CREATURE_MAGNET_ITEM = register("creature_magnet", new CreatureMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(100)));
    public static final MineralMagnetItem MINERAL_MAGNET_ITEM = register("mineral_magnet", new MineralMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(9 * 64)));
    public static final CropMagnetItem CROP_MAGNET_ITEM = register("crop_magnet", new CropMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(32 * 64)));

    public static final MagnetControllerItem MAGNET_CONTROLLER_ITEM = register("magnet_controller", new MagnetControllerItem(new FabricItemSettings().maxCount(1).maxDamage(100)));
    public static final PortableDemagnetizerItem PORTABLE_DEMAGNETIZER_ITEM = register("portable_demagnetizer", new PortableDemagnetizerItem(new FabricItemSettings().maxCount(1)));

    public static SwordItem MAGNETIC_IRON_SWORD = register("magnetic_iron_sword", new SwordItem(MagneticIronToolMaterial.INSTANCE, 3, -2.4f, new Item.Settings()));
    public static CustomPickaxeItem MAGNETIC_IRON_PICKAXE = register("magnetic_iron_pickaxe", new CustomPickaxeItem(MagneticIronToolMaterial.INSTANCE, 1, -2.8f, new Item.Settings()));
    public static CustomAxeItem MAGNETIC_IRON_AXE = register("magnetic_iron_axe", new CustomAxeItem(MagneticIronToolMaterial.INSTANCE, 5.5f, -3.0f, new Item.Settings()));
    public static CustomShovelItem MAGNETIC_IRON_SHOVEL = register("magnetic_iron_shovel", new CustomShovelItem(MagneticIronToolMaterial.INSTANCE, 1.5f, -3.0f, new Item.Settings()));
    public static CustomHoeItem MAGNETIC_IRON_HOE = register("magnetic_iron_hoe", new CustomHoeItem(MagneticIronToolMaterial.INSTANCE, -2, -1.0f, new Item.Settings()));

    public static SwordItem NETHERITE_MAGNETIC_IRON_SWORD = register("netherite_magnetic_iron_sword", new SwordItem(NetheriteMagneticIronToolMaterial.INSTANCE, 4, -2.4f, new Item.Settings().fireproof()));
    public static CustomPickaxeItem NETHERITE_MAGNETIC_IRON_PICKAXE = register("netherite_magnetic_iron_pickaxe", new CustomPickaxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2, -2.8f, new Item.Settings().fireproof()));
    public static CustomAxeItem NETHERITE_MAGNETIC_IRON_AXE = register("netherite_magnetic_iron_axe", new CustomAxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 6.0f, -3.0f, new Item.Settings().fireproof()));
    public static CustomShovelItem NETHERITE_MAGNETIC_IRON_SHOVEL = register("netherite_magnetic_iron_shovel", new CustomShovelItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2.5f, -3.0f, new Item.Settings().fireproof()));
    public static CustomHoeItem NETHERITE_MAGNETIC_IRON_HOE = register("netherite_magnetic_iron_hoe", new CustomHoeItem(NetheriteMagneticIronToolMaterial.INSTANCE, -3, 0.0f, new Item.Settings().fireproof()));

    public static final MagneticIronArmorMaterial MAGNETIC_IRON_MATERIAL = new MagneticIronArmorMaterial();
    public static final MagneticIronArmorItem MAGNETIC_IRON_HELMET = register("magnetic_iron_helmet", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.HEAD, new Item.Settings()));
    public static final MagneticIronArmorItem MAGNETIC_IRON_CHESTPLATE = register("magnetic_iron_chestplate", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.CHEST, new Item.Settings()));
    public static final MagneticIronArmorItem MAGNETIC_IRON_LEGGINGS = register("magnetic_iron_leggings", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.LEGS, new Item.Settings()));
    public static final MagneticIronArmorItem MAGNETIC_IRON_BOOTS = register("magnetic_iron_boots", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, EquipmentSlot.FEET, new Item.Settings()));

    public static final NetheriteMagneticIronArmorMaterial NETHERITE_MAGNETIC_IRON_MATERIAL = new NetheriteMagneticIronArmorMaterial();
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_HELMET = register("netherite_magnetic_iron_helmet", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.HEAD, new Item.Settings().fireproof()));
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_CHESTPLATE = register("netherite_magnetic_iron_chestplate", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.CHEST, new Item.Settings().fireproof()));
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_LEGGINGS = register("netherite_magnetic_iron_leggings", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.LEGS, new Item.Settings().fireproof()));
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_BOOTS = register("netherite_magnetic_iron_boots", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, EquipmentSlot.FEET, new Item.Settings().fireproof()));

    public static final HorseArmorItem MAGNETIC_IRON_HORSE_ARMOR = register("magnetic_iron_horse_armor", new HorseArmorItem(9, "magnetic_iron", new Item.Settings().maxCount(1)));

    public static void register() {
        MagnetCraft.LOGGER.info("ItemRegistries.class Loaded");
    }

    static <T extends Item> T register(String id, T item) {
        Registry.register(Registries.ITEM, id(id), item);
        return item;
    }
}
