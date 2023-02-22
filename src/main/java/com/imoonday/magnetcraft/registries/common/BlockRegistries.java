package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.blocks.*;
import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
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
    public static final BlockEntityType<LodestoneEntity> LODESTONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("lodestone"), FabricBlockEntityTypeBuilder.create(LodestoneEntity::new, LODESTONE_BLOCK).build());
    public static final MaglevRailBlock MAGLEV_RAIL_BLOCK = new MaglevRailBlock(FabricBlockSettings.copy(Blocks.RAIL).nonOpaque());
    public static final MaglevPoweredRailBlock MAGLEV_POWERED_RAIL_BLOCK = new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.POWERED_RAIL).nonOpaque());
    public static final MaglevDetectorRailBlock MAGLEV_DETECTOR_RAIL_BLOCK = new MaglevDetectorRailBlock(FabricBlockSettings.copy(Blocks.DETECTOR_RAIL).nonOpaque());
    public static final MaglevPoweredRailBlock MAGLEV_ACTIVATOR_RAIL_BLOCK = new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.ACTIVATOR_RAIL).nonOpaque());
    public static final MaglevLever MAGLEV_LEVER_BLOCK = new MaglevLever(FabricBlockSettings.copy(Blocks.LEVER).nonOpaque());

    public static void register() {
        Registry.register(Registries.BLOCK, id("magnetite"), MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, id("deepslate_magnetite"), DEEPSLATE_MAGNETITE_BLOCK);
        Registry.register(Registries.BLOCK, id("magnet_block"), MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, id("netherite_magnet_block"), NETHERITE_MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, id("raw_magnet_block"), RAW_MAGNET_BLOCK);
        Registry.register(Registries.BLOCK, id("lodestone"), LODESTONE_BLOCK);
        Registry.register(Registries.BLOCK, id("maglev_rail"), MAGLEV_RAIL_BLOCK);
        Registry.register(Registries.BLOCK, id("maglev_powered_rail"), MAGLEV_POWERED_RAIL_BLOCK);
        Registry.register(Registries.BLOCK, id("maglev_detector_rail"), MAGLEV_DETECTOR_RAIL_BLOCK);
        Registry.register(Registries.BLOCK, id("maglev_activator_rail"), MAGLEV_ACTIVATOR_RAIL_BLOCK);
        Registry.register(Registries.BLOCK, id("maglev_lever"), MAGLEV_LEVER_BLOCK);
    }

    public static void registerClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_POWERED_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_DETECTOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_ACTIVATOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_LEVER_BLOCK, RenderLayer.getCutout());
    }
}