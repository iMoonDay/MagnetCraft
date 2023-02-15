package com.imoonday.magnetcraft.common.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class CraftingModuleItem extends Item {
    public CraftingModuleItem(Settings settings) {
        super(settings);
    }

//    @Override
//    public ItemStack getDefaultStack() {
//        ItemStack stack = super.getDefaultStack();
//        NbtCompound nbt = new NbtCompound();
//        nbt.putString("Module", "Empty");
//        stack.setNbt(nbt);
//        return stack;
//    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        String key = itemStack.getTranslationKey() + "_tooltip";
        tooltip.add(Text.translatable(key).formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }
}