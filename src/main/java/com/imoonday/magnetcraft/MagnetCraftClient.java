package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.common.items.*;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.registries.special.IdentifierRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class MagnetCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        keyBindings();

        ElectroMagnetItem.register();
        MagnetControllerItem.register();
        PermanentMagnetItem.register();
        PolorMagnetItem.register();
        CreatureMagnetItem.register();

    }

    void keyBindings() {
        KeyBinding attractEnchantmentsSwitch = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.magnetcraft.controller",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "key.category.magnetcraft"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            PacketByteBuf buf = PacketByteBufs.create();
            while (attractEnchantmentsSwitch.wasPressed() && player != null && world != null) {
                if (!player.getItemCooldownManager().isCoolingDown(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                    for (int i = 0; i < 40; i++) {
                        if (player.getInventory().getStack(i).isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
//                            client.inGameHud.setOverlayMessage(Text.literal("测试"), false);
                            MagnetControllerItem.useTask(player, null, false);
                            buf.writeInt(0);
                            buf.retain();
                            ClientPlayNetworking.send(IdentifierRegistries.KEYBINDINGS_PACKET_ID, buf);
                            break;
                        }
                    }
                }
            }
        });
    }

}