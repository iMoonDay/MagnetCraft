package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.CreatureMethod;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.GET_OTHER_ENTITIES_PACKET_ID;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.KEYBINDINGS_PACKET_ID;

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

        ServerPlayNetworking.registerGlobalReceiver(GET_OTHER_ENTITIES_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            int degaussingDis = ModConfig.getConfig().value.degaussingDis;
            CreatureMethod.entityCanAttract = player.getWorld().getOtherEntities(null, new Box(
                            player.getPos().getX() + degaussingDis,
                            player.getPos().getY() + degaussingDis,
                            player.getPos().getZ() + degaussingDis,
                            player.getPos().getX() - degaussingDis,
                            player.getPos().getY() - degaussingDis,
                            player.getPos().getZ() - degaussingDis),
                    e -> (e instanceof LivingEntity && ((LivingEntity) e)
                            .hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)
                            && e.distanceTo(player) <= degaussingDis && !e.isSpectator())).isEmpty();
        }));

        ClientPlayNetworking.registerGlobalReceiver(IdentifierRegistries.USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> client.execute(() -> {
            if (client.player != null) {
                MagnetControllerItem.useTask(client.player, null, false);
            }
        }));

    }
}
