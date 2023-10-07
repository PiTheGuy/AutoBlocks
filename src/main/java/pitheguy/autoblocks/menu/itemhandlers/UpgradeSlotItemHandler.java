package pitheguy.autoblocks.menu.itemhandlers;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import pitheguy.autoblocks.AllItems;

public class UpgradeSlotItemHandler extends SlotItemHandler {
    public UpgradeSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (!super.mayPlace(stack)) return false;
        return stack.is(AllItems.SPEED_UPGRADE.get()) || stack.is(AllItems.RANGE_UPGRADE.get()) || stack.is(AllItems.ENERGY_UPGRADE.get());
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
