package com.imoonday.magnetcraft.registries.special;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import org.lwjgl.glfw.GLFW;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;

public class KeyBindingRegistries {
    public static void registerClient() {
        KeyBinding attractEnchantmentsSwitch = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.controller",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "key.category.magnetcraft"));

        KeyBinding addOrRemoveBlacklist = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.blacklist",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_MINUS,
                        "key.category.magnetcraft"));

        KeyBinding addOrRemoveWhitelist = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.whitelist",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_EQUAL,
                        "key.category.magnetcraft"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            while (attractEnchantmentsSwitch.wasPressed() && player != null && world != null) {
                ClientPlayNetworking.send(KEYBINDINGS_PACKET_ID, PacketByteBufs.empty());
            }
            while (addOrRemoveBlacklist.wasPressed() && player != null && world != null) {
                ClientPlayNetworking.send(BLACKLIST_PACKET_ID, PacketByteBufs.empty());
            }
            while (addOrRemoveWhitelist.wasPressed() && player != null && world != null) {
                ClientPlayNetworking.send(WHITELIST_PACKET_ID, PacketByteBufs.empty());
            }
        });
    }
}
