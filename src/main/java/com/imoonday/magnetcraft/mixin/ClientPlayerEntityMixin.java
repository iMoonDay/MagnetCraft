package com.imoonday.magnetcraft.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.JUMPING_PACKET_ID;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    protected volatile boolean lastJumping = false;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V"))
    public void tick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (player.input.jumping != this.lastJumping) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(player.input.jumping);
            ClientPlayNetworking.send(JUMPING_PACKET_ID, buf);
            this.lastJumping = player.input.jumping;
        }
    }

}
