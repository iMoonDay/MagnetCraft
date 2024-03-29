package com.imoonday.magnetcraft.common.recipes;

import com.imoonday.magnetcraft.common.items.magnets.MineralMagnetItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import static com.imoonday.magnetcraft.registries.special.RecipeRegistries.*;
import static com.imoonday.magnetcraft.common.tags.ItemTags.CORES;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.MINERAL_MAGNET_ITEM;

public class CoresAddRecipe extends SpecialCraftingRecipe {

    public CoresAddRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasMagnet = false;
        boolean hasCores = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.isOf(MINERAL_MAGNET_ITEM)) {
                    hasMagnet = true;
                } else if (stack.isIn(CORES)) {
                    hasCores = true;
                } else {
                    return false;
                }
            }
        }
        return hasMagnet && hasCores;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        boolean hasMagnet = false;
        boolean hasCores = false;
        int count = 0;
        Item[] cores = new Item[inventory.size()];
        ItemStack magnetStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (!stack.isOf(MINERAL_MAGNET_ITEM) && !stack.isIn(CORES)) {
                    return ItemStack.EMPTY;
                }
                if (stack.isOf(MINERAL_MAGNET_ITEM) && hasMagnet) {
                    return ItemStack.EMPTY;
                }
                if (stack.isOf(MINERAL_MAGNET_ITEM)) {
                    hasMagnet = true;
                    magnetStack = stack.copy();
                }
                for (Item core : cores) {
                    if (core == stack.getItem()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (stack.isIn(CORES)) {
                    hasCores = true;
                    count++;
                    cores[i] = stack.getItem();
                }
            }
        }
        Item[] items = new Item[count];
        if (hasMagnet && hasCores) {
            int num = 0;
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) {
                    if (stack.isIn(CORES)) {
                        items[num] = stack.getItem();
                        num++;
                        if (num >= count) {
                            break;
                        }
                    }
                }
            }
            MineralMagnetItem.coresSet(magnetStack, items);
            return magnetStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CORES_ADD_RECIPE;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        return excludeItemRemainder(inventory, MINERAL_MAGNET_ITEM);
    }

    public static DefaultedList<ItemStack> excludeItemRemainder(CraftingInventory inventory, Item excludeItem){
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = inventory.getStack(i).getItem();
            if (!item.hasRecipeRemainder() || item == excludeItem) {
                continue;
            }
            defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
        }
        return defaultedList;
    }

}
