package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.blocks.*;
import com.imoonday.magnetcraft.common.blocks.entities.AttractSensorEntity;
import com.imoonday.magnetcraft.common.blocks.entities.DemagnetizerEntity;
import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class BlockRegistries {

    public static final Block MAGNETITE_BLOCK = register("magnetite", new Block(FabricBlockSettings.copy(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_MAGNETITE_BLOCK = register("deepslate_magnetite", new Block(FabricBlockSettings.copy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block MAGNET_BLOCK = register("magnet_block", new Block(FabricBlockSettings.copy(Blocks.IRON_BLOCK)));
    public static final Block NETHERITE_MAGNET_BLOCK = register("netherite_magnet_block", new Block(FabricBlockSettings.copy(Blocks.NETHERITE_BLOCK)));
    public static final Block RAW_MAGNET_BLOCK = register("raw_magnet_block", new Block(FabricBlockSettings.copy(Blocks.RAW_IRON_BLOCK)));
    public static final LodestoneBlock LODESTONE_BLOCK = register("lodestone", new LodestoneBlock(FabricBlockSettings.copy(Blocks.LODESTONE)));
    public static final BlockEntityType<LodestoneEntity> LODESTONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("lodestone"), FabricBlockEntityTypeBuilder.create(LodestoneEntity::new, LODESTONE_BLOCK).build());
    public static final MaglevRailBlock MAGLEV_RAIL_BLOCK = register("maglev_rail", new MaglevRailBlock(FabricBlockSettings.copy(Blocks.RAIL).nonOpaque()));
    public static final MaglevPoweredRailBlock MAGLEV_POWERED_RAIL_BLOCK = register("maglev_powered_rail", new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.POWERED_RAIL).nonOpaque()));
    public static final MaglevDetectorRailBlock MAGLEV_DETECTOR_RAIL_BLOCK = register("maglev_detector_rail", new MaglevDetectorRailBlock(FabricBlockSettings.copy(Blocks.DETECTOR_RAIL).nonOpaque()));
    public static final MaglevPoweredRailBlock MAGLEV_ACTIVATOR_RAIL_BLOCK = register("maglev_activator_rail", new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.ACTIVATOR_RAIL).nonOpaque()));
    public static final MaglevLever MAGLEV_LEVER_BLOCK = register("maglev_lever", new MaglevLever(FabricBlockSettings.copy(Blocks.LEVER).nonOpaque()));
    public static final MagneticPressurePlateBlock MAGNETIC_PRESSURE_PLATE = register("magnetic_pressure_plate", new MagneticPressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.METAL).requiresTool().noCollision().strength(0.5f).sounds(BlockSoundGroup.METAL), SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON));
    public static final DemagnetizerBlock DEMAGNETIZER_BLOCK = register("demagnetizer", new DemagnetizerBlock(FabricBlockSettings.copy(MAGNET_BLOCK)));
    public static final BlockEntityType<DemagnetizerEntity> DEMAGNETIZER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("demagnetizer"), FabricBlockEntityTypeBuilder.create(DemagnetizerEntity::new, DEMAGNETIZER_BLOCK).build());
    public static final AttractSensorBlock ATTRACT_SENSOR_BLOCK = register("attract_sensor", new AttractSensorBlock(FabricBlockSettings.copy(MAGNET_BLOCK)));
    public static final BlockEntityType<AttractSensorEntity> ATTRACT_SENSOR_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("attract_sensor"), FabricBlockEntityTypeBuilder.create(AttractSensorEntity::new, ATTRACT_SENSOR_BLOCK).build());
    public static final AdvancedGrindstoneBlock ADVANCED_GRINDSTONE_BLOCK = register("advanced_grindstone",new AdvancedGrindstoneBlock(FabricBlockSettings.copy(Blocks.GRINDSTONE)));

    public static void register() {
//        Registry.register(Registries.BLOCK, id("magnetite"), MAGNETITE_BLOCK);
//        Registry.register(Registries.BLOCK, id("deepslate_magnetite"), DEEPSLATE_MAGNETITE_BLOCK);
//        Registry.register(Registries.BLOCK, id("magnet_block"), MAGNET_BLOCK);
//        Registry.register(Registries.BLOCK, id("netherite_magnet_block"), NETHERITE_MAGNET_BLOCK);
//        Registry.register(Registries.BLOCK, id("raw_magnet_block"), RAW_MAGNET_BLOCK);
//        Registry.register(Registries.BLOCK, id("lodestone"), LODESTONE_BLOCK);
//        Registry.register(Registries.BLOCK, id("maglev_rail"), MAGLEV_RAIL_BLOCK);
//        Registry.register(Registries.BLOCK, id("maglev_powered_rail"), MAGLEV_POWERED_RAIL_BLOCK);
//        Registry.register(Registries.BLOCK, id("maglev_detector_rail"), MAGLEV_DETECTOR_RAIL_BLOCK);
//        Registry.register(Registries.BLOCK, id("maglev_activator_rail"), MAGLEV_ACTIVATOR_RAIL_BLOCK);
//        Registry.register(Registries.BLOCK, id("maglev_lever"), MAGLEV_LEVER_BLOCK);
//        Registry.register(Registries.BLOCK, id("magnetic_pressure_plate"), MAGNETIC_PRESSURE_PLATE);
//        Registry.register(Registries.BLOCK, id("demagnetizer"), DEMAGNETIZER_BLOCK);
//        Registry.register(Registries.BLOCK, id("attract_sensor"), ATTRACT_SENSOR_BLOCK);
//
//        Registry.register(Registries.ITEM, id("magnetite"), new BlockItem(MAGNETITE_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("deepslate_magnetite"), new BlockItem(DEEPSLATE_MAGNETITE_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("magnet_block"), new BlockItem(MAGNET_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("netherite_magnet_block"), new BlockItem(NETHERITE_MAGNET_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("raw_magnet_block"), new BlockItem(RAW_MAGNET_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("lodestone"), new LodestoneBlockItem(LODESTONE_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("maglev_rail"), new BlockItem(MAGLEV_RAIL_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("maglev_powered_rail"), new BlockItem(MAGLEV_POWERED_RAIL_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("maglev_detector_rail"), new BlockItem(MAGLEV_DETECTOR_RAIL_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("maglev_activator_rail"), new BlockItem(MAGLEV_ACTIVATOR_RAIL_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("maglev_lever"), new BlockItem(MAGLEV_LEVER_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("magnetic_pressure_plate"), new BlockItem(MAGNETIC_PRESSURE_PLATE, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("demagnetizer"), new BlockItem(DEMAGNETIZER_BLOCK, new FabricItemSettings()));
//        Registry.register(Registries.ITEM, id("attract_sensor"), new BlockItem(ATTRACT_SENSOR_BLOCK, new FabricItemSettings()));
    }

    private static <T extends Block> T register(String id, T block) {
        Registry.register(Registries.BLOCK, id(id), block);
        Registry.register(Registries.ITEM, id(id), new BlockItem(block, new FabricItemSettings()));
        return block;
    }

    public static void registerClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_POWERED_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_DETECTOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_ACTIVATOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_LEVER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGNETIC_PRESSURE_PLATE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.ADVANCED_GRINDSTONE_BLOCK, RenderLayer.getCutout());
    }
}