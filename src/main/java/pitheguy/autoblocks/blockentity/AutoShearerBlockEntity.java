package pitheguy.autoblocks.blockentity;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import pitheguy.autoblocks.init.ModBlockEntityTypes;
import pitheguy.autoblocks.menu.AutoShearerMenu;

import java.util.List;
import java.util.Map;

public class AutoShearerBlockEntity extends EntityBasedAutoBlockEntity {
    private static final Map<DyeColor, ItemLike> ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (p_29841_) -> {
        p_29841_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        p_29841_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        p_29841_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        p_29841_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        p_29841_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        p_29841_.put(DyeColor.LIME, Blocks.LIME_WOOL);
        p_29841_.put(DyeColor.PINK, Blocks.PINK_WOOL);
        p_29841_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        p_29841_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        p_29841_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        p_29841_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        p_29841_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        p_29841_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        p_29841_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        p_29841_.put(DyeColor.RED, Blocks.RED_WOOL);
        p_29841_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });
    public AutoShearerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AUTO_SHEARER.get(), pos, state, 58, 3, 1, Sheep.class);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.autoblocks.auto_shearer");
    }

    @Override
    public ItemStack getUpgrade1() {
        return this.inventory.getStackInSlot(1);
    }

    @Override
    public ItemStack getUpgrade2() {
        return this.inventory.getStackInSlot(2);
    }

    @Override
    public ItemStack getUpgrade3() {
        return this.inventory.getStackInSlot(3);
    }

    @Override
    public boolean runAction() {
        Sheep sheep = (Sheep) getAffectedEntity();
        if (sheep == null) return true;
        sheep.level().playSound(null, sheep, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        sheep.setSheared(true);
        int woolDropped = 1 + level.random.nextInt(3);
        this.addItemToInventory(new ItemStack(ITEM_BY_DYE.get(sheep.getColor()), woolDropped));
        ItemStack shearsStack = inventory.getStackInSlot(0);
        if (shearsStack.hurt(1, level.random, null)) shearsStack.shrink(1);
        return true;
    }

    @Override
    protected <E extends Entity> boolean isValidEntity(E entity) {
        return entity instanceof Sheep sheep && sheep.readyForShearing();
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public Status getStatus() {
        if (!hasInventorySpace()) return Status.INVENTORY_FULL;
        if (!(inventory.getStackInSlot(0).getItem() instanceof ShearsItem)) return Status.MISSING_TOOL;
        findFuelSource();
        if (fuelSource == null) return Status.NOT_ENOUGH_FUEL;
        if (findNearbyEntities().isEmpty()) return Status.WAITING;
        return Status.RUNNING;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoShearerMenu(containerId, playerInventory, this);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        int[] result = new int[55];
        result[0] = 0;
        for (int i = 1; i < 55; i++) result[i] = i + 3;
        return result;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return index == 0 && stack.getItem() instanceof ShearsItem;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index >= 4;
    }
}
