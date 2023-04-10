package com.imoonday.magnetcraft.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings({"unused", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MagnetCraftEntity {

    default void shuttle(ArrayList<Vec3d> route) {
    }

    default boolean isShuttling() {
        return false;
    }

    default void setShuttling(boolean shuttling) {
    }

    default ArrayList<Vec3d> getRoute() {
        return new ArrayList<>();
    }

    default void setRoute(ArrayList<Vec3d> route) {
    }

    default int getCurrentRouteIndex() {
        return 0;
    }

    default void setCurrentRouteIndex(int currentRouteIndex) {
    }

    default void addCurrentRouteIndex() {
    }

    default int getShuttleCooldown() {
        return 0;
    }

    default void setShuttleCooldown(int shuttleCooldown) {
    }

    default void minusShuttleCooldown() {
    }

    default void tryAttract() {
    }

    default void setCooldown(ItemStack stack, int cooldown) {
    }

    default void addDamage(Hand hand, int damage, boolean unbreaking) {
    }

    default boolean isBroken(Hand hand) {
        return false;
    }

    default boolean hasEnchantment(EquipmentSlot equipmentSlot, Enchantment enchantment) {
        return false;
    }

    default boolean hasEnchantmentOnArmor(Enchantment enchantment) {
        return false;
    }

    default boolean hasEnchantment(Enchantment enchantment) {
        return false;
    }

    default int getEnchantmentLvl(EquipmentSlot equipmentSlot, Enchantment enchantment) {
        return 0;
    }

    default int getEnchantmentLvlOnArmor(Enchantment enchantment) {
        return 0;
    }

    default int getEnchantmentLvl(Enchantment enchantment) {
        return 0;
    }

    default void tryLevitation() {
    }

    default boolean canReachTo(Vec3d pos) {
        return true;
    }

    default NbtCompound getAttractData() {
        return null;
    }

    default boolean clearAttractData() {
        return false;
    }

    default void setAttractDis(double dis) {
    }

    default double getAttractDis() {
        return 0;
    }

    default boolean isAttracting() {
        return false;
    }

    default void setAttracting(boolean attracting) {
    }

    default void setAttracting(boolean attracting, double dis) {
    }

    default boolean getEnable() {
        return false;
    }

    default void setEnable(boolean enable) {
    }

    default UUID getAttractOwner() {
        return null;
    }

    default void setAttractOwner(UUID uuid) {
    }

    default boolean canAttract() {
        return false;
    }

    default boolean isFollowing() {
        return false;
    }

    default void setFollowing(boolean following) {
    }

    default boolean ignoreFallDamage() {
        return false;
    }

    default void setIgnoreFallDamage(boolean ignore) {
    }

    default boolean getMagneticLevitationMode() {
        return false;
    }

    default void setMagneticLevitationMode(boolean mode) {
    }

    default int getLevitationTick() {
        return 0;
    }

    default void setLevitationTick(int tick) {
    }

    default boolean getAutomaticLevitation() {
        return false;
    }

    default void setAutomaticLevitation(boolean enable) {
    }

    default boolean isAdsorbedByEntity() {
        return false;
    }

    default void setAdsorbedByEntity(boolean adsorbed) {
    }

    default boolean isAdsorbedByBlock() {
        return false;
    }

    default void setAdsorbedByBlock(boolean adsorbed) {
    }

    default UUID getAdsorptionEntityId() {
        return null;
    }

    default void setAdsorptionEntityId(UUID uuid, boolean clear) {
    }

    default BlockPos getAdsorptionBlockPos() {
        return null;
    }

    default void setAdsorptionBlockPos(BlockPos pos, boolean clear) {
    }

    default Vec3d getAttractSource() {
        return null;
    }

    default void setAttractSource(Vec3d pos) {
    }

}
