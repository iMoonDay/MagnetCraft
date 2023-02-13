package com.imoonday.magnetcraft.registries;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.events.AttractEvent;
import com.imoonday.magnetcraft.items.MagnetControllerItem;
import com.imoonday.magnetcraft.keybindings.KeyBindings;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import static com.imoonday.magnetcraft.registries.IdentifierRegistries.GET_OTHER_ENTITIES_PACKET_ID;
import static com.imoonday.magnetcraft.registries.IdentifierRegistries.KEYBINDINGS_PACKET_ID;

public class GlobalReceiverRegistries {

    static boolean debugMode = AutoConfig.getConfigHolder(ModConfig.class).getConfig().debugMode;
    
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDINGS_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            int task = buf.readInt();
            if (debugMode) {
                String message = "[服务器调试] Buf: " + task;
                player.sendMessage(Text.literal(message));
            }
            if (task == 0) {
                MagnetControllerItem.useTask(player, null, false);
            } else if (task == 1) {
                KeyBindings.stickyKeyServerTask(player);
            } else {
                String message = "[服务器调试] Error: " + buf;
                player.sendMessage(Text.literal(message));
            }
            buf.release();
        }));

        ServerPlayNetworking.registerGlobalReceiver(GET_OTHER_ENTITIES_PACKET_ID, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            int degaussingDis = buf.readInt();
            double dis = buf.readDouble();
            AttractEvent.EntityCanAttract = player.getWorld().getOtherEntities(null, new Box(
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
            buf.release();
        }));
    }
}
