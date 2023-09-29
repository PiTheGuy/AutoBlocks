package pitheguy.autoblocks.menu.itemhandlers;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import pitheguy.autoblocks.AllItems;
import pitheguy.autoblocks.items.FilterItem;

public class FilterSlotItemHandler extends SlotItemHandler {
    public FilterSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return super.mayPlace(stack) && stack.is(AllItems.FILTER.get()) && FilterItem.hasFilterBlock(stack);
    }
}
