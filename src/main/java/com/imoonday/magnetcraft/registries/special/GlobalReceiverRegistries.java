package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.methods.AttractMethod;
import com.imoonday.magnetcraft.methods.CreatureMethod;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.GET_OTHER_ENTITIES_PACKET_ID;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.KEYBINDINGS_PACKET_ID;

public class GlobalReceiverRegistries {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            int task = buf.readInt();
            if (task == 0) {
                MagnetControllerItem.useTask(player, null, false);
            }
            buf.release();
        }));

        ServerPlayNetworking.registerGlobalReceiver(GET_OTHER_ENTITIES_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            int degaussingDis = buf.readInt();
            double dis = buf.readDouble();
            byte mode = buf.readByte();
            if (mode == 2) {
                AttractMethod.entityCanAttract = player.getWorld().getOtherEntities(null, new Box(
                                player.getPos().getX() + degaussingDis,
                                player.getPos().getY() + degaussingDis,
                                player.getPos().getZ() + degaussingDis,
                                player.getPos().getX() - degaussingDis,
                                player.getPos().getY() - degaussingDis,
                                player.getPos().getZ() - degaussingDis),
                        e -> (e instanceof LivingEntity && ((LivingEntity) e)
                                .hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)
                                && e.distanceTo(player) <= degaussingDis && !e.isSpectator())).isEmpty()
                        && player.getWorld().getOtherEntities(player, new Box(
                                player.getPos().getX() + degaussingDis,
                                player.getPos().getY() + degaussingDis,
                                player.getPos().getZ() + degaussingDis,
                                player.getPos().getX() - degaussingDis,
                                player.getPos().getY() - degaussingDis,
                                player.getPos().getZ() - degaussingDis),
                        e -> (e instanceof LivingEntity && e.getScoreboardTags().contains("isAttracting")
                                && e.distanceTo(player) <= dis && !e.isSpectator())).isEmpty();
            } else if (mode == 1) {
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
            }
            buf.release();
        }));
    }
}
