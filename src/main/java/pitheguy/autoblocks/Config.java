package pitheguy.autoblocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = AutoBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

   // private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
   //         .comment("Whether to log the dirt block on common setup")
   //         .define("logDirtBlock", true);
//
   // private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
   //         .comment("A magic number")
   //         .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
//
   // public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
   //         .comment("What you want the introduction message to be for the magic number")
   //         .define("magicNumberIntroduction", "The magic number is... ");
//
   // // a list of strings that are treated as resource locations for items
   // private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
   //         .comment("A list of items to log on common setup.")
   //         .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    private static final ForgeConfigSpec.IntValue BASE_COOLDOWN = BUILDER
            .comment("The base cooldown of auto blocks before speed upgrades are applied, in ticks.")
            .defineInRange("baseCooldown", 60, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue MAX_BLOCKS_PER_TICK = BUILDER
            .comment("The maximum number of block that can be scanned each tick. Each speed upgrade will double this value.")
            .defineInRange("maxBlocksPerTick", 10, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue ENERGIZER_SEARCH_RADIUS = BUILDER
            .comment("How far away auto blocks will search for an energizer to fuel them.")
            .defineInRange("energizerSearchRadius", 4, 1, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int baseCooldown;
    public static int maxBlocksPerTick;
    public static int energizerSearchRadius;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        baseCooldown = BASE_COOLDOWN.get();
        maxBlocksPerTick = MAX_BLOCKS_PER_TICK.get();
        energizerSearchRadius = ENERGIZER_SEARCH_RADIUS.get();
    }
}
