package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.AbstractFilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

import static com.imoonday.magnetcraft.common.items.magnets.ElectromagnetItem.tryInsertIntoShulkerBox;
import static net.minecraft.state.property.Properties.*;

public class CropMagnetItem extends AbstractFilterableItem {

    public static final String FILTERABLE = "Filterable";
    public static final String FILTER = "Filter";
    public static final String WHITELIST = "Whitelist";

    public CropMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.CROP_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> livingEntity instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(ItemRegistries.CROP_MAGNET_ITEM) ? 0.0F : itemStack.isBroken() ? 0.0F : 1.0F);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateNbt().putBoolean(FILTERABLE, true);
        return stack;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.GOLDEN_CARROT);
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(ItemRegistries.CROP_MAGNET_CRAFTING_MODULE_ITEM);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player)) {
            return stack;
        }
        int levelEveryCount = ModConfig.getValue().removeFoodLevelEveryCount;
        Hand hand = user.getActiveHand();
        if (!user.isBroken(hand)) {
            int crops = searchCrops(player, hand);
            int removeFoodLevel = levelEveryCount != 0 ? crops / levelEveryCount + (crops % levelEveryCount == 0 ? 0 : 1) : 0;
            boolean success = crops > 0;
            if (success && !player.isCreative()) {
                player.getHungerManager().setFoodLevel(player.getHungerManager().getFoodLevel() - removeFoodLevel);
                player.setCooldown(stack, (1 + removeFoodLevel) * 5 * 20);
            } else {
                player.setCooldown(stack, 20);
            }
        }
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        if (stackInHand.getOrCreateNbt().getBoolean(FILTERABLE) && user.isSneaky() && !user.getAbilities().flying) {
            if (!user.world.isClient) {
                openScreen(user, hand, this);
            }
        } else {
            if (user.getAbilities().creativeMode || !stackInHand.isBroken()) {
                user.setCurrentHand(hand);
            }
            return TypedActionResult.fail(stackInHand);
        }
        return TypedActionResult.success(stackInHand, !stackInHand.isBroken());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        if (user instanceof PlayerEntity player && user.isSpectator() && player.getItemCooldownManager().isCoolingDown(this)) {
            player.getItemCooldownManager().remove(this);
        }
    }

    public static int searchCrops(PlayerEntity player, Hand hand) {
        int count = 0;
        if (!player.world.isClient) {
            player.addDamage(hand, 1, false);
            broken:
            for (int x = -15; x <= 15; x++) {
                for (int y = 15; y >= -15; y--) {
                    for (int z = -15; z <= 15; z++) {
                        if (player.isBroken(hand)) {
                            break broken;
                        }
                        BlockPos pos = player.getBlockPos().add(x, y, z);
                        BlockState state = player.world.getBlockState(pos);
                        ServerWorld world = (ServerWorld) player.world;
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        Block block = state.getBlock();
                        ItemStack stack = player.getStackInHand(hand);
                        if (block instanceof CropBlock cropBlock && cropBlock.isMature(state)) {
                            if (state.isOf(Blocks.WHEAT) && !canBreak(stack, Items.WHEAT) || state.isOf(Blocks.CARROTS) && !canBreak(stack, Items.CARROT) || state.isOf(Blocks.POTATOES) && !canBreak(stack, Items.POTATO) || state.isOf(Blocks.BEETROOTS) && !canBreak(stack, Items.BEETROOT_SEEDS)) {
                                continue;
                            }
                            count = getCountAfterBreakAndPlant(player, hand, count, pos, state, world, blockEntity, cropBlock);
                        } else if ((block instanceof MelonBlock && canBreak(stack, Items.MELON_SEEDS)) || (block instanceof PumpkinBlock && canBreak(stack, Items.PUMPKIN_SEEDS))) {
                            count = getCountAfterBreakBlock(player, hand, count, pos, state, world, blockEntity);
                        } else if ((block instanceof CaveVinesHeadBlock || block instanceof CaveVinesBodyBlock) && state.get(BERRIES) && canBreak(stack, Items.GLOW_BERRIES)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.setBlockState(pos, state.with(BERRIES, false));
                            count++;
                            player.addDamage(hand, 1, true);
                        } else if ((block instanceof SugarCaneBlock && world.getBlockState(pos.down()).isOf(Blocks.SUGAR_CANE) && canBreak(stack, Items.SUGAR_CANE)) || (block instanceof BambooBlock && world.getBlockState(pos.down()).isOf(Blocks.BAMBOO) && canBreak(stack, Items.BAMBOO)) || (block instanceof CactusBlock && world.getBlockState(pos.down()).isOf(Blocks.CACTUS) && canBreak(stack, Items.CACTUS)) || (block instanceof KelpBlock && world.getBlockState(pos.down()).isOf(Blocks.KELP_PLANT)) && canBreak(stack, Items.KELP)) {
                            count = getCountAfterBreakBlock(player, hand, count, pos, state, world, blockEntity);
                        } else if (block instanceof SweetBerryBushBlock && state.get(AGE_3) > 1 && canBreak(stack, Items.SWEET_BERRIES)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.setBlockState(pos, state.with(AGE_3, 1));
                            count++;
                            player.addDamage(hand, 1, true);
                        } else if (block instanceof NetherWartBlock && state.get(AGE_3) == 3 && canBreak(stack, Items.NETHER_WART)) {
                            count = getCountAfterBreakBlock(player, hand, count, pos, state, world, blockEntity);
                            restoreAge(player, pos, state, world, block, AGE_3);
                        } else if (block instanceof CocoaBlock && state.get(AGE_2) == 2 && canBreak(stack, Items.COCOA_BEANS)) {
                            count = getCountAfterBreakBlock(player, hand, count, pos, state, world, blockEntity);
                            restoreAge(player, pos, state, world, block, AGE_2);
                        }
                    }
                }
            }
        }
        return count;
    }

    private static int getCountAfterBreakAndPlant(PlayerEntity player, Hand hand, int count, BlockPos pos, BlockState state, ServerWorld world, BlockEntity blockEntity, CropBlock cropBlock) {
        boolean alreadyRemoved = false;
        for (ItemStack droppedStacks : Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY)) {
            ItemStack seed = cropBlock.getPickStack(world, pos, state);
            if (droppedStacks.isItemEqual(seed) && !alreadyRemoved) {
                droppedStacks.setCount(droppedStacks.getCount() - 1);
                alreadyRemoved = true;
            }
            tryInsertIntoShulkerBox(player, hand, droppedStacks);
        }
        world.breakBlock(pos, false, player);
        if (alreadyRemoved) {
            world.setBlockState(pos, cropBlock.withAge(0));
        }
        player.addDamage(hand, 1, true);
        return ++count;
    }

    private static void restoreAge(PlayerEntity player, BlockPos pos, BlockState state, ServerWorld world, Block block, IntProperty age) {
        ItemStack seed = block.getPickStack(world, pos, state);
        if (player.getInventory().contains(seed)) {
            player.getInventory().removeOne(seed);
            world.setBlockState(pos, state.with(age, 0));
        }
    }

    private static int getCountAfterBreakBlock(PlayerEntity player, Hand hand, int count, BlockPos pos, BlockState state, ServerWorld world, BlockEntity blockEntity) {
        Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
        world.breakBlock(pos, false, player);
        player.addDamage(hand, 1, true);
        return ++count;
    }

    public static boolean canBreak(ItemStack stack, Item requiredItem) {
        if (stack != null && stack.getNbt() != null && stack.getNbt().getBoolean(FILTERABLE)) {
            String item = Registries.ITEM.getId(requiredItem).toString();
            NbtList list = stack.getNbt().getList(FILTER, NbtElement.COMPOUND_TYPE);
            boolean inList = list.stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound && Objects.equals(((NbtCompound) nbtElement).getString("id"), item));
            boolean isWhitelist = stack.getNbt().getBoolean(WHITELIST);
            return (isWhitelist && inList) || (!isWhitelist && !inList);
        }
        return true;
    }

    @Override
    public boolean getSwitchable() {
        return false;
    }

    @Override
    public int getEnchantability() {
        return 14;
    }

    @Override
    public boolean canTeleportItems() {
        return true;
    }

}
