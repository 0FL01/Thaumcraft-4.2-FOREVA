package thaumcraft.common;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.compat.ThaumcraftSixCompatibility;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.research.ConfigResearch;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.events.EventHandlerRunic;
import thaumcraft.common.lib.events.EventHandlerWorld;
import thaumcraft.common.lib.events.ServerTickEventsFML;
import thaumcraft.common.lib.events.CommandThaumcraft;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.lib.world.ComponentBankerHome;
import thaumcraft.common.lib.world.ComponentWizardTower;
import thaumcraft.common.lib.world.VillageBankerManager;
import thaumcraft.common.lib.world.VillageWizardManager;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.items.BehaviorDispenseAlumetum;
import thaumcraft.common.items.wands.WandRodPrimalOnUpdate;

import java.util.Arrays;

@Mod(
    modid = Thaumcraft.MODID,
    name = Thaumcraft.NAME,
    version = Thaumcraft.VERSION,
    dependencies = "required-after:forge@[14.23.5.2847,);required-after:baubles@[1.5.2,)",
    guiFactory = "thaumcraft.client.gui.GuiFactory"
)
public class Thaumcraft {
    public static final CreativeTabs tabTC = CreativeTabThaumcraft.tabThaumcraft;

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
    public final EventHandlerRunic runicEventHandler = new EventHandlerRunic();

    // ---- Mod lifecycle ----

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log.info("Thaumcraft {} initializing", VERSION);

        Config.init(event.getSuggestedConfigurationFile());

        // Register capabilities
        PlayerKnowledgeProvider.register();

        // Initialise aspects (must happen early)
        initAspects();
        ThaumcraftApi.registerScanEventhandler(new ScanManager());

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
        MinecraftForge.EVENT_BUS.register(runicEventHandler);
        MinecraftForge.EVENT_BUS.register(new ServerTickEventsFML());
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventHandlerWorld());

        // Init network
        PacketHandler.init();

        // Init world generator
        worldGen = new ThaumcraftWorldGenerator();
        GameRegistry.registerWorldGenerator(worldGen, 0);

        // Init biomes (creates biome instances, must happen before registry event)
        ThaumcraftWorldGenerator.initBiomes();

        // Init config sub-modules
        ConfigBlocks.init();
        ConfigItems.init();
        initWandComponents();
        ConfigEntities.init();
        ThaumcraftWorldGenerator.registerBiomeManager();

        // Register tile entities
        ConfigBlocks.registerTileEntities();

        // Register dimension
        registerOuterLandsDimension();

        // Register entity renderers (must be called in preInit for Forge 1.12.2
        // RenderingRegistry.registerEntityRenderingHandler(Class, IRenderFactory))
        proxy.registerEntityRenders();
    }

    private void registerOuterLandsDimension() {
        if (DimensionManager.isDimensionRegistered(Config.dimensionOuterId)) {
            throw new IllegalStateException("Thaumcraft Outer Lands dimension id already registered: " + Config.dimensionOuterId);
        }
        for (DimensionType type : DimensionType.values()) {
            if (type.getId() == Config.dimensionOuterId) {
                throw new IllegalStateException("Thaumcraft Outer Lands dimension type id already registered: " + Config.dimensionOuterId);
            }
        }
        DimensionType outerLands = DimensionType.register(
                "OUTER_LANDS", "_outerlands", Config.dimensionOuterId,
                thaumcraft.common.lib.world.dim.WorldProviderOuter.class, false);
        DimensionManager.registerDimension(Config.dimensionOuterId, outerLands);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerDisplayInformation();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        // Register key bindings
        proxy.registerKeyBindings();
        proxy.registerHandlers();

        Config.registerBiomes();
        Config.initLoot();
        Config.initMisc();
        if (ConfigItems.itemResource != null) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ConfigItems.itemResource, new BehaviorDispenseAlumetum());
        }

        // Register village components with MapGenStructureIO
        MapGenStructureIO.registerStructureComponent(ComponentWizardTower.class, "TCWizTower");
        MapGenStructureIO.registerStructureComponent(ComponentBankerHome.class, "TCBankerHome");

        // Register village creation handlers
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageWizardManager());
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageBankerManager());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Config.initModCompatibility();
        initOptionalWandComponents();
        ConfigRecipes.init();
        ConfigAspects.init();
        ThaumcraftSixCompatibility.postAspectRegistryEvent();
        ConfigResearch.init();
        ConfigEntities.initEntitySpawns();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandThaumcraft());
    }

    @Mod.EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        ConfigEntities.processIMC(event.getMessages());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (MODID.equals(event.getModID()) && Config.config != null) {
            Config.syncConfigurable();
            if (Config.config.hasChanged()) {
                Config.save();
            }
        }
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
        event.getRegistry().register(new BlockJarItem(ConfigBlocks.blockJar).setRegistryName(ConfigBlocks.blockJar.getRegistryName()));
        ConfigBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        log.info("Registering recipes");
        ConfigRecipes.registerSpecialRecipes(event.getRegistry());
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
        event.getRegistry().registerAll(
                ThaumcraftWorldGenerator.biomeMagicalForest,
                ThaumcraftWorldGenerator.biomeTaint,
                ThaumcraftWorldGenerator.biomeEerie,
                ThaumcraftWorldGenerator.biomeEldritchLands
        );
    }

    @SubscribeEvent
    public void registerVillagerProfessions(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event) {
        log.info("Registering villager professions");
        event.getRegistry().registerAll(ConfigEntities.PROFESSIONS.toArray(new VillagerRegistry.VillagerProfession[0]));
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
            thaumcraft.common.lib.research.ResearchManager.syncWarp(player);
        }
    }

    public static void addStickyWarpToPlayer(net.minecraft.entity.player.EntityPlayer player, int amount) {
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.addWarpSticky(amount);
            thaumcraft.common.lib.research.ResearchManager.syncWarp(player);
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
        new WandCap("iron", 1.1f, new ItemStack(ConfigItems.itemWandCap, 1, 0), 1);
        new WandCap("gold", 1.0f, new ItemStack(ConfigItems.itemWandCap, 1, 1), 3);
        new WandCap("thaumium", 0.9f, new ItemStack(ConfigItems.itemWandCap, 1, 2), 6);
        new WandCap("void", 0.8f, new ItemStack(ConfigItems.itemWandCap, 1, 7), 9);
        new WandRod("wood", 25, new ItemStack(Items.STICK), 1);
        new WandRod("greatwood", 50, new ItemStack(ConfigItems.itemWandRod, 1, 0), 3);
        new WandRod("obsidian", 75, new ItemStack(ConfigItems.itemWandRod, 1, 1), 6, new WandRodPrimalOnUpdate(Aspect.EARTH));
        new WandRod("blaze", 75, new ItemStack(ConfigItems.itemWandRod, 1, 6), 6, new WandRodPrimalOnUpdate(Aspect.FIRE)).setGlowing(true);
        new WandRod("ice", 75, new ItemStack(ConfigItems.itemWandRod, 1, 3), 6, new WandRodPrimalOnUpdate(Aspect.WATER));
        new WandRod("quartz", 75, new ItemStack(ConfigItems.itemWandRod, 1, 4), 6, new WandRodPrimalOnUpdate(Aspect.ORDER));
        new WandRod("bone", 75, new ItemStack(ConfigItems.itemWandRod, 1, 7), 6, new WandRodPrimalOnUpdate(Aspect.ENTROPY));
        new WandRod("reed", 75, new ItemStack(ConfigItems.itemWandRod, 1, 5), 6, new WandRodPrimalOnUpdate(Aspect.AIR));
        new WandRod("silverwood", 100, new ItemStack(ConfigItems.itemWandRod, 1, 2), 9);

        new StaffRod("greatwood", 125, new ItemStack(ConfigItems.itemWandRod, 1, 50), 8);
        new StaffRod("obsidian", 175, new ItemStack(ConfigItems.itemWandRod, 1, 51), 14, new WandRodPrimalOnUpdate(Aspect.EARTH));
        StaffRod blazeStaff = new StaffRod("blaze", 175, new ItemStack(ConfigItems.itemWandRod, 1, 56), 14, new WandRodPrimalOnUpdate(Aspect.FIRE));
        blazeStaff.setGlowing(true);
        new StaffRod("ice", 175, new ItemStack(ConfigItems.itemWandRod, 1, 53), 14, new WandRodPrimalOnUpdate(Aspect.WATER));
        new StaffRod("quartz", 175, new ItemStack(ConfigItems.itemWandRod, 1, 54), 14, new WandRodPrimalOnUpdate(Aspect.ORDER));
        new StaffRod("bone", 175, new ItemStack(ConfigItems.itemWandRod, 1, 57), 14, new WandRodPrimalOnUpdate(Aspect.ENTROPY));
        new StaffRod("reed", 175, new ItemStack(ConfigItems.itemWandRod, 1, 55), 14, new WandRodPrimalOnUpdate(Aspect.AIR));
        new StaffRod("silverwood", 250, new ItemStack(ConfigItems.itemWandRod, 1, 52), 24);
        StaffRod primal = new StaffRod("primal", 250, new ItemStack(ConfigItems.itemWandRod, 1, 100), 32, new WandRodPrimalOnUpdate());
        primal.setRunes(true);

        log.info("Wand components registered: {} rods, {} caps", WandRod.rods.size(), WandCap.caps.size());
    }

    private void initOptionalWandComponents() {
        if (Config.foundCopperIngot && !WandCap.caps.containsKey("copper")) {
            new WandCap("copper", 1.1f, Arrays.asList(Aspect.ORDER, Aspect.ENTROPY), 1.0f,
                    new ItemStack(ConfigItems.itemWandCap, 1, 3), 2);
        }
        if (Config.foundSilverIngot && !WandCap.caps.containsKey("silver")) {
            new WandCap("silver", 1.0f, Arrays.asList(Aspect.AIR, Aspect.EARTH, Aspect.FIRE, Aspect.WATER), 0.95f,
                    new ItemStack(ConfigItems.itemWandCap, 1, 4), 4);
        }
        log.info("Optional wand components registered: {} rods, {} caps", WandRod.rods.size(), WandCap.caps.size());
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
