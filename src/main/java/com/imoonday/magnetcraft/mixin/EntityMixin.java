package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.methods.AttractMethods;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttractNbt {

    protected NbtCompound attractData = new NbtCompound();
    protected double attractDis = 0;
    protected boolean isAttracting = false;
    protected boolean enable = true;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!this.attractData.contains("AttractDis")) {
            this.attractData.putDouble("AttractDis", 0);
        }
        if (!this.attractData.contains("isAttracting")) {
            this.attractData.putBoolean("isAttracting", false);
        }
        if (!this.attractData.contains("Enable")) {
            this.attractData.putBoolean("Enable", true);
        }
        this.attractDis = this.attractData.getDouble("AttractDis");
        this.isAttracting = this.attractData.getBoolean("isAttracting");
        this.enable = this.attractData.getBoolean("Enable");
        if (this.isAttracting && this.enable) {
            AttractMethods.attracting(entity, this.attractDis);
        }
    }

    @Override
    public NbtCompound getAttractData() {
        return this.attractData;
    }

    @Override
    public boolean clearAttractData() {
        this.attractData = new NbtCompound();
        return true;
    }

    @Override
    public double getAttractDis() {
        return this.attractData.contains("AttractDis", NbtElement.DOUBLE_TYPE) ? this.attractData.getDouble("AttractDis") : 0;
    }

    @Override
    public void setAttractDis(double dis) {
        this.attractData.putDouble("AttractDis", dis);
    }

    @Override
    public boolean isAttracting() {
        return this.attractData.getBoolean("isAttracting");
    }

    @Override
    public void setAttracting(boolean attracting) {
        this.attractData.putBoolean("isAttracting", attracting);
        if (!attracting) {
            this.attractData.putDouble("AttractDis", 0);
        }
    }

    @Override
    public void setAttracting(boolean attracting, double dis) {
        setAttracting(attracting);
        setAttractDis(dis);
    }

    @Override
    public boolean getEnable() {
        return this.enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void writePocketsDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.put("AttractData", this.attractData);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.attractData = nbt.getCompound("AttractData");
    }

}
