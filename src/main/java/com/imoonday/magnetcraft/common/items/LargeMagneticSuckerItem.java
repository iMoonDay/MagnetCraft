package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.api.AbstractMagneticSuckerItem;
import com.imoonday.magnetcraft.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LargeMagneticSuckerItem extends AbstractMagneticSuckerItem {

    public LargeMagneticSuckerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Extend")) {
                int extend = stack.getNbt().getInt("Extend");
                tooltip.add(Text.translatable("item.magnetcraft.large_magnetic_sucker.tooltip.1", ++extend));
            }
            if (stack.getNbt().contains("Replace")) {
                boolean replace = stack.getOrCreateNbt().getBoolean("Replace");
                Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.mode." + (replace ? 1 : 2));
                tooltip.add(Text.translatable("item.magnetcraft.large_magnetic_sucker.tooltip.2", mode));
            }
            if (stack.getNbt().contains("Blocks")) {
                stack.getNbt().getList("Blocks", NbtElement.COMPOUND_TYPE).stream().map(NbtCompound.class::cast).map(nbtCompound -> ItemStack.fromNbt(nbtCompound.getCompound("Block"))).map(ItemStack::getName).forEach(text -> tooltip.add(text.copyContentOnly().formatted(Formatting.GRAY).formatted(Formatting.BOLD)));
            }
            //相对位置
            //玩家朝向
            //完全复制
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player) || stack.getNbt() == null || !stack.getNbt().contains("Pos") || stack.getNbt().getIntArray("Pos").length != 3) {
            return stack;
        }
        BlockPos centerBlockPos = new BlockPos(stack.getOrCreateNbt().getIntArray("Pos")[0], stack.getOrCreateNbt().getIntArray("Pos")[1], stack.getOrCreateNbt().getIntArray("Pos")[2]);
        NbtList list = stack.getOrCreateNbt().getList("Blcoks", NbtElement.COMPOUND_TYPE);
        int extend = stack.getOrCreateNbt().getInt("Extend");
        for (int i = extend; i >= -extend; --i) {
            for (int j = extend; j >= -extend; --j) {
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
                NbtCompound blocks = new NbtCompound();
                NbtCompound itemNbt = getItemNbt(world, pos, state, blockStack);
                blocks.put("Block", itemNbt);
                blocks.putInt("OffsetX", i);
                blocks.putInt("OffsetZ", j);
                list.add(blocks);
                breakBlock(stack, world, player, pos, state, block);
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
        if (stack.getNbt() != null && stack.getNbt().contains("Direction")) {
            stack.getNbt().remove("Direction");
        }
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        nbtSet(stack);
        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        nbtSet(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        nbtCheck(stack);
    }

    private static void nbtCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("Extend") || !stack.getNbt().contains("Replace")) {
            nbtSet(stack);
        }
    }

    private static void nbtSet(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("Extend")) {
            stack.getOrCreateNbt().putInt("Extend", 1);
        }
        if (stack.getNbt() == null || !stack.getNbt().contains("Replace")) {
            stack.getOrCreateNbt().putBoolean("Replace", false);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!user.isSneaking()) {
            int extend = stack.getOrCreateNbt().getInt("Extend");
            int maxRadius = ModConfig.getValue().suckerMaxRadius;
            if (++extend > --maxRadius) {
                extend = 0;
            }
            stack.getOrCreateNbt().putInt("Extend", extend);
            if (!world.isClient) {
                user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.tooltip.1", ++extend), true);
            }
        } else {
            boolean finalValue = !stack.getOrCreateNbt().getBoolean("Replace");
            stack.getOrCreateNbt().putBoolean("Replace", finalValue);
            Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.mode." + (finalValue ? 1 : 2));
            if (!world.isClient) {
                user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.tooltip.2", mode), true);
            }
        }
        return TypedActionResult.success(stack);
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
        if (player == null) {
            return ActionResult.FAIL;
        }
        if (stack.getNbt() != null && stack.getNbt().contains("Blocks")) {
            List<NbtCompound> blocks = stack.getNbt().getList("Blocks", NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).toList();
            for (NbtCompound nbt : blocks) {
                Vec3i offset = new Vec3i(nbt.getInt("OffsetX"), 0, nbt.getInt("OffsetZ"));
                Direction originalDir = Direction.byId(stack.getOrCreateNbt().getInt("Direction"));
                Direction placeDir = player.getHorizontalFacing();
                BlockRotation rotation = getOffsetRotation(originalDir, placeDir);
                ItemStack blockItemStack = ItemStack.fromNbt(nbt.getCompound("Block"));
                Block blockFromItem = Block.getBlockFromItem(blockItemStack.getItem());
                BlockPos offsetPos = BlockPos.ORIGIN.add(offset).rotate(rotation);
                BlockPos placePos = blockPos.offset(side).add(offsetPos);
                Vec3d newHitPos = hitPos.offset(side, 1).add(Vec3d.of(offsetPos));
                BlockHitResult hit = new BlockHitResult(newHitPos, Direction.UP, placePos.down(), false);
                ItemUsageContext newContext = new ItemUsageContext(world, player, hand, blockItemStack, hit);
                ItemPlacementContext ctx = new ItemPlacementContext(newContext);
                BlockState state = getPlacementState(blockFromItem, ctx);
                BlockState blockState = world.getBlockState(placePos);
                boolean replace = stack.getOrCreateNbt().getBoolean("Replace");
                if (!blockState.isAir() && offerOrBreak(player, world, blockItemStack, placePos, replace)) {
                    continue;
                }
                if (state == null || tryPlaceFailed(ctx, state)) {
                    player.getInventory().offerOrDrop(blockItemStack);
                    continue;
                }
                place(stack, player, world, blockItemStack, placePos, state);
            }
            stack.getNbt().remove("Blocks");
            stack.getNbt().remove("Direction");
            return ActionResult.SUCCESS;
        } else {
            if (player.getAbilities().creativeMode || !MagnetCraft.DamageMethods.isEmptyDamage(stack)) {
                stack.getOrCreateNbt().putIntArray("Pos", new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                player.setCurrentHand(hand);
            }
        }
        return ActionResult.FAIL;
    }

    private static boolean offerOrBreak(PlayerEntity player, World world, ItemStack blockItemStack, BlockPos placePos, boolean replace) {
        if (world.getBlockState(placePos).getHardness(world, placePos) == -1.0f && !player.isCreative() || !replace) {
            player.getInventory().offerOrDrop(blockItemStack);
            return true;
        }
        world.breakBlock(placePos, true, player);
        return false;
    }

    @NotNull
    private static BlockRotation getOffsetRotation(Direction originalDir, Direction placeDir) {
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
        return rotation;
    }


}
