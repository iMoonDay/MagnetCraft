package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.entities.BackpackFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "<init>",at = @At("TAIL"))
    public void PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci){
        PlayerEntityRenderer renderer = (PlayerEntityRenderer) (Object) this;
        renderer.addFeature(new BackpackFeatureRenderer<>(renderer, ctx.getModelLoader()));
    }

}
