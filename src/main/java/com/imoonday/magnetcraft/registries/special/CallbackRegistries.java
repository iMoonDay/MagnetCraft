package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.callbacks.MiningBlockCallback;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class CallbackRegistries {
    public static void register() {
        MiningBlockCallback.EVENT.register((world, player, stack, block, state, pos, blockEntity) -> {
            boolean hasEnchantment = NbtClassMethod.hasEnchantment(stack, EnchantmentRegistries.AUTOMATIC_COLLECTION_ENCHANTMENT);
            if (hasEnchantment) {
                Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, stack).forEach(e -> {
                    boolean hasSlot = player.getInventory().getEmptySlot() != -1;
                    ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY() + 1, player.getZ(), e);
                    if (hasSlot) {
                        player.giveItemStack(e);
                    } else {
                        world.spawnEntity(itemEntity);
                    }
                });
                return ActionResult.FAIL;
            } else {
                return ActionResult.PASS;
            }
        });
    }

}
