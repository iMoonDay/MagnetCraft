package com.imoonday.magnetcraft.screen;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.screen.handler.LodestoneScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

@SuppressWarnings("AlibabaUndefineMagicConstant")
public class LodestoneScreen extends HandledScreen<LodestoneScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/lodestone.png");

    public LodestoneScreen(LodestoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        boolean onRedstone = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.magnetcraft.message.redstone_mode")) && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        boolean onLeftClick = mouseX >= x + 132 && mouseX <= x + 132 + 8 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        boolean onRightClick = mouseX >= x + 133 + 16 + 8 && mouseX <= x + 133 + 16 + 16 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        boolean onFilter = mouseX >= x + 110 && mouseX <= x + 110 + 8 && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        if (onRedstone || onLeftClick || onRightClick || onFilter) {
            this.handler.getPlayer().playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            int id;
            if (onRedstone) {
                id = 0;
            } else if (onLeftClick) {
                id = 1;
            } else if (onRightClick) {
                id = 2;
            } else {
                id = 3;
            }
            if (this.client != null) {
                this.handler.onButtonClick(this.client.player, id);
                if (this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(this.handler.syncId, id);
                }
            }
        }
        return true;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        if (!this.handler.getPlayer().world.getBlockState(this.handler.getPos()).isOf(BlockRegistries.LODESTONE_BLOCK)) {
            close();
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        int dis = this.handler.getDis();
        boolean redstone = this.handler.getRedstone() == 1;
        boolean filter = this.handler.isFilter();
        boolean onRedstone = mouseX >= x + 11 && mouseX <= x + 11 + 8 + textRenderer.getWidth(Text.translatable("text.magnetcraft.message.redstone_mode")) && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        boolean onLeftClick = mouseX >= x + 132 && mouseX <= x + 132 + 8 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        boolean onRightClick = mouseX >= x + 133 + 16 + 8 && mouseX <= x + 133 + 16 + 16 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        boolean onFilter = mouseX >= x + 110 && mouseX <= x + 110 + 8 && mouseY >= y + 20 && mouseY <= y + 20 + 8;
        drawTexture(matrices, x + 11, y + 20, onRedstone ? 8 : 0, redstone ? 166 + 8 : 166, 8, 8);
        if (onLeftClick) {
            drawTexture(matrices, x + 132, y + 17, 48, 166, 16, 16);
            drawTexture(matrices, x + 133 + 16, y + 17, 16 + 16, 166, 16, 16);
        } else if (onRightClick) {
            drawTexture(matrices, x + 132, y + 17, 16, 166, 16, 16);
            drawTexture(matrices, x + 133 + 16, y + 17, 48 + 16, 166, 16, 16);
        } else {
            drawTexture(matrices, x + 132, y + 17, 16, 166, 16, 16);
            drawTexture(matrices, x + 133 + 16, y + 17, 16 + 16, 166, 16, 16);
        }
        drawTexture(matrices, x + 110, y + 20, onFilter ? 8 : 0, filter ? 166 + 8 : 166, 8, 8);
        textRenderer.draw(matrices, Text.translatable("text.magnetcraft.message.redstone_mode"), x + 11 + 10, y + 20, Color.black.getRGB());
        textRenderer.draw(matrices, Text.translatable("text.magnetcraft.message.direction." + this.handler.getDirection()), x + 11, y + 10, Color.black.getRGB());
        int filterX = x + 110 + 4 - textRenderer.getWidth(Text.translatable("text.magnetcraft.message.filter")) / 2;
        textRenderer.draw(matrices, Text.translatable("text.magnetcraft.message.filter"), filterX, y + 10, Color.black.getRGB());
        int x1 = ((x + 133 + 16) * 2 - textRenderer.getWidth(String.valueOf(dis))) / 2;
        int textWidth = textRenderer.getWidth(Text.translatable("text.magnetcraft.message.attract"));
        int half = textWidth / 2;
        int startX = x1 + textRenderer.getWidth(String.valueOf(dis)) / 2 - half;
        int endX = startX + textWidth;
        if (endX > x + backgroundWidth) {
            startX = x + 86;
        }
        if (x + 132 + 8 + textRenderer.getWidth(Text.translatable("text.magnetcraft.message.attract")) < x + backgroundWidth) {
            textRenderer.draw(matrices, Text.translatable("text.magnetcraft.message.attract"), startX, y + 7, Color.black.getRGB());
        } else {
            textRenderer.draw(matrices, Text.translatable("text.magnetcraft.message.attract"), x + 132 + 8 - 8, y + 7, Color.black.getRGB());
        }
        textRenderer.draw(matrices, String.valueOf(dis), x1, y + 21, Color.black.getRGB());
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
        titleX = (backgroundWidth - textRenderer.getWidth(title)) - 10;
    }

}
