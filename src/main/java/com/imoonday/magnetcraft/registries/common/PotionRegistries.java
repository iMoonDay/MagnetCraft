package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Arrays;

import static com.imoonday.magnetcraft.registries.common.EffectRegistries.*;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;
import static com.imoonday.magnetcraft.registries.special.ItemGroupRegistries.MAGNET_OHTERS;

@SuppressWarnings("unused")
public class PotionRegistries {

    public static final int DEFAULT_DURATION = 5 * 60 * 20;
    public static final Item[] ITEMS = {Items.TIPPED_ARROW, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};

    public static final Potion ATTRACT_POTION = register("attract", ATTRACT_EFFECT, Potions.AWKWARD, ItemRegistries.MAGNET_POWDER);
    public static final Potion DEGAUSSING_POTION = register("degaussing", DEGAUSSING_EFFECT, Potions.AWKWARD, ItemRegistries.DEMAGNETIZED_POWDER_ITEM);
    public static final Potion UNATTRACT_POTION = register("unattract", UNATTRACT_EFFECT);

    public static void register() {
        MagnetCraft.LOGGER.info("PotionRegistries.class Loaded");
    }

    static Potion register(String id, StatusEffect effect, Potion neededPotion, Item neededItem) {
        Potion potion = new Potion(new StatusEffectInstance(effect, DEFAULT_DURATION));
        Arrays.stream(ITEMS).toList().forEach(item -> ItemGroupEvents.modifyEntriesEvent(MAGNET_OHTERS).register(content -> content.add(PotionUtil.setPotion(new ItemStack(item), potion))));
        if (neededPotion != null && neededItem != null) {
            BrewingRecipeRegistry.registerPotionRecipe(neededPotion, neededItem, potion);
        }
        return Registry.register(Registries.POTION, id(id), potion);
    }

    static Potion register(String id, StatusEffect effect) {
        Potion potion = new Potion(new StatusEffectInstance(effect, DEFAULT_DURATION));
        Arrays.stream(ITEMS).toList().forEach(item -> ItemGroupEvents.modifyEntriesEvent(MAGNET_OHTERS).register(content -> content.add(PotionUtil.setPotion(new ItemStack(item), potion))));
        return Registry.register(Registries.POTION, id(id), potion);
    }

}
