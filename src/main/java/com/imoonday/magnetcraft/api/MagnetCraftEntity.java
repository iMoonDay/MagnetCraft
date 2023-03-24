package com.imoonday.magnetcraft.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * @author iMoonDay
 */
@SuppressWarnings({"unused", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MagnetCraftEntity {

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

    boolean getMagneticLevitationMode();

    void setMagneticLevitationMode(boolean mode);

    int getLevitationTick();

    void setLevitationTick(int tick);

    boolean getAutomaticLevitation();

    void setAutomaticLevitation(boolean enable);

    boolean isAdsorbedByEntity();

    void setAdsorbedByEntity(boolean adsorbed);

    boolean isAdsorbedByBlock();

    void setAdsorbedByBlock(boolean adsorbed);

    UUID getAdsorptionEntityId();

    void setAdsorptionEntityId(UUID uuid, boolean clear);

    BlockPos getAdsorptionBlockPos();

    void setAdsorptionBlockPos(BlockPos pos, boolean clear);

}
