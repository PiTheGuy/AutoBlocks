package pitheguy.autoblocks.menu.itemhandlers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SingleItemSlotItemHandler extends SlotItemHandler {
    final Item validItem;
    public SingleItemSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item validItem) {
        super(itemHandler, index, xPosition, yPosition);
        this.validItem = validItem;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return stack.getItem() == validItem && super.mayPlace(stack);
    }
}