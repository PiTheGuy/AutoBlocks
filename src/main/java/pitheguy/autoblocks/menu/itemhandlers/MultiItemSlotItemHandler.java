package pitheguy.autoblocks.menu.itemhandlers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class MultiItemSlotItemHandler extends SlotItemHandler {
    private final Collection<Item> validItems;

    public MultiItemSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Collection<Item> validItems) {
        super(itemHandler, index, xPosition, yPosition);
        this.validItems = validItems;
    }

    public MultiItemSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item... validItems) {
        this(itemHandler, index, xPosition, yPosition, Arrays.asList(validItems));
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return validItems.contains(stack.getItem()) && super.mayPlace(stack);
    }
}
