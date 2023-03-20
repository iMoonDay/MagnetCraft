package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import static com.imoonday.magnetcraft.registries.common.EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;
@SuppressWarnings("RedundantCast")
public class GlobalReceiverRegistries {

    public static void serverPlayNetworkingRegister() {
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (!player.getItemCooldownManager().isCoolingDown(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                if (player.getInventory().containsAny(stack -> stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) || ((Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) && stack.getNbt() != null && stack.getNbt().getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).anyMatch(nbtCompound -> nbtCompound.getString("id").equals(Registries.ITEM.getId(ItemRegistries.MAGNET_CONTROLLER_ITEM).toString()))))) {
                    PacketByteBuf newBuf = PacketByteBufs.create();
                    newBuf.writeBoolean(!((MagnetCraftEntity) player).getEnable());
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

        ServerPlayNetworking.registerGlobalReceiver(MAGNETIC_LEVITATION_MODE_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (MagnetCraft.EnchantmentMethods.hasEnchantment(player, EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT)) {
                boolean mode = !((MagnetCraftEntity) player).getMagneticLevitationMode();
                ((MagnetCraftEntity) player).setMagneticLevitationMode(mode);
                String OnOff = mode ? "on" : "off";
                player.sendMessage(Text.translatable("text.magnetcraft.message.magnetic_levitation_mode." + OnOff), true);
                if (!mode && !player.getAbilities().flying && MagnetCraft.EnchantmentMethods.hasEnchantment(player, EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT) && !player.isOnGround()) {
                    player.setNoGravity(false);
                }
                PacketByteBuf clientBuf = PacketByteBufs.create();
                clientBuf.writeBoolean(mode);
                ServerPlayNetworking.send(player, MAGNETIC_LEVITATION_MODE_PACKET_ID, clientBuf);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(JUMPING_PACKET_ID, (server, player, handler, buf, packetSender) -> {
            boolean jumping = buf.readBoolean();
            server.execute(() -> player.jumping = jumping);
        });

        ServerPlayNetworking.registerGlobalReceiver(AUTOMATIC_LEVITATION_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (MagnetCraft.EnchantmentMethods.hasEnchantment(player, EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT)) {
                boolean enable = !((MagnetCraftEntity) player).getAutomaticLevitation();
                ((MagnetCraftEntity) player).setAutomaticLevitation(enable);
                String OnOff = enable ? "on" : "off";
                player.sendMessage(Text.translatable("text.magnetcraft.message.automatic_levitation." + OnOff));
                PacketByteBuf clientBuf = PacketByteBufs.create();
                clientBuf.writeBoolean(enable);
                ServerPlayNetworking.send(player, AUTOMATIC_LEVITATION_PACKET_ID, clientBuf);
            }
        }));

    }

    public static void clientPlayNetworkingRegister() {
        ClientPlayNetworking.registerGlobalReceiver(USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean enable = buf.readBoolean();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    ((MagnetCraftEntity) player).setEnable(enable);
                    SoundEvent sound = enable ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE;
                    player.getInventory().markDirty();
                    player.playSound(sound, 1, 1);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(MAGNETIC_LEVITATION_MODE_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean mode = buf.readBoolean();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    ((MagnetCraftEntity) player).setMagneticLevitationMode(mode);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AUTOMATIC_LEVITATION_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean enable = buf.readBoolean();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    ((MagnetCraftEntity) player).setAutomaticLevitation(enable);
                }
            });
        });

    }

}
