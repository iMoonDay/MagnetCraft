package com.imoonday.magnetcraft.api;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

@SuppressWarnings("unused")
public interface EntityAttractNbt {

    NbtCompound getAttractData();

    boolean clearAttractData();

    void setAttractDis(double dis);

    double getAttractDis();

    boolean isAttracting();

    void setAttracting(boolean attracting);

    void setAttracting(boolean attracting, double dis);

    boolean getEnable();

    void setEnable(boolean enable);

    UUID getAttractOwner();

    void setAttractOwner(UUID uuid);

    boolean canAttract();

    boolean isFollowing();

    void setFollowing(boolean following);

    boolean ignoreFallDamage();

    void setIgnoreFallDamage(boolean ignore);

}
