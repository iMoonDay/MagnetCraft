package com.imoonday.magnetcraft.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MagnetRecipe implements CraftingRecipe {

    @Override
    public CraftingRecipeCategory getCategory() {
        return null;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return null;
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
