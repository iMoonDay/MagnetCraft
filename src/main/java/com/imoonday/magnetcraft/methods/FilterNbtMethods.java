package com.imoonday.magnetcraft.methods;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;

import static net.minecraft.item.Items.AIR;

public class FilterNbtMethods {
    public static void filterCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("Filter") || !stack.getNbt().contains("Whitelist") || !stack.getNbt().contains("CompareDamage") || !stack.getNbt().contains("CompareNbt") || !stack.getNbt().contains("Filterable")) {
            filterSet(stack);
        }
    }

    public static void filterSet(ItemStack stack) {
        if (!stack.getOrCreateNbt().contains("Filterable")) {
            stack.getOrCreateNbt().putBoolean("Filterable", false);
        }
        if (stack.getNbt() != null && stack.getNbt().getBoolean("Filterable")) {
            if (!stack.getOrCreateNbt().contains("Whitelist")) {
                stack.getOrCreateNbt().putBoolean("Whitelist", false);
            }
            if (!stack.getOrCreateNbt().contains("Filter")) {
                stack.getOrCreateNbt().put("Filter", new NbtList());
            }
            if (!stack.getOrCreateNbt().contains("CompareDamage")) {
                stack.getOrCreateNbt().putBoolean("CompareDamage", false);
            }
            if (!stack.getOrCreateNbt().contains("CompareNbt")) {
                stack.getOrCreateNbt().putBoolean("CompareNbt", false);
            }
        }
    }

    public static void setFilterItems(ItemStack stack, ArrayList<ItemStack> stacks) {
        filterCheck(stack);
        NbtList list = stack.getOrCreateNbt().getList("Filter", NbtElement.STRING_TYPE);
        list.clear();
        for (ItemStack otherStack : stacks) {
            NbtCompound otherStackNbt = otherStack.writeNbt(new NbtCompound());
            if (otherStack.isOf(AIR)) {
                continue;
            }
            otherStackNbt.putInt("Count", 1);
            if (!list.contains(otherStackNbt)) {
                list.add(otherStackNbt);
            }
        }
        stack.getOrCreateNbt().put("Filter", list);
    }

    public static void setBoolean(ItemStack stack, String key, boolean b) {
        stack.getOrCreateNbt().putBoolean(key, b);
    }

    public static void setBoolean(ItemStack stack, String key) {
        stack.getOrCreateNbt().putBoolean(key, !stack.getOrCreateNbt().getBoolean(key));
    }
}