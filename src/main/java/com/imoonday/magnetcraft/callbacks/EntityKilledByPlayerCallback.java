package com.imoonday.magnetcraft.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EntityKilledByPlayerCallback {

    Event<EntityKilledByPlayerCallback> EVENT = EventFactory.createArrayBacked(EntityKilledByPlayerCallback.class,
            (listeners) -> (world, player, stack, pos, entity) -> {
                for (EntityKilledByPlayerCallback listener : listeners) {
                    ActionResult result = listener.interact(world, player, stack, pos, entity);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(World world, PlayerEntity player, ItemStack stack, BlockPos pos, LivingEntity entity);

}
