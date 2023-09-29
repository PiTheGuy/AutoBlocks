package pitheguy.autoblocks;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import pitheguy.autoblocks.items.FilterItem;
import pitheguy.autoblocks.items.SchematicCreatorItem;

public class AllItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AutoBlocks.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> FILTER = ITEMS.register("filter", FilterItem::new);
    public static final RegistryObject<Item> SCHEMATIC_CREATOR = ITEMS.register("schematic_creator", SchematicCreatorItem::new);
    public static final RegistryObject<Item> SPEED_UPGRADE = ITEMS.register("speed_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENERGY_UPGRADE = ITEMS.register("energy_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RANGE_UPGRADE = ITEMS.register("range_upgrade", () -> new Item(new Item.Properties()));
}
