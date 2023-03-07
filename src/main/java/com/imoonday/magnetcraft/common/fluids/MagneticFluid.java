package com.imoonday.magnetcraft.common.fluids;

import com.imoonday.magnetcraft.api.AbstractMagneticFluid;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
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

    public static void registerClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(STILL_MAGNETIC_FLUID, FLOWING_MAGNETIC_FLUID, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0XA2A2A2
        ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), STILL_MAGNETIC_FLUID, FLOWING_MAGNETIC_FLUID);
    }
}
