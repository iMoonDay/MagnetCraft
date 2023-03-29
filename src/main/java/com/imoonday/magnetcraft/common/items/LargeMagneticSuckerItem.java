package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.api.AbstractMagneticSuckerItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.MutableText;
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

import java.util.Arrays;
import java.util.List;

public class LargeMagneticSuckerItem extends AbstractMagneticSuckerItem {

    public static final String EXTEND = "Extend";
    public static final String REPLACE = "Replace";
    public static final String BLOCKS = "Blocks";
    public static final String BLOCK = "Block";
    public static final String OFFSET_X = "OffsetX";
    public static final String OFFSET_Z = "OffsetZ";
    public static final String POS = "Pos";
    public static final String DIRECTION = "Direction";
    public static final String CURRENT_SELECT = "CurrentSelect";
    public static final String POSITION = "Position";
    public static final String TOWARDS = "Towards";
    public static final String COPY = "Copy";
    public static final String[] NBTS = {CURRENT_SELECT, EXTEND, REPLACE, POSITION, TOWARDS, COPY};
    public static final String LORE = "Lore";
    public static final String DISPLAY = "display";
    public static final String NBT = "(+NBT)";

    public LargeMagneticSuckerItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.LARGE_MAGNETIC_SUCKER_ITEM, new Identifier("contains"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getNbt() == null || !itemStack.getNbt().contains(BLOCKS) ? 0.0F : 1.0F);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Item.fromBlock(BlockRegistries.MAGNET_BLOCK));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getNbt() != null) {
            tooltip.add(Text.translatable("item.magnetcraft.large_magnetic_sucker.settings").formatted(Formatting.YELLOW).formatted(Formatting.BOLD));
            int select = -1;
            MutableText selectedText = Text.empty();
            if (stack.getNbt().contains(CURRENT_SELECT)) {
                select = stack.getOrCreateNbt().getInt(CURRENT_SELECT);
                selectedText = Text.translatable("item.magnetcraft.large_magnetic_sucker.select").formatted(Formatting.YELLOW).formatted(Formatting.BOLD);
            }
            if (stack.getNbt().contains(EXTEND)) {
                int extend = stack.getOrCreateNbt().getInt(EXTEND);
                int scope = (++extend) * 2 - 1;
                MutableText text = Text.translatable("item.magnetcraft.large_magnetic_sucker.scope", scope);
                if (select == 0) {
                    text.append(selectedText);
                }
                tooltip.add(text);
            }
            if (stack.getNbt().contains(REPLACE)) {
                boolean replace = stack.getOrCreateNbt().getBoolean(REPLACE);
                Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.replace." + (replace ? 1 : 2));
                MutableText text = Text.translatable("item.magnetcraft.large_magnetic_sucker.replace", mode);
                if (select == 1) {
                    text.append(selectedText);
                }
                tooltip.add(text);
            }
            if (stack.getNbt().contains(POSITION)) {
                boolean position = stack.getOrCreateNbt().getBoolean(POSITION);
                Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.position." + (position ? 1 : 2));
                MutableText text = Text.translatable("item.magnetcraft.large_magnetic_sucker.position", mode);
                if (select == 2) {
                    text.append(selectedText);
                }
                tooltip.add(text);
            }
            if (stack.getNbt().contains(TOWARDS)) {
                boolean towards = stack.getOrCreateNbt().getBoolean(TOWARDS);
                Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.towards." + (towards ? 1 : 2));
                MutableText text = Text.translatable("item.magnetcraft.large_magnetic_sucker.towards", mode);
                if (select == 3) {
                    text.append(selectedText);
                }
                tooltip.add(text);
            }
            if (stack.getNbt().contains(COPY)) {
                boolean copy = stack.getOrCreateNbt().getBoolean(COPY);
                Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.copy." + (copy ? 1 : 2));
                MutableText text = Text.translatable("item.magnetcraft.large_magnetic_sucker.copy", mode);
                if (select == 4) {
                    text.append(selectedText);
                }
                tooltip.add(text);
            }
            if (stack.getNbt().contains(BLOCKS)) {
                tooltip.add(Text.translatable("item.magnetcraft.large_magnetic_sucker.blocks").formatted(Formatting.YELLOW).formatted(Formatting.BOLD));
                stack.getNbt().getList(BLOCKS, NbtElement.COMPOUND_TYPE).stream().map(NbtCompound.class::cast).map(nbtCompound -> ItemStack.fromNbt(nbtCompound.getCompound(BLOCK))).map(ItemStack::getName).forEach(text -> tooltip.add(text.copyContentOnly().formatted(Formatting.GRAY).formatted(Formatting.BOLD)));
            }
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player) || stack.getNbt() == null || !stack.getNbt().contains(POS) || stack.getNbt().getIntArray(POS).length != 3) {
            return stack;
        }
        BlockPos centerBlockPos = new BlockPos(stack.getOrCreateNbt().getIntArray(POS)[0], stack.getOrCreateNbt().getIntArray(POS)[1], stack.getOrCreateNbt().getIntArray(POS)[2]);
        NbtList list = stack.getOrCreateNbt().getList(BLOCKS, NbtElement.COMPOUND_TYPE);
        int extend = stack.getOrCreateNbt().getInt(EXTEND);
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
                if (blockStack.isEmpty()) {
                    continue;
                }
                NbtCompound blocks = new NbtCompound();
                NbtCompound itemNbt = getItemNbt(world, pos, state, blockStack);
                blocks.put(BLOCK, itemNbt);
                blocks.putInt(OFFSET_X, i);
                blocks.putInt(OFFSET_Z, j);
                list.add(blocks);
                breakBlock(stack, world, player, pos, state, block);
            }
        }
        stack.getNbt().remove(POS);
        if (list.isEmpty()) {
            return stack;
        }
        stack.getOrCreateNbt().put(BLOCKS, list);
        Direction horizontalFacing = player.getHorizontalFacing();
        stack.getOrCreateNbt().putInt(DIRECTION, horizontalFacing.getId());
        player.getInventory().markDirty();
        player.getItemCooldownManager().set(this, 20);
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (stack.getNbt() != null && stack.getNbt().contains(POS)) {
            stack.getNbt().remove(POS);
        }
        if (stack.getNbt() != null && stack.getNbt().contains(DIRECTION)) {
            stack.getNbt().remove(DIRECTION);
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
        int containNbt = (int) Arrays.stream(NBTS).filter(nbt -> stack.getNbt() != null && stack.getNbt().contains(nbt)).count();
        if (containNbt < NBTS.length || stack.getNbt() == null) {
            nbtSet(stack);
        }
    }

    private static void nbtSet(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains(CURRENT_SELECT)) {
            stack.getOrCreateNbt().putInt(CURRENT_SELECT, 0);
        }
        if (stack.getNbt() == null || !stack.getNbt().contains(EXTEND)) {
            stack.getOrCreateNbt().putInt(EXTEND, 1);
        }
        if (stack.getNbt() == null || !stack.getNbt().contains(REPLACE)) {
            stack.getOrCreateNbt().putBoolean(REPLACE, false);
        }
        if (stack.getNbt() == null || !stack.getNbt().contains(POSITION)) {
            stack.getOrCreateNbt().putBoolean(POSITION, true);
        }
        if (stack.getNbt() == null || !stack.getNbt().contains(TOWARDS)) {
            stack.getOrCreateNbt().putBoolean(TOWARDS, true);
        }
        if (stack.getNbt() == null || !stack.getNbt().contains(COPY)) {
            stack.getOrCreateNbt().putBoolean(COPY, false);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        int select = stack.getOrCreateNbt().getInt(CURRENT_SELECT);
        if (user.isSneaking()) {
            switch (select) {
                case 0 -> {
                    int extend = stack.getOrCreateNbt().getInt(EXTEND);
                    int maxRadius = ModConfig.getValue().suckerMaxRadius;
                    if (++extend > --maxRadius) {
                        extend = 0;
                    }
                    stack.getOrCreateNbt().putInt(EXTEND, extend);
                    int scope = (++extend) * 2 - 1;
                    if (!world.isClient) {
                        user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.scope", scope), true);
                    }
                }
                case 1 -> {
                    boolean finalValue = !stack.getOrCreateNbt().getBoolean(REPLACE);
                    stack.getOrCreateNbt().putBoolean(REPLACE, finalValue);
                    Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.replace." + (finalValue ? 1 : 2));
                    if (!world.isClient) {
                        user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.replace", mode), true);
                    }
                }
                case 2 -> {
                    boolean finalValue = !stack.getOrCreateNbt().getBoolean(POSITION);
                    stack.getOrCreateNbt().putBoolean(POSITION, finalValue);
                    Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.position." + (finalValue ? 1 : 2));
                    if (!world.isClient) {
                        user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.position", mode), true);
                    }
                }
                case 3 -> {
                    boolean finalValue = !stack.getOrCreateNbt().getBoolean(TOWARDS);
                    stack.getOrCreateNbt().putBoolean(TOWARDS, finalValue);
                    Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.towards." + (finalValue ? 1 : 2));
                    if (!world.isClient) {
                        user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.towards", mode), true);
                    }
                }
                case 4 -> {
                    boolean finalValue = !stack.getOrCreateNbt().getBoolean(COPY);
                    stack.getOrCreateNbt().putBoolean(COPY, finalValue);
                    Text mode = Text.translatable("item.magnetcraft.large_magnetic_sucker.copy." + (finalValue ? 1 : 2));
                    if (!world.isClient) {
                        user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.copy", mode), true);
                    }
                }
            }
        } else {
            if (++select >= NBTS.length - 1) {
                select = 0;
            }
            stack.getOrCreateNbt().putInt(CURRENT_SELECT, select);
            String mode = switch (select) {
                case 0 -> "scope";
                case 1 -> "replace";
                case 2 -> "position";
                case 3 -> "towards";
                case 4 -> "copy";
                default -> throw new IllegalStateException("Unexpected value: " + select);
            };
            Text selected = Text.translatable("item.magnetcraft.large_magnetic_sucker." + mode);
            String name = selected.getString().replaceAll(":.*", "");
            if (!world.isClient) {
                user.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.current_select", name), true);
            }
        }
        return TypedActionResult.success(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return placeOrUse(context);
    }

    private ActionResult placeOrUse(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        return stack.getNbt() != null && stack.getNbt().contains(BLOCKS) ? placeBlocks(context) : startUsing(context);
    }

    @NotNull
    private ActionResult placeBlocks(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        Hand hand = context.getHand();
        BlockPos blockPos = context.getBlockPos();
        Direction side = context.getSide();
        Vec3d hitPos = context.getHitPos();
        if (stack.getNbt() == null || player == null) {
            return ActionResult.FAIL;
        }
        List<NbtCompound> blocks = stack.getNbt().getList(BLOCKS, NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).toList();
        boolean replace = stack.getOrCreateNbt().getBoolean(REPLACE);
        boolean position = stack.getOrCreateNbt().getBoolean(POSITION);
        boolean towards = stack.getOrCreateNbt().getBoolean(TOWARDS);
        boolean copy = stack.getOrCreateNbt().getBoolean(COPY);
        int failedCount = 0;
        for (NbtCompound nbt : blocks) {
            Vec3i offset = new Vec3i(nbt.getInt(OFFSET_X), 0, nbt.getInt(OFFSET_Z));
            Direction originalDir = Direction.byId(stack.getOrCreateNbt().getInt(DIRECTION));
            Direction placeDir = player.getHorizontalFacing();
            BlockRotation rotation = getOffsetRotation(originalDir, placeDir, position);
            ItemStack blockItemStack = ItemStack.fromNbt(nbt.getCompound(BLOCK));
            Block blockFromItem = Block.getBlockFromItem(blockItemStack.getItem());
            BlockPos offsetPos = BlockPos.ORIGIN.add(offset).rotate(rotation);
            BlockPos placePos = blockPos.offset(side).add(offsetPos);
            Vec3d newHitPos = hitPos.offset(side, 1).add(Vec3d.of(offsetPos));
            BlockHitResult hit = new BlockHitResult(newHitPos, Direction.UP, placePos.down(), false);
            ItemUsageContext newContext = new ItemUsageContext(world, player, hand, blockItemStack, hit);
            ItemPlacementContext ctx = new ItemPlacementContext(newContext);
            BlockState state = getPlacementState(blockFromItem, ctx);
            BlockState originalBlockState = world.getBlockState(placePos);
            if (!originalBlockState.isAir() && offerOrBreak(player, world, blockItemStack, placePos, replace)) {
                failedCount++;
                continue;
            }
            if (state == null || tryPlaceFailed(ctx, state)) {
                player.getInventory().offerOrDrop(withNbtLore(blockItemStack));
                failedCount++;
                continue;
            }
            place(stack, player, world, blockItemStack, placePos, state, towards, copy);
        }
        if (failedCount > 0) {
            if (!world.isClient) {
                player.sendMessage(Text.translatable("item.magnetcraft.large_magnetic_sucker.blocked", failedCount));
            }
        }
        stack.getNbt().remove(BLOCKS);
        stack.getNbt().remove(DIRECTION);
        return ActionResult.SUCCESS;
    }

    private static ItemStack withNbtLore(ItemStack blockItemStack) {
        NbtList lore = new NbtList();
        NbtCompound display = new NbtCompound();
        NbtString nbtString = NbtString.of(NbtString.escape(NBT));
        lore.add(nbtString);
        display.put(LORE, lore);
        blockItemStack.getOrCreateNbt().put(DISPLAY, display);
        return blockItemStack;
    }

    private static boolean offerOrBreak(PlayerEntity player, World world, ItemStack blockItemStack, BlockPos placePos, boolean replace) {
        if (cannotBreak(player, world, placePos) || !replace) {
            player.getInventory().offerOrDrop(withNbtLore(blockItemStack));
            return true;
        }
        world.breakBlock(placePos, true, player);
        return false;
    }

    @NotNull
    private static BlockRotation getOffsetRotation(Direction originalDir, Direction placeDir, boolean position) {
        BlockRotation rotation = BlockRotation.NONE;
        if (!originalDir.equals(Direction.UP) && !originalDir.equals(Direction.DOWN) && position) {
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
