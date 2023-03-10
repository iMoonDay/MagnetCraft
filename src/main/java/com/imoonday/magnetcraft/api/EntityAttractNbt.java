package com.imoonday.magnetcraft.api;

import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("unused")
public interface EntityAttractNbt {

    NbtCompound getAttractData();

    boolean clearAttractData();

    void setAttractDis(double dis);

    double getAttractDis();

    boolean isAttracting();

    void setAttracting(boolean attracting);

    void setAttracting(boolean attracting,double dis);

    boolean getEnable();

    void setEnable(boolean enable);

}
