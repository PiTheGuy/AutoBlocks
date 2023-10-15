package pitheguy.autoblocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import pitheguy.autoblocks.blocks.*;

public class AllBlocks {
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AutoBlocks.MODID);


    public static final RegistryObject<Block> ENERGIZER = BLOCKS.register("energizer", EnergizerBlock::new);
    public static final RegistryObject<Block> AUTO_MINER = BLOCKS.register("auto_miner", AutoMinerBlock::new);
    public static final RegistryObject<Block> AUTO_LOGGER = BLOCKS.register("auto_logger", AutoLoggerBlock::new);
    public static final RegistryObject<Block> AUTO_PLACER = BLOCKS.register("auto_placer", AutoPlacerBlock::new);
    public static final RegistryObject<Block> AUTO_FARMER = BLOCKS.register("auto_farmer", AutoFarmerBlock::new);
    public static final RegistryObject<Block> AUTO_SHEARER = BLOCKS.register("auto_shearer", AutoShearerBlock::new);
    public static final RegistryObject<Block> AUTO_BREEDER = BLOCKS.register("auto_breeder", AutoBreederBlock::new);

    //Block items
    public static final RegistryObject<Item> ENERGIZER_BLOCK_ITEM = AllItems.ITEMS.register("energizer", () -> new BlockItem(ENERGIZER.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTO_MINER_BLOCK_ITEM = AllItems.ITEMS.register("auto_miner", () -> new BlockItem(AUTO_MINER.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTO_LOGGER_BLOCK_ITEM = AllItems.ITEMS.register("auto_logger", () -> new BlockItem(AUTO_LOGGER.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTO_PLACER_BLOCK_ITEM = AllItems.ITEMS.register("auto_placer", () -> new BlockItem(AUTO_PLACER.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTO_FARMER_BLOCK_ITEM = AllItems.ITEMS.register("auto_farmer", () -> new BlockItem(AUTO_FARMER.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTO_SHEARER_BLOCK_ITEM = AllItems.ITEMS.register("auto_shearer", () -> new BlockItem(AUTO_SHEARER.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTO_BREEDER_BLOCK_ITEM = AllItems.ITEMS.register("auto_breeder", () -> new BlockItem(AUTO_BREEDER.get(), new Item.Properties()));
}
