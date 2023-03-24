package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.MagnetCraft.EnchantmentMethods;
import com.imoonday.magnetcraft.common.fluids.MagneticFluid;
import com.imoonday.magnetcraft.common.items.armors.MagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.armors.NetheriteMagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.magnets.AdsorptionMagnetItem;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.common.tags.FluidTags;
import com.imoonday.magnetcraft.common.tags.ItemTags;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.FluidRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * @author iMoonDay
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void tick(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity != null) {
            World world = ((LivingEntity) (Object) this).world;
            if (world == null || entity.isPlayer() && entity.isSpectator()) {
                return;
            }
            tick(entity);
            MagneticFluid.tick(entity);
            CreatureMagnetItem.followingCheck(entity);
            if (world instanceof ServerWorld serverWorld) {
                AdsorptionMagnetItem.tickCheck(serverWorld);
            }
            if (entity.isOnGround()) {
                entity.setIgnoreFallDamage(false);
            }
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "drop")
    public void drop(DamageSource source, CallbackInfo ci) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            Entity sourceEntity = source.getSource();
            if (sourceEntity != null) {
                LivingEntity entity = (LivingEntity) (Object) this;
                World world = player.world;
                ItemStack stack;
                stack = sourceEntity instanceof TridentEntity tridentEntity ? tridentEntity.asItemStack() : player.getMainHandStack();
                boolean hasEnchantment = EnchantmentMethods.hasEnchantment(stack, EnchantmentRegistries.AUTOMATIC_LOOTING_ENCHANTMENT);
                if (hasEnchantment) {
                    world.getOtherEntities(null, entity.getBoundingBox(), targetEntity -> ((targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.age == 0)).forEach(targetEntity -> {
                        if (targetEntity instanceof ExperienceOrbEntity orb) {
                            int amount = orb.getExperienceAmount();
                            player.addExperience(amount);
                        } else {
                            player.getInventory().offerOrDrop(((ItemEntity) targetEntity).getStack());
                        }
                        targetEntity.kill();
                    });
                }
            }
        }
    }

    @Inject(method = "onDeath", at = @At(value = "TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity) && entity.isAttracting() && entity.getEnable()) {
            entity.setAttracting(false);
            entity.setEnable(false);
        }
    }

    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true)
    protected void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.ignoreFallDamage() && source.isOf(DamageTypes.FALL) && !entity.world.isClient) {
            entity.setIgnoreFallDamage(false);
            cir.setReturnValue(false);
        }
    }

    private static void tick(LivingEntity entity) {
        ModConfig.DefaultValue value = ModConfig.getConfig().value;
        double[] minDis = new double[]{value.electromagnetAttractMinDis, value.permanentMagnetAttractMinDis, value.polarMagnetAttractMinDis};
        double creatureDis = value.creatureMagnetAttractDis;
        double horseArmorAttractDis = value.horseArmorAttractDis;
        double magnetHandSpacing = value.magnetHandSpacing;
        double attractDefaultDis = value.attractDefaultDis;
        double disPerAmplifier = value.disPerAmplifier;
        double enchDefaultDis = value.enchDefaultDis;
        double disPerLvl = value.disPerLvl;
        double magnetSetMultiplier = value.magnetSetMultiplier >= 1 ? value.magnetSetMultiplier : 1.5;
        double netheriteMagnetSetMultiplier = value.netheriteMagnetSetMultiplier >= 1 ? value.netheriteMagnetSetMultiplier : 2;
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        ItemStack mainhandStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack offhandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
        boolean mainhandEnabled = mainhandStack.getNbt() != null && mainhandStack.getNbt().getBoolean("Enable");
        boolean offhandEnabled = offhandStack.getNbt() != null && offhandStack.getNbt().getBoolean("Enable");
        boolean mainhandElectromagnet = mainhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPermanent = mainhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPolar = mainhandStack.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandCreature = mainhandStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandMagnet = mainhandElectromagnet || mainhandPermanent || mainhandPolar;
        boolean offhandElectromagnet = offhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && offhandEnabled;
        boolean offhandPermanent = offhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && offhandEnabled;
        boolean offhandPolar = offhandStack.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && offhandEnabled;
        boolean offhandCreature = offhandStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && offhandEnabled;
        boolean offhandMagnet = offhandElectromagnet || offhandPermanent || offhandPolar;
        boolean handElectromagnet = mainhandElectromagnet || offhandElectromagnet;
        boolean handPermanent = mainhandPermanent || offhandPermanent;
        boolean handPolar = mainhandPolar || offhandPolar;
        boolean handCreature = mainhandCreature || offhandCreature;
        boolean handMagnet = mainhandMagnet || offhandMagnet;
        boolean hasEnch = EnchantmentMethods.hasEnchantment(entity, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandHasEnch = EnchantmentMethods.hasEnchantment(mainhandStack, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = EnchantmentMethods.hasEnchantment(offhandStack, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandMagnetHasEnch = mainhandMagnet && mainhandHasEnch;
        boolean offhandMagnetHasEnch = offhandMagnet && offhandHasEnch;
        boolean handMagnetHasEnch = mainhandMagnetHasEnch || offhandMagnetHasEnch;
        boolean hasEffect = entity.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT);
        boolean horseArmorAttracting = entity instanceof HorseEntity horseEntity && horseEntity.getArmorType().isOf(ItemRegistries.MAGNETIC_IRON_HORSE_ARMOR);
        boolean isAttracting = (hasEnch || handMagnet || hasEffect || horseArmorAttracting) && entity.canAttract() && entity.isAlive();
        boolean display = ModConfig.getConfig().displayActionBar;
        boolean[] handItems = new boolean[]{handElectromagnet, handPermanent, handPolar};
        boolean[] mainhandItems = new boolean[]{mainhandElectromagnet, mainhandPermanent, mainhandPolar};
        boolean[] offhandItems = new boolean[]{offhandElectromagnet, offhandPermanent, offhandPolar};
        int enchLvl = EnchantmentMethods.getEnchantmentLvl(entity, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        int mainhandUsedTick = entity.getMainHandStack().getNbt() != null ? entity.getMainHandStack().getNbt().getInt("UsedTick") : 0;
        int offhandUsedTick = entity.getOffHandStack().getNbt() != null ? entity.getOffHandStack().getNbt().getInt("UsedTick") : 0;
        int tickPerDamage = value.secPerDamage * 20;
        double enchMinDis = enchDefaultDis + disPerLvl;
        double finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;
        double telDis;
        double finalTelDis = 0;
        while (mainhandCreature && mainhandUsedTick >= tickPerDamage) {
            NbtCompound nbt = entity.getMainHandStack().getOrCreateNbt();
            nbt.putInt("UsedTick", mainhandUsedTick - tickPerDamage);
            entity.getMainHandStack().setNbt(nbt);
            MagnetCraft.DamageMethods.addDamage(entity, Hand.MAIN_HAND, 1, true);
            mainhandUsedTick -= tickPerDamage;
        }
        while (mainhandCreature && offhandUsedTick >= tickPerDamage) {
            NbtCompound nbt = entity.getOffHandStack().getOrCreateNbt();
            nbt.putInt("UsedTick", offhandUsedTick - tickPerDamage);
            entity.getMainHandStack().setNbt(nbt);
            MagnetCraft.DamageMethods.addDamage(entity, Hand.OFF_HAND, 1, true);
            offhandUsedTick -= tickPerDamage;
        }
        if (entity instanceof PlayerEntity player && player.isSubmergedIn(FluidTags.MAGNETIC_FLUID)) {
            boolean success = false;
            int mainhandRepair = 0;
            int offhandRepair = 0;
            BlockPos pos = new BlockPos((int) player.getEyePos().x, (int) player.getEyePos().y, (int) player.getEyePos().z);
            BlockState state = player.world.getBlockState(pos);
            if (!player.world.isClient && state.isOf(FluidRegistries.MAGNETIC_FLUID)) {
                Random random = entity.getRandom();
                if (mainhandStack.isIn(ItemTags.MAGNETS) && mainhandStack.isDamageable() && mainhandStack.isDamaged()) {
                    int damage = mainhandStack.getDamage();
                    int maxDamage = mainhandStack.getMaxDamage();
                    if (random.nextBetween(1, maxDamage * 200) <= damage) {
                        MagnetCraft.DamageMethods.addDamage(entity, Hand.MAIN_HAND, -maxDamage / 10, false);
                        mainhandRepair = damage - mainhandStack.getDamage();
                        success = true;
                    }
                }
                if (offhandStack.isIn(ItemTags.MAGNETS) && offhandStack.isDamageable() && offhandStack.isDamaged()) {
                    int damage = offhandStack.getDamage();
                    int maxDamage = offhandStack.getMaxDamage();
                    if (random.nextBetween(1, maxDamage * 200) <= damage) {
                        MagnetCraft.DamageMethods.addDamage(entity, Hand.OFF_HAND, -maxDamage / 10, false);
                        offhandRepair = damage - offhandStack.getDamage();
                        success = true;
                    }
                }
                if (success && random.nextBetween(1, 100) <= mainhandRepair + offhandRepair && state.isOf(FluidRegistries.MAGNETIC_FLUID)) {
                    player.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                }
            }
            player.getInventory().markDirty();
        }
        //检测吸引
        if (isAttracting) {
            int amplifier;
            double dis;
            if (handMagnet || hasEnch) {
                for (int i = 0; i < handItems.length; i++) {
                    if (handItems[i]) {
                        dis = minDis[i];
                        if (mainhandItems[i]) {
                            dis += magnetHandSpacing;
                            if (offhandItems[i]) {
                                dis += magnetHandSpacing;
                            }
                        }
                        if (handMagnetHasEnch) {
                            dis += enchMinDis + (enchLvl - 1) * disPerLvl;
                        }
                        finalDis = Math.max(dis,finalDis);
                    }
                }
            }
            if (hasEffect) {
                amplifier = Objects.requireNonNull(entity.getStatusEffect(EffectRegistries.ATTRACT_EFFECT)).getAmplifier();
                dis = attractDefaultDis + amplifier * disPerAmplifier;
                finalDis = Math.max(dis,finalDis);
            }
            if (horseArmorAttracting) {
                dis = horseArmorAttractDis;
                finalDis = Math.max(dis,finalDis);
            }
            if (MagneticIronArmorItem.isInMagneticIronSuit(entity)) {
                finalDis *= magnetSetMultiplier;
            }
            if (NetheriteMagneticIronArmorItem.isInNetheriteMagneticIronSuit(entity)) {
                finalDis *= netheriteMagnetSetMultiplier;
            }
            entity.setAttracting(true, finalDis);
        } else {
            entity.setAttracting(false);
        }
        //信息栏
        if ((isAttracting || handCreature) && display && entity instanceof PlayerEntity player) {
            String message;
            Text text;
            if (isAttracting) {
                message = ": " + finalDis;
                text = Text.translatable("text.magnetcraft.message.attract").append(message);
                if (handElectromagnet || handPermanent) {
                    if (mainhandElectromagnet) {
                        telDis = value.electromagnetTeleportMinDis + value.magnetHandSpacing;
                        finalTelDis = Math.max(telDis,finalTelDis);
                    }
                    if (offhandElectromagnet) {
                        telDis = value.electromagnetTeleportMinDis;
                        finalTelDis = Math.max(telDis,finalTelDis);
                    }
                    if (mainhandPermanent) {
                        telDis = value.permanentMagnetTeleportMinDis + value.magnetHandSpacing;
                        finalTelDis = Math.max(telDis,finalTelDis);
                    }
                    if (offhandPermanent) {
                        telDis = value.permanentMagnetTeleportMinDis;
                        finalTelDis = Math.max(telDis,finalTelDis);
                    }
                    message = ": " + finalTelDis;
                    text = text.copy().append(" ").append(Text.translatable("text.magnetcraft.message.teleport").append(message));
                }
                if (handCreature) {
                    message = ": " + creatureDis;
                    text = text.copy().append(" ").append(Text.translatable("text.magnetcraft.message.creature_attract").append(message));
                }
            } else {
                message = ": " + creatureDis;
                text = Text.translatable("text.magnetcraft.message.creature_attract").append(message);
            }
            if (!player.world.isClient && (!EnchantmentMethods.hasEnchantment(feet, EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT) || !player.getMagneticLevitationMode() && player.getLevitationTick() <= 0)) {
                player.sendMessage(text, true);
            }
        }
        if (entity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT) && EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.CHEST, EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT)) {
            entity.removeStatusEffect(EffectRegistries.UNATTRACT_EFFECT);
        }
    }

}