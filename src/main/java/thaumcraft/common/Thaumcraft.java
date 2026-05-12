package thaumcraft.common;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.events.EventHandlerRunic;
import thaumcraft.common.lib.events.EventHandlerWorld;
import thaumcraft.common.lib.events.ServerTickEventsFML;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.tiles.*;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

@Mod(
    modid = Thaumcraft.MODID,
    name = Thaumcraft.NAME,
    version = Thaumcraft.VERSION,
    dependencies = "required-after:forge@[14.23.5.2847,);required-after:baubles@[1.12-1.5.2,)",
    guiFactory = "thaumcraft.client.gui.GuiFactory"
)
public class Thaumcraft {
    public static final CreativeTabs tabTC = new CreativeTabs("thaumcraft") {
        public ItemStack createIcon() { return new ItemStack(Items.ENDER_EYE); }
        public ItemStack getTabIconItem() { return createIcon(); }
    };

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

        // Register capabilities
        PlayerKnowledgeProvider.register();

        // Initialise aspects (must happen early)
        initAspects();

        // Register default wand rods and caps
        initWandComponents();

        // Register potion instances (registry names set here; actual registry via event)
        Config.initPotions();

        // Enchantment instances (registry names set here; actual registry via event)
        initEnchantments();

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

        // Register tile entities
        GameRegistry.registerTileEntity(TileJarFillable.class, new net.minecraft.util.ResourceLocation("thaumcraft", "jar_fillable"));
        GameRegistry.registerTileEntity(TileJarBrain.class, new net.minecraft.util.ResourceLocation("thaumcraft", "jar_brain"));
        GameRegistry.registerTileEntity(TileJarNode.class, new net.minecraft.util.ResourceLocation("thaumcraft", "jar_node"));
        GameRegistry.registerTileEntity(TileJarFillableVoid.class, new net.minecraft.util.ResourceLocation("thaumcraft", "jar_fillable_void"));
        GameRegistry.registerTileEntity(TileCrystal.class, new net.minecraft.util.ResourceLocation("thaumcraft", "crystal"));
        GameRegistry.registerTileEntity(TileEldritchCrystal.class, new net.minecraft.util.ResourceLocation("thaumcraft", "eldritch_crystal"));
        GameRegistry.registerTileEntity(TileNode.class, new net.minecraft.util.ResourceLocation("thaumcraft", "node"));
        GameRegistry.registerTileEntity(TileTable.class, new net.minecraft.util.ResourceLocation("thaumcraft", "table"));
        GameRegistry.registerTileEntity(TileMagicWorkbench.class, new net.minecraft.util.ResourceLocation("thaumcraft", "magic_workbench"));
        GameRegistry.registerTileEntity(TileArcaneWorkbench.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_workbench"));
        GameRegistry.registerTileEntity(TileDeconstructionTable.class, new net.minecraft.util.ResourceLocation("thaumcraft", "deconstruction_table"));
        GameRegistry.registerTileEntity(TileResearchTable.class, new net.minecraft.util.ResourceLocation("thaumcraft", "research_table"));
        GameRegistry.registerTileEntity(TilePedestal.class, new net.minecraft.util.ResourceLocation("thaumcraft", "pedestal"));
        GameRegistry.registerTileEntity(TileWandPedestal.class, new net.minecraft.util.ResourceLocation("thaumcraft", "wand_pedestal"));
        GameRegistry.registerTileEntity(TileAlchemyFurnace.class, new net.minecraft.util.ResourceLocation("thaumcraft", "alchemy_furnace"));
        GameRegistry.registerTileEntity(TileInfusionMatrix.class, new net.minecraft.util.ResourceLocation("thaumcraft", "infusion_matrix"));
        GameRegistry.registerTileEntity(TileInfusionPillar.class, new net.minecraft.util.ResourceLocation("thaumcraft", "infusion_pillar"));
        GameRegistry.registerTileEntity(TileNodeStabilizer.class, new net.minecraft.util.ResourceLocation("thaumcraft", "node_stabilizer"));
        GameRegistry.registerTileEntity(TileNodeConverter.class, new net.minecraft.util.ResourceLocation("thaumcraft", "node_converter"));
        GameRegistry.registerTileEntity(TileSpa.class, new net.minecraft.util.ResourceLocation("thaumcraft", "spa"));
        GameRegistry.registerTileEntity(TileFocalManipulator.class, new net.minecraft.util.ResourceLocation("thaumcraft", "focal_manipulator"));
        GameRegistry.registerTileEntity(TileFluxScrubber.class, new net.minecraft.util.ResourceLocation("thaumcraft", "flux_scrubber"));
        GameRegistry.registerTileEntity(TileCrucible.class, new net.minecraft.util.ResourceLocation("thaumcraft", "crucible"));
        GameRegistry.registerTileEntity(TileArcaneBore.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_bore"));
        GameRegistry.registerTileEntity(TileArcaneBoreBase.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_bore_base"));
        GameRegistry.registerTileEntity(TileArcaneFurnace.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_furnace"));
        GameRegistry.registerTileEntity(TileArcaneFurnaceNozzle.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_furnace_nozzle"));
        GameRegistry.registerTileEntity(TileBellows.class, new net.minecraft.util.ResourceLocation("thaumcraft", "bellows"));
        GameRegistry.registerTileEntity(TileCentrifuge.class, new net.minecraft.util.ResourceLocation("thaumcraft", "centrifuge"));
        GameRegistry.registerTileEntity(TileEssentiaReservoir.class, new net.minecraft.util.ResourceLocation("thaumcraft", "essentia_reservoir"));
        GameRegistry.registerTileEntity(TileMirror.class, new net.minecraft.util.ResourceLocation("thaumcraft", "mirror"));
        GameRegistry.registerTileEntity(TileMirrorEssentia.class, new net.minecraft.util.ResourceLocation("thaumcraft", "mirror_essentia"));
        GameRegistry.registerTileEntity(TileVisRelay.class, new net.minecraft.util.ResourceLocation("thaumcraft", "vis_relay"));
        GameRegistry.registerTileEntity(TileMagicWorkbenchCharger.class, new net.minecraft.util.ResourceLocation("thaumcraft", "magic_workbench_charger"));
        GameRegistry.registerTileEntity(TileOwned.class, new net.minecraft.util.ResourceLocation("thaumcraft", "owned"));
        GameRegistry.registerTileEntity(TileArcanePressurePlate.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_pressure_plate"));
        GameRegistry.registerTileEntity(TileBanner.class, new net.minecraft.util.ResourceLocation("thaumcraft", "banner"));
        GameRegistry.registerTileEntity(TileSensor.class, new net.minecraft.util.ResourceLocation("thaumcraft", "sensor"));
        GameRegistry.registerTileEntity(TileLifter.class, new net.minecraft.util.ResourceLocation("thaumcraft", "lifter"));
        GameRegistry.registerTileEntity(TileHole.class, new net.minecraft.util.ResourceLocation("thaumcraft", "hole"));
        GameRegistry.registerTileEntity(TileGrate.class, new net.minecraft.util.ResourceLocation("thaumcraft", "grate"));
        GameRegistry.registerTileEntity(TileAlembic.class, new net.minecraft.util.ResourceLocation("thaumcraft", "alembic"));
        GameRegistry.registerTileEntity(TileArcaneLamp.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_lamp"));
        GameRegistry.registerTileEntity(TileArcaneLampGrowth.class, new net.minecraft.util.ResourceLocation("thaumcraft", "arcane_lamp_growth"));
        GameRegistry.registerTileEntity(TileThaumatorium.class, new net.minecraft.util.ResourceLocation("thaumcraft", "thaumatorium"));
        GameRegistry.registerTileEntity(TileThaumatoriumTop.class, new net.minecraft.util.ResourceLocation("thaumcraft", "thaumatorium_top"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerDisplayInformation();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        // Register key bindings
        proxy.registerKeyBindings();

        Config.registerBiomes();
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
        event.getRegistry().registerAll(ConfigBlocks.getAllBlocks());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        log.info("Registering items");
        event.getRegistry().registerAll(ConfigItems.getAllItems());
        // Register ItemBlocks for blocks (via ConfigBlocks helper + manual jar)
        event.getRegistry().register(new BlockJarItem(ConfigBlocks.blockJar).setRegistryName("thaumcraft", "jar"));
        ConfigBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        log.info("Registering entities");
        event.getRegistry().registerAll(ConfigEntities.ENTITIES.toArray(new EntityEntry[0]));
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        log.info("Registering potions");
        event.getRegistry().registerAll(
                Config.potionFluxTaint,
                Config.potionVisExhaust,
                Config.potionInfectiousVisExhaust,
                Config.potionUnnaturalHunger,
                Config.potionWarpWard,
                Config.potionDeathGaze,
                Config.potionBlurredVision,
                Config.potionSunScorned,
                Config.potionThaumarhia
        );
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        log.info("Registering enchantments");
        event.getRegistry().registerAll(
                Config.enchHaste,
                Config.enchRepair,
                Config.enchFrugal,
                Config.enchPotency,
                Config.enchWandFortune
        );
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

    // ---- Warp utilities ----

    public static void addWarpToPlayer(net.minecraft.entity.player.EntityPlayer player, int amount, boolean temporary) {
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            if (temporary) {
                knowledge.addWarpTemp(amount);
            } else {
                knowledge.addWarpPerm(amount);
            }
        }
    }

    public static void addStickyWarpToPlayer(net.minecraft.entity.player.EntityPlayer player, int amount) {
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.addWarpSticky(amount);
        }
    }

    // ---- Aspect Initialisation ----

    private void initAspects() {
        // Primal aspects are already created as static fields in Aspect class.
        // Ensure the aspect order list matches the original game.
        // The 6 primal aspects are automatically registered as static fields.
        // Compound aspects are created via the Aspect constructor chaining.
        log.info("Aspects initialised: {} total", Aspect.aspects.size());
    }

    // ---- Wand Component Registration ----

    private void initWandComponents() {
        // Wand Rods
        // Note: these use the item field from ConfigItems - but ConfigItems.init() hasn't been called yet.
        // We create ItemStack references that will be filled in later.
        // For now, register rods with null items (the game will still work).
        new WandRod("wood", 100, ItemStack.EMPTY, 5);
        new WandRod("greatwood", 500, ItemStack.EMPTY, 25);
        new WandRod("silverwood", 1000, ItemStack.EMPTY, 75);

        // Wand Caps
        new WandCap("iron", 1.0f, ItemStack.EMPTY, 5);
        new WandCap("gold", 1.2f, ItemStack.EMPTY, 7);
        new WandCap("thaumium", 0.9f, ItemStack.EMPTY, 15);
        new WandCap("void", 0.8f, ItemStack.EMPTY, 25);
        new WandCap("bone", 0.85f, ItemStack.EMPTY, 10);
        new WandCap("alchemical", 0.75f, ItemStack.EMPTY, 30);

        log.info("Wand components registered: {} rods, {} caps", WandRod.rods.size(), WandCap.caps.size());
    }

    // ---- Enchantment Initialisation ----

    private void initEnchantments() {
        Config.enchFrugal = new thaumcraft.common.lib.enchantment.EnchantmentFrugal();
        Config.enchPotency = new thaumcraft.common.lib.enchantment.EnchantmentPotency();
        Config.enchHaste = new thaumcraft.common.lib.enchantment.EnchantmentHaste();
        Config.enchWandFortune = new thaumcraft.common.lib.enchantment.EnchantmentWandFortune();
        Config.enchRepair = new thaumcraft.common.lib.enchantment.EnchantmentRepair();
    }
}
