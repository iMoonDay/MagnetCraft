package com.imoonday.magnetcraft.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "magnetcraft")
public class ModConfig implements ConfigData {

    public boolean displayActionBar = true;
    public boolean displayQuantityFeedback = true;
    public boolean enableSneakToSwitch = true;

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public DefaultValue value = new DefaultValue();

    public static class DefaultValue {
        @ConfigEntry.BoundedDiscrete(max = 100)
        public int electromagnetAttractMinDis = 10;
        @ConfigEntry.BoundedDiscrete(max = 300)
        public int permanentMagnetAttractMinDis = 30;
        @ConfigEntry.BoundedDiscrete(max = 200)
        public int polarMagnetAttractMinDis = 20;
        @ConfigEntry.BoundedDiscrete(max = 300)
        public int creatureMagnetAttractDis = 30;
        @ConfigEntry.BoundedDiscrete(max = 150)
        public int electromagnetTeleportMinDis = 15;
        @ConfigEntry.BoundedDiscrete(max = 250)
        public int permanentMagnetTeleportMinDis = 25;
        @ConfigEntry.BoundedDiscrete(max = 150)
        public int degaussingDis = 15;
        @ConfigEntry.BoundedDiscrete(max = 50)
        public int magnetHandSpacing = 5;
        @ConfigEntry.BoundedDiscrete(max = 100)
        public int enchDefaultDis = 10;
        @ConfigEntry.BoundedDiscrete(max = 20)
        public int disPerLvl = 2;
        public double magnetSetMultiplier = 1.5;
        public double netheriteMagnetSetMultiplier = 2;
    }

    public static void register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }
    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}