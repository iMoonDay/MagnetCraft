package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.items.ElectroMagnetItem;
import com.imoonday.magnetcraft.items.MagnetControllerItem;
import com.imoonday.magnetcraft.items.PermanentMagnetItem;
import com.imoonday.magnetcraft.items.PolorMagnetItem;
import com.imoonday.magnetcraft.keybindings.KeyBindings;
import net.fabricmc.api.ClientModInitializer;

public class MagnetCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeyBindings.keyBindings();

        ElectroMagnetItem.register();
        MagnetControllerItem.register();
        PermanentMagnetItem.register();
        PolorMagnetItem.register();

    }
}