package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.screen.FilterableMagnetScreen;
import com.imoonday.magnetcraft.screen.LodestoneScreen;
import com.imoonday.magnetcraft.screen.MineralMagnetScreen;
import com.imoonday.magnetcraft.screen.handler.FilterableMagnetScreenHandler;
import com.imoonday.magnetcraft.screen.handler.LodestoneScreenHandler;
import com.imoonday.magnetcraft.screen.handler.MineralMagnetScreenHandler;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ScreenRegistries {

    public static final ScreenHandlerType<LodestoneScreenHandler> LODESTONE_SCREEN_HANDLER;
    public static final ScreenHandlerType<FilterableMagnetScreenHandler> FILTERABLE_MAGNET_SCREEN_HANDLER;
    public static final ScreenHandlerType<MineralMagnetScreenHandler> MINERAL_MAGNET_SCREEN_HANDLER;

    static {
        LODESTONE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("lodestone"),LodestoneScreenHandler::new);
        FILTERABLE_MAGNET_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("filterable_magnet"), FilterableMagnetScreenHandler::new);
        MINERAL_MAGNET_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("mineral_magnet"), MineralMagnetScreenHandler::new);
    }

    public static void register(){
        System.out.println("Screen Registries Loaded");
    }

    public static void registerClient(){
        ScreenRegistry.register(LODESTONE_SCREEN_HANDLER, LodestoneScreen::new);
        ScreenRegistry.register(FILTERABLE_MAGNET_SCREEN_HANDLER, FilterableMagnetScreen::new);
        ScreenRegistry.register(MINERAL_MAGNET_SCREEN_HANDLER, MineralMagnetScreen::new);
    }
}
