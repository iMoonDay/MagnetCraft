package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.blocks.*;
import com.imoonday.magnetcraft.common.blocks.entities.AttractSensorEntity;
import com.imoonday.magnetcraft.common.blocks.entities.DemagnetizerEntity;
import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import com.imoonday.magnetcraft.common.blocks.maglev.*;
import com.imoonday.magnetcraft.common.items.LodestoneBlockItem;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.Map;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

@SuppressWarnings("unused")
public class BlockRegistries {

    public static final Block MAGNETITE_BLOCK = register("magnetite", new Block(FabricBlockSettings.copy(Blocks.IRON_ORE)));
    public static final RegistryKey<PlacedFeature> ORE_MAGNETITE_MIDDLE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("ore_magnetite_middle"));
    public static final RegistryKey<PlacedFeature> ORE_MAGNETITE_SMALL_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("ore_magnetite_small"));
    public static final RegistryKey<PlacedFeature> ORE_MAGNETITE_UPPER_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("ore_magnetite_upper"));
    public static final Block DEEPSLATE_MAGNETITE_BLOCK = register("deepslate_magnetite", new Block(FabricBlockSettings.copy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block MAGNET_BLOCK = register("magnet_block", new Block(FabricBlockSettings.copy(Blocks.IRON_BLOCK)));
    public static final Block NETHERITE_MAGNET_BLOCK = register("netherite_magnet_block", new Block(FabricBlockSettings.copy(Blocks.NETHERITE_BLOCK)));
    public static final Block RAW_MAGNET_BLOCK = register("raw_magnet_block", new Block(FabricBlockSettings.copy(Blocks.RAW_IRON_BLOCK)));
    public static final LodestoneBlock LODESTONE_BLOCK = registerBlock("lodestone", new LodestoneBlock(FabricBlockSettings.copy(Blocks.LODESTONE)));
    public static final LodestoneBlockItem LODESTONE_BLOCK_ITEM = registerBlockItem("lodestone", new LodestoneBlockItem(LODESTONE_BLOCK, new FabricItemSettings()));
    public static final BlockEntityType<LodestoneEntity> LODESTONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("lodestone"), FabricBlockEntityTypeBuilder.create(LodestoneEntity::new, LODESTONE_BLOCK).build());
    public static final MaglevRailBlock MAGLEV_RAIL_BLOCK = register("maglev_rail", new MaglevRailBlock(FabricBlockSettings.copy(Blocks.RAIL).noCollision()));
    public static final MaglevPoweredRailBlock MAGLEV_POWERED_RAIL_BLOCK = register("maglev_powered_rail", new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.POWERED_RAIL).noCollision()));
    public static final MaglevDetectorRailBlock MAGLEV_DETECTOR_RAIL_BLOCK = register("maglev_detector_rail", new MaglevDetectorRailBlock(FabricBlockSettings.copy(Blocks.DETECTOR_RAIL).noCollision()));
    public static final MaglevPoweredRailBlock MAGLEV_ACTIVATOR_RAIL_BLOCK = register("maglev_activator_rail", new MaglevPoweredRailBlock(FabricBlockSettings.copy(Blocks.ACTIVATOR_RAIL).noCollision()));
    public static final MaglevLeverBlock MAGLEV_LEVER_BLOCK = register("maglev_lever", new MaglevLeverBlock(FabricBlockSettings.copy(Blocks.LEVER).noCollision()));
    public static final MaglevButtonBlock MAGLEV_STONE_BUTTON_BLOCK = register("maglev_stone_button", new MaglevButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).noCollision(), BlockSetType.STONE, 20, false));
    public static final MaglevButtonBlock MAGLEV_OAK_BUTTON_BLOCK = register("maglev_oak_button", new MaglevButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).noCollision(), BlockSetType.OAK, 30, true));
    public static final MaglevDoorBlock MAGLEV_IRON_DOOR_BLOCK = register("maglev_iron_door", new MaglevDoorBlock(FabricBlockSettings.copy(Blocks.IRON_DOOR).nonOpaque(), BlockSetType.IRON));
    public static final MaglevDoorBlock MAGLEV_OAK_DOOR_BLOCK = register("maglev_oak_door", new MaglevDoorBlock(FabricBlockSettings.copy(Blocks.OAK_DOOR).nonOpaque(), BlockSetType.OAK));
    public static final MaglevRepeaterBlock MAGLEV_REPEATER_BLOCK = register("maglev_repeater", new MaglevRepeaterBlock(FabricBlockSettings.copy(Blocks.REPEATER).nonOpaque()));
    public static final MaglevComparatorBlock MAGLEV_COMPARATOR_BLOCK = register("maglev_comparator", new MaglevComparatorBlock(FabricBlockSettings.copy(Blocks.COMPARATOR).nonOpaque()));
    public static final MagneticPressurePlateBlock MAGNETIC_PRESSURE_PLATE = register("magnetic_pressure_plate", new MagneticPressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.METAL).requiresTool().noCollision().strength(0.5f).sounds(BlockSoundGroup.METAL), BlockSetType.IRON));
    public static final DemagnetizerBlock DEMAGNETIZER_BLOCK = register("demagnetizer", new DemagnetizerBlock(FabricBlockSettings.copy(MAGNET_BLOCK)));
    public static final BlockEntityType<DemagnetizerEntity> DEMAGNETIZER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("demagnetizer"), FabricBlockEntityTypeBuilder.create(DemagnetizerEntity::new, DEMAGNETIZER_BLOCK).build());
    public static final AttractSensorBlock ATTRACT_SENSOR_BLOCK = register("attract_sensor", new AttractSensorBlock(FabricBlockSettings.copy(MAGNET_BLOCK)));
    public static final BlockEntityType<AttractSensorEntity> ATTRACT_SENSOR_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("attract_sensor"), FabricBlockEntityTypeBuilder.create(AttractSensorEntity::new, ATTRACT_SENSOR_BLOCK).build());
    public static final AdvancedGrindstoneBlock ADVANCED_GRINDSTONE_BLOCK = register("advanced_grindstone", new AdvancedGrindstoneBlock(FabricBlockSettings.copy(Blocks.GRINDSTONE)));
    public static final VerticalRepeaterBlock VERTICAL_REPEATER_BLOCK = register("vertical_repeater", new VerticalRepeaterBlock(FabricBlockSettings.copy(Blocks.OBSERVER)));
    public static final CircularRepeaterBlock CIRCULAR_REPEATER_BLOCK = register("circular_repeater", new CircularRepeaterBlock(FabricBlockSettings.copy(Blocks.REPEATER)));
    public static final ElectromagneticRelayBlock ELECTROMAGNETIC_RELAY_BLOCK = register("electromagnetic_relay", new ElectromagneticRelayBlock(FabricBlockSettings.copy(Blocks.COMPARATOR)));

    public static final Map<Item, CauldronBehavior> MAGNETIC_FLUID_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final Block MAGNETIC_FLUID_CAULDRON = registerBlock("magnetic_fluid_cauldron", new LeveledCauldronBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON), precipitation -> false, MAGNETIC_FLUID_CAULDRON_BEHAVIOR));

    public static void register() {
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            if (PotionUtil.getPotion(stack) != PotionRegistries.ATTRACT_POTION) {
                return ActionResult.PASS;
            }
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, MAGNETIC_FLUID_CAULDRON.getDefaultState());
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(FluidRegistries.MAGNETIC_FLUID_BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.fillCauldron(world, pos, player, hand, stack, MAGNETIC_FLUID_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY));
        MAGNETIC_FLUID_CAULDRON_BEHAVIOR.put(FluidRegistries.MAGNETIC_FLUID_BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.fillCauldron(world, pos, player, hand, stack, MAGNETIC_FLUID_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY));
        MAGNETIC_FLUID_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state2, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(state2, world, pos, player, hand, stack, new ItemStack(FluidRegistries.MAGNETIC_FLUID_BUCKET), state -> state.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
        MAGNETIC_FLUID_CAULDRON_BEHAVIOR.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionUtil.setPotion(new ItemStack(Items.POTION), PotionRegistries.ATTRACT_POTION)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.success(world.isClient);
        });
        MAGNETIC_FLUID_CAULDRON_BEHAVIOR.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            if (state.get(LeveledCauldronBlock.LEVEL) == 3 || PotionUtil.getPotion(stack) != PotionRegistries.ATTRACT_POTION) {
                return ActionResult.PASS;
            }
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                world.setBlockState(pos, state.cycle(LeveledCauldronBlock.LEVEL));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_MAGNETITE_SMALL_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_MAGNETITE_MIDDLE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_MAGNETITE_UPPER_PLACED_KEY);
        MagnetCraft.LOGGER.info("BlockRegistries.class Loaded");
    }

    static <T extends Block> T register(String id, T block) {
        registerBlock(id, block);
        registerBlockItem(id, new BlockItem(block, new FabricItemSettings()));
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
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_POWERED_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_DETECTOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_ACTIVATOR_RAIL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_LEVER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGNETIC_PRESSURE_PLATE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ADVANCED_GRINDSTONE_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_STONE_BUTTON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_OAK_BUTTON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_IRON_DOOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_OAK_DOOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_REPEATER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGLEV_COMPARATOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGNETIC_FLUID_CAULDRON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CIRCULAR_REPEATER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ELECTROMAGNETIC_RELAY_BLOCK, RenderLayer.getCutout());
    }
}