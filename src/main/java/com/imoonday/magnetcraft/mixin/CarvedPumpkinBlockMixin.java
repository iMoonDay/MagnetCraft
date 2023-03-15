package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.entities.MagneticIronGolemEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    private BlockPattern magneticIronGolemWithLodestoneDispenserPattern;
    private BlockPattern magneticIronGolemDispenserPattern;
    private BlockPattern magneticIronGolemWithLodestonePattern;
    private BlockPattern magneticIronGolemPattern;
    private static final Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE = state -> state != null && (state.isOf(Blocks.CARVED_PUMPKIN) || state.isOf(Blocks.JACK_O_LANTERN));

    @Shadow
    private static void spawnEntity(World world, BlockPattern.Result patternResult, Entity entity, BlockPos pos) {
    }

    @Inject(method = "canDispense", at = @At(value = "HEAD"), cancellable = true)
    public void canDispense(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.getMagneticIronGolemWithLodestoneDispenserPattern().searchAround(world, pos) != null || this.getMagneticIronGolemDispenserPattern().searchAround(world, pos) != null) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "trySpawnEntity", at = @At(value = "TAIL"))
    private void trySpawnEntity(World world, BlockPos pos, CallbackInfo ci) {
        MagneticIronGolemEntity magneticIronGolemEntity;
        BlockPattern.Result result1 = this.getMagneticIronGolemWithLodestonePattern().searchAround(world, pos);
        BlockPattern.Result result2 = this.getMagneticIronGolemPattern().searchAround(world, pos);
        if (result1 != null && (magneticIronGolemEntity = EntityRegistries.MAGNETIC_IRON_GOLEM.create(world)) != null) {
            magneticIronGolemEntity.setPlayerCreated(true);
            spawnEntity(world, result1, magneticIronGolemEntity.withLodestone(), result1.translate(1, 2, 0).getBlockPos());
//            if (!world.isClient) {
//                PacketByteBuf buf = PacketByteBufs.create();
//                buf.writeInt(magneticIronGolemEntity.getId());
//                PlayerLookup.tracking(magneticIronGolemEntity).forEach(player -> ServerPlayNetworking.send(player, GOLEM_PACKET_ID, buf));
//            }
        } else {
            if (result2 != null && (magneticIronGolemEntity = EntityRegistries.MAGNETIC_IRON_GOLEM.create(world)) != null) {
                magneticIronGolemEntity.setPlayerCreated(true);
                spawnEntity(world, result2, magneticIronGolemEntity, result2.translate(1, 2, 0).getBlockPos());
            }
        }
    }

    private BlockPattern getMagneticIronGolemWithLodestoneDispenserPattern() {
        if (this.magneticIronGolemWithLodestoneDispenserPattern == null) {
            this.magneticIronGolemWithLodestoneDispenserPattern = BlockPatternBuilder.start().aisle("~ ~", "#$#", "~#~").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(BlockRegistries.MAGNET_BLOCK))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).where('$', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(BlockRegistries.LODESTONE_BLOCK))).build();
        }
        return this.magneticIronGolemWithLodestoneDispenserPattern;
    }

    private BlockPattern getMagneticIronGolemWithLodestonePattern() {
        if (this.magneticIronGolemWithLodestonePattern == null) {
            this.magneticIronGolemWithLodestonePattern = BlockPatternBuilder.start().aisle("~^~", "#$#", "~#~").where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE)).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(BlockRegistries.MAGNET_BLOCK))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).where('$', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(BlockRegistries.LODESTONE_BLOCK))).build();
        }
        return this.magneticIronGolemWithLodestonePattern;
    }

    private BlockPattern getMagneticIronGolemDispenserPattern() {
        if (this.magneticIronGolemDispenserPattern == null) {
            this.magneticIronGolemDispenserPattern = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(BlockRegistries.MAGNET_BLOCK))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.magneticIronGolemDispenserPattern;
    }

    private BlockPattern getMagneticIronGolemPattern() {
        if (this.magneticIronGolemPattern == null) {
            this.magneticIronGolemPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE)).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(BlockRegistries.MAGNET_BLOCK))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.magneticIronGolemPattern;
    }

}
