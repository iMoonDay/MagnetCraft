package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.common.items.armors.MagneticShulkerBackpackItem;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.registries.common.EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;

public class GlobalReceiverRegistries {

    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String ITEMS = "Items";
    public static final String ID = "id";
    public static final String ON = "on";
    public static final String OFF = "off";

    public static void serverPlayNetworkingRegister() {
        registerServer(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (!player.getItemCooldownManager().isCoolingDown(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                boolean contains = player.getInventory().containsAny(stack -> stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) || ((Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock || stack.getItem() instanceof MagneticShulkerBackpackItem) && stack.getNbt() != null && stack.getNbt().getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).anyMatch(nbtCompound -> nbtCompound.getString(ID).equals(Registries.ITEM.getId(ItemRegistries.MAGNET_CONTROLLER_ITEM).toString()))));
                if (contains) {
                    PacketByteBuf newBuf = PacketByteBufs.create();
                    newBuf.writeBoolean(!player.getEnable());
                    MagnetControllerItem.useController(player, null, false);
                    player.getInventory().markDirty();
                    ServerPlayNetworking.send(player, IdentifierRegistries.USE_CONTROLLER_PACKET_ID, newBuf);
                }
            }
        }));

        registerServer(BLACKLIST_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (player.hasPermissionLevel(3)) {
                CommandRegistries.itemListHandling(player, null, CommandRegistries.ListType.BLACKLIST, null);
            }
        }));

        registerServer(WHITELIST_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (player.hasPermissionLevel(3)) {
                CommandRegistries.itemListHandling(player, null, CommandRegistries.ListType.WHITELIST, null);
            }
        }));

        registerServer(MAGNETIC_LEVITATION_MODE_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (player.hasEnchantment(EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT)) {
                boolean mode = !player.getMagneticLevitationMode();
                player.setMagneticLevitationMode(mode);
                String onOff = mode ? ON : OFF;
                player.sendMessage(Text.translatable("text.magnetcraft.message.magnetic_levitation_mode." + onOff), true);
                if (!mode && !player.getAbilities().flying && player.hasEnchantment(EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT) && !player.isOnGround()) {
                    player.setNoGravity(false);
                }
                PacketByteBuf clientBuf = PacketByteBufs.create();
                clientBuf.writeBoolean(mode);
                ServerPlayNetworking.send(player, MAGNETIC_LEVITATION_MODE_PACKET_ID, clientBuf);
            }
        }));

        registerServer(JUMPING_PACKET_ID, (server, player, handler, buf, packetSender) -> {
            boolean jumping = buf.readBoolean();
            server.execute(() -> player.jumping = jumping);
        });

        registerServer(AUTOMATIC_LEVITATION_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (player.hasEnchantment(EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT)) {
                boolean enable = !player.getAutomaticLevitation();
                player.setAutomaticLevitation(enable);
                String onOff = enable ? ON : OFF;
                player.sendMessage(Text.translatable("text.magnetcraft.message.automatic_levitation." + onOff));
                PacketByteBuf clientBuf = PacketByteBufs.create();
                clientBuf.writeBoolean(enable);
                ServerPlayNetworking.send(player, AUTOMATIC_LEVITATION_PACKET_ID, clientBuf);
            }
        }));

        registerServer(OPEN_BACKPACK, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (stack.getItem() instanceof MagneticShulkerBackpackItem backpack) {
                backpack.openScreen(player, stack, 38);
            }
        }));

    }

    public static void clientPlayNetworkingRegister() {
        registerClient(USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean enable = buf.readBoolean();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    player.setEnable(enable);
                    SoundEvent sound = enable ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE;
                    player.getInventory().markDirty();
                    player.playSound(sound, 1, 1);
                }
            });
        });

        registerClient(MAGNETIC_LEVITATION_MODE_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean mode = buf.readBoolean();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    player.setMagneticLevitationMode(mode);
                }
            });
        });

        registerClient(AUTOMATIC_LEVITATION_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean enable = buf.readBoolean();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    player.setAutomaticLevitation(enable);
                }
            });
        });

    }

    private static void registerServer(Identifier channelName, ServerPlayNetworking.PlayChannelHandler channelHandler) {
        ServerPlayNetworking.registerGlobalReceiver(channelName, channelHandler);
    }

    private static void registerClient(Identifier channelName, ClientPlayNetworking.PlayChannelHandler channelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, channelHandler);
    }

}
