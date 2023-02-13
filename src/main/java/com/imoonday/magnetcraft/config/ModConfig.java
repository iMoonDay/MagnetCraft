package com.imoonday.magnetcraft.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "magnetcraft")
public class ModConfig implements ConfigData {

    public boolean debugMode = true;

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public DefaultValue value = new DefaultValue();

    public static class DefaultValue {
        public int electromagnetAttractMinDis = 10;
        public int permanentMagnetAttractMinDis = 30;
        public int polarMagnetAttractMinDis = 20;
        public int electromagnetTeleportMinDis = 15;
        public int permanentMagnetTeleportMinDis = 25;
        public int magnetHandSpacing = 5;
        public int enchDefaultDis = 10;
        public int disPerLvl = 2;
    }

    public static void register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }
}