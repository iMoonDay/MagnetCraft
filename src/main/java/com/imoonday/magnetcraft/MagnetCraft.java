package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.*;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import com.imoonday.magnetcraft.registries.special.GlobalReceiverRegistries;
import com.imoonday.magnetcraft.registries.special.ItemGroupRegistries;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagnetCraft implements ModInitializer {
    public static final String MOD_ID = "magnetcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
//    public static final RecipeSerializer<MagnetCraftingRecipe> MAGNET_CRAFTING_RECIPE = RecipeSerializer.register("magnet_crafting",new SpecialRecipeSerializer<>(MagnetCraftingRecipe::new));
//    public static final RecipeSerializer<ModuleCraftingRecipe> MODULE_CRAFTING_RECIPE = RecipeSerializer.register("module_crafting",new SpecialRecipeSerializer<>(ModuleCraftingRecipe::new));

    @Override
    public void onInitialize() {

        ModConfig.register();

        ItemRegistries.register();
        BlockRegistries.register();
        EffectRegistries.register();
        PotionRegistries.register();
        EnchantmentRegistries.register();
        ItemGroupRegistries.register();
        GlobalReceiverRegistries.register();
        CustomStatRegistries.register();

    }
}