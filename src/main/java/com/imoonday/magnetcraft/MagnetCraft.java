package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.*;
import net.fabricmc.api.ModInitializer;

public class MagnetCraft implements ModInitializer {
    public static final String MOD_ID = "magnetcraft";
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ModConfig.register();

        ItemRegistries.register();
        BlockRegistries.register();
        EffectRegistries.register();
        PotionRegistries.register();
        EnchantmentRegistries.register();
        ItemGroupRegistries.register();
        GlobalReceiverRegistries.register();
        CustomStatRegistries.register();

    }
}