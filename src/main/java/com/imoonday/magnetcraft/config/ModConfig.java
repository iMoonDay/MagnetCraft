package com.imoonday.magnetcraft.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "magnetcraft")
public class ModConfig implements ConfigData {

    public boolean displayActionBar = true;
    public boolean displayMessageFeedback = true;
    public boolean enableSneakToSwitch = true;
    public boolean rightClickReversal = false;

    @ConfigEntry.Gui.CollapsibleObject
    public Whitelist whitelist = new Whitelist();

    public static class Whitelist {
        public boolean enable = false;
        public String[] list = new String[]{};
    }

    @ConfigEntry.Gui.CollapsibleObject
    public Blacklist blacklist = new Blacklist();

    public static class Blacklist {
        public boolean enable = false;
        public String[] list = new String[]{};
    }

    @ConfigEntry.Gui.CollapsibleObject
    public DefaultValue value = new DefaultValue();

    public static class DefaultValue {
        //吸引距离
        @ConfigEntry.BoundedDiscrete(max = 100)
        public int electromagnetAttractMinDis = 10;
        @ConfigEntry.BoundedDiscrete(max = 300)
        public int permanentMagnetAttractMinDis = 30;
        @ConfigEntry.BoundedDiscrete(max = 200)
        public int polarMagnetAttractMinDis = 20;
        @ConfigEntry.BoundedDiscrete(max = 300)
        public int creatureMagnetAttractDis = 30;
        @ConfigEntry.BoundedDiscrete(max = 50)
        public int horseArmorAttractDis = 5;
        @ConfigEntry.BoundedDiscrete(max = 100)
        public int arrowAttractDis = 10;
        @ConfigEntry.BoundedDiscrete(max = 100)
        public int droppedMagnetAttractDis = 10;
        @ConfigEntry.BoundedDiscrete(max = 300)
        public int lodestoneMaxDis = 30;
        //传送距离
        @ConfigEntry.BoundedDiscrete(max = 150)
        public int electromagnetTeleportMinDis = 15;
        @ConfigEntry.BoundedDiscrete(max = 250)
        public int permanentMagnetTeleportMinDis = 25;
        //消磁距离
        @ConfigEntry.BoundedDiscrete(max = 150)
        public int degaussingDis = 15;
        //手持距离差距
        @ConfigEntry.BoundedDiscrete(max = 50)
        public int magnetHandSpacing = 5;
        //层级距离
        @ConfigEntry.BoundedDiscrete(max = 200)
        public int attractDefaultDis = 20;
        @ConfigEntry.BoundedDiscrete(max = 20)
        public int disPerAmplifier = 2;
        @ConfigEntry.BoundedDiscrete(max = 100)
        public int enchDefaultDis = 10;
        @ConfigEntry.BoundedDiscrete(max = 20)
        public int disPerLvl = 2;
        @ConfigEntry.BoundedDiscrete(max = 20)
        public int disPerPower = 2;
        @ConfigEntry.BoundedDiscrete(max = 30)
        public int disEachClick = 5;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        @ConfigEntry.Gui.RequiresRestart
        public int maxEnchLvl = 5;
        @ConfigEntry.BoundedDiscrete(max = 30)
        public int requiredExperienceLevel = 5;
        @ConfigEntry.BoundedDiscrete(max = 5 * 64)
        public int removeFoodLevelEveryCount = 32;
        //装备乘数
        public double magnetSetMultiplier = 1.5;
        public double netheriteMagnetSetMultiplier = 2;
    }

    public static void register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static DefaultValue getValue() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig().value;
    }

}