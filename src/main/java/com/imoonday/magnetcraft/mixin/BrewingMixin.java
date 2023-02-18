package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.registries.common.PotionRegistries;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingMixin {

    @Inject(at = @At("HEAD"), method = "registerDefaults")
    private static void registerDefaults(CallbackInfo ci) {
        BrewingMixin.registerPotionRecipe(Potions.AWKWARD, ItemRegistries.MAGNET_POWDER, PotionRegistries.ATTRACT_POTION);
        BrewingMixin.registerPotionRecipe(Potions.AWKWARD, Items.FLINT, PotionRegistries.DEGAUSSING_POTION);
    }

    @Invoker("registerPotionRecipe")
    public static void registerPotionRecipe(Potion input, Item item, Potion output) {
    }
}
