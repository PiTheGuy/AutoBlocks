package pitheguy.autoblocks.util;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class FunctionalIntDataSlot extends DataSlot {

    private final IntSupplier getter;
    private final IntConsumer setter;

    public FunctionalIntDataSlot(final IntSupplier getter, final IntConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get() {
        return this.getter.getAsInt();
    }

    @Override
    public void set(final int value) {
        this.setter.accept(value);
    }
}
