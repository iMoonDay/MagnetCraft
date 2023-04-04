package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.screen.*;
import com.imoonday.magnetcraft.screen.handler.*;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ScreenRegistries {

    public static final ScreenHandlerType<LodestoneScreenHandler> LODESTONE_SCREEN_HANDLER;
    public static final ScreenHandlerType<FilterableMagnetScreenHandler> FILTERABLE_MAGNET_SCREEN_HANDLER;
    public static final ScreenHandlerType<MineralMagnetScreenHandler> MINERAL_MAGNET_SCREEN_HANDLER;
    public static final ScreenHandlerType<AdvancedGrindstoneScreenHandler> ADVANCED_GRINDSTONE_SCREEN_HANDLER;
    public static final ScreenHandlerType<MagneticShulkerBackpackScreenHandler> MAGNETIC_SHULKER_BACKPACK_SCREEN_HANDLER;

    static {
        LODESTONE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("lodestone"),LodestoneScreenHandler::new);
        FILTERABLE_MAGNET_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("filterable_magnet"), FilterableMagnetScreenHandler::new);
        MINERAL_MAGNET_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("mineral_magnet"), MineralMagnetScreenHandler::new);
        ADVANCED_GRINDSTONE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("advanced_grindstone"), AdvancedGrindstoneScreenHandler::new);
        MAGNETIC_SHULKER_BACKPACK_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("magnetic_shulker_backpack"),MagneticShulkerBackpackScreenHandler::new);
    }

    public static void register(){
        System.out.println("ScreenRegistries.class Loaded");
    }

    public static void registerClient(){
        ScreenRegistry.register(LODESTONE_SCREEN_HANDLER, LodestoneScreen::new);
        ScreenRegistry.register(FILTERABLE_MAGNET_SCREEN_HANDLER, FilterableMagnetScreen::new);
        ScreenRegistry.register(MINERAL_MAGNET_SCREEN_HANDLER, MineralMagnetScreen::new);
        ScreenRegistry.register(ADVANCED_GRINDSTONE_SCREEN_HANDLER, AdvancedGrindstoneScreen::new);
        ScreenRegistry.register(MAGNETIC_SHULKER_BACKPACK_SCREEN_HANDLER, MagneticShulkerBackpackScreen::new);
    }
}
