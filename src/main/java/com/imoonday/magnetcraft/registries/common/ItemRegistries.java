package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
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
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;
import static com.imoonday.magnetcraft.registries.special.ItemGroupRegistries.MAGNET_BLOCKS;
import static com.imoonday.magnetcraft.registries.special.ItemGroupRegistries.MAGNET_ITEMS;

public class ItemRegistries {

    //原材料
    public static final Item RAW_MAGNET_ITEM = registerItem("raw_magnet");
    public static final Item MAGNET_FRAGMENT_ITEM = registerItem("magnet_fragment");
    public static final Item MAGNETIC_IRON_INGOT = registerItem("magnetic_iron_ingot");
    public static final Item NETHERITE_MAGNETIC_IRON_INGOT = register("netherite_magnetic_iron_ingot", new Item(fireproof()));
    public static final Item MAGNET_POWDER = registerItem("magnet_powder");
    public static final Item DEMAGNETIZED_POWDER_ITEM = registerItem("demagnetized_powder");
    public static final Item MAGNETIC_SUSPENDED_POWDER_ITEM = register("magnetic_suspended_powder", new GlintItem(settings().recipeRemainder(DEMAGNETIZED_POWDER_ITEM)));

    //合成模块
    public static final CraftingModuleItem EMPTY_CRAFTING_MODULE_ITEM = registerModule("empty_crafting_module");
    public static final CraftingModuleItem POLAR_MAGNET_CRAFTING_MODULE_ITEM = registerModule("polar_magnet_crafting_module");
    public static final CraftingModuleItem ELECTROMAGNET_CRAFTING_MODULE_ITEM = registerModule("electromagnet_crafting_module");
    public static final CraftingModuleItem PERMANENT_MAGNET_CRAFTING_MODULE_ITEM = registerModule("permanent_magnet_crafting_module");
    public static final CraftingModuleItem CREATURE_MAGNET_CRAFTING_MODULE_ITEM = registerModule("creature_magnet_crafting_module");
    public static final CraftingModuleItem MINERAL_MAGNET_CRAFTING_MODULE_ITEM = registerModule("mineral_magnet_crafting_module");
    public static final CraftingModuleItem CROP_MAGNET_CRAFTING_MODULE_ITEM = registerModule("crop_magnet_crafting_module");
    public static final CraftingModuleItem ADSORPTION_MAGNET_CRAFTING_MODULE_ITEM = registerModule("adsorption_magnet_crafting_module");

    //其他模块
    public static final Item RESTORE_MODULE_ITEM = registerItem("restore_module",16);
    public static final Item FILTER_MODULE_ITEM = registerItem("filter_module", 16);
    public static final Item EXTRACTION_MODULE_ITEM = registerItem("extraction_module", 16);
    public static final Item DEMAGNETIZE_MODULE_ITEM = registerItem("demagnetize_module", 16);

    //磁铁
    public static final Item MAGNET_TEMPLATE_ITEM = registerItem("magnet_template");
    public static final PolorMagnetItem POLAR_MAGNET_ITEM = register("polar_magnet", new PolorMagnetItem(nonStackable()));
    public static final ElectromagnetItem ELECTROMAGNET_ITEM = register("electromagnet", new ElectromagnetItem(nonStackable(12 * 64)));
    public static final PermanentMagnetItem PERMANENT_MAGNET_ITEM = register("permanent_magnet", new PermanentMagnetItem(nonStackable()));
    public static final CreatureMagnetItem CREATURE_MAGNET_ITEM = register("creature_magnet", new CreatureMagnetItem(nonStackable(300)));
    public static final MineralMagnetItem MINERAL_MAGNET_ITEM = register("mineral_magnet", new MineralMagnetItem(nonStackable(9 * 64)));
    public static final CropMagnetItem CROP_MAGNET_ITEM = register("crop_magnet", new CropMagnetItem(nonStackable(32 * 64)));
    public static final AdsorptionMagnetItem ADSORPTION_MAGNET_ITEM = register("adsorption_magnet", new AdsorptionMagnetItem(nonStackable(5 * 64)));

    //控制器
    public static final MagnetControllerItem MAGNET_CONTROLLER_ITEM = register("magnet_controller", new MagnetControllerItem(nonStackable(100)));
    public static final PortableDemagnetizerItem PORTABLE_DEMAGNETIZER_ITEM = register("portable_demagnetizer", new PortableDemagnetizerItem(nonStackable()));

    //磁吸盘
    public static final Item MAGNETIC_SUCKER_CORE_ITEM = registerItem("magnetic_sucker_core", 1);
    public static final SmallMagneticSuckerItem SMALL_MAGNETIC_SUCKER_ITEM = register("small_magnetic_sucker", new SmallMagneticSuckerItem(nonStackable(100)));
    public static final LargeMagneticSuckerItem LARGE_MAGNETIC_SUCKER_ITEM = register("large_magnetic_sucker", new LargeMagneticSuckerItem(nonStackable(300).fireproof()));

    //磁铁工具
    public static final SwordItem MAGNETIC_IRON_SWORD = register("magnetic_iron_sword", new SwordItem(MagneticIronToolMaterial.INSTANCE, 3, -2.4f, settings()));
    public static final CustomPickaxeItem MAGNETIC_IRON_PICKAXE = register("magnetic_iron_pickaxe", new CustomPickaxeItem(MagneticIronToolMaterial.INSTANCE, 1, -2.8f, settings()));
    public static final CustomAxeItem MAGNETIC_IRON_AXE = register("magnetic_iron_axe", new CustomAxeItem(MagneticIronToolMaterial.INSTANCE, 5.5f, -3.0f, settings()));
    public static final CustomShovelItem MAGNETIC_IRON_SHOVEL = register("magnetic_iron_shovel", new CustomShovelItem(MagneticIronToolMaterial.INSTANCE, 1.5f, -3.0f, settings()));
    public static final CustomHoeItem MAGNETIC_IRON_HOE = register("magnetic_iron_hoe", new CustomHoeItem(MagneticIronToolMaterial.INSTANCE, -2, -1.0f, settings()));

    //磁扳手
    public static final MagneticWrenchItem MAGNETIC_WRENCH_ITEM = register("magnetic_wrench", new MagneticWrenchItem(settings()));

    //下界磁铁工具
    public static final SwordItem NETHERITE_MAGNETIC_IRON_SWORD = register("netherite_magnetic_iron_sword", new SwordItem(NetheriteMagneticIronToolMaterial.INSTANCE, 4, -2.4f, fireproof()));
    public static final CustomPickaxeItem NETHERITE_MAGNETIC_IRON_PICKAXE = register("netherite_magnetic_iron_pickaxe", new CustomPickaxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2, -2.8f, fireproof()));
    public static final CustomAxeItem NETHERITE_MAGNETIC_IRON_AXE = register("netherite_magnetic_iron_axe", new CustomAxeItem(NetheriteMagneticIronToolMaterial.INSTANCE, 6.0f, -3.0f, fireproof()));
    public static final CustomShovelItem NETHERITE_MAGNETIC_IRON_SHOVEL = register("netherite_magnetic_iron_shovel", new CustomShovelItem(NetheriteMagneticIronToolMaterial.INSTANCE, 2.5f, -3.0f, fireproof()));
    public static final CustomHoeItem NETHERITE_MAGNETIC_IRON_HOE = register("netherite_magnetic_iron_hoe", new CustomHoeItem(NetheriteMagneticIronToolMaterial.INSTANCE, -3, 0.0f, fireproof()));

    //磁铁套装
    public static final MagneticIronArmorMaterial MAGNETIC_IRON_MATERIAL = new MagneticIronArmorMaterial();
    public static final MagneticIronArmorItem MAGNETIC_IRON_HELMET = register("magnetic_iron_helmet", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, ArmorItem.Type.HELMET, settings()));
    public static final MagneticIronArmorItem MAGNETIC_IRON_CHESTPLATE = register("magnetic_iron_chestplate", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, ArmorItem.Type.CHESTPLATE, settings()));
    public static final MagneticIronArmorItem MAGNETIC_IRON_LEGGINGS = register("magnetic_iron_leggings", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, ArmorItem.Type.LEGGINGS, settings()));
    public static final MagneticIronArmorItem MAGNETIC_IRON_BOOTS = register("magnetic_iron_boots", new MagneticIronArmorItem(MAGNETIC_IRON_MATERIAL, ArmorItem.Type.BOOTS, settings()));

    //下界磁铁套装
    public static final NetheriteMagneticIronArmorMaterial NETHERITE_MAGNETIC_IRON_MATERIAL = new NetheriteMagneticIronArmorMaterial();
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_HELMET = register("netherite_magnetic_iron_helmet", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, ArmorItem.Type.HELMET, fireproof()));
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_CHESTPLATE = register("netherite_magnetic_iron_chestplate", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, ArmorItem.Type.CHESTPLATE, fireproof()));
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_LEGGINGS = register("netherite_magnetic_iron_leggings", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, ArmorItem.Type.LEGGINGS, fireproof()));
    public static final NetheriteMagneticIronArmorItem NETHERITE_MAGNETIC_IRON_BOOTS = register("netherite_magnetic_iron_boots", new NetheriteMagneticIronArmorItem(NETHERITE_MAGNETIC_IRON_MATERIAL, ArmorItem.Type.BOOTS, fireproof()));

    //磁铁马铠
    public static final HorseArmorItem MAGNETIC_IRON_HORSE_ARMOR = register("magnetic_iron_horse_armor", new HorseArmorItem(9, "magnetic_iron", nonStackable()));

    static FabricItemSettings settings() {
        return new FabricItemSettings();
    }

    static FabricItemSettings fireproof() {
        return settings().fireproof();
    }
    
    public static void register() {
        addSpecialStackInGroup();
        MagnetCraft.LOGGER.info("ItemRegistries.class Loaded");
    }

    private static void addSpecialStackInGroup() {
        ItemGroupEvents.modifyEntriesEvent(MAGNET_ITEMS).register(content -> content.addAfter(MINERAL_MAGNET_ITEM, MineralMagnetItem.getAllCoresStack()));
    }

    static <T extends Item> T register(String id, T item) {
        ItemGroupEvents.modifyEntriesEvent(MAGNET_ITEMS).register(content -> content.add(item.getDefaultStack()));
        return Registry.register(Registries.ITEM, id(id), item);
    }

    static <T extends Item> T registerBlockItem(String id, T blockItem) {
        ItemGroupEvents.modifyEntriesEvent(MAGNET_BLOCKS).register(content -> content.add(blockItem.getDefaultStack()));
        return Registry.register(Registries.ITEM, id(id), blockItem);
    }

    static Item registerItem(String id) {
        Item item = new Item(settings());
        return register(id, item);
    }

    static Item registerItem(String id, int maxCount) {
        Item item = new Item(settings().maxCount(maxCount));
        return register(id, item);
    }

    static CraftingModuleItem registerModule(String id) {
        CraftingModuleItem item = new CraftingModuleItem(settings().maxCount(16));
        return register(id, item);
    }

    static FabricItemSettings nonStackable() {
        return settings().maxCount(1);
    }
    
    static FabricItemSettings nonStackable(int maxDamage) {
        return settings().maxCount(1).maxDamage(maxDamage);
    }

}
