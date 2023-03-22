package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.MagnetCraft;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LargeMagneticSuckerItem extends Item {

    public LargeMagneticSuckerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player) || stack.getNbt() == null || !stack.getNbt().contains("Pos") || stack.getNbt().getIntArray("Pos").length != 3) {
            return stack;
        }
        BlockPos centerBlockPos = new BlockPos(stack.getOrCreateNbt().getIntArray("Pos")[0], stack.getOrCreateNbt().getIntArray("Pos")[1], stack.getOrCreateNbt().getIntArray("Pos")[2]);
        NbtList list = stack.getOrCreateNbt().getList("Blcoks", NbtElement.COMPOUND_TYPE);
        for (int i = 1; i >= -1; --i) {
            for (int j = 1; j >= -1; --j) {
                BlockPos pos = centerBlockPos.add(i, 0, j);
                BlockState state = world.getBlockState(pos);
                if (state.getHardness(world, centerBlockPos) == -1.0f && !player.isCreative()) {
                    continue;
                }
                if (state.isAir()) {
                    continue;
                }
                Block block = state.getBlock();
                ItemStack blockStack = new ItemStack(block);
                NbtCompound nbt = new NbtCompound();
                NbtCompound itemNbt = blockStack.writeNbt(new NbtCompound());
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    NbtCompound tag = new NbtCompound();
                    tag.put(BlockItem.BLOCK_ENTITY_TAG_KEY, blockEntity.createNbtWithId());
                    itemNbt.put("tag", tag);
                }
                nbt.putInt("OffsetX", i);
                nbt.putInt("OffsetZ", j);
                nbt.put("Block", itemNbt);
                list.add(nbt);
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
        }
        stack.getOrCreateNbt().put("Blocks", list);
        Direction horizontalFacing = player.getHorizontalFacing();
        stack.getOrCreateNbt().putInt("Direction", horizontalFacing.getId());
        stack.getNbt().remove("Pos");
        player.getInventory().markDirty();
        player.getItemCooldownManager().set(this, 20);
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (stack.getNbt() != null && stack.getNbt().contains("Pos")) {
            stack.getNbt().remove("Pos");
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        Hand hand = context.getHand();
        BlockPos blockPos = context.getBlockPos();
        Direction side = context.getSide();
        Vec3d hitPos = context.getHitPos();
        boolean inside = context.hitsInsideBlock();
        if (player == null) {
            return ActionResult.FAIL;
        }
        if (stack.getNbt() != null && stack.getNbt().contains("Blocks")) {
            List<NbtCompound> stackNbts = stack.getNbt().getList("Blocks", NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).toList();
            for (NbtCompound nbt : stackNbts) {
                Vec3i offset = new Vec3i(nbt.getInt("OffsetX"), 0, nbt.getInt("OffsetZ"));
                Direction originalDir = Direction.byId(stack.getOrCreateNbt().getInt("Direction"));
                Direction placeDir = player.getHorizontalFacing();
                BlockRotation rotation = BlockRotation.NONE;
                if (!originalDir.equals(Direction.UP) && !originalDir.equals(Direction.DOWN)) {
                    if (originalDir.rotateYClockwise().equals(placeDir)) {
                        rotation = BlockRotation.CLOCKWISE_90;
                    } else if (originalDir.rotateYCounterclockwise().equals(placeDir)) {
                        rotation = BlockRotation.COUNTERCLOCKWISE_90;
                    } else if (originalDir.getOpposite().equals(placeDir)) {
                        rotation = BlockRotation.CLOCKWISE_180;
                    }
                }
                ItemStack stackInMagnet = ItemStack.fromNbt(nbt.getCompound("Block"));
                Block blockFromItem = Block.getBlockFromItem(stackInMagnet.getItem());
                BlockPos offsetPos = BlockPos.ORIGIN.add(offset).rotate(rotation);
                BlockPos placePos = blockPos.offset(side).add(offsetPos);
                ItemPlacementContext ctx = new ItemPlacementContext(world, player, hand, stackInMagnet, new BlockHitResult(hitPos.add(Vec3d.of(offset)), Direction.UP, placePos, inside));
                BlockState state = blockFromItem.getPlacementState(ctx);
                if (state == null) {
                    state = blockFromItem.getDefaultState();
                }
                if (!world.getBlockState(placePos).isAir()) {
                    if (world.getBlockState(placePos).getHardness(world, placePos) == -1.0f && !player.isCreative()) {
                        player.getInventory().offerOrDrop(stackInMagnet);
                        continue;
                    }
                    world.breakBlock(placePos, true, player);
                }
                if (state.canPlaceAt(world, placePos) && (state.getCollisionShape(world, placePos).isEmpty() || world.getOtherEntities(null, new Box(placePos), entity -> entity instanceof LivingEntity livingEntity && !livingEntity.canAvoidTraps()).isEmpty())) {
                    world.setBlockState(placePos, state);
                    blockFromItem.onPlaced(world, placePos, state, player, stackInMagnet);
                    BlockItem.writeNbtToBlockEntity(world, player, placePos, stackInMagnet);
                    BlockSoundGroup blockSoundGroup = state.getSoundGroup();
                    world.playSound(player, placePos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, placePos, GameEvent.Emitter.of(player, state));
                }
            }
            stack.getNbt().remove("Blocks");
            return ActionResult.SUCCESS;
        } else {
            if (player.getAbilities().creativeMode || !MagnetCraft.DamageMethods.isEmptyDamage(stack)) {
                stack.getOrCreateNbt().putIntArray("Pos", new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                player.setCurrentHand(hand);
            }
        }
        return ActionResult.FAIL;
    }

}
