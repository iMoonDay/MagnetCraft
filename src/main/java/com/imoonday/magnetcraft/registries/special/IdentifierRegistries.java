package com.imoonday.magnetcraft.registries.special;

import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;

public class IdentifierRegistries {
    public static final Identifier KEYBINDINGS_PACKET_ID = id("keybindings");
    public static final Identifier GET_DEGAUSSING_ENTITIES_PACKET_ID = id("get_other_entities");
    public static final Identifier GET_ENTITIES_PACKET_ID = id("get_entities");
    public static final Identifier USE_CONTROLLER_PACKET_ID = id("use_controller");
    public static Identifier id(String id) {
        return new Identifier(MOD_ID, id);
    }
}
