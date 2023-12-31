package pitheguy.autoblocks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import pitheguy.autoblocks.AutoBlocks;
import pitheguy.autoblocks.menu.AutoPlacerMenu;
import pitheguy.autoblocks.networking.*;

public class AutoPlacerScreen extends AbstractContainerScreen<AutoPlacerMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(AutoBlocks.MODID, "textures/gui/auto_placer.png");

    private EditBox schematicName;
    private LoadButton loadButton;
    private StartButton startButton;
    private MaterialsButton materialsButton;

    public AutoPlacerScreen(AutoPlacerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 200;
        this.imageHeight = 222;
        this.inventoryLabelY = 128;
    }

    @Override
    protected void init() {
        super.init();
        schematicName = new EditBox(this.font, this.leftPos + 7, this.topPos + 90, 162, 16, Component.literal("Schematic Name"));
        schematicName.setTextColor(-1);
        schematicName.setValue(menu.tileEntity.getSchematicName());
        schematicName.setResponder(input -> schematicName.active = !input.isEmpty() && !input.equals(menu.tileEntity.getSchematicName()));
        this.addRenderableWidget(schematicName);
        loadButton = new LoadButton();
        loadButton.active = !menu.tileEntity.hasSchematic();
        this.addRenderableWidget(loadButton);
        startButton = new StartButton();
        startButton.active = menu.tileEntity.hasSchematic() && menu.tileEntity.isRunning();
        this.addRenderableWidget(startButton);
        materialsButton = new MaterialsButton();
        materialsButton.active = menu.tileEntity.hasSchematic();
        this.addRenderableWidget(materialsButton);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) this.minecraft.player.closeContainer();

        if (!this.schematicName.keyPressed(keyCode, scanCode, modifiers) && !this.schematicName.canConsumeInput())
            return super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.minecraft.getTextureManager().bindForSetup(TEXTURE);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(TEXTURE, this.leftPos + 98, this.topPos + 129, 0, 222, getProgressScaled(), 5);
    }

    public int getProgressScaled() {
        return (int) (menu.tileEntity.getProgress() * 71);
    }

    class LoadButton extends AbstractButton {

        public LoadButton() {
            super(leftPos + 7, topPos + 108, 54, 16, Component.translatable("gui.auto_placer.load"));
        }

        @Override
        public void onPress() {
            AllPackets.CHANNEL.sendToServer(new LoadSchematicPacket(schematicName.getValue()));
            loadButton.active = false;
            startButton.active = true;
            materialsButton.active = true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }

    class StartButton extends AbstractButton {
        public StartButton() {
            super(leftPos + 61, topPos + 108, 54, 16, Component.translatable("gui.auto_placer.start"));
        }

        @Override
        public void onPress() {
            AllPackets.CHANNEL.sendToServer(new StartPlacerPacket());
            //minecraft.player.closeContainer();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }

    class MaterialsButton extends AbstractButton {

        public MaterialsButton() {
            super(leftPos + 115, topPos + 108, 54, 16, Component.translatable("gui.auto_placer.materials"));
        }

        @Override
        public void onPress() {
            minecraft.setScreen(new MaterialsScreen(menu.tileEntity));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }
}
