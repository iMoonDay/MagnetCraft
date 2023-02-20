package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.callbacks.MiningBlockCallback;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class CallbackRegistries {
    public static void register() {
        MiningBlockCallback.EVENT.register((world, player, stack, block, state, pos, blockEntity) -> {
            boolean hasEnchantment = NbtClassMethod.hasEnchantment(stack, EnchantmentRegistries.AUTOMATIC_COLLECTION_ENCHANTMENT);
            if (hasEnchantment) {
                Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, stack).forEach(e -> {
                    PlayerInventory inventory = player.getInventory();
                    boolean hasSlot = inventory.getEmptySlot() != -1;
                    ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), e);
                    if (hasSlot) {
                        player.giveItemStack(e);
                    } else {
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack stack1 = inventory.getStack(i);
                            Item stack1Item = stack1.getItem();
                            Item eItem = e.getItem();
                            if (stack1Item == eItem) {
                                int stack1Count = stack1.getCount();
                                int eCount = e.getCount();
                                int totalCount = stack1Count + eCount;
                                if (totalCount <= 64) {
                                    stack1.setCount(totalCount);
                                } else {
                                    world.spawnEntity(itemEntity);
                                    boolean isClient = world.isClient;
                                    boolean displayMessageFeedback = ModConfig.getConfig().displayMessageFeedback;
                                    if (!isClient && displayMessageFeedback) {
                                        player.sendMessage(Text.translatable("text.magnetcraft.message.inventory_full"));
                                    }
                                }
                                break;
                            }
                        }
                    }
                });
                return ActionResult.FAIL;
            } else {
                return ActionResult.PASS;
            }
        });
    }

}
