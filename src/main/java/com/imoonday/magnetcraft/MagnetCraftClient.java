package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.common.fluids.MagneticFluid;
import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.common.items.PortableDemagnetizerItem;
import com.imoonday.magnetcraft.common.items.magnets.*;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.special.ClientReceiverRegistries;
import com.imoonday.magnetcraft.registries.special.KeyBindingRegistries;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MagnetCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRegistries.registerClient();
        KeyBindingRegistries.registerClient();
        ElectromagnetItem.registerClient();
        MagnetControllerItem.registerClient();
        PermanentMagnetItem.registerClient();
        PolorMagnetItem.registerClient();
        CreatureMagnetItem.registerClient();
        MineralMagnetItem.registerClient();
        CropMagnetItem.registerClient();
        ScreenRegistries.registerClient();
        ClientReceiverRegistries.registerClient();
        PortableDemagnetizerItem.registerClient();
        MagneticFluid.registerClient();
    }
}