package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.common.entities.MagneticIronGolemEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.GOLEM_PACKET_ID;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.USE_CONTROLLER_PACKET_ID;

public class S2CRegistries {

    @SuppressWarnings("RedundantCast")
    public static void registerClient() {

        ClientPlayNetworking.registerGlobalReceiver(USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean enable = buf.readBoolean();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    ((EntityAttractNbt) player).setEnable(enable);
                    SoundEvent sound = enable ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE;
                    player.getInventory().markDirty();
                    player.playSound(sound, 1, 1);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(GOLEM_PACKET_ID, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null && client.world != null) {
                    Entity entity = client.world.getEntityById(id);
                    if (entity instanceof MagneticIronGolemEntity golem) {
                        golem.setHasLodestone(true);
                    }
                }
            });
        });
    }
}
