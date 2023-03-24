package com.imoonday.magnetcraft.registries.special;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
public class CustomStatRegistries {

    public static final Identifier ITEMS_TELEPORTED_TO_PLAYER = id("items_teleported_to_player");

    public static void register(){
        Registry.register(Registries.CUSTOM_STAT, "items_teleported_to_player", ITEMS_TELEPORTED_TO_PLAYER);
        Stats.CUSTOM.getOrCreateStat(ITEMS_TELEPORTED_TO_PLAYER, StatFormatter.DEFAULT);}
}
