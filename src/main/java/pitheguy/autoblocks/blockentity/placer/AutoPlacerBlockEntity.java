package pitheguy.autoblocks.blockentity.placer;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pitheguy.autoblocks.blockentity.AutoBlockEntity;
import pitheguy.autoblocks.init.ModBlocKEntityTypes;
import pitheguy.autoblocks.menu.AutoPlacerMenu;

public class AutoPlacerBlockEntity extends AutoBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private PlacerBuildingPlan plan;
    private boolean placing;
    private String schematicName = "";

    public AutoPlacerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocKEntityTypes.AUTO_PLACER.get(), pos, state, 38, 20, 0, ActionArea.ABOVE);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.autoblocks.auto_placer");
    }

    @Override
    public ItemStack getUpgrade1() {
        return this.inventory.getStackInSlot(0);
    }

    @Override
    public ItemStack getUpgrade2() {
        return this.inventory.getStackInSlot(1);
    }

    @Override
    public void runAction() {
        if (level != null && !level.isClientSide) {
            BlockPos placePos = this.getBlockPos().offset(offsetX, offsetY, offsetZ);
            BlockState blockToPlace = this.plan.getBlock(offsetX, offsetY, offsetZ);
            level.setBlock(placePos, blockToPlace, Block.UPDATE_ALL);
            level.playLocalSound(placePos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1, false);
            removeItemFromInventory(blockToPlace.getBlock().asItem());
        }
    }

    public String getSchematicName() {
        return schematicName;
    }

    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("SchematicName", schematicName);
        tag.putBoolean("Running", placing);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        String schem = tag.getString("SchematicName");
        if (!schem.isEmpty()) {
            schematicName = schem;
            loadSchematic(schem);
        }
        placing = plan != null && tag.getBoolean("Running");
    }

    protected void advanceToNextPosition() {
        int range = getRange();
        offsetX++;
        if (offsetX > range) {
            offsetX = 0;
            offsetZ++;
            if (offsetZ > range) {
                offsetZ = 0;
                offsetY += actionArea.getDirection();
                if (offsetY > range) offsetY = actionArea.getDirection();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean canRunAtPosition(BlockPos pos) {
        if (plan == null) return false;
        if (level.getBlockState(pos) == plan.getBlock(offsetX, offsetY, offsetZ)) return false;
        return level.getBlockState(pos).isAir(); //TODO add breaking of blocks?
    }

    @Override
    public Status getStatus() {
        if (!isPlacing()) return Status.STOPPED;
        if (!hasEnoughMaterials()) return Status.NOT_ENOUGH_MATERIALS;
        findFuelSource();
        if (fuelSource == null) return Status.NOT_ENOUGH_FUEL;
        return Status.RUNNING;
    }

    public void loadSchematic(String schematicName) {
        plan = PlacerBuildingPlan.fromSchematicName(schematicName);
        placing = false;
    }

    public void start() {
        if (plan == null) {
            LOGGER.warn("Tried to start placer before a schematic was loaded");
            return;
        }
        offsetX = 0;
        offsetY = 1;
        offsetZ = 0;
        placing = true;
    }

    private void removeItemFromInventory(Item item) {
        for (int i = 2; i < 38; i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.is(item)) {
                this.inventory.decrStackSize(i, 1);
                return;
            }
        }
    }

    private boolean hasItemInInventory(Item item) {
        for (int i = 2; i < 38; i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.is(item)) return true;
        }
        return false;
    }

    public boolean hasEnoughMaterials() {
        return plan.getMaterials().keySet().stream().anyMatch(this::hasItemInInventory);
    }

    public boolean isPlacing() {
        //LOGGER.debug("Running is {}", placing);
        return placing;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoPlacerMenu(containerId, playerInventory, this);
    }
}
