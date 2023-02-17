package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.common.items.*;
import com.imoonday.magnetcraft.registries.special.KeyBindingRegistries;
import net.fabricmc.api.ClientModInitializer;

public class MagnetCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyBindingRegistries.register();
        ElectroMagnetItem.register();
        MagnetControllerItem.register();
        PermanentMagnetItem.register();
        PolorMagnetItem.register();
        CreatureMagnetItem.register();
    }
}