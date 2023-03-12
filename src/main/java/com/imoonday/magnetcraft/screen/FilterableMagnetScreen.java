package com.imoonday.magnetcraft.screen;

import com.imoonday.magnetcraft.screen.handler.FilterableMagnetScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class FilterableMagnetScreen extends HandledScreen<FilterableMagnetScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/magnet_filter.png");

    public FilterableMagnetScreen(FilterableMagnetScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void close() {
        super.close();
        this.handler.getFilterSlots().clear();
        this.handler.getShulkerBoxSlots().clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        PlayerEntity player = this.handler.getPlayer();
        boolean crop = this.handler.isCropMagnet();
        boolean onEnable = !crop && mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.enable")) && mouseY >= y + 6 && mouseY <= y + 6 + 8;
        boolean onWhitelist = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.autoconfig.magnetcraft.option.whitelist")) && mouseY >= y + 22 && mouseY <= y + 22 + 8;
        boolean onBlacklist = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.autoconfig.magnetcraft.option.blacklist")) && mouseY >= y + 38 && mouseY <= y + 38 + 8;
        boolean onCompareDamage = !crop && mouseX >= x + 157 - 2 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.damage")) && mouseX <= x + 157 + 8 && mouseY >= y + 22 && mouseY <= y + 22 + 8;
        boolean onCompareNbt = !crop && mouseX >= x + 157 - 2 - textRenderer.getWidth(Text.translatable("text.magnetcraft.screen.compare.nbt")) && mouseX <= x + 157 + 8 && mouseY >= y + 38 && mouseY <= y + 38 + 8;
        boolean onButton = onEnable || onWhitelist || onBlacklist || onCompareDamage || onCompareNbt;
        int id;
        if (onButton) {
            if (onWhitelist) {
                id = 0;
            } else if (onBlacklist) {
                id = 1;
            } else if (onCompareDamage) {
                id = 2;
            } else if (onCompareNbt) {
                id = 3;
            } else {
                id = 4;
            }
            if (this.client != null) {
                this.handler.onButtonClick(this.client.player, id);
                if (this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(this.handler.syncId, id);
                }
            }
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        NbtCompound nbt = this.handler.getStack().getOrCreateNbt();
        boolean whitelist = nbt.getBoolean("Whitelist");
        boolean enable = nbt.getBoolean("Enable");
        boolean compareDamage = nbt.getBoolean("CompareDamage");
        boolean compareNbt = nbt.getBoolean("CompareNbt");
        boolean crop = this.handler.isCropMagnet();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        if (this.handler.canTeleportItems()) {
            drawTexture(matrices, x, y, 0, 0, backgroundWidth + 23, backgroundHeight);
        } else {
            drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        }
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
        if (!crop) {
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

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        if (this.handler.canTeleportItems()) {
            return mouseX < (double) left || mouseY < (double) top || mouseX >= (double) (left + this.backgroundWidth + 23) || mouseY >= (double) (top + this.backgroundHeight);
        } else {
            return mouseX < (double) left || mouseY < (double) top || mouseX >= (double) (left + this.backgroundWidth) || mouseY >= (double) (top + this.backgroundHeight);
        }
    }

}
