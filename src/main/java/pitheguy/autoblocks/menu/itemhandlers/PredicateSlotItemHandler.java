package pitheguy.autoblocks.menu.itemhandlers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class PredicateSlotItemHandler extends SlotItemHandler {
    private final Predicate<Item> predicate;

    public PredicateSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<Item> predicate) {
        super(itemHandler, index, xPosition, yPosition);
        this.predicate = predicate;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return predicate.test(stack.getItem()) && super.mayPlace(stack);
    }
}
