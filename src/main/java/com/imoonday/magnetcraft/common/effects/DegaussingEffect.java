package com.imoonday.magnetcraft.common.effects;

import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * @author iMoonDay
 */
public class DegaussingEffect extends StatusEffect {
    public DegaussingEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0xFFFFFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.removeStatusEffect(EffectRegistries.ATTRACT_EFFECT);
    }
}
