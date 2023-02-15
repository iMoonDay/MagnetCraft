package com.imoonday.magnetcraft.common.recipes;

import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Objects;

public class MagnetCraftingRecipe extends SpecialCraftingRecipe {
    public MagnetCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasModule = false;
        boolean hasTemplate = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM)) {
                    hasModule = true;
                } else if (stack.isOf(ItemRegistries.MAGNET_TEMPLATE_ITEM)) {
                    hasTemplate = true;
                } else {
                    return false;
                }
            }
        }
        return hasModule && hasTemplate;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        boolean hasModule = false;
        boolean hasTemplate = false;
        String module = "Empty";
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (!stack.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && !stack.isOf(ItemRegistries.MAGNET_TEMPLATE_ITEM)) {
                    return ItemStack.EMPTY;
                }
                if (stack.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && hasModule) {
                    return ItemStack.EMPTY;
                }
                if (stack.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && stack.getNbt() != null) {
                    hasModule = true;
                    module = stack.getNbt().getString("Module");
                }
                if (stack.isOf(ItemRegistries.MAGNET_TEMPLATE_ITEM) && hasTemplate) {
                    return ItemStack.EMPTY;
                }
                if (stack.isOf(ItemRegistries.MAGNET_TEMPLATE_ITEM)) {
                    hasTemplate = true;
                }
            }
        }
        if (hasModule && hasTemplate) {
            ItemStack stack;
            if (Objects.equals(module, "Electromagnet")) {
                stack = new ItemStack(ItemRegistries.ELECTROMAGNET_ITEM);
            } else if (Objects.equals(module, "PermanentMagnet")) {
                stack = new ItemStack(ItemRegistries.PERMANENT_MAGNET_ITEM);
            } else if (Objects.equals(module, "PolorMagnet")) {
                stack = new ItemStack(ItemRegistries.POLAR_MAGNET_ITEM);
            } else {
                stack = ItemStack.EMPTY;
            }
            NbtClassMethod.enabledSet(stack);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
