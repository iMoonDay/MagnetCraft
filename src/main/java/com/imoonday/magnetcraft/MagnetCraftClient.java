package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.common.fluids.MagneticFluid;
import com.imoonday.magnetcraft.common.items.LargeMagneticSuckerItem;
import com.imoonday.magnetcraft.common.items.MagnetControllerItem;
import com.imoonday.magnetcraft.common.items.PortableDemagnetizerItem;
import com.imoonday.magnetcraft.common.items.SmallMagneticSuckerItem;
import com.imoonday.magnetcraft.common.items.magnets.*;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRendererRegistries;
import com.imoonday.magnetcraft.registries.special.GlobalReceiverRegistries;
import com.imoonday.magnetcraft.registries.special.KeyBindingRegistries;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * @author iMoonDay
 */
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
        GlobalReceiverRegistries.clientPlayNetworkingRegister();
        PortableDemagnetizerItem.registerClient();
        MagneticFluid.registerClient();
        EntityRendererRegistries.registerClient();
        AdsorptionMagnetItem.registerClient();
        SmallMagneticSuckerItem.registerClient();
        LargeMagneticSuckerItem.registerClient();
    }
}