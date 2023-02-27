package com.imoonday.magnetcraft.screen;

import com.imoonday.magnetcraft.screen.handler.LodestoneScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class LodestoneScreen extends HandledScreen<LodestoneScreenHandler> {
    private static final Identifier TEXTURE = id("textures/gui/lodestone.png");

    /**
     * x:目标x坐标 -1
     * y:目标y坐标 -1
     * u:替代x坐标 -1
     * v:替代y坐标 -1
     * w:替代的宽度
     * h:替代的高度
     */

    //第一格坐标159,86,一格大小18*18
    public LodestoneScreen(LodestoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        boolean redstone = this.handler.getRedstone() == 1;
        boolean onRedstone = mouseX >= x + 11 && mouseX <= x + 11 + 8 && mouseY >= y + 17 && mouseY <= y + 17 + 8;
        boolean onLeftClick = mouseX >= x + 133 && mouseX <= x + 133 + 8 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        boolean onRightClick = mouseX >= x + 133 + 16 + 8 && mouseX <= x + 133 + 16 + 16 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        if (onRedstone) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(String.valueOf(redstone)));
        }
        if (onLeftClick) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("-"));
        }
        if (onRightClick) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("+"));
        }
        return true;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        boolean redstone = this.handler.getRedstone() == 1;
        boolean onRedstone = mouseX >= x + 11 && mouseX <= x + 11 + 8 && mouseY >= y + 17 && mouseY <= y + 17 + 8;
        boolean onLeftClick = mouseX >= x + 133 && mouseX <= x + 133 + 8 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        boolean onRightClick = mouseX >= x + 133 + 16 + 8 && mouseX <= x + 133 + 16 + 16 && mouseY >= y + 17 && mouseY <= y + 17 + 16;
        if (onRedstone) {
            if (redstone) {
                drawTexture(matrices, x + 11, y + 17, 8, 166 + 8, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 17, 8, 166, 8, 8);
            }
        } else {
            if (redstone) {
                drawTexture(matrices, x + 11, y + 17, 0, 166 + 8, 8, 8);
            } else {
                drawTexture(matrices, x + 11, y + 17, 0, 166, 8, 8);
            }
        }
        if (onLeftClick) {
            drawTexture(matrices, x + 133, y + 17, 48, 166, 16, 16);
            drawTexture(matrices, x + 133 + 16, y + 17, 16 + 16, 166, 16, 16);
        } else if (onRightClick) {
            drawTexture(matrices, x + 133, y + 17, 16, 166, 16, 16);
            drawTexture(matrices, x + 133 + 16, y + 17, 48 + 16, 166, 16, 16);
        } else {
            drawTexture(matrices, x + 133, y + 17, 16, 166, 16, 16);
            drawTexture(matrices, x + 133 + 16, y + 17, 16 + 16, 166, 16, 16);
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
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

}
