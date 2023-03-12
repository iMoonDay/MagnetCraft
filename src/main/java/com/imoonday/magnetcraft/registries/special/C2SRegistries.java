package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;

public class C2SRegistries {

    @SuppressWarnings("RedundantCast")
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (!player.getItemCooldownManager().isCoolingDown(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                if (player.getInventory().containsAny(stack -> stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM))) {
                    PacketByteBuf newBuf = PacketByteBufs.create();
                    newBuf.writeBoolean(!((EntityAttractNbt) player).getEnable());
                    MagnetControllerItem.useController(player, null, false);
                    player.getInventory().markDirty();
                    ServerPlayNetworking.send(player, IdentifierRegistries.USE_CONTROLLER_PACKET_ID, newBuf);
                }
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(BLACKLIST_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (player.hasPermissionLevel(4)) {
                CommandRegistries.itemListHandling(player, null, CommandRegistries.ListType.BLACKLIST, null);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(WHITELIST_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (player.hasPermissionLevel(4)) {
                CommandRegistries.itemListHandling(player, null, CommandRegistries.ListType.WHITELIST, null);
            }
        }));

    }
}
