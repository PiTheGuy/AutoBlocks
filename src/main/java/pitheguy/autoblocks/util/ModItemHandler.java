package pitheguy.autoblocks.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;

public class ModItemHandler extends ItemStackHandler {
    public ModItemHandler (int size, ItemStack... stacks) {
        super(size);
        for (int i = 0; i < stacks.length; i++) {
            this.stacks.set(i, stacks[i]);
        }
    }

    public void clear() {
        for (int i = 0; i < this.getSlots(); i++) {
            this.stacks.set(i, ItemStack.EMPTY);
            this.onContentsChanged(i);
        }
    }

    public boolean isEmpty() {
        return this.stacks.stream().anyMatch(stack -> stack.isEmpty() || stack.getItem() == Items.AIR);
    }

    public void decrStackSize(int index, int count) {
        ItemStack stack = getStackInSlot(index);
        stack.shrink(count);
        this.onContentsChanged(index);
    }

    public void removeStackFromSlot(int index) {
        this.stacks.set(index, ItemStack.EMPTY);
        this.onContentsChanged(index);
    }

    public NonNullList<ItemStack> toNonNullList() {
        NonNullList<ItemStack> items = NonNullList.create();
        items.addAll(this.stacks);
        return items;
    }

    public void setNonNullList(NonNullList<ItemStack> items) {
        if (items.isEmpty())
            return;
        if (items.size() != this.getSlots())
            throw new IndexOutOfBoundsException("NonNullList must be same size as ItemStackHandler!");
        for (int index = 0; index < items.size(); index++) {
            this.stacks.set(index, items.get(index));
        }
    }

    @Override
    public String toString() {
        return this.stacks.toString();
    }
}
