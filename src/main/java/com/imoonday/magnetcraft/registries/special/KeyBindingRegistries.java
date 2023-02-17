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

public class KeyBindingRegistries {
    public static void register() {
        KeyBinding attractEnchantmentsSwitch = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.controller",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "key.category.magnetcraft"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            while (attractEnchantmentsSwitch.wasPressed() && player != null && world != null) {
                ClientPlayNetworking.send(IdentifierRegistries.KEYBINDINGS_PACKET_ID, PacketByteBufs.empty());
            }
        });
    }
}
