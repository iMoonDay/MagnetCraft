package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.common.items.*;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.special.KeyBindingRegistries;
import net.fabricmc.api.ClientModInitializer;

public class MagnetCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRegistries.registerClient();
        KeyBindingRegistries.register();
        ElectroMagnetItem.register();
        MagnetControllerItem.register();
        PermanentMagnetItem.register();
        PolorMagnetItem.register();
        CreatureMagnetItem.register();
        MineralMagnetItem.register();
    }
}