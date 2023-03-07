package com.imoonday.magnetcraft.screen;

import com.imoonday.magnetcraft.screen.handler.AdvancedGrindstoneScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class AdvancedGrindstoneScreen extends HandledScreen<AdvancedGrindstoneScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/advanced_grindstone.png");

    public AdvancedGrindstoneScreen(AdvancedGrindstoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean onLeftButton = mouseX >= x + 128 && mouseX <= x + 128 + 7 && mouseY >= y + 53 && mouseY <= y + 53 + 11;
        boolean onRightButton = mouseX >= x + 139 && mouseX <= x + 139 + 7 && mouseY >= y + 53 && mouseY <= y + 53 + 11;
        int id;
        if (onLeftButton || onRightButton) {
            id = onLeftButton ? 0 : 1;
            if (this.client != null && this.client.player != null) {
                if (this.client.interactionManager != null) {
                    this.handler.onButtonClick(this.client.player, id);
                    this.client.interactionManager.clickButton(this.handler.syncId, id);
                }
                this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            }
        }
        this.handler.updateToClient();
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
        boolean onLeftButton = mouseX >= x + 128 && mouseX <= x + 128 + 7 && mouseY >= y + 53 && mouseY <= y + 53 + 11;
        boolean onRightButton = mouseX >= x + 139 && mouseX <= x + 139 + 7 && mouseY >= y + 53 && mouseY <= y + 53 + 11;
        if (onLeftButton) {
            drawTexture(matrices, x + 128, y + 53, 176, 0, 7, 11);
        } else if (onRightButton) {
            drawTexture(matrices, x + 139, y + 53, 187, 0, 7, 11);
        }
        List<Text> tooltip = new ArrayList<>();
        NbtCompound nbtCompound = EnchantedBookItem.getEnchantmentNbt(this.handler.getResult().getStack(0)).getCompound(0);
        Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(e -> tooltip.add(e.getName(EnchantmentHelper.getLevelFromNbt(nbtCompound))));
        if (!tooltip.isEmpty()) {
            int textWidth = textRenderer.getWidth(tooltip.get(0));
            int half = textWidth / 2;
            int startX = x + 128 + 9 - half;
            int endX = startX + textWidth;
            if (endX > x + backgroundWidth) {
                startX = x + 86;
            }
            textRenderer.draw(matrices, tooltip.get(0).getString(), startX, y + 23, Color.black.getRGB());
        }
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
//        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

}
