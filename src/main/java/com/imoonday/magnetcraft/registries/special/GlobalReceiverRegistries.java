package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethod;
import com.imoonday.magnetcraft.methods.CreatureMethod;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

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

        ServerPlayNetworking.registerGlobalReceiver(GET_DEGAUSSING_ENTITIES_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            int degaussingDis = ModConfig.getConfig().value.degaussingDis;
            CreatureMethod.entityCanAttract = player.getWorld().getOtherEntities(null, new Box(
                            player.getX() + degaussingDis,
                            player.getY() + degaussingDis,
                            player.getZ() + degaussingDis,
                            player.getX() - degaussingDis,
                            player.getY() - degaussingDis,
                            player.getZ() - degaussingDis),
                    e -> (e instanceof LivingEntity && ((LivingEntity) e)
                            .hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)
                            && e.distanceTo(player) <= degaussingDis && !e.isSpectator())).isEmpty();
        }));

        ServerPlayNetworking.registerGlobalReceiver(GET_ENTITIES_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            double dis = AttractMethod.getEntitiesDis;
            BlockPos pos = AttractMethod.blockPos;
            Entity e = AttractMethod.expectEntity;
            float blockDistanceTo = AttractMethod.blockDistance;
            AttractMethod.hasNearerEntity = !e.getWorld().getOtherEntities(e, new Box(pos.getX() - dis, pos.getY() - dis, pos.getZ() - dis, pos.getX() + dis, pos.getY() + dis, pos.getZ() + dis), o -> (!(o instanceof PlayerEntity) && o.distanceTo(e) < blockDistanceTo && o.getScoreboardTags().contains("MagnetCraft.isAttracting"))).isEmpty();
        }));

        ClientPlayNetworking.registerGlobalReceiver(USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> client.execute(() -> {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                MagnetControllerItem.useTask(player, null, false);
            }
        }));

    }
}
