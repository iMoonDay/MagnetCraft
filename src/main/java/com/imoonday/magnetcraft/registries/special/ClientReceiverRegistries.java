package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.USE_CONTROLLER_PACKET_ID;

public class ClientReceiverRegistries {

    public static void registerClient() {

        ClientPlayNetworking.registerGlobalReceiver(USE_CONTROLLER_PACKET_ID, (client, handler, buf, responseSender) -> client.execute(() -> {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                MagnetControllerItem.useTask(player, null, false);
            }
        }));

    }
}
