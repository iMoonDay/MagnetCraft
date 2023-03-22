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
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmallMagneticSuckerItem extends Item {
    public SmallMagneticSuckerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        NbtCompound block = stack.getSubNbt("Block");
        if (nbt != null && nbt.contains("Block") && block != null && block.contains("id")) {
            ItemStack stackInMagnet = ItemStack.fromNbt(block);
            tooltip.add(stackInMagnet.getName().copyContentOnly().formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        }
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
        BlockPos pos = new BlockPos(stack.getOrCreateNbt().getIntArray("Pos")[0], stack.getOrCreateNbt().getIntArray("Pos")[1], stack.getOrCreateNbt().getIntArray("Pos")[2]);
        BlockState state = world.getBlockState(pos);
        if (state.getHardness(world, pos) == -1.0f && !player.isCreative()) {
            return stack;
        }
        stack.getNbt().remove("Pos");
        Block block = state.getBlock();
        ItemStack blockStack = new ItemStack(block);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockStack.getOrCreateNbt().put(BlockItem.BLOCK_ENTITY_TAG_KEY, blockEntity.createNbtWithId());
        }
        stack.getOrCreateNbt().put("Block", blockStack.writeNbt(new NbtCompound()));
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
        player.getInventory().markDirty();
        player.getItemCooldownManager().set(this, 20);
        if (!player.getAbilities().creativeMode) {
            MagnetCraft.DamageMethods.addDamage(stack, player.getRandom(), 1, true);
        }
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
        Direction direction = context.getSide();
        Vec3d hitPos = context.getHitPos();
        boolean inside = context.hitsInsideBlock();
        if (stack.getNbt() != null && stack.getNbt().contains("Block")) {
            ItemStack stackInMagnet = ItemStack.fromNbt(stack.getNbt().getCompound("Block"));
            Block blockFromItem = Block.getBlockFromItem(stackInMagnet.getItem());
            ItemPlacementContext ctx = new ItemPlacementContext(world, player, hand, stackInMagnet, new BlockHitResult(hitPos, direction, blockPos, inside));
            BlockPos placePos = blockPos.offset(direction);
            BlockState state = blockFromItem.getPlacementState(ctx);
            if (state == null) {
                state = blockFromItem.getDefaultState();
            }
            if (state.canPlaceAt(world, placePos) && (world.getBlockState(placePos).isAir() || !world.getBlockState(placePos).getFluidState().isEmpty()) && (state.getCollisionShape(world, placePos).isEmpty() || world.getOtherEntities(null, new Box(placePos), entity -> entity instanceof LivingEntity livingEntity && !livingEntity.canAvoidTraps()).isEmpty())) {
                world.setBlockState(placePos, state);
                blockFromItem.onPlaced(world, placePos, state, player, stackInMagnet);
                BlockItem.writeNbtToBlockEntity(world, player, placePos, stackInMagnet);
                stack.getNbt().remove("Block");
                BlockSoundGroup blockSoundGroup = state.getSoundGroup();
                world.playSound(player, placePos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
                world.emitGameEvent(GameEvent.BLOCK_PLACE, placePos, GameEvent.Emitter.of(player, state));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        } else {
            if (player != null && (player.getAbilities().creativeMode || !MagnetCraft.DamageMethods.isEmptyDamage(stack))) {
                stack.getOrCreateNbt().putIntArray("Pos", new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                player.setCurrentHand(hand);
            }
        }
        return ActionResult.FAIL;
    }

}
