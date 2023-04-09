package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.blocks.*;
import com.imoonday.magnetcraft.common.blocks.entities.*;
import com.imoonday.magnetcraft.common.blocks.maglev.*;
import com.imoonday.magnetcraft.common.items.armors.MagneticShulkerBackpackItem;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
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

import java.util.ArrayList;
import java.util.Map;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BlockRegistries {

    private static final ArrayList<Block> BLOCKS = new ArrayList<>();

    public static final Block MAGNETITE_BLOCK = register("magnetite", blockCopy(Blocks.IRON_ORE));
    public static final RegistryKey<PlacedFeature> ORE_MAGNETITE_MIDDLE_PLACED_KEY = registerFeature("ore_magnetite_middle");
    public static final RegistryKey<PlacedFeature> ORE_MAGNETITE_SMALL_PLACED_KEY = registerFeature("ore_magnetite_small");
    public static final RegistryKey<PlacedFeature> ORE_MAGNETITE_UPPER_PLACED_KEY = registerFeature("ore_magnetite_upper");
    public static final Block DEEPSLATE_MAGNETITE_BLOCK = register("deepslate_magnetite", blockCopy(Blocks.DEEPSLATE_IRON_ORE));
    public static final Block MAGNET_BLOCK = register("magnet_block", blockCopy(Blocks.IRON_BLOCK));
    public static final Block NETHERITE_MAGNET_BLOCK = register("netherite_magnet_block", blockCopy(Blocks.NETHERITE_BLOCK));
    public static final Block RAW_MAGNET_BLOCK = register("raw_magnet_block", blockCopy(Blocks.RAW_IRON_BLOCK));
    public static final LodestoneBlock LODESTONE_BLOCK = register("lodestone", new LodestoneBlock(settingCopy(Blocks.LODESTONE)));
    public static final BlockEntityType<LodestoneEntity> LODESTONE_ENTITY = registerBlockEntity("lodestone", LodestoneEntity::new, LODESTONE_BLOCK);
    public static final MaglevRailBlock MAGLEV_RAIL_BLOCK = register("maglev_rail", new MaglevRailBlock(settingCopy(Blocks.RAIL)));
    public static final MaglevPoweredRailBlock MAGLEV_POWERED_RAIL_BLOCK = register("maglev_powered_rail", new MaglevPoweredRailBlock(settingCopy(Blocks.POWERED_RAIL)));
    public static final MaglevDetectorRailBlock MAGLEV_DETECTOR_RAIL_BLOCK = register("maglev_detector_rail", new MaglevDetectorRailBlock(settingCopy(Blocks.DETECTOR_RAIL)));
    public static final MaglevPoweredRailBlock MAGLEV_ACTIVATOR_RAIL_BLOCK = register("maglev_activator_rail", new MaglevPoweredRailBlock(settingCopy(Blocks.ACTIVATOR_RAIL)));
    public static final MaglevLeverBlock MAGLEV_LEVER_BLOCK = register("maglev_lever", new MaglevLeverBlock(settingCopyNoCollision(Blocks.LEVER)));
    public static final MaglevButtonBlock MAGLEV_STONE_BUTTON_BLOCK = register("maglev_stone_button", new MaglevButtonBlock(settingCopyNoCollision(Blocks.STONE_BUTTON), BlockSetType.STONE, 20, false));
    public static final MaglevButtonBlock MAGLEV_OAK_BUTTON_BLOCK = register("maglev_oak_button", new MaglevButtonBlock(settingCopyNoCollision(Blocks.STONE_BUTTON), BlockSetType.OAK, 30, true));
    public static final MaglevDoorBlock MAGLEV_IRON_DOOR_BLOCK = register("maglev_iron_door", new MaglevDoorBlock(settingCopyNonOpaque(Blocks.IRON_DOOR), BlockSetType.IRON));
    public static final MaglevDoorBlock MAGLEV_OAK_DOOR_BLOCK = register("maglev_oak_door", new MaglevDoorBlock(settingCopyNonOpaque(Blocks.OAK_DOOR), BlockSetType.OAK));
    public static final MaglevRepeaterBlock MAGLEV_REPEATER_BLOCK = register("maglev_repeater", new MaglevRepeaterBlock(settingCopyNonOpaque(Blocks.REPEATER)));
    public static final MaglevComparatorBlock MAGLEV_COMPARATOR_BLOCK = register("maglev_comparator", new MaglevComparatorBlock(settingCopyNonOpaque(Blocks.COMPARATOR)));
    public static final MagneticPressurePlateBlock MAGNETIC_PRESSURE_PLATE = register("magnetic_pressure_plate", new MagneticPressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.METAL).requiresTool().noCollision().strength(0.5f).sounds(BlockSoundGroup.METAL), BlockSetType.IRON));
    public static final DemagnetizerBlock DEMAGNETIZER_BLOCK = register("demagnetizer", new DemagnetizerBlock(settingCopy(MAGNET_BLOCK)));
    public static final BlockEntityType<DemagnetizerEntity> DEMAGNETIZER_ENTITY = registerBlockEntity("demagnetizer", DemagnetizerEntity::new, DEMAGNETIZER_BLOCK);
    public static final AttractSensorBlock ATTRACT_SENSOR_BLOCK = register("attract_sensor", new AttractSensorBlock(settingCopy(MAGNET_BLOCK)));
    public static final BlockEntityType<AttractSensorEntity> ATTRACT_SENSOR_ENTITY = registerBlockEntity("attract_sensor", AttractSensorEntity::new, ATTRACT_SENSOR_BLOCK);
    public static final AdvancedGrindstoneBlock ADVANCED_GRINDSTONE_BLOCK = register("advanced_grindstone", new AdvancedGrindstoneBlock(settingCopy(Blocks.GRINDSTONE)));
    public static final VerticalRepeaterBlock VERTICAL_REPEATER_BLOCK = register("vertical_repeater", new VerticalRepeaterBlock(settingCopy(Blocks.OBSERVER)));
    public static final CircularRepeaterBlock CIRCULAR_REPEATER_BLOCK = register("circular_repeater", new CircularRepeaterBlock(settingCopy(Blocks.REPEATER)));
    public static final ElectromagneticRelayBlock ELECTROMAGNETIC_RELAY_BLOCK = register("electromagnetic_relay", new ElectromagneticRelayBlock(settingCopy(Blocks.COMPARATOR)));
    public static final MagneticAntennaBlock MAGNETIC_ANTENNA_BLOCK = register("magnetic_antenna", new MagneticAntennaBlock(settingCopy(Blocks.LIGHTNING_ROD).luminance(MagneticAntennaBlock.getLuminance())));
    public static final Map<Item, CauldronBehavior> MAGNETIC_FLUID_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final Block MAGNETIC_FLUID_CAULDRON = registerBlock("magnetic_fluid_cauldron", new LeveledCauldronBlock(settingCopy(Blocks.CAULDRON), precipitation -> false, MAGNETIC_FLUID_CAULDRON_BEHAVIOR));
    public static final MagneticShulkerBackpackBlock MAGNETIC_SHULKER_BACKPACK_BLOCK = registerBlock("magnetic_shulker_backpack", new MagneticShulkerBackpackBlock(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static final BlockEntityType<MagneticShulkerBackpackEntity> MAGNETIC_SHULKER_BACKPACK_ENTITY = registerBlockEntity("magnetic_shulker_backpack", MagneticShulkerBackpackEntity::new, MAGNETIC_SHULKER_BACKPACK_BLOCK);
    public static final MagneticShulkerBackpackItem MAGNETIC_SHULKER_BACKPACK_ITEM = registerItemWithBlock("magnetic_shulker_backpack", new MagneticShulkerBackpackItem(ItemRegistries.nonStackable()));
    public static final MagneticFilterLayerBlock MAGNETIC_FILTER_LAYER_BLOCK = register("magnetic_filter_layer", new MagneticFilterLayerBlock(settingCopyNoCollision(Blocks.GLASS_PANE)));
    public static final GlassBlock MAGNETIC_FILTER_GlASS_BLOCK = register("magnetic_filter_glass", new GlassBlock(settingCopy(Blocks.GLASS)));
    public static final ElectromagneticShuttleBlock ELECTROMAGNETIC_SHUTTLE_BLOCK = register("electromagnetic_shuttle", new ElectromagneticShuttleBlock(settingCopy(Blocks.OBSIDIAN)));
    public static final BlockEntityType<ElectromagneticShuttleEntity> ELECTROMAGNETIC_SHUTTLE_ENTITY = registerBlockEntity("electromagnetic_shuttle", ElectromagneticShuttleEntity::new, ELECTROMAGNETIC_SHUTTLE_BLOCK);

    public static void register() {
        registerCauldronBehavior();
        MagnetCraft.LOGGER.info("BlockRegistries.class Loaded");
    }

    private static void registerCauldronBehavior() {
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
    }

    static RegistryKey<PlacedFeature> registerFeature(String id) {
        RegistryKey<PlacedFeature> key = RegistryKey.of(RegistryKeys.PLACED_FEATURE, id(id));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key);
        return key;
    }

    static <T extends Block> T register(String id, T block) {
        registerBlockItem(id, new BlockItem(block, ItemRegistries.settings()));
        return registerBlock(id, block);
    }

    static <T extends Block> T registerBlock(String id, T block) {
        if (!block.getDefaultState().isOpaque()) {
            BLOCKS.add(block);
        }
        return Registry.register(Registries.BLOCK, id(id), block);
    }

    static <T extends BlockItem> T registerBlockItem(String id, T blockItem) {
        return ItemRegistries.registerBlockItem(id, blockItem);
    }

    @SuppressWarnings("SameParameterValue")
    static <T extends BlockItem> T registerItemWithBlock(String id, T blockItem) {
        return ItemRegistries.registerItemWithBlock(id, blockItem);
    }

    static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id(id), FabricBlockEntityTypeBuilder.create(factory, block).build());
    }

    static Block blockCopy(Block block) {
        return new Block(settingCopy(block));
    }

    static AbstractBlock.Settings settingCopy(Block block) {
        return FabricBlockSettings.copy(block);
    }


    static AbstractBlock.Settings settingCopyNonOpaque(Block block) {
        return FabricBlockSettings.copy(block).nonOpaque();
    }

    static AbstractBlock.Settings settingCopyNoCollision(Block block) {
        return FabricBlockSettings.copy(block).noCollision();
    }

    public static void registerClient() {
        BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout()));
    }
}