package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.entities.BackpackFeatureRenderer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntityRenderer.class)
public class ArmorStandEntityRendererMixin {

    @Inject(method = "<init>",at = @At("TAIL"))
    public void ArmorStandEntityRenderer(EntityRendererFactory.Context context, CallbackInfo ci){
        ArmorStandEntityRenderer renderer = (ArmorStandEntityRenderer) (Object) this;
        renderer.addFeature(new BackpackFeatureRenderer<>(renderer, context.getModelLoader()));
    }

}
