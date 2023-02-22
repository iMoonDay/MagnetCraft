package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.KEYBINDINGS_PACKET_ID;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.USE_CONTROLLER_PACKET_ID;

public class GlobalReceiverRegistries {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (!player.getItemCooldownManager().isCoolingDown(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                for (int i = 0; i < 40; i++) {
                    if (player.getInventory().getStack(i).isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                        MagnetControllerItem.useTask(player, null, false);
                        ServerPlayNetworking.send(player, IdentifierRegistries.USE_CONTROLLER_PACKET_ID, PacketByteBufs.empty());
                        break;
                    }
                }
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> client.execute(() -> {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                MagnetControllerItem.useTask(player, null, false);
            }
        }));

    }
}
