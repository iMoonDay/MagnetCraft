package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.blocks.*;
import com.imoonday.magnetcraft.common.blocks.entities.AttractSensorEntity;
import com.imoonday.magnetcraft.common.blocks.entities.DemagnetizerEntity;
import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import com.imoonday.magnetcraft.common.blocks.maglev.*;
import com.imoonday.magnetcraft.common.items.LodestoneBlockItem;
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

@SuppressWarnings("unused")
public class BlockRegistries {

    public static final Block MAGNETITE_BLOCK = register("magnetite", new Block(FabricBlockSettings.copy(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_MAGNETITE_BLOCK = register("deepslate_magnetite", new Block(FabricBlockSettings.copy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block MAGNET_BLOCK = register("magnet_block", new Block(FabricBlockSettings.copy(Blocks.IRON_BLOCK)));
    public static final Block NETHERITE_MAGNET_BLOCK = register("netherite_magnet_block", new Block(FabricBlockSettings.copy(Blocks.NETHERITE_BLOCK)));
    public static final Block RAW_MAGNET_BLOCK = register("raw_magnet_block", new Block(FabricBlockSettings.copy(Blocks.RAW_IRON_BLOCK)));
    public static final LodestoneBlock LODESTONE_BLOCK = registerBlock("lodestone", new LodestoneBlock(FabricBlockSettings.copy(Blocks.LODESTONE)));
    public static final LodestoneBlockItem LODESTONE_BLOCK_ITEM = registerBlockItem("lodestone",new LodestoneBlockItem(LODESTONE_BLOCK,new FabricItemSettings()));
    public static final BlockEntityType<LodestoneEntity> LODESTONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("lodestone"), FabricBlockEntityTypeBuilder.create(LodestoneEntity::new, LODESTONE_BLOCK).build());
    public static final MaglevRailBlock MAGLEV_RAIL_BLOCK = register("maglev_rail", new MaglevRailBlock(FabricBlockSettings.copy(Blocks.RAIL).nonOpaque()));
    public static final MaglevPoweredRailBlock MAGLEV_POWERED_RAIL_BLOCK = register("maglev_powered_rail", new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.POWERED_RAIL).nonOpaque()));
    public static final MaglevDetectorRailBlock MAGLEV_DETECTOR_RAIL_BLOCK = register("maglev_detector_rail", new MaglevDetectorRailBlock(FabricBlockSettings.copy(Blocks.DETECTOR_RAIL).nonOpaque()));
    public static final MaglevPoweredRailBlock MAGLEV_ACTIVATOR_RAIL_BLOCK = register("maglev_activator_rail", new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.ACTIVATOR_RAIL).nonOpaque()));
    public static final MaglevLeverBlock MAGLEV_LEVER_BLOCK = register("maglev_lever", new MaglevLeverBlock(FabricBlockSettings.copy(Blocks.LEVER).nonOpaque()));
    public static final MaglevButtonBlock MAGLEV_STONE_BUTTON_BLOCK = register("maglev_stone_button", new MaglevButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).nonOpaque(), 20, false, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final MaglevButtonBlock MAGLEV_OAK_BUTTON_BLOCK = register("maglev_oak_button", new MaglevButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).nonOpaque(), 30, true, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON));
    public static final MaglevDoorBlock MAGLEV_IRON_DOOR_BLOCK = register("maglev_iron_door", new MaglevDoorBlock(FabricBlockSettings.copy(Blocks.IRON_DOOR).nonOpaque(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN));
    public static final MaglevDoorBlock MAGLEV_OAK_DOOR_BLOCK = register("maglev_oak_door", new MaglevDoorBlock(FabricBlockSettings.copy(Blocks.OAK_DOOR).nonOpaque(), SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN));
    public static final MaglevRepeaterBlock MAGLEV_REPEATER_BLOCK = register("maglev_repeater", new MaglevRepeaterBlock(FabricBlockSettings.copy(Blocks.REPEATER).nonOpaque()));
    public static final MaglevComparatorBlock MAGLEV_COMPARATOR_BLOCK = register("maglev_comparator", new MaglevComparatorBlock(FabricBlockSettings.copy(Blocks.COMPARATOR).nonOpaque()));
    public static final MagneticPressurePlateBlock MAGNETIC_PRESSURE_PLATE = register("magnetic_pressure_plate", new MagneticPressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.METAL).requiresTool().noCollision().strength(0.5f).sounds(BlockSoundGroup.METAL), SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON));
    public static final DemagnetizerBlock DEMAGNETIZER_BLOCK = register("demagnetizer", new DemagnetizerBlock(FabricBlockSettings.copy(MAGNET_BLOCK)));
    public static final BlockEntityType<DemagnetizerEntity> DEMAGNETIZER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("demagnetizer"), FabricBlockEntityTypeBuilder.create(DemagnetizerEntity::new, DEMAGNETIZER_BLOCK).build());
    public static final AttractSensorBlock ATTRACT_SENSOR_BLOCK = register("attract_sensor", new AttractSensorBlock(FabricBlockSettings.copy(MAGNET_BLOCK)));
    public static final BlockEntityType<AttractSensorEntity> ATTRACT_SENSOR_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("attract_sensor"), FabricBlockEntityTypeBuilder.create(AttractSensorEntity::new, ATTRACT_SENSOR_BLOCK).build());
    public static final AdvancedGrindstoneBlock ADVANCED_GRINDSTONE_BLOCK = register("advanced_grindstone", new AdvancedGrindstoneBlock(FabricBlockSettings.copy(Blocks.GRINDSTONE)));

    public static void register() {
        MagnetCraft.LOGGER.info("BlockRegistries.class Loaded");
    }

    static <T extends Block> T register(String id, T block) {
        registerBlock(id,block);
        registerBlockItem(id,new BlockItem(block, new FabricItemSettings()));
        return block;
    }

    static <T extends Block> T registerBlock(String id, T block) {
        Registry.register(Registries.BLOCK, id(id), block);
        return block;
    }

    static <T extends BlockItem> T registerBlockItem(String id, T blockitem) {
        Registry.register(Registries.ITEM, id(id), blockitem);
        return blockitem;
    }

    public static void registerClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_POWERED_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_DETECTOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_ACTIVATOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_LEVER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGNETIC_PRESSURE_PLATE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.ADVANCED_GRINDSTONE_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_STONE_BUTTON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_OAK_BUTTON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_IRON_DOOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_OAK_DOOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_REPEATER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.MAGLEV_COMPARATOR_BLOCK, RenderLayer.getCutout());
    }
}