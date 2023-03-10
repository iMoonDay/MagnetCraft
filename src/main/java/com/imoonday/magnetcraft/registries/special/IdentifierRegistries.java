package com.imoonday.magnetcraft.registries.special;

import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;

public class IdentifierRegistries {
    public static final Identifier KEYBINDINGS_PACKET_ID = id("keybindings");
    public static final Identifier USE_CONTROLLER_PACKET_ID = id("use_controller");
    public static final Identifier BLACKLIST_PACKET_ID = id("blacklist");
    public static final Identifier WHITELIST_PACKET_ID = id("whitelist");

    public static Identifier id(String id) {
        return new Identifier(MOD_ID, id);
    }
}
