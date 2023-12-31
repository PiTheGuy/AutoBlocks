package pitheguy.autoblocks.blockentity.placer;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pitheguy.autoblocks.blockentity.BlockBasedAutoBlockEntity;
import pitheguy.autoblocks.init.ModBlockEntityTypes;
import pitheguy.autoblocks.menu.AutoPlacerMenu;

import java.util.Map;
import java.util.stream.IntStream;

public class AutoPlacerBlockEntity extends BlockBasedAutoBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private PlacerBuildingPlan plan;
    private boolean running;
    private String schematicName = "";

    public AutoPlacerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AUTO_PLACER.get(), pos, state, 39, 20, 0, ActionArea.ABOVE);
        this.mainInventoryStartSlot = 2;
        this.mainInventoryEndSlot = 38;

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
    public ItemStack getUpgrade3() {
        return this.inventory.getStackInSlot(2);
    }

    @Override
    public boolean runAction() {
        if (level != null && !level.isClientSide) {
            BlockPos placePos = getRunningPosition();
            BlockState blockToPlace = this.plan.getBlock(offsetX, offsetY - 1, offsetZ);
            level.setBlock(placePos, blockToPlace, Block.UPDATE_ALL);
            level.playSound(null, placePos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1);
            removeItemFromInventory(blockToPlace.getBlock().asItem());
        }
        return true;
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
        tag.putBoolean("Running", running);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        String schem = tag.getString("SchematicName");
        if (!schem.isEmpty()) {
            schematicName = schem;
            loadSchematic(schem);
        }
        running = plan != null && tag.getBoolean("Running");
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean canRunAtPosition(BlockPos pos) {
        if (plan == null) return false;
        if (plan.getBlock(offsetX, offsetY - 1, offsetZ) == null) return false;
        if (level.getBlockState(pos) == plan.getBlock(offsetX, offsetY - 1, offsetZ)) return false;
        if (!hasItemInInventory(plan.getBlock(offsetX, offsetY - 1, offsetZ).getBlock().asItem())) return false;
        return level.getBlockState(pos).isAir(); //TODO add breaking of blocks?
    }

    @Override
    public Status getStatus() {
        if (!isRunning()) return Status.STOPPED;
        if (!hasEnoughMaterials()) return Status.NOT_ENOUGH_MATERIALS;
        findFuelSource();
        if (fuelSource == null) return Status.NOT_ENOUGH_FUEL;
        if (getProgress() == 1.0) return Status.FINISHED;
        return Status.RUNNING;
    }

    public void loadSchematic(String schematicName) {
        if (!schematicName.endsWith(".nbt")) schematicName += ".nbt";
        plan = PlacerBuildingPlan.fromSchematicName(schematicName);
        this.schematicName = schematicName;
        running = false;
    }

    public Map<Item, Integer> getRemainingMaterials() {
        if (plan == null) return null;
        Map<Item, Integer> materials = plan.getMaterials();
        for (int x = 0; x < plan.getSize().getX(); x++) {
            for (int y = 0; y < plan.getSize().getY(); y++) {
                for (int z = 0; z < plan.getSize().getZ(); z++) {
                    BlockState block = plan.getBlock(x, y, z);
                    if (blockStatesAreEqual(level.getBlockState(this.getBlockPos().offset(x, y + 1, z)), block)) {
                        if (block == null) continue;
                        Item item = block.getBlock().asItem();
                        if (materials.get(item) == 1) materials.remove(item);
                        else materials.put(item, materials.get(item) - 1);
                    }
                }
            }
        }
        return materials;
    }

    public double getProgress() {
        if (plan == null) return 0.0;
        int blocksCorrect = 0;
        int totalBlocks = 0;
        int sizeX = plan.getSize().getX();
        int sizeY = plan.getSize().getY();
        int sizeZ = plan.getSize().getZ();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {

                    BlockState currentBlock = level.getBlockState(this.getBlockPos().offset(x, y + 1, z));
                    BlockState correctBlock = plan.getBlock(x, y, z);
                    if (currentBlock.isAir() && correctBlock == null) continue;
                    if (blockStatesAreEqual(currentBlock, correctBlock)) blocksCorrect++;
                    totalBlocks++;
                }
            }
        }
        return (double) blocksCorrect / totalBlocks;
    }

    private static boolean blockStatesAreEqual(BlockState first, BlockState second) {
        if (second == null) return false;
        if (first.equals(second)) return true;
        return first.isAir() && second.isAir();
    }

    public void start() {
        if (plan == null) {
            LOGGER.warn("Tried to start placer before a schematic was loaded");
            return;
        }
        offsetX = 0;
        offsetY = 1;
        offsetZ = 0;
        running = true;
    }

    public boolean hasEnoughMaterials() {
        return getRemainingMaterials().keySet().stream().anyMatch(this::hasItemInInventory);
    }

    public boolean isRunning() {
        //LOGGER.debug("Running is {}", placing);
        return running;
    }

    public boolean hasSchematic() {
        return plan != null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoPlacerMenu(containerId, playerInventory, this);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(3, 39).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return index >= 3;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }
}
