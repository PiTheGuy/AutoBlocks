package pitheguy.autoblocks;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import pitheguy.autoblocks.client.gui.*;
import pitheguy.autoblocks.init.ModBlockEntityTypes;
import pitheguy.autoblocks.init.ModMenuTypes;
import pitheguy.autoblocks.networking.AllPackets;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AutoBlocks.MODID)
public class AutoBlocks {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "autoblocks";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("autoblocks", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> AllBlocks.AUTO_MINER_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(AllBlocks.ENERGIZER_BLOCK_ITEM.get());
                output.accept(AllBlocks.AUTO_MINER_BLOCK_ITEM.get());
                output.accept(AllBlocks.AUTO_LOGGER_BLOCK_ITEM.get());
                output.accept(AllBlocks.AUTO_PLACER_BLOCK_ITEM.get());
                output.accept(AllBlocks.AUTO_FARMER_BLOCK_ITEM.get());
                output.accept(AllBlocks.AUTO_SHEARER_BLOCK_ITEM.get());
                output.accept(AllItems.FILTER.get());
                output.accept(AllItems.SCHEMATIC_CREATOR.get());
                output.accept(AllItems.SPEED_UPGRADE.get());
                output.accept(AllItems.ENERGY_UPGRADE.get());
                output.accept(AllItems.RANGE_UPGRADE.get());
            }).build());

    public AutoBlocks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        AllBlocks.register(modEventBus);
        AllItems.register(modEventBus);
        AllPackets.registerPackets();

        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.ENERGIZER.get(), EnergizerScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_MINER.get(), AutoMinerScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_LOGGER.get(), AutoLoggerScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_PLACER.get(), AutoPlacerScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_FARMER.get(), AutoFarmerScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_SHEARER.get(), AutoShearerScreen::new);
        }
    }
}
