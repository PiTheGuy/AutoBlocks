package pitheguy.autoblocks.menu.itemhandlers;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import pitheguy.autoblocks.AllItems;

public class ExcludeUpgradesSlotItemHandler extends SlotItemHandler {
    public ExcludeUpgradesSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.is(AllItems.SPEED_UPGRADE.get()) || stack.is(AllItems.RANGE_UPGRADE.get()) || stack.is(AllItems.ENERGY_UPGRADE.get()))
            return false;
        else return super.mayPlace(stack);
    }

}
