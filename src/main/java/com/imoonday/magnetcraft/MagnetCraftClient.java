package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.common.items.magnets.*;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.special.ClientReceiverRegistries;
import com.imoonday.magnetcraft.registries.special.KeyBindingRegistries;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.fabricmc.api.ClientModInitializer;

public class MagnetCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRegistries.registerClient();
        KeyBindingRegistries.registerClient();
        ElectroMagnetItem.registerClient();
        MagnetControllerItem.registerClient();
        PermanentMagnetItem.registerClient();
        PolorMagnetItem.registerClient();
        CreatureMagnetItem.registerClient();
        MineralMagnetItem.registerClient();
        CropMagnetItem.registerClient();
        ScreenRegistries.registerClient();
        ClientReceiverRegistries.registerClient();
    }
}