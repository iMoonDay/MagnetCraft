package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.fluids.MagneticFluid;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class FluidRegistries {

    public static FlowableFluid STILL_MAGNETIC_FLUID = register("magnetic_fluid", new MagneticFluid.Still());
    public static FlowableFluid FLOWING_MAGNETIC_FLUID = register("flowing_magnetic_fluid", new MagneticFluid.Flowing());
    public static Item MAGNETIC_FLUID_BUCKET = ItemRegistries.register("magnetic_fluid_bucket", new BucketItem(STILL_MAGNETIC_FLUID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static Block MAGNETIC_FLUID = BlockRegistries.register("magnetic_fluid", new FluidBlock(STILL_MAGNETIC_FLUID, FabricBlockSettings.copy(Blocks.WATER)) {});

    public static void register() {
        MagnetCraft.LOGGER.info("FluidRegistries.class Loaded");
    }

    static <T extends FlowableFluid> T register(String id, T fluid) {
        Registry.register(Registries.FLUID, id(id), fluid);
        return fluid;
    }

}
