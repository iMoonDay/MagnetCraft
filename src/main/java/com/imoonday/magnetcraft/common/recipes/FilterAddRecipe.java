package com.imoonday.magnetcraft.common.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import static com.imoonday.magnetcraft.registries.special.RecipeRegistries.*;
import static com.imoonday.magnetcraft.common.tags.ItemTags.FILTERABLE_MAGNETS;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.FILTER_MODULE_ITEM;

/**
 * @author iMoonDay
 */
public class FilterAddRecipe extends SpecialCraftingRecipe {

    public static final String FILTERABLE = "Filterable";

    public FilterAddRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasMagnet = false;
        boolean hasFilterModule = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.isIn(FILTERABLE_MAGNETS)) {
                    hasMagnet = true;
                } else if (stack.isOf(FILTER_MODULE_ITEM)) {
                    hasFilterModule = true;
                } else {
                    return false;
                }
            }
        }
        return hasMagnet && hasFilterModule;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        boolean hasMagnet = false;
        boolean hasFilterModule = false;
        ItemStack magnetStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (!stack.isIn(FILTERABLE_MAGNETS) && !stack.isOf(FILTER_MODULE_ITEM)) {
                    return ItemStack.EMPTY;
                }
                if (stack.isIn(FILTERABLE_MAGNETS) && hasMagnet) {
                    return ItemStack.EMPTY;
                }
                if (stack.isIn(FILTERABLE_MAGNETS)) {
                    hasMagnet = true;
                    magnetStack = stack.copy();
                }
                if (stack.isOf(FILTER_MODULE_ITEM)) {
                    hasFilterModule = true;
                }
            }
        }
        if (hasMagnet && hasFilterModule && magnetStack != ItemStack.EMPTY) {
            magnetStack.getOrCreateNbt().putBoolean(FILTERABLE, true);
            return magnetStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        return excludeItemRemainder(inventory, FILTERABLE_MAGNETS);
    }

    public static DefaultedList<ItemStack> excludeItemRemainder(CraftingInventory inventory, TagKey<Item> tags) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();
            if (!item.hasRecipeRemainder() || stack.isIn(tags)) {
                continue;
            }
            defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FILTER_ADD_RECIPE;
    }
}
