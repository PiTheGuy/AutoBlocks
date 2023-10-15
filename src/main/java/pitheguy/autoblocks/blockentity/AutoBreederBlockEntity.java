package pitheguy.autoblocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import pitheguy.autoblocks.init.ModBlockEntityTypes;
import pitheguy.autoblocks.menu.AutoBreederMenu;

import java.util.stream.IntStream;

public class AutoBreederBlockEntity extends EntityBasedAutoBlockEntity {
    public AutoBreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AUTO_BREEDER.get(), pos, state, 57, 3, 1, Animal.class);
        this.mainInventoryStartSlot = 3;
        this.mainInventoryEndSlot = 57;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.autoblocks.auto_breeder");
    }

    @Override
    public ItemStack getUpgrade1() {
        return inventory.getStackInSlot(0);
    }

    @Override
    public ItemStack getUpgrade2() {
        return inventory.getStackInSlot(1);
    }

    @Override
    public ItemStack getUpgrade3() {
        return inventory.getStackInSlot(2);
    }

    @Override
    public boolean runAction() {
        Animal animal = (Animal) getAffectedEntity();
        if (animal == null) return true;
        //TODO don't breed if animal doesn't have a partner
        animal.setInLove(null);
        removeItemFromInventory(animal::isFood);
        return true;
    }

    @Override
    protected <E extends Entity> boolean isValidEntity(E entity) {
        return entity instanceof Animal animal && animal.getAge() == 0 && animal.canFallInLove() && hasItemInInventory(animal::isFood);
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public Status getStatus() {
        if (!hasItemInInventory(stack -> true)) return Status.NOT_ENOUGH_MATERIALS;
        findFuelSource();
        if (fuelSource == null) return Status.NOT_ENOUGH_FUEL;
        if (findNearbyEntities().isEmpty()) return Status.WAITING;
        return Status.RUNNING;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(3, 57).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        return slot >= 3;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoBreederMenu(containerId, playerInventory, this);
    }
}
