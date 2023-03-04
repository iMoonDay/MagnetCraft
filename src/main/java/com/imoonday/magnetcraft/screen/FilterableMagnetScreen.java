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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.CHANGE_FILTER_PACKET_ID;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class FilterableMagnetScreen extends HandledScreen<FilterableMagnetScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/magnet_filter.png");

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
        super.close();
        this.handler.getInventory().clear();
    }

    @Override
    protected void handledScreenTick() {
        this.handler.getPlayer().getInventory().markDirty();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int slot = this.handler.getSlot();
        Inventory inventory = this.handler.getInventory();
        PlayerEntity player = this.handler.getPlayer();
        boolean onEnable = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.enable")) && mouseY >= y + 6 && mouseY <= y + 6 + 8;
        boolean onWhitelist = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.autoconfig.magnetcraft.option.whitelist")) && mouseY >= y + 22 && mouseY <= y + 22 + 8;
        boolean onBlacklist = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.autoconfig.magnetcraft.option.blacklist")) && mouseY >= y + 38 && mouseY <= y + 38 + 8;
        boolean onCompareDamage = mouseX >= x + 157 - 2 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.damage")) && mouseX <= x + 157 + 8 && mouseY >= y + 22 && mouseY <= y + 22 + 8;
        boolean onCompareNbt = mouseX >= x + 157 - 2 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.nbt")) && mouseX <= x + 157 + 8 && mouseY >= y + 38 && mouseY <= y + 38 + 8;
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            stacks.add(inventory.getStack(i));
        }
        ItemStack newStack = this.handler.getStack().copy();
        FilterNbtMethods.setFilterItems(newStack, stacks);
        PacketByteBuf buf = PacketByteBufs.create();
        if (onWhitelist) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            buf.writeBoolean(true);
            buf.writeNbt(newStack.getOrCreateNbt());
            buf.writeInt(1);
            buf.writeInt(slot);
            buf.writeString("Whitelist");
            buf.writeBoolean(true);
            ClientPlayNetworking.send(CHANGE_FILTER_PACKET_ID, buf);
        } else if (onBlacklist) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            buf.writeBoolean(true);
            buf.writeNbt(newStack.getOrCreateNbt());
            buf.writeInt(1);
            buf.writeInt(slot);
            buf.writeString("Whitelist");
            buf.writeBoolean(false);
            ClientPlayNetworking.send(CHANGE_FILTER_PACKET_ID, buf);
        } else if (onCompareDamage && !this.handler.isCropMagnet()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            buf.writeBoolean(true);
            buf.writeNbt(newStack.getOrCreateNbt());
            buf.writeInt(2);
            buf.writeInt(slot);
            buf.writeString("CompareDamage");
            ClientPlayNetworking.send(CHANGE_FILTER_PACKET_ID, buf);
        } else if (onCompareNbt && !this.handler.isCropMagnet()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            buf.writeBoolean(true);
            buf.writeNbt(newStack.getOrCreateNbt());
            buf.writeInt(2);
            buf.writeInt(slot);
            buf.writeString("CompareNbt");
            ClientPlayNetworking.send(CHANGE_FILTER_PACKET_ID, buf);
        } else if (onEnable && !this.handler.isCropMagnet()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            buf.writeBoolean(true);
            buf.writeNbt(newStack.getOrCreateNbt());
            buf.writeInt(2);
            buf.writeInt(slot);
            buf.writeString("Enable");
            ClientPlayNetworking.send(CHANGE_FILTER_PACKET_ID, buf);
        } else {
            buf.writeBoolean(false);
            buf.writeNbt(newStack.getOrCreateNbt());
            buf.writeInt(slot);
            ClientPlayNetworking.send(CHANGE_FILTER_PACKET_ID, buf);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        ItemStack stack = this.handler.getStack();
        NbtCompound nbt = stack.getOrCreateNbt();
        boolean whitelist = nbt.getBoolean("Whitelist");
        boolean enable = nbt.getBoolean("Enable");
        boolean compareDamage = nbt.getBoolean("CompareDamage");
        boolean compareNbt = nbt.getBoolean("CompareNbt");
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        boolean onEnable = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.enable")) && mouseY >= y + 6 && mouseY <= y + 6 + 8;
        boolean onWhitelist = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.autoconfig.magnetcraft.option.whitelist")) && mouseY >= y + 22 && mouseY <= y + 22 + 8;
        boolean onBlacklist = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.autoconfig.magnetcraft.option.blacklist")) && mouseY >= y + 38 && mouseY <= y + 38 + 8;
        boolean onCompareDamage = mouseX >= x + 157 - 2 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.damage")) && mouseX <= x + 157 + 8 && mouseY >= y + 22 && mouseY <= y + 22 + 8;
        boolean onCompareNbt = mouseX >= x + 157 - 2 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.nbt")) && mouseX <= x + 157 + 8 && mouseY >= y + 38 && mouseY <= y + 38 + 8;
        if (whitelist) {
            if (onWhitelist) {
                drawTexture(matrices, x + 11, y + 22, 8, 174, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 22, 0, 174, 8, 8);
            }
            if (onBlacklist) {
                drawTexture(matrices, x + 11, y + 38, 8, 166, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 38, 0, 166, 8, 8);
            }
        } else {
            if (onWhitelist) {
                drawTexture(matrices, x + 11, y + 22, 8, 166, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 22, 0, 166, 8, 8);
            }
            if (onBlacklist) {
                drawTexture(matrices, x + 11, y + 38, 8, 174, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 38, 0, 174, 8, 8);
            }
        }
        if (!this.handler.isCropMagnet()) {
            if (compareDamage) {
                if (onCompareDamage) {
                    drawTexture(matrices, x + 157, y + 22, 8, 174, 8, 8);
                } else {
                    drawTexture(matrices, x + 157, y + 22, 0, 174, 8, 8);
                }
            } else {
                if (onCompareDamage) {
                    drawTexture(matrices, x + 157, y + 22, 8, 166, 8, 8);
                } else {
                    drawTexture(matrices, x + 157, y + 22, 0, 166, 8, 8);
                }
            }
            if (compareNbt) {
                if (onCompareNbt) {
                    drawTexture(matrices, x + 157, y + 38, 8, 174, 8, 8);
                } else {
                    drawTexture(matrices, x + 157, y + 38, 0, 174, 8, 8);
                }
            } else {
                if (onCompareNbt) {
                    drawTexture(matrices, x + 157, y + 38, 8, 166, 8, 8);
                } else {
                    drawTexture(matrices, x + 157, y + 38, 0, 166, 8, 8);
                }
            }
            if (enable) {
                if (onEnable) {
                    drawTexture(matrices, x + 11, y + 6, 8, 174, 8, 8);
                } else {
                    drawTexture(matrices, x + 11, y + 6, 0, 174, 8, 8);
                }
            } else {
                if (onEnable) {
                    drawTexture(matrices, x + 11, y + 6, 8, 166, 8, 8);
                } else {
                    drawTexture(matrices, x + 11, y + 6, 0, 166, 8, 8);
                }
            }
            textRenderer.draw(matrices, Text.translatable("text.magnetcraft.screen.compare.damage"), x + 157 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.damage")) - 2, y + 22, Color.black.getRGB());
            textRenderer.draw(matrices, Text.translatable("text.magnetcraft.screen.compare.nbt"), x + 157 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.nbt")) - 2, y + 38, Color.black.getRGB());
            textRenderer.draw(matrices, Text.translatable("text.magnetcraft.screen.enable"), x + 11 + 8 + 2, y + 6, Color.black.getRGB());
        }
        textRenderer.draw(matrices, Text.translatable("text.autoconfig.magnetcraft.option.whitelist"), x + 11 + 8 + 2, y + 22, Color.black.getRGB());
        textRenderer.draw(matrices, Text.translatable("text.autoconfig.magnetcraft.option.blacklist"), x + 11 + 8 + 2, y + 38, Color.black.getRGB());
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
        if (!this.handler.isCropMagnet()) {
            titleX = (backgroundWidth - textRenderer.getWidth(title)) / 3 * 2;
        } else {
            titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        }
}

    public void updateFilter() {
        Inventory inventory = this.handler.getInventory();
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            stacks.add(inventory.getStack(i));
        }
        ItemStack newStack = this.handler.getStack().copy();
        FilterNbtMethods.setFilterItems(newStack, stacks);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);
        buf.writeNbt(newStack.getOrCreateNbt());
        buf.writeInt(this.handler.getSlot());
        ClientPlayNetworking.send(CHANGE_FILTER_PACKET_ID, buf);
    }

}
