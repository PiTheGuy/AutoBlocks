package pitheguy.autoblocks.util;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.*;

public class SchematicHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Path SCHEMATICS = FMLPaths.GAMEDIR.get().resolve("schematics");

    /**
     * Save a schematic to a file from a world.
     *
     * @param dir       the directory the schematic will be created in
     * @param fileName  the ideal name of the schematic, may not be the name of the created file
     * @param overwrite whether overwriting an existing schematic is allowed
     * @param level     the level where the schematic structure is placed
     * @param first     the first corner of the schematic area
     * @param second    the second corner of the schematic area
     * @return a SchematicExportResult, or null if an error occurred.
     */
    public static SchematicExportResult saveSchematic(Path dir, String fileName, boolean overwrite, Level level, BlockPos first, BlockPos second) {
        BoundingBox bb = BoundingBox.fromCorners(first, second);
        BlockPos origin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
        BlockPos bounds = new BlockPos(bb.getXSpan(), bb.getYSpan(), bb.getZSpan());

        StructureTemplate structure = new StructureTemplate();
        structure.fillFromWorld(level, origin, bounds, true, Blocks.AIR);
        CompoundTag data = structure.save(new CompoundTag());

        if (fileName.isEmpty())
            return null;
        if (!overwrite)
            fileName = FileHelper.findFirstValidFilename(fileName, dir, "nbt");
        if (!fileName.endsWith(".nbt"))
            fileName += ".nbt";
        Path file = dir.resolve(fileName).toAbsolutePath();

        try {
            Files.createDirectories(dir);
            boolean overwritten = Files.deleteIfExists(file);
            try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
                NbtIo.writeCompressed(data, out);
            }
            return new SchematicExportResult(file, dir, fileName, overwritten, origin, bounds);
        } catch (IOException e) {
            LOGGER.error("An error occurred while saving schematic [" + fileName + "]", e);
            return null;
        }
    }

    public static CompoundTag getSchematic(String name) throws IOException {
        File schematicFile = SCHEMATICS.resolve(name).toFile();
        return NbtIo.readCompressed(schematicFile);
    }

    public record SchematicExportResult(Path file, Path dir, String fileName, boolean overwritten, BlockPos origin,
                                        BlockPos bounds) {
    }
}
