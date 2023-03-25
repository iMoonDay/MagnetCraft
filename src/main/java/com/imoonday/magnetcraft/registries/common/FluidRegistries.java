package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.fluids.MagneticFluid;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class FluidRegistries {

    public static final FlowableFluid STILL_MAGNETIC_FLUID = register("magnetic_fluid", new MagneticFluid.Still());
    public static final FlowableFluid FLOWING_MAGNETIC_FLUID = register("flowing_magnetic_fluid", new MagneticFluid.Flowing());
    public static final Item MAGNETIC_FLUID_BUCKET = ItemRegistries.register("magnetic_fluid_bucket", new BucketItem(STILL_MAGNETIC_FLUID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Block MAGNETIC_FLUID = BlockRegistries.registerBlock("magnetic_fluid", new FluidBlock(STILL_MAGNETIC_FLUID, FabricBlockSettings.copy(Blocks.WATER)) {
    }, false);

    public static void register() {
        itemDispenserBehaviorRegister();
        MagnetCraft.LOGGER.info("FluidRegistries.class Loaded");
    }

    private static void itemDispenserBehaviorRegister() {
        ItemDispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
            private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                FluidModificationItem fluidModificationItem = (FluidModificationItem) stack.getItem();
                BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                ServerWorld world = pointer.getWorld();
                if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
                    fluidModificationItem.onEmptied(null, world, stack, blockPos);
                    return new ItemStack(Items.BUCKET);
                }
                return this.fallbackBehavior.dispense(pointer, stack);
            }
        };
        DispenserBlock.registerBehavior(MAGNETIC_FLUID_BUCKET, dispenserBehavior);
    }

    static <T extends FlowableFluid> T register(String id, T fluid) {
        Registry.register(Registries.FLUID, id(id), fluid);
        return fluid;
    }

}
