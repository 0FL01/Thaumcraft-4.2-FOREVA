package thaumcraft.common;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.events.EventHandlerRunic;
import thaumcraft.common.lib.events.EventHandlerWorld;
import thaumcraft.common.lib.events.ServerTickEventsFML;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

@Mod(
    modid = Thaumcraft.MODID,
    name = Thaumcraft.NAME,
    version = Thaumcraft.VERSION,
    dependencies = "required-after:forge@[14.23.5.2847,);required-after:baubles@[1.12-1.5.2,)",
    guiFactory = "thaumcraft.client.gui.GuiFactory"
)
public class Thaumcraft {

    public static final String MODID = "thaumcraft";
    public static final String NAME = "Thaumcraft";
    public static final String VERSION = "@VERSION@";

    public static final Logger log = LogManager.getLogger("THAUMCRAFT");

    @Mod.Instance(MODID)
    public static Thaumcraft instance;

    @SidedProxy(
        clientSide = "thaumcraft.client.ClientProxy",
        serverSide = "thaumcraft.common.CommonProxy"
    )
    public static CommonProxy proxy;

    public ThaumcraftWorldGenerator worldGen;

    // ---- Mod lifecycle ----

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log.info("Thaumcraft {} initializing", VERSION);

        Config.init(event.getSuggestedConfigurationFile());

        // Set up internal method handler bridge
        ThaumcraftApi.internalMethods = new InternalMethodHandler();

        // Register event buses
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventHandlerWorld());
        MinecraftForge.EVENT_BUS.register(new EventHandlerEntity());
        MinecraftForge.EVENT_BUS.register(new EventHandlerRunic());
        MinecraftForge.EVENT_BUS.register(new ServerTickEventsFML());
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventHandlerWorld());

        // Init network
        PacketHandler.init();

        // Init world generator
        worldGen = new ThaumcraftWorldGenerator();
        GameRegistry.registerWorldGenerator(worldGen, 0);

        // Init config sub-modules
        ConfigBlocks.init();
        ConfigItems.init();
        ConfigEntities.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerDisplayInformation();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        // Register key bindings
        proxy.registerKeyBindings();

        Config.registerBiomes();
        Config.initPotions();
        Config.initLoot();
        Config.initMisc();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ConfigRecipes.init();
        ConfigAspects.init();
        ConfigResearch.init();
        Config.initModCompatibility();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        // Will register server commands in Phase 4
    }

    // ---- Registry events (1.12.2 pattern) ----

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        log.info("Registering blocks");
        // Phase 4: register actual blocks
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        log.info("Registering items");
        // Phase 5: register actual items
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        log.info("Registering entities");
        // Phase 6: register actual entities
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        log.info("Registering potions");
        // Phase 6: register actual potions
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        log.info("Registering enchantments");
        // Phase 6: register actual enchantments
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        log.info("Registering biomes");
        // Phase 6: register actual biomes
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        log.info("Registering sound events");
        // Phase 7: register actual sounds
    }

    // ---- Warp utilities (called by API / InternalMethodHandler) ----

    public static void addWarpToPlayer(net.minecraft.entity.player.EntityPlayer player, int amount, boolean temporary) {
        // Phase 3: implement via capabilities
    }

    public static void addStickyWarpToPlayer(net.minecraft.entity.player.EntityPlayer player, int amount) {
        // Phase 3: implement via capabilities
    }
}
