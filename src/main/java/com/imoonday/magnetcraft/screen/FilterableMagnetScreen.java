package com.imoonday.magnetcraft.screen;

import com.imoonday.magnetcraft.methods.FilterNbtMethods;
import com.imoonday.magnetcraft.screen.handler.FilterableMagnetScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.HashSet;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class FilterableMagnetScreen extends HandledScreen<FilterableMagnetScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/magnet_filter.png");

    /**
     * x:目标x坐标 -1
     * y:目标y坐标 -1
     * u:替代x坐标 -1
     * v:替代y坐标 -1
     * w:替代的宽度
     * h:替代的高度
     */

    public FilterableMagnetScreen(FilterableMagnetScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
        updateFilter();
    }

    @Override
    public void close() {
        updateFilter();
        super.close();
        this.handler.getInventory().clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean onWhitelist = mouseX >= x + 11 && mouseX <= x + 11 + 8 && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        boolean onBlacklist = mouseX >= x + 11 && mouseX <= x + 11 + 8 && mouseY >= y + 36 && mouseY <= y + 36 + 8;
        boolean onCompareDamage = mouseX >= x + 157 && mouseX <= x + 157 + 8 && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        boolean onCompareNbt = mouseX >= x + 157 && mouseX <= x + 157 + 8 && mouseY >= y + 36 && mouseY <= y + 36 + 8;
        int slot = this.handler.getSlot();
        HashSet<ItemStack> stacks = new HashSet<>();
        for (int i = 0; i < this.handler.getInventory().size(); i++) {
            if (!this.handler.getInventory().getStack(i).isOf(Items.AIR)) {
                stacks.add(this.handler.getInventory().getStack(i));
            }
        }
        FilterNbtMethods.setFilterItems(this.handler.getStack(), stacks);
        setNbt(this.handler.getPlayer(), slot, this.handler.getStack().getOrCreateNbt());
        PacketByteBuf buf = PacketByteBufs.create();
        if (onWhitelist) {
            buf.writeBoolean(true);
            buf.writeNbt(this.handler.getStack().getOrCreateNbt());
            buf.writeInt(1);
            buf.writeInt(slot);
            buf.writeString("Whitelist");
            buf.writeBoolean(true);
            buf.retain();
            ClientPlayNetworking.send(id("change_filter"), buf);
            setBoolean(this.handler.getPlayer(), slot, "Whitelist", true);
        } else if (onBlacklist) {
            buf.writeBoolean(true);
            buf.writeNbt(this.handler.getStack().getOrCreateNbt());
            buf.writeInt(1);
            buf.writeInt(slot);
            buf.writeString("Whitelist");
            buf.writeBoolean(false);
            buf.retain();
            ClientPlayNetworking.send(id("change_filter"), buf);
            setBoolean(this.handler.getPlayer(), slot, "Whitelist", false);
        } else if (onCompareDamage) {
            buf.writeBoolean(true);
            buf.writeNbt(this.handler.getStack().getOrCreateNbt());
            buf.writeInt(2);
            buf.writeInt(slot);
            buf.writeString("CompareDamage");
            buf.retain();
            ClientPlayNetworking.send(id("change_filter"), buf);
            setBoolean(this.handler.getPlayer(), slot, "CompareDamage");
        } else if (onCompareNbt) {
            buf.writeBoolean(true);
            buf.writeNbt(this.handler.getStack().getOrCreateNbt());
            buf.writeInt(2);
            buf.writeInt(slot);
            buf.writeString("CompareNbt");
            buf.retain();
            ClientPlayNetworking.send(id("change_filter"), buf);
            setBoolean(this.handler.getPlayer(), slot, "CompareNbt");
        } else {
            buf.writeBoolean(false);
            buf.writeNbt(this.handler.getStack().getOrCreateNbt());
            buf.writeInt(slot);
            buf.retain();
            ClientPlayNetworking.send(id("change_filter"), buf);
        }
        this.handler.getPlayer().getInventory().markDirty();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        boolean whitelist = this.handler.getStack().getOrCreateNbt().getBoolean("Whitelist");
        boolean compareDamage = this.handler.getStack().getOrCreateNbt().getBoolean("CompareDamage");
        boolean compareNbt = this.handler.getStack().getOrCreateNbt().getBoolean("CompareNbt");
        boolean onWhitelist = mouseX >= x + 11 && mouseX <= x + 11 + 8 && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        boolean onBlacklist = mouseX >= x + 11 && mouseX <= x + 11 + 8 && mouseY >= y + 36 && mouseY <= y + 36 + 8;
        boolean onCompareDamage = mouseX >= x + 157 && mouseX <= x + 157 + 8 && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        boolean onCompareNbt = mouseX >= x + 157 && mouseX <= x + 157 + 8 && mouseY >= y + 36 && mouseY <= y + 36 + 8;
        if (whitelist) {
            if (onWhitelist) {
                drawTexture(matrices, x + 11, y + 20, 8, 174, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 20, 0, 174, 8, 8);
            }
            if (onBlacklist) {
                drawTexture(matrices, x + 11, y + 36, 8, 166, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 36, 0, 166, 8, 8);
            }
        } else {
            if (onWhitelist) {
                drawTexture(matrices, x + 11, y + 20, 8, 166, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 20, 0, 166, 8, 8);
            }
            if (onBlacklist) {
                drawTexture(matrices, x + 11, y + 36, 8, 174, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 36, 0, 174, 8, 8);
            }
        }
        if (compareDamage) {
            if (onCompareDamage) {
                drawTexture(matrices, x + 157, y + 20, 8, 174, 8, 8);
            } else {
                drawTexture(matrices, x + 157, y + 20, 0, 174, 8, 8);
            }
        } else {
            if (onCompareDamage) {
                drawTexture(matrices, x + 157, y + 20, 8, 166, 8, 8);
            } else {
                drawTexture(matrices, x + 157, y + 20, 0, 166, 8, 8);
            }
        }
        if (compareNbt) {
            if (onCompareNbt) {
                drawTexture(matrices, x + 157, y + 36, 8, 174, 8, 8);
            } else {
                drawTexture(matrices, x + 157, y + 36, 0, 174, 8, 8);
            }
        } else {
            if (onCompareNbt) {
                drawTexture(matrices, x + 157, y + 36, 8, 166, 8, 8);
            } else {
                drawTexture(matrices, x + 157, y + 36, 0, 166, 8, 8);
            }
        }
        textRenderer.draw(matrices, Text.literal("白名单"), x + 11 + 8 + 2, y + 20, Color.black.getRGB());
        textRenderer.draw(matrices, Text.literal("黑名单"), x + 11 + 8 + 2, y + 36, Color.black.getRGB());
        textRenderer.draw(matrices, Text.literal("对比耐久"), x + 157 - textRenderer.getWidth(Text.literal("对比耐久")) - 2, y + 20, Color.black.getRGB());
        textRenderer.draw(matrices, Text.literal("对比NBT"), x + 157 - textRenderer.getWidth(Text.literal("对比NBT")) - 2, y + 36, Color.black.getRGB());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    public void updateFilter(){
        int slot = this.handler.getSlot();
        HashSet<ItemStack> stacks = new HashSet<>();
        for (int i = 0; i < this.handler.getInventory().size(); i++) {
            if (!this.handler.getInventory().getStack(i).isOf(Items.AIR)) {
                stacks.add(this.handler.getInventory().getStack(i));
            }
        }
        FilterNbtMethods.setFilterItems(this.handler.getStack(), stacks);
        setNbt(this.handler.getPlayer(), slot, this.handler.getStack().getOrCreateNbt());
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);
        buf.writeNbt(this.handler.getStack().getOrCreateNbt());
        buf.writeInt(slot);
        buf.retain();
        ClientPlayNetworking.send(id("change_filter"), buf);
    }

    public static void setNbt(PlayerEntity player, int slot, NbtCompound nbt) {
        ItemStack stack;
        if (slot != -1) {
            stack = player.getInventory().getStack(slot);
        } else {
            stack = player.getOffHandStack();
        }
        stack.setNbt(nbt);
    }

    public static void setBoolean(PlayerEntity player, int slot, String key, boolean b) {
        ItemStack stack;
        if (slot != -1) {
            stack = player.getInventory().getStack(slot);
        } else {
            stack = player.getOffHandStack();
        }
        stack.getOrCreateNbt().putBoolean(key, b);
    }

    public static void setBoolean(PlayerEntity player, int slot, String key) {
        ItemStack stack;
        if (slot != -1) {
            stack = player.getInventory().getStack(slot);
        } else {
            stack = player.getOffHandStack();
        }
        stack.getOrCreateNbt().putBoolean(key, !stack.getOrCreateNbt().getBoolean(key));
    }

}
