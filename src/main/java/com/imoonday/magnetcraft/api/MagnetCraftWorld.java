package com.imoonday.magnetcraft.api;

import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * @author iMoonDay
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface MagnetCraftWorld {
    default void attractItems(Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {}

}
