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

    public static final String CATEGORY = "key.category.magnetcraft";

    public static void registerClient() {
        KeyBinding attractEnchantmentsSwitch = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.controller",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        CATEGORY));

        KeyBinding addOrRemoveBlacklist = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.blacklist",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_MINUS,
                        CATEGORY));

        KeyBinding addOrRemoveWhitelist = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.whitelist",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_EQUAL,
                        CATEGORY));

        KeyBinding changeMagneticLevitationMode = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.magnetic_levitation_mode",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_LEFT_ALT,
                        CATEGORY));

        KeyBinding changeAutomaticLevitation = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.automatic_levitation",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_ALT,
                        CATEGORY));

        KeyBinding openBackpack = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.open_backpack",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_B,
                        CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            if (player == null || world == null) {
                return;
            }
            while (attractEnchantmentsSwitch.wasPressed()) {
                ClientPlayNetworking.send(KEYBINDINGS_PACKET_ID, PacketByteBufs.empty());
            }
            while (addOrRemoveBlacklist.wasPressed()) {
                ClientPlayNetworking.send(BLACKLIST_PACKET_ID, PacketByteBufs.empty());
            }
            while (addOrRemoveWhitelist.wasPressed()) {
                ClientPlayNetworking.send(WHITELIST_PACKET_ID, PacketByteBufs.empty());
            }
            while (changeMagneticLevitationMode.wasPressed()) {
                ClientPlayNetworking.send(MAGNETIC_LEVITATION_MODE_PACKET_ID, PacketByteBufs.empty());
            }
            while (changeAutomaticLevitation.wasPressed()) {
                ClientPlayNetworking.send(AUTOMATIC_LEVITATION_PACKET_ID, PacketByteBufs.empty());
            }
            while (openBackpack.wasPressed()) {
                ClientPlayNetworking.send(OPEN_BACKPACK_PACKET_ID, PacketByteBufs.empty());
            }
        });
    }
}
