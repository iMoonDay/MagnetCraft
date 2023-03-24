package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.AbstractFilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

import static com.imoonday.magnetcraft.common.items.magnets.ElectromagnetItem.tryInsertIntoShulkerBox;
import static net.minecraft.state.property.Properties.*;

/**
 * @author iMoonDay
 */
public class CropMagnetItem extends AbstractFilterableItem {

    public static final String FILTERABLE = "Filterable";
    public static final String FILTER = "Filter";
    public static final String WHITELIST = "Whitelist";

    public CropMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.CROP_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> livingEntity instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(ItemRegistries.CROP_MAGNET_ITEM) ? 0.0F : MagnetCraft.DamageMethods.isEmptyDamage(itemStack) ? 0.0F : 1.0F);
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        if (stackInHand.getOrCreateNbt().getBoolean(FILTERABLE) && user.isSneaky() && !user.getAbilities().flying) {
            if (!user.world.isClient) {
                openScreen(user, hand, this);
            }
        } else {
            int levelEveryCount = ModConfig.getValue().removeFoodLevelEveryCount;
            if (!MagnetCraft.DamageMethods.isEmptyDamage(user, hand)) {
                int crops = searchCrops(user, hand);
                int removeFoodLevel = levelEveryCount != 0 ? crops / levelEveryCount + (crops % levelEveryCount == 0 ? 0 : 1) : 0;
                boolean success = crops > 0;
                if (success && !user.isCreative()) {
                    user.getHungerManager().setFoodLevel(user.getHungerManager().getFoodLevel() - removeFoodLevel);
                    MagnetCraft.CooldownMethods.setCooldown(user, stackInHand, (1 + removeFoodLevel) * 5 * 20);
                } else {
                    MagnetCraft.CooldownMethods.setCooldown(user, stackInHand, 20);
                }
            }
        }
        return TypedActionResult.success(stackInHand, !MagnetCraft.DamageMethods.isEmptyDamage(stackInHand));
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
            MagnetCraft.DamageMethods.addDamage(player, hand, 1, false);
            for (int x = -15; x <= 15; x++) {
                if (MagnetCraft.DamageMethods.isEmptyDamage(player, hand)) {
                    break;
                }
                for (int y = 15; y >= -15; y--) {
                    if (MagnetCraft.DamageMethods.isEmptyDamage(player, hand)) {
                        break;
                    }
                    for (int z = -15; z <= 15; z++) {
                        if (MagnetCraft.DamageMethods.isEmptyDamage(player, hand)) {
                            break;
                        }
                        BlockPos pos = player.getBlockPos().add(x, y, z);
                        BlockState state = player.world.getBlockState(pos);
                        ServerWorld world = (ServerWorld) player.world;
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        Block block = state.getBlock();
                        ItemStack stack = player.getStackInHand(hand);
                        if (block instanceof CropBlock cropBlock && cropBlock.isMature(state)) {
                            if (state.isOf(Blocks.WHEAT) && !canBreak(stack, Items.WHEAT)) {
                                continue;
                            } else if (state.isOf(Blocks.CARROTS) && !canBreak(stack, Items.CARROT)) {
                                continue;
                            } else if (state.isOf(Blocks.POTATOES) && !canBreak(stack, Items.POTATO)) {
                                continue;
                            } else if (state.isOf(Blocks.BEETROOTS) && !canBreak(stack, Items.BEETROOT_SEEDS)) {
                                continue;
                            }
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
                            count++;
                            MagnetCraft.DamageMethods.addDamage(player, hand, 1, true);
                        } else if ((block instanceof MelonBlock && canBreak(stack, Items.MELON_SEEDS)) || (block instanceof PumpkinBlock && canBreak(stack, Items.PUMPKIN_SEEDS))) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.breakBlock(pos, false, player);
                            count++;
                            MagnetCraft.DamageMethods.addDamage(player, hand, 1, true);
                        } else if ((block instanceof CaveVinesHeadBlock || block instanceof CaveVinesBodyBlock) && state.get(BERRIES) && canBreak(stack, Items.GLOW_BERRIES)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.setBlockState(pos, state.with(BERRIES, false));
                            count++;
                            MagnetCraft.DamageMethods.addDamage(player, hand, 1, true);
                        } else if ((block instanceof SugarCaneBlock && world.getBlockState(pos.down()).isOf(Blocks.SUGAR_CANE) && canBreak(stack, Items.SUGAR_CANE)) || (block instanceof BambooBlock && world.getBlockState(pos.down()).isOf(Blocks.BAMBOO) && canBreak(stack, Items.BAMBOO)) || (block instanceof CactusBlock && world.getBlockState(pos.down()).isOf(Blocks.CACTUS) && canBreak(stack, Items.CACTUS)) || (block instanceof KelpBlock && world.getBlockState(pos.down()).isOf(Blocks.KELP_PLANT)) && canBreak(stack, Items.KELP)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.breakBlock(pos, false, player);
                            count++;
                            MagnetCraft.DamageMethods.addDamage(player, hand, 1, true);
                        } else if (block instanceof SweetBerryBushBlock && state.get(AGE_3) > 1 && canBreak(stack, Items.SWEET_BERRIES)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.setBlockState(pos, state.with(AGE_3, 1));
                            count++;
                            MagnetCraft.DamageMethods.addDamage(player, hand, 1, true);
                        } else if (block instanceof NetherWartBlock netherWartBlock && state.get(AGE_3) == 3 && canBreak(stack, Items.NETHER_WART)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.breakBlock(pos, false, player);
                            ItemStack seed = netherWartBlock.getPickStack(world, pos, state);
                            if (player.getInventory().contains(seed)) {
                                player.getInventory().removeOne(seed);
                                world.setBlockState(pos, state.with(AGE_3, 0));
                            }
                            count++;
                            MagnetCraft.DamageMethods.addDamage(player, hand, 1, true);
                        } else if (block instanceof CocoaBlock cocoaBlock && state.get(AGE_2) == 2 && canBreak(stack, Items.COCOA_BEANS)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(itemStack -> tryInsertIntoShulkerBox(player, hand, itemStack));
                            world.breakBlock(pos, false, player);
                            ItemStack seed = cocoaBlock.getPickStack(world, pos, state);
                            if (player.getInventory().contains(seed)) {
                                player.getInventory().removeOne(seed);
                                world.setBlockState(pos, state.with(AGE_2, 0));
                            }
                            count++;
                            MagnetCraft.DamageMethods.addDamage(player, hand, 1, true);
                        }
                    }
                }
            }
        }
        return count;
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
