package com.imoonday.magnetcraft.keybindings;

import com.imoonday.magnetcraft.MagnetCraft;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static void keyBindings() {
        KeyBinding attractEnchantmentsSwitch = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.controller",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "key.category.magnetcraft"));

        KeyBinding StickyKey = KeyBindingHelper.registerKeyBinding(
                new StickyKeyBinding(
                        "key.magnetcraft.sticky",
                        GLFW.GLFW_KEY_V,
                        "key.category.magnetcraft",
                        () -> true));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (attractEnchantmentsSwitch.wasPressed() && client.player != null) {
                boolean hasController = client.player.getInventory().contains(MagnetCraft.MAGNET_CONTROLLER_ITEM.getDefaultStack());
                if (hasController) {
                }
                if (MagnetCraft.TEST_MODE)
                    client.player.sendMessage(Text.literal("[调试]" + hasController), false);
//                ClientPlayNetworking.send(new Identifier("magnetcraft", "keybindings"), PacketByteBufs.empty());
            }

            if (StickyKey.isPressed() && client.player != null) {
                if (MagnetCraft.TEST_MODE)
                    client.player.sendMessage(Text.translatable("key.magnetcraft.sticky"), false);

            }
        });
    }
}