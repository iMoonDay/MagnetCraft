package com.imoonday.magnetcraft.common.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * @author iMoonDay
 */
public class AttractEffect extends StatusEffect {

    public AttractEffect() {
        super(
                StatusEffectCategory.BENEFICIAL,
                0x76428A);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }
}
