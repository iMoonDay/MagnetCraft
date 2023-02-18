package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.blocks.LodestoneBlock;
import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class BlockRegistries {

    public static final Block MAGNETITE_BLOCK = new Block(FabricBlockSettings.copy(Blocks.IRON_ORE));
    public static final Block DEEPSLATE_MAGNETITE_BLOCK = new Block(FabricBlockSettings.copy(Blocks.DEEPSLATE_IRON_ORE));
    public static final Block MAGNET_BLOCK = new Block(FabricBlockSettings.copy(Blocks.IRON_BLOCK));
    public static final Block NETHERITE_MAGNET_BLOCK = new Block(FabricBlockSettings.copy(Blocks.NETHERITE_BLOCK));
    public static final Block RAW_MAGNET_BLOCK = new Block(FabricBlockSettings.copy(Blocks.RAW_IRON_BLOCK));
    public static final LodestoneBlock LODESTONE_BLOCK = new LodestoneBlock(FabricBlockSettings.copy(Blocks.LODESTONE));
    public static final BlockEntityType<LodestoneEntity> LODESTONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("lodestone"), FabricBlockEntityTypeBuilder.create(LodestoneEntity::new,LODESTONE_BLOCK).build());

    public static void register() {
        Registry.register(Registries.BLOCK, id("magnetite"), MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, id("deepslate_magnetite"), DEEPSLATE_MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, id("magnet_block"), MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, id("netherite_magnet_block"), NETHERITE_MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, id("raw_magnet_block"), RAW_MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, id("lodestone"), LODESTONE_BLOCK);

    }
}