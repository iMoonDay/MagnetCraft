package com.imoonday.magnetcraft.common.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class CustomPickaxeItem extends PickaxeItem {

    public CustomPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        world.getOtherEntities(miner, new Box(pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3, pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3), e -> (e instanceof ItemEntity)).forEach(e -> {
            boolean player = miner instanceof PlayerEntity;
            if (player) {
                boolean hasSlot = ((PlayerEntity) miner).getInventory().getEmptySlot() != -1;
                if (hasSlot) {
                    ((PlayerEntity) miner).giveItemStack(((ItemEntity) e).getStack());
                    e.kill();
                } else {
                    e.teleport(miner.getX(), miner.getY() + 1, miner.getZ());
                }
            }
        });
        return super.postMine(stack, world, state, pos, miner);
    }
}