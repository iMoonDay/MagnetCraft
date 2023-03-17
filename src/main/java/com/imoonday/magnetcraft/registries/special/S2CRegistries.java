package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.*;

public class S2CRegistries {

    @SuppressWarnings("RedundantCast")
    public static void registerClient() {

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
