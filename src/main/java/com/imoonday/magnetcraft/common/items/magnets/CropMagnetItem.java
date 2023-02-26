package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.methods.DamageMethods;
import com.imoonday.magnetcraft.methods.TeleportMethods;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.*;

public class CropMagnetItem extends Item {
    public CropMagnetItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.CROP_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).getItemCooldownManager().isCoolingDown(ItemRegistries.CROP_MAGNET_ITEM)) {
                return 0.0F;
            }
            return DamageMethods.isEmptyDamage(itemStack) ? 0.0F : 1.0F;
        });
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.GOLDEN_CARROT) || super.canRepair(stack, ingredient);
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
        if (!DamageMethods.isEmptyDamage(user, hand)) {
            int crops = searchCrops(user, hand);
            int removeFoodLevel = crops / 32 + (crops % 32 == 0 ? 0 : 1);
            boolean success = crops > 0;
            if (success && !user.isCreative()) {
                user.getHungerManager().setFoodLevel(user.getHungerManager().getFoodLevel() - removeFoodLevel);
                user.getItemCooldownManager().set(this, crops * 20);
            } else {
                user.getItemCooldownManager().set(this, 20);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        if (user instanceof PlayerEntity && user.isSpectator() && ((PlayerEntity) user).getItemCooldownManager().isCoolingDown(this)) {
            ((PlayerEntity) user).getItemCooldownManager().remove(this);
        }
    }

    public static int searchCrops(PlayerEntity player, Hand hand) {
        int count = 0;
        if (!player.world.isClient) {
            DamageMethods.addDamage(player, hand, 1);
            for (int x = -15; x <= 15; x++) {
                if (DamageMethods.isEmptyDamage(player, hand)) {
                    break;
                }
                for (int y = 15; y >= -15; y--) {
                    if (DamageMethods.isEmptyDamage(player, hand)) {
                        break;
                    }
                    for (int z = -15; z <= 15; z++) {
                        if (DamageMethods.isEmptyDamage(player, hand)) {
                            break;
                        }
                        BlockPos pos = player.getBlockPos().add(x, y, z);
                        BlockState state = player.world.getBlockState(pos);
                        ServerWorld world = (ServerWorld) player.world;
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        Block block = state.getBlock();
                        if (block instanceof CropBlock cropBlock && cropBlock.isMature(state)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            world.breakBlock(pos, false, player);
                            ItemStack seed = cropBlock.getPickStack(world, pos, state);
                            if (player.getInventory().contains(seed)) {
                                player.getInventory().removeOne(seed);
                                world.setBlockState(pos, cropBlock.withAge(0));
                            }
                            count++;
                            DamageMethods.addDamage(player, hand, 1);
                        } else if (block instanceof MelonBlock || block instanceof PumpkinBlock) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            world.breakBlock(pos, false, player);
                            count++;
                            DamageMethods.addDamage(player, hand, 1);
                        } else if ((block instanceof CaveVinesHeadBlock || block instanceof CaveVinesBodyBlock) && state.get(BERRIES)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            world.setBlockState(pos, state.with(BERRIES, false));
                            count++;
                            DamageMethods.addDamage(player, hand, 1);
                        } else if ((block instanceof SugarCaneBlock && world.getBlockState(pos.down()).isOf(Blocks.SUGAR_CANE)) || (block instanceof BambooBlock && world.getBlockState(pos.down()).isOf(Blocks.BAMBOO)) || (block instanceof CactusBlock && world.getBlockState(pos.down()).isOf(Blocks.CACTUS)) || (block instanceof KelpBlock && world.getBlockState(pos.down()).isOf(Blocks.KELP_PLANT))) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            world.breakBlock(pos, false, player);
                            count++;
                            DamageMethods.addDamage(player, hand, 1);
                        } else if (block instanceof SweetBerryBushBlock && state.get(AGE_3) > 1) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            world.setBlockState(pos, state.with(AGE_3, 1));
                            count++;
                            DamageMethods.addDamage(player, hand, 1);
                        } else if (block instanceof NetherWartBlock netherWartBlock && state.get(AGE_3) == 3) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            world.breakBlock(pos, false, player);
                            ItemStack seed = netherWartBlock.getPickStack(world, pos, state);
                            if (player.getInventory().contains(seed)) {
                                player.getInventory().removeOne(seed);
                                world.setBlockState(pos, state.with(AGE_3, 0));
                            }
                            count++;
                            DamageMethods.addDamage(player, hand, 1);
                        } else if (block instanceof CocoaBlock cocoaBlock && state.get(AGE_2) == 2) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, ItemStack.EMPTY).forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            world.breakBlock(pos, false, player);
                            ItemStack seed = cocoaBlock.getPickStack(world, pos, state);
                            if (player.getInventory().contains(seed)) {
                                player.getInventory().removeOne(seed);
                                world.setBlockState(pos, state.with(AGE_2, 0));
                            }
                            count++;
                            DamageMethods.addDamage(player, hand, 1);
                        }
                    }
                }
            }
        }
        return count;
    }
}
