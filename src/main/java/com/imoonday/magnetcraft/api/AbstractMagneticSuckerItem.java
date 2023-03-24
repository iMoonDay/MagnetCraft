package com.imoonday.magnetcraft.api;

import com.imoonday.magnetcraft.MagnetCraft;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author iMoonDay
 */
public abstract class AbstractMagneticSuckerItem extends Item {

    public static final String BLOCK_ENTITY_TAG_KEY = "BlockEntityTag";
    public static final String BLOCK_STATE_TAG_KEY = "BlockStateTag";
    public static final String TAG = "tag";
    public static final String BLOCK = "Block";
    public static final String FACING = "facing";
    public static final String POWER = "power";

    public AbstractMagneticSuckerItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    @NotNull
    protected static NbtCompound getItemNbt(World world, BlockPos pos, BlockState state, ItemStack blockStack) {
        NbtCompound itemNbt = blockStack.writeNbt(new NbtCompound());
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            NbtCompound tag = new NbtCompound();
            tag.put(BLOCK_ENTITY_TAG_KEY, blockEntity.createNbtWithId());
            itemNbt.put(TAG, tag);
        }
        Collection<Property<?>> properties = state.getProperties();
        if (!properties.isEmpty()) {
            NbtCompound tag = Optional.ofNullable(itemNbt.getCompound(TAG)).orElse(new NbtCompound());
            NbtCompound nbt = new NbtCompound();
            properties.forEach(property -> nbt.putString(property.getName(), state.get(property).toString()));
            tag.put(BLOCK_STATE_TAG_KEY, nbt);
            itemNbt.put(TAG, tag);
        }
        return itemNbt;
    }

    protected static void breakBlock(ItemStack stack, World world, PlayerEntity player, BlockPos pos, BlockState state, Block block) {
        world.removeBlockEntity(pos);
        if (block instanceof DoorBlock) {
            world.breakBlock(state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos, false, player);
        } else if (block instanceof BedBlock) {
            world.breakBlock(state.get(BedBlock.PART) == BedPart.FOOT ? pos.offset(state.get(BedBlock.FACING)) : pos, false, player);
        } else {
            world.breakBlock(pos, false, player);
        }
        BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        world.playSound(player, pos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
        if (!player.getAbilities().creativeMode) {
            MagnetCraft.DamageMethods.addDamage(stack, player.getRandom(), 1, true);
        }
    }

    protected static boolean cannotBreak(PlayerEntity player, World world, BlockPos placePos) {
        return world.getBlockState(placePos).getHardness(world, placePos) == -1.0f && !player.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Nullable
    protected BlockState getPlacementState(Block block, ItemPlacementContext context) {
        BlockState blockState = block.getPlacementState(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        PlayerEntity playerEntity = context.getPlayer();
        ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
        return (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected ActionResult place(ItemStack stack, PlayerEntity player, World world, ItemStack blockItemStack, BlockPos placePos, BlockState state, boolean towards, boolean copy) {
        state = placeFromNbt(placePos, world, blockItemStack, state, towards, copy);
        postPlacement(placePos, world, player, blockItemStack);
        state.getBlock().onPlaced(world, placePos, state, player, blockItemStack);
        if (player instanceof ServerPlayerEntity) {
            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) player, placePos, blockItemStack);
        }
        stack.getOrCreateNbt().remove(BLOCK);
        BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        world.playSound(player, placePos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
        world.emitGameEvent(GameEvent.BLOCK_PLACE, placePos, GameEvent.Emitter.of(player, state));
        return ActionResult.SUCCESS;
    }

    protected boolean tryPlaceFailed(ItemPlacementContext context, BlockState state) {
        return !context.getWorld().getBlockState(context.getBlockPos()).isAir() || !context.getWorld().setBlockState(context.getBlockPos(), state, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
    }

    protected void postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack) {
        BlockItem.writeNbtToBlockEntity(world, player, pos, stack);
    }

    protected BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state, boolean towards, boolean copy) {
        BlockState blockState = state;
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound(BLOCK_STATE_TAG_KEY);
            StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateManager();
            for (String string : nbtCompound2.getKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property == null) {
                    continue;
                }
                String name = property.getName();
                if (towards) {
                    //玩家朝向
                    if (FACING.equals(name)) {
                        continue;
                    }
                }
                if (!copy) {
                    //基本复制
                    if (!FACING.equals(name)) {
                        if (!(property instanceof IntProperty) || POWER.equals(name)) {
                            continue;
                        }
                    }
                }
                String string2 = Objects.requireNonNull(nbtCompound2.get(string)).asString();
                blockState = with(blockState, property, string2);
            }
        }
        if (blockState != state) {
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        }
        return blockState;
    }

    protected static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return property.parse(name).map(value -> state.with(property, value)).orElse(state);
    }

}
