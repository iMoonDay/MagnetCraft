package com.imoonday.magnetcraft.mixin;

import com.google.common.collect.Lists;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getPossibleEntries", at = @At("HEAD"), cancellable = true)
    private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl = stack.isOf(Items.BOOK);
        block0:
        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (enchantment.isTreasure() && !treasureAllowed || !enchantment.isAvailableForRandomSelection() || !enchantment.target.isAcceptableItem(item) && !bl) {
                continue;
            }
            boolean isNotAcceptable = EnchantmentRegistries.CHECK_ENCHANTMENTS.stream().anyMatch(enchantment1 -> isNotAcceptableItemStack(stack, enchantment, enchantment1));
            boolean isExcludedItem = isExcludedItem(enchantment, stack, Enchantments.UNBREAKING, ItemRegistries.POLAR_MAGNET_ITEM, ItemRegistries.PERMANENT_MAGNET_ITEM, ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM);
            if (isNotAcceptable || isExcludedItem) {
                continue;
            }
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) {
                    continue;
                }
                list.add(new EnchantmentLevelEntry(enchantment, i));
                continue block0;
            }
        }
        cir.setReturnValue(list);
    }

    private static boolean isNotAcceptableItemStack(ItemStack stack, Enchantment enchantment, Enchantment testEnchantment) {
        return enchantment == testEnchantment && !testEnchantment.isAcceptableItem(stack);
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean isExcludedItem(Enchantment enchantment, ItemStack stack, Enchantment checkEnchantment, Item... items) {
        return enchantment == checkEnchantment && Arrays.stream(items).anyMatch(stack::isOf);
    }

}
