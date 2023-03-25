package com.imoonday.magnetcraft.common.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class UnattractEffect extends StatusEffect {
    public UnattractEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0xFFFFFF); 
    }

}
