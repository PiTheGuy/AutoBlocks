package pitheguy.autoblocks.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.*;
import pitheguy.autoblocks.AutoBlocks;
import pitheguy.autoblocks.menu.*;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AutoBlocks.MODID);

    public static final RegistryObject<MenuType<EnergizerMenu>> ENERGIZER = MENU_TYPES.register("energizer", () -> IForgeMenuType.create(EnergizerMenu::new));
    public static final RegistryObject<MenuType<AutoMinerMenu>> AUTO_MINER = MENU_TYPES.register("auto_miner", () -> IForgeMenuType.create(AutoMinerMenu::new));
    public static final RegistryObject<MenuType<AutoLoggerMenu>> AUTO_LOGGER = MENU_TYPES.register("auto_logger", () -> IForgeMenuType.create(AutoLoggerMenu::new));
    public static final RegistryObject<MenuType<AutoPlacerMenu>> AUTO_PLACER = MENU_TYPES.register("auto_placer", () -> IForgeMenuType.create(AutoPlacerMenu::new));
    public static final RegistryObject<MenuType<AutoFarmerMenu>> AUTO_FARMER = MENU_TYPES.register("auto_farmer", () -> IForgeMenuType.create(AutoFarmerMenu::new));
    public static final RegistryObject<MenuType<AutoShearerMenu>> AUTO_SHEARER = MENU_TYPES.register("auto_shearer", () -> IForgeMenuType.create(AutoShearerMenu::new));
}
