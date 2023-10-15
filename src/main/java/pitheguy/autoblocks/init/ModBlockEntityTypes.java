package pitheguy.autoblocks.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.*;
import pitheguy.autoblocks.AllBlocks;
import pitheguy.autoblocks.AutoBlocks;
import pitheguy.autoblocks.blockentity.*;
import pitheguy.autoblocks.blockentity.placer.AutoPlacerBlockEntity;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AutoBlocks.MODID);

    public static final RegistryObject<BlockEntityType<EnergizerBlockEntity>> ENERGIZER = BLOCK_ENTITY_TYPES.register("energizer", () -> BlockEntityType.Builder.of(EnergizerBlockEntity::new, AllBlocks.ENERGIZER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoMinerBlockEntity>> AUTO_MINER = BLOCK_ENTITY_TYPES.register("auto_miner", () -> BlockEntityType.Builder.of(AutoMinerBlockEntity::new, AllBlocks.AUTO_MINER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoLoggerBlockEntity>> AUTO_LOGGER = BLOCK_ENTITY_TYPES.register("auto_logger", () -> BlockEntityType.Builder.of(AutoLoggerBlockEntity::new, AllBlocks.AUTO_LOGGER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoPlacerBlockEntity>> AUTO_PLACER = BLOCK_ENTITY_TYPES.register("auto_placer", () -> BlockEntityType.Builder.of(AutoPlacerBlockEntity::new, AllBlocks.AUTO_PLACER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoFarmerBlockEntity>> AUTO_FARMER = BLOCK_ENTITY_TYPES.register("auto_farmer", () -> BlockEntityType.Builder.of(AutoFarmerBlockEntity::new, AllBlocks.AUTO_FARMER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoShearerBlockEntity>> AUTO_SHEARER = BLOCK_ENTITY_TYPES.register("auto_shearer", () -> BlockEntityType.Builder.of(AutoShearerBlockEntity::new, AllBlocks.AUTO_SHEARER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoBreederBlockEntity>> AUTO_BREEDER = BLOCK_ENTITY_TYPES.register("auto_breeder", () -> BlockEntityType.Builder.of(AutoBreederBlockEntity::new, AllBlocks.AUTO_BREEDER.get()).build(null));
}
