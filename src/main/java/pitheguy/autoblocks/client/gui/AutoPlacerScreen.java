package pitheguy.autoblocks.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import pitheguy.autoblocks.AutoBlocks;
import pitheguy.autoblocks.blockentity.placer.PlacerLoadException;
import pitheguy.autoblocks.blocks.AutoPlacerBlock;
import pitheguy.autoblocks.menu.AutoPlacerMenu;

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
        this.addRenderableWidget(schematicName);
        loadButton = new LoadButton();
        this.addRenderableWidget(loadButton);
        startButton = new StartButton();
        startButton.active = false;
        this.addRenderableWidget(startButton);
        materialsButton = new MaterialsButton();
        materialsButton.active = false;
        this.addRenderableWidget(materialsButton);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }

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
    }

    class LoadButton extends AbstractButton {

        public LoadButton() {
            super(leftPos + 7, topPos + 108, 54, 16, Component.literal("Load"));
        }

        @Override
        public void onPress() {
            try {
                menu.tileEntity.loadSchematic(schematicName.getValue());
                menu.tileEntity.setSchematicName(schematicName.getValue());
            } catch (PlacerLoadException e) {
                minecraft.player.closeContainer();
                minecraft.player.displayClientMessage(Component.literal(e.getMessage()), true);
                loadButton.active = true;
                startButton.active = false;
                materialsButton.active = false;
                return;
            }
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
            super(leftPos + 61, topPos + 108, 54, 16, Component.literal("Start"));
        }

        @Override
        public void onPress() {
            menu.tileEntity.start();
            minecraft.player.closeContainer();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }

    class MaterialsButton extends AbstractButton {

        public MaterialsButton() {
            super(leftPos + 115, topPos + 108, 54, 16, Component.literal("Materials"));
        }

        @Override
        public void onPress() {
            //TODO add materials logic
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }
}
