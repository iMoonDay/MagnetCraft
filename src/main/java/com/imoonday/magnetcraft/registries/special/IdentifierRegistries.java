package com.imoonday.magnetcraft.registries.special;

import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;

public class IdentifierRegistries {
    public static final Identifier KEYBINDINGS_PACKET_ID = id("keybindings");
    public static final Identifier USE_CONTROLLER_PACKET_ID = id("use_controller");
    public static final Identifier BLACKLIST_PACKET_ID = id("blacklist");
    public static final Identifier WHITELIST_PACKET_ID = id("whitelist");
    public static final Identifier MAGNETIC_LEVITATION_MODE_PACKET_ID = id("magnetic_levitation_mode");
    public static final Identifier JUMPING_PACKET_ID = id("jumping");
    public static final Identifier AUTOMATIC_LEVITATION_PACKET_ID = id("automatic_levitation");
    public static final Identifier OPEN_BACKPACK_PACKET_ID = id("open_backpack");

    public static Identifier id(String id) {
        return new Identifier(MOD_ID, id);
    }
}
