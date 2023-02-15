package com.imoonday.magnetcraft.common.recipes;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModuleCraftingRecipe extends SpecialCraftingRecipe {
    public ModuleCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        ItemStack stack2 = inventory.getStack(1);
        ItemStack stack4 = inventory.getStack(3);
        ItemStack stack5 = inventory.getStack(4);
        ItemStack stack6 = inventory.getStack(5);
        ItemStack stack8 = inventory.getStack(7);
        boolean polar = stack2.isOf(Items.REDSTONE) && stack4.isOf(Items.REDSTONE) && stack5.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && stack6.isOf(Items.REDSTONE) && stack8.isOf(Items.REDSTONE);
        boolean electro = stack2.isOf(Items.ENDER_PEARL) && stack4.isOf(Items.LAPIS_LAZULI) && stack5.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && stack6.isOf(Items.REDSTONE) && stack8.isOf(Items.DIAMOND);
        boolean permanent = stack2.isOf(Items.ENDER_PEARL) && stack4.isOf(Items.DIAMOND) && stack5.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && stack6.isOf(Items.DIAMOND) && stack8.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_INGOT);

        return polar || electro || permanent;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack stack2 = inventory.getStack(1);
        ItemStack stack4 = inventory.getStack(3);
        ItemStack stack5 = inventory.getStack(4);
        ItemStack stack6 = inventory.getStack(5);
        ItemStack stack8 = inventory.getStack(7);
        ItemStack stack = new ItemStack(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM);
        boolean polar = stack2.isOf(Items.REDSTONE) && stack4.isOf(Items.REDSTONE) && stack5.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && stack6.isOf(Items.REDSTONE) && stack8.isOf(Items.REDSTONE);
        boolean electro = stack2.isOf(Items.ENDER_PEARL) && stack4.isOf(Items.LAPIS_LAZULI) && stack5.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && stack6.isOf(Items.REDSTONE) && stack8.isOf(Items.DIAMOND);
        boolean permanent = stack2.isOf(Items.ENDER_PEARL) && stack4.isOf(Items.DIAMOND) && stack5.isOf(ItemRegistries.EMPTY_CRAFTING_MODULE_ITEM) && stack6.isOf(Items.DIAMOND) && stack8.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_INGOT);
        NbtCompound nbt = new NbtCompound();
        NbtCompound nbt1 = new NbtCompound();
        NbtString string;
        NbtList list = new NbtList();
        if (polar || electro || permanent) {
            if (polar) {
                nbt.putString("Module", "PolorMagnet");
                string = NbtString.of(NbtString.escape("无极磁铁"));

            } else if (electro) {
                nbt.putString("Module", "Electromagnet");
                string = NbtString.of(NbtString.escape("电磁铁"));
            } else {
                nbt.putString("Module", "PermanentMagnet");
                string = NbtString.of(NbtString.escape("永磁铁"));
            }
            list.add(string);
            nbt1.put("Lore",list);
            nbt.put("display",nbt1);
            stack.setNbt(nbt);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
