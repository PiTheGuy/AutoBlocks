package pitheguy.autoblocks.blockentity.placer;

import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import pitheguy.autoblocks.util.SchematicHandler;

import java.io.*;
import java.util.*;

public class PlacerBuildingPlan {
    public static final int MAX_SIZE = 20;
    private BlockState[][][] blocks;
    private Vec3i size;

    private PlacerBuildingPlan (BlockState[][][] blocks) {
        this.blocks = blocks;
    }

    public BlockState getBlock(int x, int y, int z) {
        BlockState result;
        try {
            result = blocks[x][y][z];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return result;
    }

    public Map<Item, Integer> getMaterials() {
        Map<Item, Integer> result = new HashMap<>();
        for (BlockState[][] plane : blocks) {
            for (BlockState[] line : plane) {
                for (BlockState state : line) {
                    if (state == null) continue;
                    Item item = state.getBlock().asItem();
                    result.put(item, result.getOrDefault(item, 0) + 1);
                }
            }
        }
        return result;
    }

    public Vec3i getSize() {
        return size;
    }

    public static PlacerBuildingPlan fromSchematicName(String schematicName) {
        CompoundTag structureTag;
        try {
            structureTag = loadSchematic(schematicName);
        } catch (FileNotFoundException e) {
            throw new PlacerLoadException("Unknown schematic: " + schematicName);
        } catch (IOException e) {
            throw new PlacerLoadException("Failed to load schematic " + schematicName);
        }
        Vec3i size = loadSize(schematicName, structureTag);
        List<BlockState> palette = loadPalette(structureTag);
        ListTag blocksTag = structureTag.getList("blocks", Tag.TAG_COMPOUND);
        BlockState[][][] blocks = new BlockState[size.getX()][size.getY()][size.getZ()];
        for (Tag tag : blocksTag) {
            CompoundTag blockTag = (CompoundTag) tag;
            ListTag posTag = blockTag.getList("pos", Tag.TAG_INT);
            int stateIndex = blockTag.getInt("state");
            blocks[posTag.getInt(0)][posTag.getInt(1)][posTag.getInt(2)] = palette.get(stateIndex);
        }
        PlacerBuildingPlan placerBuildingPlan = new PlacerBuildingPlan(blocks);
        placerBuildingPlan.size = size;
        return placerBuildingPlan;
    }

    @NotNull
    private static List<BlockState> loadPalette(CompoundTag structureTag) {
        ListTag paletteTag = structureTag.getList("palette", Tag.TAG_COMPOUND);
        List<BlockState> palette = new ArrayList<>();
        for (Tag tag : paletteTag)
            palette.add(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), (CompoundTag) tag));
        return palette;
    }

    @NotNull
    private static Vec3i loadSize(String schematicName, CompoundTag structureTag) {
        ListTag sizeTag = structureTag.getList("size", Tag.TAG_INT);
        for (Tag tag : sizeTag) {
            int size = ((IntTag) tag).getAsInt();
            if (size > MAX_SIZE) throw new PlacerLoadException("Schematic is too large: " + schematicName);
        }
        return new Vec3i(sizeTag.getInt(0), sizeTag.getInt(1), sizeTag.getInt(2));
    }

    private static CompoundTag loadSchematic(String schematicName) throws IOException {
        return SchematicHandler.getSchematic(schematicName);
    }
}
