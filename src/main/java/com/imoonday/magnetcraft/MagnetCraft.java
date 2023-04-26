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
    public static final String VERSION = "v1.6";

    @Override
    public void onInitialize() {
        ModConfig.register();
        ItemGroupRegistries.register();
        ItemRegistries.register();
        BlockRegistries.register();
        FluidRegistries.register();
        EntityRegistries.register();
        EffectRegistries.register();
        PotionRegistries.register();
        EnchantmentRegistries.register();
        GlobalReceiverRegistries.register();
        CustomStatRegistries.register();
        CommandRegistries.register();
        ScreenRegistries.register();
        RecipeRegistries.register();
        CallbackRegistries.register();
        TradeRegistries.register();
    }
}