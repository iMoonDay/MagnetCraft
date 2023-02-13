package com.imoonday.magnetcraft.keybindings;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.items.MagnetControllerItem;
import com.imoonday.magnetcraft.registries.EffectRegistries;
import com.imoonday.magnetcraft.registries.IdentifierRegistries;
import com.imoonday.magnetcraft.registries.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
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
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            PacketByteBuf buf = PacketByteBufs.create();
            while (attractEnchantmentsSwitch.wasPressed() && player != null && world != null) {
                if (!player.getItemCooldownManager().isCoolingDown(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                    for (int i = 0; i < 40; i++) {
                        if (player.getInventory().getStack(i).isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                            MagnetControllerItem.useTask(player, null, false);
                            buf.writeInt(0);
                            buf.retain();
                            ClientPlayNetworking.send(IdentifierRegistries.KEYBINDINGS_PACKET_ID, buf);
                            break;
                        }
                    }
                }
            }

            if (StickyKey.isPressed() && player != null) {
                boolean debugMode = AutoConfig.getConfigHolder(ModConfig.class).getConfig().debugMode;
                if (debugMode)
                    player.sendMessage(Text.translatable("key.magnetcraft.sticky"), false);
                buf.writeInt(1);
                buf.retain();
                ClientPlayNetworking.send(IdentifierRegistries.KEYBINDINGS_PACKET_ID, buf);
            }
        });
    }

    public static void stickyKeyServerTask(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance
                (EffectRegistries.ATTRACT_EFFECT, 20, 0, false, false, true));
    }
}