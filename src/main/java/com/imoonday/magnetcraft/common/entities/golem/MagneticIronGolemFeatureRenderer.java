package com.imoonday.magnetcraft.common.entities.golem;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Map;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
public class MagneticIronGolemFeatureRenderer extends FeatureRenderer<MagneticIronGolemEntity, MagneticIronGolemEntityModel> {

    private static final Map<MagneticIronGolemEntity.Crack, Identifier> DAMAGE_TO_TEXTURE = ImmutableMap.of(MagneticIronGolemEntity.Crack.LOW, id("textures/entity/magnetic_iron_golem/magnetic_iron_golem_crackiness_low.png"), MagneticIronGolemEntity.Crack.MEDIUM, id("textures/entity/magnetic_iron_golem/magnetic_iron_golem_crackiness_medium.png"), MagneticIronGolemEntity.Crack.HIGH, id("textures/entity/magnetic_iron_golem/magnetic_iron_golem_crackiness_high.png"));


    public MagneticIronGolemFeatureRenderer(FeatureRendererContext<MagneticIronGolemEntity, MagneticIronGolemEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, MagneticIronGolemEntity entity, float f, float g, float h, float j, float k, float l) {
        if (entity.isInvisible()) {
            return;
        }
        Identifier identifier;
        identifier = id("textures/entity/magnetic_iron_golem/magnetic_iron_golem_with_lodestone.png");
        if (entity.isHasLodestone()) {
            MagneticIronGolemFeatureRenderer.renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, i, entity, 1.0f, 1.0f, 1.0f);
        }
        MagneticIronGolemEntity.Crack crack = entity.getCrack();
        if (crack == MagneticIronGolemEntity.Crack.NONE) {
            return;
        }
        identifier = DAMAGE_TO_TEXTURE.get(crack);
        MagneticIronGolemFeatureRenderer.renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, i, entity, 1.0f, 1.0f, 1.0f);
    }

}
