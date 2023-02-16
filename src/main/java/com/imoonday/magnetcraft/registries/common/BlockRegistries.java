package com.imoonday.magnetcraft.registries.common;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;

public class BlockRegistries {

public static final Block MAGNETITE_BLOCK = new Block(FabricBlockSettings.copy(Blocks.IRON_ORE));
    public static final Block DEEPSLATE_MAGNETITE_BLOCK = new Block(FabricBlockSettings.copy(Blocks.DEEPSLATE_IRON_ORE));
    public static final Block MAGNET_BLOCK = new Block(FabricBlockSettings.copy(Blocks.IRON_BLOCK));
    public static final Block NETHERITE_MAGNET_BLOCK = new Block(FabricBlockSettings.copy(Blocks.NETHERITE_BLOCK));
    public static final Block RAW_MAGNET_BLOCK = new Block(FabricBlockSettings.copy(Blocks.RAW_IRON_BLOCK));

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "magnetite"), MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "deepslate_magnetite"), DEEPSLATE_MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "magnet_block"), MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "netherite_magnet_block"), NETHERITE_MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "raw_magnet_block"), RAW_MAGNET_BLOCK);
    }
}