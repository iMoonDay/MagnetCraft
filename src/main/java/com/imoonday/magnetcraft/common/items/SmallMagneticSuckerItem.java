package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.api.AbstractMagneticSuckerItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author iMoonDay
 */
public class SmallMagneticSuckerItem extends AbstractMagneticSuckerItem {

    public static final String BLOCK = "Block";
    public static final String ID = "id";
    public static final String POS = "Pos";

    public SmallMagneticSuckerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        NbtCompound block = stack.getSubNbt(BLOCK);
        if (nbt != null && nbt.contains(BLOCK) && block != null && block.contains(ID)) {
            ItemStack stackInMagnet = ItemStack.fromNbt(block);
            tooltip.add(stackInMagnet.getName().copyContentOnly().formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player) || stack.getNbt() == null || !stack.getNbt().contains(POS) || stack.getNbt().getIntArray(POS).length != 3) {
            return stack;
        }
        BlockPos pos = new BlockPos(stack.getOrCreateNbt().getIntArray(POS)[0], stack.getOrCreateNbt().getIntArray(POS)[1], stack.getOrCreateNbt().getIntArray(POS)[2]);
        BlockState state = world.getBlockState(pos);
        if (cannotBreak(player, world, pos)) {
            return stack;
        }
        stack.getNbt().remove(POS);
        Block block = state.getBlock();
        ItemStack blockStack = new ItemStack(block);
        NbtCompound itemNbt = getItemNbt(world, pos, state, blockStack);
        stack.getOrCreateNbt().put(BLOCK, itemNbt);
        breakBlock(stack, world, player, pos, state, block);
        player.getInventory().markDirty();
        player.getItemCooldownManager().set(this, 20);
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (stack.getNbt() != null && stack.getNbt().contains(POS)) {
            stack.getNbt().remove(POS);
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
        boolean insideBlock = context.hitsInsideBlock();
        if (stack.getNbt() != null && stack.getNbt().contains(BLOCK)) {
            ItemStack blockItemStack = ItemStack.fromNbt(stack.getNbt().getCompound(BLOCK));
            Block blockFromItem = Block.getBlockFromItem(blockItemStack.getItem());
            BlockHitResult hit = new BlockHitResult(hitPos, direction, blockPos, insideBlock);
            ItemUsageContext newContext = new ItemUsageContext(world, player, hand, blockItemStack, hit);
            ItemPlacementContext ctx = new ItemPlacementContext(newContext);
            BlockPos placePos = blockPos.offset(direction);
            BlockState state = getPlacementState(blockFromItem, ctx);
            return state == null || tryPlaceFailed(ctx, state) ? ActionResult.FAIL : place(stack, player, world, blockItemStack, placePos, state);
        } else {
            if (player != null && (player.getAbilities().creativeMode || !MagnetCraft.DamageMethods.isEmptyDamage(stack))) {
                stack.getOrCreateNbt().putIntArray(POS, new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                player.setCurrentHand(hand);
            }
        }
        return ActionResult.FAIL;
    }

}
