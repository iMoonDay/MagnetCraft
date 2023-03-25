package com.imoonday.magnetcraft.common.blocks.maglev;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MaglevDetectorRailBlock extends DetectorRailBlock {

    public MaglevDetectorRailBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient || !world.getBlockState(pos).isOf(this)) {
            return;
        }
        this.updateBlockState(state, world, pos, sourceBlock);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return MaglevRailBlock.railExpand(world, pos, player, hand, hit);
    }

}
