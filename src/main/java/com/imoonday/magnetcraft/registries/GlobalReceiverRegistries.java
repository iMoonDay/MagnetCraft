package com.imoonday.magnetcraft.registries;

import com.imoonday.magnetcraft.items.MagnetControllerItem;
import com.imoonday.magnetcraft.keybindings.KeyBindings;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static com.imoonday.magnetcraft.MagnetCraft.TEST_MODE;
import static com.imoonday.magnetcraft.registries.IdentifierRegistries.KEYBINDINGS_PACKET_ID;
import static com.imoonday.magnetcraft.registries.IdentifierRegistries.PLAYER_TAG_PACKET_ID;

public class GlobalReceiverRegistries {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            World world = player.getWorld();
            int task = buf.readInt();
            if (TEST_MODE) {
                String message = "[服务器调试] Buf: " + task;
                player.sendMessage(Text.literal(message));
            }
            if (task == 0) {
                MagnetControllerItem.useTask(world, player, null, false);
            } else if (task == 1) {
                KeyBindings.stickyKeyServerTask(player);
            } else {
                String message = "[服务器调试] Error: " + buf;
                player.sendMessage(Text.literal(message));
            }
            buf.release();
        }));

        ClientPlayNetworking.registerGlobalReceiver(PLAYER_TAG_PACKET_ID, (client, handler, buf, responseSender) -> client.execute(() -> {

        }));
    }
}
