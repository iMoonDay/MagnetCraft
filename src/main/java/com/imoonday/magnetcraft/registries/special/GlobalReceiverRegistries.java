package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.screen.FilterableMagnetScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;

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

        ServerPlayNetworking.registerGlobalReceiver(LODESTONE_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            BlockPos pos = buf.readBlockPos();
            int taskNum = buf.readInt();
            BlockEntity entity = player.world.getBlockEntity(pos);
            if (player.world.getBlockState(pos).isOf(BlockRegistries.LODESTONE_BLOCK) && entity != null) {
                NbtCompound nbt = entity.createNbt();
                final int disEachClick = ModConfig.getValue().disEachClick;
                switch (taskNum) {
                    case 0 -> {
                        nbt.putBoolean("redstone", !entity.createNbt().getBoolean("redstone"));
                        if (entity.createNbt().getBoolean("redstone")) {
                            nbt.putDouble("dis", 0);
                        }
                    }
                    case 1 -> {
                        if (!entity.createNbt().getBoolean("redstone")) {
                            nbt.putDouble("dis", entity.createNbt().getDouble("dis") - disEachClick >= 0 ? entity.createNbt().getDouble("dis") - disEachClick : 0);
                        }
                    }
                    case 2 -> {
                        if (!entity.createNbt().getBoolean("redstone")) {
                            nbt.putDouble("dis", entity.createNbt().getDouble("dis") + disEachClick <= ModConfig.getValue().lodestoneMaxDis ? entity.createNbt().getDouble("dis") + disEachClick : ModConfig.getValue().lodestoneMaxDis);
                        }
                    }
                }
                entity.readNbt(nbt);
                entity.markDirty();
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_FILTER_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            boolean onButton = buf.readBoolean();
            if (onButton) {
                NbtCompound nbt = buf.readNbt();
                int method = buf.readInt();
                int slot = buf.readInt();
                String key = buf.readString();
                FilterableMagnetScreen.setNbt(player, slot, nbt);
                switch (method) {
                    case 1 -> {
                        boolean b = buf.readBoolean();
                        FilterableMagnetScreen.setBoolean(player, slot, key, b);
                    }
                    case 2 -> {
                        FilterableMagnetScreen.setBoolean(player, slot, key);
                    }
                }
            } else {
                NbtCompound nbt = buf.readNbt();
                int slot = buf.readInt();
                FilterableMagnetScreen.setNbt(player, slot, nbt);
            }
            player.getInventory().markDirty();
        }));

        ClientPlayNetworking.registerGlobalReceiver(USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> client.execute(() -> {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                MagnetControllerItem.useTask(player, null, false);
            }
        }));

    }

}
