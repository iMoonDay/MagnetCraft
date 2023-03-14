package com.imoonday.magnetcraft.common.fluids;

import com.imoonday.magnetcraft.api.AbstractMagneticFluid;
import com.imoonday.magnetcraft.common.tags.FluidTags;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.registries.common.FluidRegistries.*;

public class MagneticFluid extends AbstractMagneticFluid {

    @Override
    public Fluid getFlowing() {
        return FLOWING_MAGNETIC_FLUID;
    }

    @Override
    public Fluid getStill() {
        return STILL_MAGNETIC_FLUID;
    }

    @Override
    public Item getBucketItem() {
        return MAGNETIC_FLUID_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return MAGNETIC_FLUID.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public boolean isStill(FluidState state) {
        return false;
    }

    @Override
    public int getLevel(FluidState state) {
        return 0;
    }

    public static class Flowing extends MagneticFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }

    }

    public static class Still extends MagneticFluid {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }

    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(STILL_MAGNETIC_FLUID, FLOWING_MAGNETIC_FLUID, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0XA2A2A2
        ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), STILL_MAGNETIC_FLUID, FLOWING_MAGNETIC_FLUID);
    }

    public static void tick(LivingEntity entity) {
        FluidState fluidState = entity.getBlockStateAtPos().getFluidState();
        BlockState blockState = entity.getBlockStateAtPos();
        if ((fluidState.isIn(FluidTags.MAGNETIC_FLUID) && entity.isTouchingWater()) || blockState.isOf(BlockRegistries.MAGNETIC_FLUID_CAULDRON)) {
            double fluidHeight = fluidState.getHeight();
            int level = fluidState.getLevel();
            double multiplier = 0.9 - level * 0.05;
            if (fluidHeight > 0.0) {
                entity.setVelocity(entity.getVelocity().multiply(multiplier));
                if (!entity.world.isClient && !(entity instanceof PlayerEntity)) {
                    PlayerLookup.tracking(entity).forEach(player -> player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity)));
                }
            }
            StatusEffectInstance effect = entity.getStatusEffect(EffectRegistries.ATTRACT_EFFECT);
            int i;
            int b;
            if (entity.isSubmergedIn(FluidTags.MAGNETIC_FLUID)) {
                i = effect != null ? effect.getDuration() + 2 : 2;
                if (i > 60 * 5 * 20) {
                    i--;
                }
                b = Math.max(effect != null ? effect.getAmplifier() : 0, i / (60 * 20));
            } else {
                i = 2;
                b = 0;
            }
            entity.addStatusEffect(new StatusEffectInstance(EffectRegistries.ATTRACT_EFFECT, i, b, false, false, false));
        }
    }
}
