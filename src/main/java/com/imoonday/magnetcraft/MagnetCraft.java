package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.*;
import com.imoonday.magnetcraft.registries.special.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagnetCraft implements ModInitializer {

    public static final String MOD_ID = "magnetcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("MagnetCraft");

    @Override
    public void onInitialize() {
        ModConfig.register();
        ItemRegistries.register();
        FluidRegistries.register();
        BlockRegistries.register();
        EffectRegistries.register();
        PotionRegistries.register();
        EnchantmentRegistries.register();
        ItemGroupRegistries.register();
        C2SRegistries.register();
        CustomStatRegistries.register();
        CommandRegistries.register();
        ScreenRegistries.register();
//        EventRegistries.register();
        RecipeRegistries.register();
        EntityRegistries.register();
    }
}