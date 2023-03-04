package com.imoonday.magnetcraft.registries.special;

import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;

public class IdentifierRegistries {
    public static final Identifier KEYBINDINGS_PACKET_ID = id("keybindings");
    public static final Identifier USE_CONTROLLER_PACKET_ID = id("use_controller");
    public static final Identifier BLACKLIST_PACKET_ID = id("blacklist");
    public static final Identifier WHITELIST_PACKET_ID = id("whitelist");
    public static final Identifier LODESTONE_PACKET_ID = id("lodestone");
    public static final Identifier CHANGE_FILTER_PACKET_ID = id("change_filter");
    public static final Identifier CHANGE_CORES_ENABLE_PACKET_ID = id("change_cores_enable");

    public static Identifier id(String id) {
        return new Identifier(MOD_ID, id);
    }
}
