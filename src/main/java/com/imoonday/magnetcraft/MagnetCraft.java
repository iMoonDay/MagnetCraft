package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.effects.AttractEffect;
import com.imoonday.magnetcraft.effects.DegaussingEffect;
import com.imoonday.magnetcraft.enchantments.AttractEnchantment;
import com.imoonday.magnetcraft.items.ElectroMagnetItem;
import com.imoonday.magnetcraft.items.MagnetControllerItem;
import com.imoonday.magnetcraft.items.PermanentMagnetItem;
import com.imoonday.magnetcraft.items.PolorMagnetItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagnetCraft implements ModInitializer {
    public static final String MOD_ID = "magnetcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("magnetcraft");

    //调试输出模式
    public static final boolean TEST_MODE = true;

    //物品
    public static final Item RAW_MAGNET_ITEM = new Item(new FabricItemSettings().maxCount(64));
    public static final Item MAGNET_FRAGMENT_ITEM = new Item(new FabricItemSettings().maxCount(64));
    public static final Item MAGNET_TEMPLATE_ITEM = new Item(new FabricItemSettings().maxCount(64));
    public static final Item POLAR_MAGNET_ITEM = new PolorMagnetItem(new FabricItemSettings().maxCount(1));
    public static final Item ELECTROMAGNET_ITEM = new ElectroMagnetItem(new FabricItemSettings().maxCount(1).maxDamage(500));
    public static final Item PERMANENT_MAGNET_ITEM = new PermanentMagnetItem(new FabricItemSettings().maxCount(1));
    public static final Item MAGNET_CONTROLLER_ITEM = new MagnetControllerItem(new FabricItemSettings().maxCount(1).maxDamage(100));

    //方块
    public static final Block MAGNETITE_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).hardness(3.0f).requiresTool());
    public static final Block DEEPSLATE_MAGNETITE_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).hardness(4.5f).requiresTool());
    public static final Block MAGNET_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).hardness(5.0f).requiresTool());
    public static final Block RAW_MAGNET_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).hardness(5.0f).requiresTool());

    //效果
    public static final AttractEffect ATTRACT_EFFECT = new AttractEffect();
    public static final DegaussingEffect DEGAUSSING_EFFECT = new DegaussingEffect();

    //药水
    public static final Potion ATTRACT_POTION = new Potion(new StatusEffectInstance(ATTRACT_EFFECT, 5 * 60 * 20));
    public static final Potion DEGAUSSING_POTION = new Potion(new StatusEffectInstance(DEGAUSSING_EFFECT, 5 * 60 * 20));

    //附魔
    public static final Enchantment ATTRACT_ENCHANTMENT = new AttractEnchantment();

    //统计信息
    public static final Identifier ITEMS_TELEPORTED_TO_PLAYER = new Identifier(MOD_ID, "items_teleported_to_player");

    //分组
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(
                    MOD_ID, "magnet"))
            .displayName(Text.translatable("group.magnetcraft.magnet"))
            .icon(() -> new ItemStack(ELECTROMAGNET_ITEM))
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                entries.add(MAGNETITE_BLOCK);
                entries.add(DEEPSLATE_MAGNETITE_BLOCK);
                entries.add(RAW_MAGNET_ITEM);
                entries.add(RAW_MAGNET_BLOCK);
                entries.add(MAGNET_FRAGMENT_ITEM);
                entries.add(MAGNET_BLOCK);
                entries.add(MAGNET_TEMPLATE_ITEM);
                entries.add(POLAR_MAGNET_ITEM);
                entries.add(ELECTROMAGNET_ITEM);
                entries.add(PERMANENT_MAGNET_ITEM);
                entries.add(MAGNET_CONTROLLER_ITEM);

            })
            .build();

    @Override
    public void onInitialize() {
        //物品
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "raw_magnet"), RAW_MAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_fragment"), MAGNET_FRAGMENT_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_template"), MAGNET_TEMPLATE_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "polar_magnet"), POLAR_MAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "electromagnet"), ELECTROMAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "permanent_magnet"), PERMANENT_MAGNET_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_controller"), MAGNET_CONTROLLER_ITEM);
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnetite"), new BlockItem(MAGNETITE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "deepslate_magnetite"), new BlockItem(DEEPSLATE_MAGNETITE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "magnet_block"), new BlockItem(MAGNET_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(
                MOD_ID, "raw_magnet_block"), new BlockItem(RAW_MAGNET_BLOCK, new FabricItemSettings()));

        //方块
        Registry.register(Registries.BLOCK, new Identifier(
                MOD_ID, "magnetite"), MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(
                MOD_ID, "deepslate_magnetite"), DEEPSLATE_MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(
                MOD_ID, "magnet_block"), MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(
                MOD_ID, "raw_magnet_block"), RAW_MAGNET_BLOCK);

        //效果
        Registry.register(Registries.STATUS_EFFECT, new Identifier(
                MOD_ID, "attract"), ATTRACT_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(
                MOD_ID, "degaussing"), DEGAUSSING_EFFECT);

        //药水
        Registry.register(Registries.POTION, new Identifier(
                MOD_ID, "attract"), ATTRACT_POTION);
        Registry.register(Registries.POTION, new Identifier(
                MOD_ID, "degaussing"), DEGAUSSING_POTION);

        //附魔
        Registry.register(Registries.ENCHANTMENT, new Identifier(
                MOD_ID, "attract"), ATTRACT_ENCHANTMENT);

        //统计信息
        Registry.register(Registries.CUSTOM_STAT, "items_teleported_to_player", ITEMS_TELEPORTED_TO_PLAYER);
        Stats.CUSTOM.getOrCreateStat(ITEMS_TELEPORTED_TO_PLAYER, StatFormatter.DEFAULT);
    }
}