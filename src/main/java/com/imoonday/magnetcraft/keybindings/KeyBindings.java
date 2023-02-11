package com.imoonday.magnetcraft.keybindings;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.registries.EffectRegistries;
import com.imoonday.magnetcraft.registries.IdentifierRegistries;
import com.imoonday.magnetcraft.registries.ItemRegistries;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
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
            PacketByteBuf buf = PacketByteBufs.create();
            while (attractEnchantmentsSwitch.wasPressed() && client.player != null) {
                boolean hasController = client.player.getInventory().contains(ItemRegistries.MAGNET_CONTROLLER_ITEM.getDefaultStack());
                if (hasController) {
                    buf.writeInt(0);
                    buf.retain();
                    ClientPlayNetworking.send(IdentifierRegistries.KEYBINDINGS_PACKET_ID, buf);
                }
//                if (MagnetCraft.TEST_MODE)
//                    client.player.sendMessage(Text.literal("[调试]" + hasController), false);
            }

            if (StickyKey.isPressed() && client.player != null) {
                if (MagnetCraft.TEST_MODE)
                    client.player.sendMessage(Text.translatable("key.magnetcraft.sticky"), false);
                buf.writeInt(1);
                buf.retain();
                ClientPlayNetworking.send(IdentifierRegistries.KEYBINDINGS_PACKET_ID, buf);
            }
        });
    }

    public static void stickyKeyServerTask(ServerPlayerEntity player){
        player.addStatusEffect(new StatusEffectInstance
                (EffectRegistries.ATTRACT_EFFECT,20,0,false,false,true));
    }
}