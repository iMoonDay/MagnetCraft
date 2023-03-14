package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;

public class C2SRegistries {

    @SuppressWarnings("RedundantCast")
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (!player.getItemCooldownManager().isCoolingDown(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                if (player.getInventory().containsAny(stack -> stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) || ((Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) && stack.getNbt() != null && stack.getNbt().getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).anyMatch(nbtCompound -> nbtCompound.getString("id").equals(Registries.ITEM.getId(ItemRegistries.MAGNET_CONTROLLER_ITEM).toString()))))) {
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