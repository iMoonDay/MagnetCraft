package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.recipes.CoresAddRecipe;
import com.imoonday.magnetcraft.common.recipes.FilterAddRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;

public class RecipeRegistries {

    public static final RecipeSerializer<CoresAddRecipe> CORES_ADD_RECIPE = RecipeSerializer.register("cores_add", new SpecialRecipeSerializer<>(CoresAddRecipe::new));
    public static final RecipeSerializer<FilterAddRecipe> FILTER_ADD_RECIPE = RecipeSerializer.register("filter_add", new SpecialRecipeSerializer<>(FilterAddRecipe::new));

    public static void register(){
        System.out.println("RecipeRegistries.class Loaded");
    }

}
