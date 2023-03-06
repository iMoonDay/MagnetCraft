package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.FilterNbtMethods;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

import static com.imoonday.magnetcraft.common.items.MagnetControllerItem.changeMagnetEnable;
import static com.imoonday.magnetcraft.common.items.magnets.MineralMagnetItem.EnabledCoreCheck;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;

public class ServerReceiverRegistries {

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

        ServerPlayNetworking.registerGlobalReceiver(LODESTONE_PACKET_ID, (server, player, handler, buf, packetSender) -> {
            BlockPos pos = buf.readBlockPos();
            int method = buf.readInt();
            server.execute(() -> {
                BlockEntity entity = player.world.getBlockEntity(pos);
                if (player.world.getBlockState(pos).isOf(BlockRegistries.LODESTONE_BLOCK) && entity != null) {
                    NbtCompound nbt = entity.createNbt();
                    final int disEachClick = ModConfig.getValue().disEachClick;
                    switch (method) {
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
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_FILTER_PACKET_ID, (server, player, handler, buf, packetSender) -> {
            int method;
            int slot;
            boolean b;
            String key;
            boolean onButton = buf.readBoolean();
            NbtCompound nbt = buf.readNbt();
            if (onButton) {
                method = buf.readInt();
                slot = buf.readInt();
                key = buf.readString();
                if (method == 1) {
                    b = buf.readBoolean();
                } else {
                    b = false;
                }
            } else {
                b = false;
                method = -1;
                key = null;
                slot = buf.readInt();
            }
            server.execute(() -> {
                FilterNbtMethods.setNbt(player, slot, nbt);
                if (onButton) {
                    switch (method) {
                        case 1 -> FilterNbtMethods.setBoolean(player, slot, key, b);
                        case 2 -> {
                            ItemStack stack = slot != -1 ? player.getInventory().getStack(slot) : player.getOffHandStack();
                            if (stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) && Objects.equals(key, "Enable")) {
                                changeMagnetEnable(player);
                            } else
                                FilterNbtMethods.setBoolean(player, slot, key);
                        }
                    }
                }
                player.getInventory().markDirty();
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_CORES_ENABLE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            String id = buf.readString();
            int slot = buf.readInt();
            ItemStack stack = slot != -1 ? player.getInventory().getStack(slot) : player.getOffHandStack();
            server.execute(() -> EnabledCoreCheck(stack, id));
        });

    }
}
