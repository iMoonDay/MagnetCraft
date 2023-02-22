package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.*;
import com.imoonday.magnetcraft.registries.special.CommandRegistries;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import com.imoonday.magnetcraft.registries.special.GlobalReceiverRegistries;
import com.imoonday.magnetcraft.registries.special.ItemGroupRegistries;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagnetCraft implements ModInitializer {
    public static final String MOD_ID = "magnetcraft";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
        CommandRegistries.register();
    }
}