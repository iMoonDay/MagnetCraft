package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.registries.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagnetCraft implements ModInitializer {
    public static final String MOD_ID = "magnetcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //调试输出模式
    public static final boolean TEST_MODE = true;

    @Override
    public void onInitialize() {

        ItemRegistries.register();
        BlockRegistries.register();
        EffectRegistries.register();
        PotionRegistries.register();
        EnchantmentRegistries.register();
        ItemGroupRegistries.register();
        GlobalReceiverRegistries.register();

    }
}