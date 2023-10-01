package pitheguy.autoblocks.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import pitheguy.autoblocks.AutoBlocks;
import pitheguy.autoblocks.blockentity.placer.AutoPlacerBlockEntity;

import java.util.Comparator;
import java.util.Map;

public class MaterialsScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(AutoBlocks.MODID, "textures/gui/auto_placer_materials.png");

    private int leftPos;
    private int topPos;
    private final int imageWidth;
    private final int imageHeight;
    private final AutoPlacerBlockEntity placer;
    private FittingMultiLineTextWidget materialList;

    public MaterialsScreen(AutoPlacerBlockEntity placer) {
        super(Component.translatable("gui.auto_placer.materials"));
        this.placer = placer;
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        materialList = new FittingMultiLineTextWidget(this.leftPos + 7, this.topPos + 17, 158, 197, getMaterialText(), this.font);
        this.addRenderableWidget(materialList);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private Component getMaterialText() {
        Map<Item, Integer> materials = placer.getRemainingMaterials();
        if (materials == null) return Component.empty();
        MutableComponent result = Component.empty();
        materials.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).forEach(entry -> result.append(getComponentForEntry(entry)).append("\n"));
        return result;
    }

    private Component getComponentForEntry(Map.Entry<Item, Integer> entry) {
        Item item = entry.getKey();
        int count = entry.getValue();
        return Component.literal(count + "x ").withStyle(ChatFormatting.GRAY).append(Component.translatable(item.getDescriptionId()));
    }
}
