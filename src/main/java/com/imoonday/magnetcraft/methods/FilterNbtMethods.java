package com.imoonday.magnetcraft.methods;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.HashSet;

import static net.minecraft.item.Items.AIR;

public class FilterNbtMethods {
    public static void filterCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("Filter") || !stack.getNbt().contains("Whitelist")) {
            filterSet(stack);
        }
    }

    public static void filterSet(ItemStack stack) {
        if (!stack.getOrCreateNbt().contains("Whitelist")) {
            stack.getOrCreateNbt().putBoolean("Whitelist", false);
        }
        if (!stack.getOrCreateNbt().contains("Filter")) {
            stack.getOrCreateNbt().put("Filter", new NbtList());
        }
    }

    public static void setFilterItems(ItemStack stack, HashSet<ItemStack> stacks) {
        filterCheck(stack);
        stacks.remove(new ItemStack(AIR));
        NbtList list = stack.getOrCreateNbt().getList("Filter", NbtElement.STRING_TYPE);
        list.clear();
        for (ItemStack otherStack : stacks) {
            NbtCompound otherStackNbt = otherStack.writeNbt(new NbtCompound());
            otherStackNbt.putInt("Count", 1);
            if (!list.contains(otherStackNbt)) {
                list.add(otherStackNbt);
            }
        }
        stack.getOrCreateNbt().put("Filter", list);
    }

    public static void addFilterItem(ItemStack stack, ItemStack otherStack) {
        filterCheck(stack);
        if (otherStack.isOf(AIR)) {
            return;
        }
        NbtList list = stack.getOrCreateNbt().getList("Filter", NbtElement.COMPOUND_TYPE);
        NbtCompound otherStackNbt = otherStack.writeNbt(new NbtCompound());
        otherStackNbt.putInt("Count", 1);
        if (!list.contains(otherStackNbt)) {
            list.add(otherStackNbt);
        }
        stack.getOrCreateNbt().put("Filter", list);
    }

    public static void removeFilterItem(ItemStack stack, ItemStack otherStack) {
        filterCheck(stack);
        if (otherStack.isOf(AIR)) {
            return;
        }
        NbtList list = stack.getOrCreateNbt().getList("Filter", NbtElement.COMPOUND_TYPE);
        NbtCompound otherStackNbt = otherStack.writeNbt(new NbtCompound());
        otherStackNbt.putInt("Count", 1);
        list.remove(otherStackNbt);
        stack.getOrCreateNbt().put("Filter", list);
    }
}