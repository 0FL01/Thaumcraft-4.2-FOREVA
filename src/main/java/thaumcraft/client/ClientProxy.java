package thaumcraft.client;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.gui.GuiArcaneBore;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.client.gui.GuiAlchemyFurnace;
import thaumcraft.client.gui.GuiDeconstructionTable;
import thaumcraft.client.gui.GuiFocalManipulator;
import thaumcraft.client.gui.GuiFocusPouch;
import thaumcraft.client.gui.GuiGolem;
import thaumcraft.client.gui.GuiHandMirror;
import thaumcraft.client.gui.GuiHoverHarness;
import thaumcraft.client.gui.GuiMagicBox;
import thaumcraft.client.gui.GuiPech;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.client.gui.GuiSpa;
import thaumcraft.client.gui.GuiThaumatorium;
import thaumcraft.client.gui.GuiTravelingTrunk;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.beams.FXArc;
import thaumcraft.client.fx.beams.FXBeam;
import thaumcraft.client.fx.beams.FXBeamBore;
import thaumcraft.client.fx.beams.FXBeamGolemBoss;
import thaumcraft.client.fx.beams.FXBeamPower;
import thaumcraft.client.fx.beams.FXBeamWand;
import thaumcraft.client.fx.bolt.FXLightningBolt;
import thaumcraft.client.fx.other.FXBlockWard;
import thaumcraft.client.fx.other.FXShieldRunes;
import thaumcraft.client.fx.other.FXSonic;
import thaumcraft.client.fx.particles.FXBoreParticles;
import thaumcraft.client.fx.particles.FXBoreSparkle;
import thaumcraft.client.fx.particles.FXBlockRunes;
import thaumcraft.client.fx.particles.FXBreaking;
import thaumcraft.client.fx.particles.FXBurst;
import thaumcraft.client.fx.particles.FXBubble;
import thaumcraft.client.fx.particles.FXBubbleAlt;
import thaumcraft.client.fx.particles.FXEssentiaTrail;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.client.fx.particles.FXSmokeDrift;
import thaumcraft.client.fx.particles.FXSmokeSpiral;
import thaumcraft.client.fx.particles.FXSpark;
import thaumcraft.client.fx.particles.FXSparkle;
import thaumcraft.client.fx.particles.FXSwarm;
import thaumcraft.client.fx.particles.FXVent;
import thaumcraft.client.fx.particles.FXVisSparkle;
import thaumcraft.client.fx.particles.FXWispArcing;
import thaumcraft.client.fx.particles.FXWisp;
import thaumcraft.client.fx.particles.FXWispEG;
import thaumcraft.client.renderers.entity.RenderFallbackBiped;
import thaumcraft.client.renderers.entity.RenderFireBat;
import thaumcraft.client.renderers.entity.RenderGolemBase;
import thaumcraft.client.renderers.entity.RenderEldritchGuardian;
import thaumcraft.client.renderers.entity.RenderEldritchGolem;
import thaumcraft.client.renderers.entity.RenderEldritchWarden;
import thaumcraft.client.renderers.entity.RenderElectricOrb;
import thaumcraft.client.renderers.entity.RenderExplosiveOrb;
import thaumcraft.client.renderers.entity.RenderEmber;
import thaumcraft.client.renderers.entity.RenderEldritchOrb;
import thaumcraft.client.renderers.entity.RenderPrimalOrb;
import thaumcraft.client.renderers.entity.RenderPrimalArrow;
import thaumcraft.client.renderers.entity.RenderPechBlast;
import thaumcraft.client.renderers.entity.RenderAlumentum;
import thaumcraft.client.renderers.entity.RenderDart;
import thaumcraft.client.renderers.entity.RenderGolemBobber;
import thaumcraft.client.renderers.entity.RenderFallingTaint;
import thaumcraft.client.renderers.entity.RenderFrostShard;
import thaumcraft.client.renderers.entity.RenderFollowingItem;
import thaumcraft.client.renderers.entity.RenderCultistPortal;
import thaumcraft.client.renderers.entity.RenderBrainyZombie;
import thaumcraft.client.renderers.entity.RenderInhabitedZombie;
import thaumcraft.client.renderers.entity.RenderThaumicSlime;
import thaumcraft.client.renderers.entity.RenderEldritchCrab;
import thaumcraft.client.renderers.entity.RenderAspectOrb;
import thaumcraft.client.renderers.entity.RenderNoop;
import thaumcraft.client.renderers.entity.RenderPech;
import thaumcraft.client.renderers.entity.RenderMindSpider;
import thaumcraft.client.renderers.entity.RenderTaintSpore;
import thaumcraft.client.renderers.entity.RenderTaintSporeSwarmer;
import thaumcraft.client.renderers.entity.RenderTaintSpider;
import thaumcraft.client.renderers.entity.RenderTaintSwarm;
import thaumcraft.client.renderers.entity.RenderTaintacle;
import thaumcraft.client.renderers.entity.RenderTaintCreeper;
import thaumcraft.client.renderers.entity.RenderTaintChicken;
import thaumcraft.client.renderers.entity.RenderTaintCow;
import thaumcraft.client.renderers.entity.RenderTaintPig;
import thaumcraft.client.renderers.entity.RenderTaintSheep;
import thaumcraft.client.renderers.entity.RenderTaintVillager;
import thaumcraft.client.renderers.entity.RenderTravelingTrunk;
import thaumcraft.client.renderers.entity.RenderWatcher;
import thaumcraft.client.renderers.entity.RenderWisp;
import thaumcraft.client.renderers.entity.RenderCultist;
import thaumcraft.client.renderers.entity.RenderSpecialItem;
import thaumcraft.client.renderers.item.ItemEldritchRenderer;
import thaumcraft.client.renderers.item.ItemJarRenderer;
import thaumcraft.client.renderers.item.ItemTrunkSpawnerRenderer;
import thaumcraft.client.renderers.item.ItemMetalDeviceRenderer;
import thaumcraft.client.renderers.item.ItemNodeRenderer;
import thaumcraft.client.renderers.item.ItemCrystalRenderer;
import thaumcraft.client.renderers.item.ItemStoneDeviceRenderer;
import thaumcraft.client.renderers.item.ItemTubeRenderer;
import thaumcraft.client.renderers.item.ItemWandRenderer;
import thaumcraft.client.renderers.item.ItemWoodenDeviceRenderer;
import thaumcraft.client.renderers.tile.TileAlembicRenderer;
import thaumcraft.client.renderers.tile.TileAlchemyFurnaceAdvancedRenderer;
import thaumcraft.client.renderers.tile.TileArcaneLampRenderer;
import thaumcraft.client.renderers.tile.TileArcaneBoreBaseRenderer;
import thaumcraft.client.renderers.tile.TileArcaneBoreRenderer;
import thaumcraft.client.renderers.tile.TileArcaneWorkbenchRenderer;
import thaumcraft.client.renderers.tile.TileBannerRenderer;
import thaumcraft.client.renderers.tile.TileBellowsRenderer;
import thaumcraft.client.renderers.tile.TileBrainboxRenderer;
import thaumcraft.client.renderers.tile.TileCentrifugeRenderer;
import thaumcraft.client.renderers.tile.TileChestHungryRenderer;
import thaumcraft.client.renderers.tile.TileCrucibleRenderer;
import thaumcraft.client.renderers.tile.TileCrystalRenderer;
import thaumcraft.client.renderers.tile.TileDeconstructionTableRenderer;
import thaumcraft.client.renderers.tile.TileEldritchCapRenderer;
import thaumcraft.client.renderers.tile.TileEldritchCrabSpawnerRenderer;
import thaumcraft.client.renderers.tile.TileEldritchCrystalRenderer;
import thaumcraft.client.renderers.tile.TileEldritchLockRenderer;
import thaumcraft.client.renderers.tile.TileEldritchNothingRenderer;
import thaumcraft.client.renderers.tile.TileEldritchObeliskRenderer;
import thaumcraft.client.renderers.tile.TileEldritchPortalRenderer;
import thaumcraft.client.renderers.tile.TileEssentiaCrystalizerRenderer;
import thaumcraft.client.renderers.tile.TileEssentiaReservoirRenderer;
import thaumcraft.client.renderers.tile.TileEtherealBloomRenderer;
import thaumcraft.client.renderers.tile.TileFocalManipulatorRenderer;
import thaumcraft.client.renderers.tile.TileFluxScrubberRenderer;
import thaumcraft.client.renderers.tile.TileHoleRenderer;
import thaumcraft.client.renderers.tile.TileInfusionPillarRenderer;
import thaumcraft.client.renderers.tile.TileJarRenderer;
import thaumcraft.client.renderers.tile.TileLifterRenderer;
import thaumcraft.client.renderers.tile.TileMagicWorkbenchChargerRenderer;
import thaumcraft.client.renderers.tile.TileManaPodRenderer;
import thaumcraft.client.renderers.tile.TileMirrorRenderer;
import thaumcraft.client.renderers.tile.TileNodeConverterRenderer;
import thaumcraft.client.renderers.tile.TileNodeEnergizedRenderer;
import thaumcraft.client.renderers.tile.TileNodeRenderer;
import thaumcraft.client.renderers.tile.TileNodeStabilizerRenderer;
import thaumcraft.client.renderers.tile.TilePedestalRenderer;
import thaumcraft.client.renderers.tile.TileResearchTableRenderer;
import thaumcraft.client.renderers.tile.TileRunicMatrixRenderer;
import thaumcraft.client.renderers.tile.TileSensorRenderer;
import thaumcraft.client.renderers.tile.TileTableRenderer;
import thaumcraft.client.renderers.tile.TileThaumatoriumRenderer;
import thaumcraft.client.renderers.tile.TileTubeBufferRenderer;
import thaumcraft.client.renderers.tile.TileTubeFilterRenderer;
import thaumcraft.client.renderers.tile.TileTubeOnewayRenderer;
import thaumcraft.client.renderers.tile.TileTubeRenderer;
import thaumcraft.client.renderers.tile.TileTubeRestrictRenderer;
import thaumcraft.client.renderers.tile.TileTubeValveRenderer;
import thaumcraft.client.renderers.tile.TileVisRelayRenderer;
import thaumcraft.client.renderers.tile.TileWandPedestalRenderer;
import thaumcraft.client.renderers.tile.TileWardedRenderer;
import thaumcraft.client.lib.ClientTickEventsFML;
import thaumcraft.client.lib.KeyHandler;
import thaumcraft.client.lib.RenderEventHandler;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.blocks.BlockCandle;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.EntityAspectOrb;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.EntityItemGrate;
import thaumcraft.common.entities.EntityPermanentItem;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.golems.EntityGolemBobber;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.events.EventHandlerRunic;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityMindSpider;
import thaumcraft.common.entities.monster.EntityCultistCleric;
import thaumcraft.common.entities.monster.EntityCultistKnight;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityWatcher;
import thaumcraft.common.entities.monster.EntityTaintChicken;
import thaumcraft.common.entities.monster.EntityTaintCow;
import thaumcraft.common.entities.monster.EntityTaintCreeper;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.entities.monster.EntityTaintSheep;
import thaumcraft.common.entities.monster.EntityTaintSpore;
import thaumcraft.common.entities.monster.EntityTaintSporeSwarmer;
import thaumcraft.common.entities.monster.EntityTaintSpider;
import thaumcraft.common.entities.monster.EntityTaintSwarm;
import thaumcraft.common.entities.monster.EntityTaintacle;
import thaumcraft.common.entities.monster.EntityTaintacleSmall;
import thaumcraft.common.entities.monster.EntityTaintVillager;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import thaumcraft.common.entities.projectile.EntityDart;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.entities.projectile.EntityEmber;
import thaumcraft.common.entities.projectile.EntityExplosiveOrb;
import thaumcraft.common.entities.projectile.EntityFrostShard;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.entities.projectile.EntityPechBlast;
import thaumcraft.common.entities.projectile.EntityPrimalArrow;
import thaumcraft.common.entities.projectile.EntityPrimalOrb;
import thaumcraft.common.entities.projectile.EntityShockOrb;
import thaumcraft.common.tiles.TileAlchemyFurnace;
import thaumcraft.common.tiles.TileArcaneBore;
import thaumcraft.common.tiles.TileArcaneLamp;
import thaumcraft.common.tiles.TileArcaneLampFertility;
import thaumcraft.common.tiles.TileArcaneLampGrowth;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileBanner;
import thaumcraft.common.tiles.TileBellows;
import thaumcraft.common.tiles.TileBrainbox;
import thaumcraft.common.tiles.TileCentrifuge;
import thaumcraft.common.tiles.TileChestHungry;
import thaumcraft.common.tiles.TileCrucible;
import thaumcraft.common.tiles.TileCrystal;
import thaumcraft.common.tiles.TileDeconstructionTable;
import thaumcraft.common.tiles.TileEldritchAltar;
import thaumcraft.common.tiles.TileEldritchCap;
import thaumcraft.common.tiles.TileEldritchCrabSpawner;
import thaumcraft.common.tiles.TileEldritchCrystal;
import thaumcraft.common.tiles.TileEldritchLock;
import thaumcraft.common.tiles.TileEldritchNothing;
import thaumcraft.common.tiles.TileEldritchObelisk;
import thaumcraft.common.tiles.TileEldritchPortal;
import thaumcraft.common.tiles.TileEssentiaCrystalizer;
import thaumcraft.common.tiles.TileEssentiaReservoir;
import thaumcraft.common.tiles.TileEtherealBloom;
import thaumcraft.common.tiles.TileFocalManipulator;
import thaumcraft.common.tiles.TileFluxScrubber;
import thaumcraft.common.tiles.TileHole;
import thaumcraft.common.tiles.TileInfusionPillar;
import thaumcraft.common.tiles.TileInfusionMatrix;
import thaumcraft.common.tiles.TileJarBrain;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarFillableVoid;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TileLifter;
import thaumcraft.common.tiles.TileMagicWorkbenchCharger;
import thaumcraft.common.tiles.TileManaPod;
import thaumcraft.common.tiles.TileMirror;
import thaumcraft.common.tiles.TileMirrorEssentia;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileNodeConverter;
import thaumcraft.common.tiles.TileNodeEnergized;
import thaumcraft.common.tiles.TileNodeStabilizer;
import thaumcraft.common.tiles.TilePedestal;
import thaumcraft.common.tiles.TileResearchTable;
import thaumcraft.common.tiles.TileSensor;
import thaumcraft.common.tiles.TileSpa;
import thaumcraft.common.tiles.TileTable;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileTubeBuffer;
import thaumcraft.common.tiles.TileTubeFilter;
import thaumcraft.common.tiles.TileTubeOneway;
import thaumcraft.common.tiles.TileTubeRestrict;
import thaumcraft.common.tiles.TileTube;
import thaumcraft.common.tiles.TileTubeValve;
import thaumcraft.common.tiles.TileVisRelay;
import thaumcraft.common.tiles.TileWandPedestal;
import thaumcraft.common.tiles.TileWarded;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.monster.EntityPech;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerDisplayInformation() {
        setupItemRenderers();
        setupEntityRenderers();
        setupBlockRenderers();
        setupTileRenderers();
    }

    private void setupItemRenderers() {
        for (Item item : ConfigItems.getAllItems()) {
            ResourceLocation registryName = item.getRegistryName();
            if (registryName == null) continue;
            if (item == ConfigItems.itemResearchNotes) {
                ModelResourceLocation noteModel = new ModelResourceLocation(registryName, "inventory");
                ModelResourceLocation discoveryModel = new ModelResourceLocation(new ResourceLocation("thaumcraft", "discovery"), "inventory");
                for (int meta = 0; meta < 64; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, noteModel);
                }
                for (int meta = 64; meta < 128; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, discoveryModel);
                }
                continue;
            }
            if (item == ConfigItems.itemNuggetEdible) {
                ModelResourceLocation chickenModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemnuggetedible_chicken"), "inventory");
                ModelResourceLocation beefModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemnuggetedible_beef"), "inventory");
                ModelResourceLocation porkModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemnuggetedible_pork"), "inventory");
                ModelResourceLocation fishModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemnuggetedible_fish"), "inventory");
                ModelLoader.setCustomModelResourceLocation(item, 0, chickenModel);
                ModelLoader.setCustomModelResourceLocation(item, 1, beefModel);
                ModelLoader.setCustomModelResourceLocation(item, 2, porkModel);
                ModelLoader.setCustomModelResourceLocation(item, 3, fishModel);
                for (int meta = 4; meta < 64; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, chickenModel);
                }
                continue;
            }
            if (ConfigBlocks.blockMirror != null && item == Item.getItemFromBlock(ConfigBlocks.blockMirror)) {
                ModelResourceLocation normalMirrorModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "blockmirror"), "inventory");
                ModelResourceLocation essentiaMirrorModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "blockmirror_essentia"), "inventory");
                for (int meta = 0; meta < 64; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, normalMirrorModel);
                }
                for (int meta = 6; meta < 12; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, essentiaMirrorModel);
                }
                continue;
            }
            if (item == ConfigItems.itemWandCasting) {
                for (int meta = 0; meta < 64; meta++) {
                    registerBuiltinItemModel(item, meta, "wandcasting_tesr");
                }
                continue;
            }
            if (item == ConfigItems.itemTrunkSpawner) {
                for (int meta = 0; meta < 64; meta++) {
                    registerBuiltinItemModel(item, meta, "trunkspawner_tesr");
                }
                continue;
            }
            ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
            for (int meta = 0; meta < 64; meta++) {
                ModelLoader.setCustomModelResourceLocation(item, meta, model);
            }
        }
        if (ConfigItems.itemResearchNotes != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemResearchNotes.getColorFromItemStack(stack, tintIndex),
                    ConfigItems.itemResearchNotes
            );
        }
        if (ConfigItems.itemManaBean != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemManaBean.getColorFromItemStack(stack),
                    ConfigItems.itemManaBean
            );
        }
        if (ConfigItems.itemEssence != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemEssence.getColorFromItemStack(stack, tintIndex),
                    ConfigItems.itemEssence
            );
        }
        if (ConfigItems.itemCrystalEssence != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemCrystalEssence.getColorFromItemStack(stack),
                    ConfigItems.itemCrystalEssence
            );
        }
        if (ConfigItems.itemWispEssence != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemWispEssence.getColorFromItemStack(stack),
                    ConfigItems.itemWispEssence
            );
        }
        if (ConfigBlocks.blockCandle != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> tintIndex == 0 ? BlockCandle.getCandleColor(stack.getItemDamage()) : -1,
                    Item.getItemFromBlock(ConfigBlocks.blockCandle)
            );
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(
                    (state, world, pos, tintIndex) -> tintIndex == 0 ? BlockCandle.getCandleColor(state.getValue(BlockCandle.TYPE)) : -1,
                    ConfigBlocks.blockCandle
            );
        }
        setupTileLinkedItemRenderers();
    }

    private void setupTileLinkedItemRenderers() {
        Item jarItem = Item.getItemFromBlock(ConfigBlocks.blockJar);
        if (jarItem != null) {
            TileEntityItemStackRenderer renderer = new ItemJarRenderer();
            jarItem.setTileEntityItemStackRenderer(renderer);
        }
        Item airyItem = Item.getItemFromBlock(ConfigBlocks.blockAiry);
        if (airyItem != null) {
            airyItem.setTileEntityItemStackRenderer(new ItemNodeRenderer());
        }
        Item crystalItem = Item.getItemFromBlock(ConfigBlocks.blockCrystal);
        if (crystalItem != null) {
            crystalItem.setTileEntityItemStackRenderer(new ItemCrystalRenderer());
        }
        Item eldritchItem = Item.getItemFromBlock(ConfigBlocks.blockEldritch);
        if (eldritchItem != null) {
            eldritchItem.setTileEntityItemStackRenderer(new ItemEldritchRenderer());
        }
        Item stoneDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockStoneDevice);
        if (stoneDeviceItem != null) {
            stoneDeviceItem.setTileEntityItemStackRenderer(new ItemStoneDeviceRenderer());
        }
        Item woodenDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockWoodenDevice);
        if (woodenDeviceItem != null) {
            woodenDeviceItem.setTileEntityItemStackRenderer(new ItemWoodenDeviceRenderer());
        }
        Item metalDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockMetalDevice);
        if (metalDeviceItem != null) {
            metalDeviceItem.setTileEntityItemStackRenderer(new ItemMetalDeviceRenderer());
        }
        Item tubeItem = Item.getItemFromBlock(ConfigBlocks.blockTube);
        if (tubeItem != null) {
            tubeItem.setTileEntityItemStackRenderer(new ItemTubeRenderer());
        }
        if (ConfigItems.itemWandCasting != null) {
            ConfigItems.itemWandCasting.setTileEntityItemStackRenderer(new ItemWandRenderer());
        }
        if (ConfigItems.itemTrunkSpawner != null) {
            ConfigItems.itemTrunkSpawner.setTileEntityItemStackRenderer(new ItemTrunkSpawnerRenderer());
        }
    }

    private void setupEntityRenderers() {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        Set<Class<? extends Entity>> registered = new HashSet<>();

        registerEntityRenderer(EntitySpecialItem.class, RenderSpecialItem::new, registered);
        registerEntityRenderer(EntityPermanentItem.class, RenderSpecialItem::new, registered);
        registerEntityRenderer(EntityFollowingItem.class, RenderFollowingItem::new, registered);
        registerEntityRenderer(EntityItemGrate.class, RenderSpecialItem::new, registered);

        registerEntityRenderer(EntityDart.class, RenderDart::new, registered);
        registerEntityRenderer(EntityPrimalArrow.class, RenderPrimalArrow::new, registered);
        registerEntityRenderer(EntityBottleTaint.class,
                manager -> new RenderSnowball<>(manager, itemOrFallback(ConfigItems.itemBottleTaint, Items.SPLASH_POTION), renderItem), registered);
        registerEntityRenderer(EntityAlumentum.class, RenderAlumentum::new, registered);
        registerEntityRenderer(EntityPrimalOrb.class, RenderPrimalOrb::new, registered);
        registerEntityRenderer(EntityFrostShard.class, RenderFrostShard::new, registered);
        registerEntityRenderer(EntityPechBlast.class, RenderPechBlast::new, registered);
        registerEntityRenderer(EntityEldritchOrb.class, RenderEldritchOrb::new, registered);
        registerEntityRenderer(EntityGolemOrb.class, RenderElectricOrb::new, registered);
        registerEntityRenderer(EntityShockOrb.class, RenderElectricOrb::new, registered);
        registerEntityRenderer(EntityExplosiveOrb.class, RenderExplosiveOrb::new, registered);
        registerEntityRenderer(EntityEmber.class, RenderEmber::new, registered);
        registerEntityRenderer(EntityGolemBobber.class, RenderGolemBobber::new, registered);
        registerEntityRenderer(EntityAspectOrb.class, RenderAspectOrb::new, registered);
        registerEntityRenderer(EntityFallingTaint.class, RenderFallingTaint::new, registered);

        registerEntityRenderer(EntityBrainyZombie.class, RenderBrainyZombie::new, registered);
        registerEntityRenderer(EntityGiantBrainyZombie.class, RenderBrainyZombie::new, registered);
        registerEntityRenderer(EntityInhabitedZombie.class, RenderInhabitedZombie::new, registered);
        registerEntityRenderer(EntityMindSpider.class, RenderMindSpider::new, registered);
        registerEntityRenderer(EntityTaintSpider.class, RenderTaintSpider::new, registered);
        registerEntityRenderer(EntityTaintChicken.class, RenderTaintChicken::new, registered);
        registerEntityRenderer(EntityTaintCow.class, RenderTaintCow::new, registered);
        registerEntityRenderer(EntityTaintPig.class, RenderTaintPig::new, registered);
        registerEntityRenderer(EntityTaintSheep.class, RenderTaintSheep::new, registered);
        registerEntityRenderer(EntityTaintVillager.class, RenderTaintVillager::new, registered);
        registerEntityRenderer(EntityTaintCreeper.class, RenderTaintCreeper::new, registered);
        registerEntityRenderer(EntityCultistKnight.class, manager -> new RenderCultist<>(manager, 0.5F), registered);
        registerEntityRenderer(EntityCultistCleric.class, manager -> new RenderCultist<>(manager, 0.5F), registered);
        registerEntityRenderer(EntityCultistLeader.class, manager -> new RenderCultist<>(manager, 0.6F), registered);
        registerEntityRenderer(EntityFireBat.class, RenderFireBat::new, registered);
        registerEntityRenderer(EntityWisp.class, RenderWisp::new, registered);
        registerEntityRenderer(EntityWatcher.class, RenderWatcher::new, registered);
        registerEntityRenderer(EntityPech.class, RenderPech::new, registered);
        registerEntityRenderer(EntityEldritchGuardian.class, RenderEldritchGuardian::new, registered);
        registerEntityRenderer(EntityEldritchWarden.class, RenderEldritchWarden::new, registered);
        registerEntityRenderer(EntityEldritchGolem.class, RenderEldritchGolem::new, registered);
        registerEntityRenderer(EntityEldritchCrab.class, RenderEldritchCrab::new, registered);
        registerEntityRenderer(EntityThaumicSlime.class, RenderThaumicSlime::new, registered);
        registerEntityRenderer(EntityTaintSpore.class, RenderTaintSpore::new, registered);
        registerEntityRenderer(EntityTaintSporeSwarmer.class, RenderTaintSporeSwarmer::new, registered);
        registerEntityRenderer(EntityTaintSwarm.class, RenderTaintSwarm::new, registered);
        registerEntityRenderer(EntityTaintacle.class, manager -> new RenderTaintacle<>(manager, 0.6F, 10), registered);
        registerEntityRenderer(EntityTaintacleSmall.class, manager -> new RenderTaintacle<>(manager, 0.2F, 6), registered);
        registerEntityRenderer(EntityTaintacleGiant.class, manager -> new RenderTaintacle<>(manager, 1.0F, 14), registered);
        registerEntityRenderer(EntityGolemBase.class, RenderGolemBase::new, registered);
        registerEntityRenderer(EntityTravelingTrunk.class, RenderTravelingTrunk::new, registered);
        registerEntityRenderer(EntityCultistPortal.class, RenderCultistPortal::new, registered);

        for (net.minecraftforge.fml.common.registry.EntityEntry entry : ConfigEntities.ENTITIES) {
            @SuppressWarnings("unchecked")
            Class<? extends Entity> entityClass = (Class<? extends Entity>) entry.getEntityClass();
            if (registered.contains(entityClass)) {
                continue;
            }
            RenderingRegistry.registerEntityRenderingHandler(entityClass, RenderNoop::new);
        }
    }

    private static <T extends Entity> void registerEntityRenderer(
            Class<T> entityClass,
            net.minecraftforge.fml.client.registry.IRenderFactory<? super T> factory,
            Set<Class<? extends Entity>> registered) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, factory);
        registered.add(entityClass);
    }

    private static Item itemOrFallback(@Nullable Item preferred, Item fallback) {
        return preferred != null ? preferred : fallback;
    }

    private void setupBlockRenderers() {
        registerBlockItemModel(ConfigBlocks.blockMagicalLeavesItem, 0, "type=0");
        registerBlockItemModel(ConfigBlocks.blockMagicalLeavesItem, 1, "type=1");
        Item airyItem = Item.getItemFromBlock(ConfigBlocks.blockAiry);
        for (int meta = 0; meta <= 12; meta++) {
            registerBlockItemModel(airyItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(airyItem, 0, "blockairy");
        registerBuiltinItemModel(airyItem, 5, "blockairy");
        Item crystalItem = Item.getItemFromBlock(ConfigBlocks.blockCrystal);
        for (int meta = 0; meta <= 7; meta++) {
            registerBuiltinItemModel(crystalItem, meta, "blockcrystal_tesr");
        }
        Item eldritchItem = Item.getItemFromBlock(ConfigBlocks.blockEldritch);
        for (int meta = 0; meta <= 10; meta++) {
            registerBlockItemModel(eldritchItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(eldritchItem, 0, "blockeldritch_tesr");
        registerBuiltinItemModel(eldritchItem, 1, "blockeldritch_tesr");
        registerBuiltinItemModel(eldritchItem, 3, "blockeldritch_tesr");
        registerBuiltinItemModel(eldritchItem, 8, "blockeldritch_tesr");
        registerBuiltinItemModel(eldritchItem, 9, "blockeldritch_tesr");
        Item stoneDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockStoneDevice);
        for (int meta = 0; meta <= 14; meta++) {
            registerBlockItemModel(stoneDeviceItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(stoneDeviceItem, 2, "blockstonedevice_tesr");
        Item metalDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockMetalDevice);
        for (int meta = 0; meta <= 14; meta++) {
            registerBlockItemModel(metalDeviceItem, meta, "type=" + meta);
        }
        Item woodenDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockWoodenDevice);
        for (int meta = 0; meta <= 8; meta++) {
            registerBlockItemModel(woodenDeviceItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(woodenDeviceItem, 0, "blockwoodendevice_tesr");
        registerBuiltinItemModel(woodenDeviceItem, 4, "blockwoodendevice_tesr");
        registerBuiltinItemModel(woodenDeviceItem, 5, "blockwoodendevice_tesr");
        registerBuiltinItemModel(woodenDeviceItem, 8, "blockwoodendevice_tesr");
        registerBuiltinItemModel(metalDeviceItem, 1, "blockmetaldevice_tesr");
        registerBuiltinItemModel(metalDeviceItem, 2, "blockmetaldevice_tesr");
        registerBuiltinItemModel(metalDeviceItem, 10, "blockmetaldevice_tesr");
        registerBuiltinItemModel(metalDeviceItem, 11, "blockmetaldevice_tesr");
        registerBuiltinItemModel(metalDeviceItem, 14, "blockmetaldevice_tesr");
        Item tubeItem = Item.getItemFromBlock(ConfigBlocks.blockTube);
        for (int meta = 0; meta <= 7; meta++) {
            registerBlockItemModel(tubeItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(tubeItem, 7, "blocktube_tesr");
        Item tableItem = Item.getItemFromBlock(ConfigBlocks.blockTable);
        for (int meta = 0; meta <= 15; meta++) {
            registerBlockItemModel(tableItem, meta, "type=" + meta);
        }
        Item mirrorItem = Item.getItemFromBlock(ConfigBlocks.blockMirror);
        for (int meta = 0; meta <= 11; meta++) {
            registerBlockItemModel(mirrorItem, meta, "type=" + meta);
        }
        Item arcaneFurnaceItem = Item.getItemFromBlock(ConfigBlocks.blockArcaneFurnace);
        for (int meta = 0; meta <= 10; meta++) {
            registerBlockItemModel(arcaneFurnaceItem, meta, "type=" + meta + ",facing=north");
        }
        registerBlockItemModel(Item.getItemFromBlock(ConfigBlocks.blockEssentiaReservoir), 0, "normal");
        Item candleItem = Item.getItemFromBlock(ConfigBlocks.blockCandle);
        for (int meta = 0; meta < 16; meta++) {
            registerBlockItemModel(candleItem, meta, "type=" + meta);
        }
    }

    private static void registerBlockItemModel(Item item, int meta, String variant) {
        if (item == null || item.getRegistryName() == null) {
            return;
        }
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(item.getRegistryName(), variant));
    }

    private static void registerBuiltinItemModel(Item item, int meta, String modelPath) {
        if (item == null) {
            return;
        }
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(new ResourceLocation("thaumcraft", modelPath), "inventory"));
    }

    private void setupTileRenderers() {
        TileJarRenderer jarRenderer = new TileJarRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileJarFillable.class, jarRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileJarFillableVoid.class, jarRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileJarBrain.class, jarRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileJarNode.class, jarRenderer);

        TileNodeRenderer nodeRenderer = new TileNodeRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileNode.class, nodeRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileNodeEnergized.class, new TileNodeEnergizedRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileNodeStabilizer.class, new TileNodeStabilizerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileNodeConverter.class, new TileNodeConverterRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVisRelay.class, new TileVisRelayRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileAlchemyFurnace.class, new TileAlchemyFurnaceAdvancedRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBellows.class, new TileBellowsRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTable.class, new TileTableRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCrucible.class, new TileCrucibleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(thaumcraft.common.tiles.TileAlembic.class, new TileAlembicRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new TilePedestalRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWandPedestal.class, new TileWandPedestalRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionMatrix.class, new TileRunicMatrixRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionPillar.class, new TileInfusionPillarRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileThaumatorium.class, new TileThaumatoriumRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneBore.class, new TileArcaneBoreRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(thaumcraft.common.tiles.TileArcaneBoreBase.class, new TileArcaneBoreBaseRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileFocalManipulator.class, new TileFocalManipulatorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.class, new TileBannerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileHole.class, new TileHoleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWarded.class, new TileWardedRenderer());

        TileArcaneLampRenderer lampRenderer = new TileArcaneLampRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneLamp.class, lampRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneLampGrowth.class, lampRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneLampFertility.class, lampRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneWorkbench.class, new TileArcaneWorkbenchRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileDeconstructionTable.class, new TileDeconstructionTableRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileResearchTable.class, new TileResearchTableRenderer());

        TileMirrorRenderer mirrorRenderer = new TileMirrorRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileMirror.class, mirrorRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileMirrorEssentia.class, mirrorRenderer);

        ClientRegistry.bindTileEntitySpecialRenderer(TileTube.class, new TileTubeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTubeFilter.class, new TileTubeFilterRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTubeBuffer.class, new TileTubeBufferRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTubeRestrict.class, new TileTubeRestrictRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTubeValve.class, new TileTubeValveRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTubeOneway.class, new TileTubeOnewayRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEssentiaCrystalizer.class, new TileEssentiaCrystalizerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCentrifuge.class, new TileCentrifugeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestHungry.class, new TileChestHungryRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystal.class, new TileCrystalRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchCrystal.class, new TileEldritchCrystalRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchCap.class, new TileEldritchCapRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEldritchAltar.class,
                new TileEldritchCapRenderer(TileEldritchCapRenderer.altarTexture()));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchObelisk.class, new TileEldritchObeliskRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchCrabSpawner.class, new TileEldritchCrabSpawnerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchPortal.class, new TileEldritchPortalRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchLock.class, new TileEldritchLockRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchNothing.class, new TileEldritchNothingRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEssentiaReservoir.class, new TileEssentiaReservoirRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEtherealBloom.class, new TileEtherealBloomRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileFluxScrubber.class, new TileFluxScrubberRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileManaPod.class, new TileManaPodRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMagicWorkbenchCharger.class, new TileMagicWorkbenchChargerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBrainbox.class, new TileBrainboxRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSensor.class, new TileSensorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileLifter.class, new TileLifterRenderer());
    }

    @Override
    public void registerKeyBindings() {
        MinecraftForge.EVENT_BUS.register(new KeyHandler());
    }

    @Override
    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(new ClientTickEventsFML());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(ParticleEngine.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void scheduleClientTask(Runnable task) {
        if (task != null) {
            Minecraft.getMinecraft().addScheduledTask(task);
        }
    }

    @Nullable
    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Nullable
    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    private static final class ClientEventHandler {
        @SubscribeEvent
        public void onItemTooltip(ItemTooltipEvent event) {
            int charge = EventHandlerRunic.getFinalCharge(event.getItemStack());
            int warp = EventHandlerRunic.getFinalWarp(event.getItemStack(), event.getEntityPlayer());

            if (charge > 0) {
                event.getToolTip().add(TextFormatting.GOLD + I18n.translateToLocal("item.runic.charge") + " +" + charge);
            }
            if (warp > 0 && event.getEntityPlayer() != null) {
                event.getToolTip().add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.warping") + " " + warp);
            }
        }
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (!(world instanceof WorldClient)) {
            return null;
        }
        switch (ID) {
            case GUI_FOCUS_POUCH:
                return new GuiFocusPouch(player.inventory, world, x, y, z);
            case GUI_HAND_MIRROR:
                return new GuiHandMirror(player.inventory, world, x, y, z);
            case GUI_HOVER_HARNESS:
                return new GuiHoverHarness(player.inventory, world, x, y, z);
            case GUI_GOLEM:
            {
                net.minecraft.entity.Entity entity = world.getEntityByID(x);
                return entity instanceof EntityGolemBase
                        ? new GuiGolem(player, (EntityGolemBase) entity)
                        : null;
            }
            case GUI_PECH:
            {
                net.minecraft.entity.Entity entity = world.getEntityByID(x);
                return entity instanceof EntityPech
                        ? new GuiPech(player, world, (EntityPech) entity)
                        : null;
            }
            case GUI_TRAVELING_TRUNK:
            {
                net.minecraft.entity.Entity entity = world.getEntityByID(x);
                return entity instanceof EntityTravelingTrunk
                        ? new GuiTravelingTrunk(player, (EntityTravelingTrunk) entity)
                        : null;
            }
            case GUI_THAUMATORIUM:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileThaumatorium
                        ? new GuiThaumatorium(player.inventory, (TileThaumatorium) tile)
                        : null;
            }
            case GUI_DECONSTRUCTION_TABLE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileDeconstructionTable
                        ? new GuiDeconstructionTable(player.inventory, (TileDeconstructionTable) tile)
                        : null;
            }
            case GUI_ALCHEMY_FURNACE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileAlchemyFurnace
                        ? new GuiAlchemyFurnace(player.inventory, (TileAlchemyFurnace) tile)
                        : null;
            }
            case GUI_RESEARCH_TABLE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileResearchTable
                        ? new GuiResearchTable(player, (TileResearchTable) tile)
                        : null;
            }
            case GUI_THAUMONOMICON:
                return new GuiResearchBrowser();
            case GUI_ARCANE_WORKBENCH:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileArcaneWorkbench
                        ? new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile)
                        : null;
            }
            case GUI_ARCANE_BORE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileArcaneBore
                        ? new GuiArcaneBore(player.inventory, (TileArcaneBore) tile)
                        : null;
            }
            case GUI_MAGIC_BOX:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof IInventory
                        ? new GuiMagicBox(player.inventory, tile)
                        : null;
            }
            case GUI_SPA:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileSpa
                        ? new GuiSpa(player.inventory, (TileSpa) tile)
                        : null;
            }
            case GUI_FOCAL_MANIPULATOR:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileFocalManipulator
                        ? new GuiFocalManipulator(player.inventory, (TileFocalManipulator) tile)
                        : null;
            }
            default:
                return null;
        }
    }

    // ---- FX overrides ----

    @Override
    public void blockSparkle(World world, int x, int y, int z, int color, int count) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(1, count));
        if (amount <= 0) return;
        if (color == -9999) {
            ParticleEngine.addEffect(world, new FXVisSparkle(world, x, y, z, 0.0f, 0.0f, 0.0f, amount, true));
            return;
        }

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        ParticleEngine.addEffect(world, new FXVisSparkle(world, x, y, z, red, green, blue, amount));
    }

    @Override
    public void blockWard(World world, double x, double y, double z, EnumFacing side, float red, float green, float blue) {
        if (world == null || !world.isRemote || side == null) return;
        ParticleEngine.addEffect(world, new FXBlockWard(world, x + 0.5D, y + 0.5D, z + 0.5D, side, red, green, blue));
    }

    @Override
    public void beam(World world, double x, double y, double z, double tx, double ty, double tz, int color, boolean flicker, int ticks) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(4, ticks / 2));
        if (amount <= 0) return;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        ParticleEngine.addEffect(world,
                new FXBeam(world, x, y, z, tx, ty, tz, red, green, blue, Math.max(6, ticks), flicker, amount));
    }

    @Override
    public void beamPulseFX(World world, Entity source, Entity target, int color) {
        if (world == null || !world.isRemote || source == null || target == null) return;
        Color tint = decodeColor(color);
        FXBeam beam = new FXBeam(
                world,
                source.posX,
                source.posY + source.getEyeHeight(),
                source.posZ,
                target.posX,
                target.posY + target.height * 0.5,
                target.posZ,
                normalizeColor(tint.getRed()),
                normalizeColor(tint.getGreen()),
                normalizeColor(tint.getBlue()),
                20,
                true,
                20);
        beam.setType(1);
        beam.setReverse(true);
        beam.setPulse(true);
        ParticleEngine.addEffect(world, beam);
    }

    @Override
    public void beamPulseGolemBossFX(World world, EntityLivingBase source, Entity target) {
        if (world == null || !world.isRemote || source == null || target == null) return;

        FXBeamGolemBoss beamA = new FXBeamGolemBoss(world, source, target, 0.07F, 0.376F, 0.325F, 20);
        beamA.setType(2);
        beamA.setPulse(true);
        ParticleEngine.addEffect(world, beamA);

        FXBeamGolemBoss beamB = new FXBeamGolemBoss(world, source, target, 1.0F, 0.5F, 0.5F, 20);
        beamB.setType(1);
        beamB.setPulse(true);
        ParticleEngine.addEffect(world, beamB);
    }

    @Override
    public Object beamCont(World world,
                           EntityPlayer player,
                           double tx, double ty, double tz,
                           int type, int color,
                           boolean reverse, float endmod,
                           Object input, int impact) {
        if (world == null || !world.isRemote || player == null) return null;
        int amount = particleCount(8);
        if (amount <= 0) return null;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        FXBeamWand beam = input instanceof FXBeamWand ? (FXBeamWand) input : null;
        if (beam == null || !beam.isAlive()) {
            beam = new FXBeamWand(world, player, tx, ty, tz, red, green, blue, 8, false, amount);
            beam.setType(type);
            beam.setEndMod(endmod);
            beam.setReverse(reverse);
            beam.setPulse(false);
            ParticleEngine.addEffect(world, beam);
        } else {
            beam.updateBeam(tx, ty, tz);
            beam.setEndMod(endmod);
        }
        beam.impact = impact;
        return beam;
    }

    @Override
    public Object beamBore(World world,
                           double px, double py, double pz,
                           double tx, double ty, double tz,
                           int type, int color,
                           boolean reverse, float endmod,
                           Object input, int impact) {
        if (world == null || !world.isRemote) return null;
        int amount = particleCount(8);
        if (amount <= 0) return null;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        FXBeamBore beam = input instanceof FXBeamBore ? (FXBeamBore) input : null;
        if (beam == null || !beam.isAlive()) {
            beam = new FXBeamBore(world, px, py, pz, tx, ty, tz, red, green, blue, 8, false, amount);
            beam.setType(type);
            beam.setEndMod(endmod);
            beam.setReverse(reverse);
            beam.setPulse(false);
            ParticleEngine.addEffect(world, beam);
        } else {
            beam.updateBeam(tx, ty, tz);
            beam.setEndMod(endmod);
        }
        beam.impact = impact;
        return beam;
    }

    @Override
    public Object beamPower(World world,
                            double px, double py, double pz,
                            double tx, double ty, double tz,
                            float red, float green, float blue,
                            boolean pulse, Object input) {
        if (world == null || !world.isRemote) return null;
        int amount = particleCount(8);
        if (amount <= 0) return null;

        FXBeamPower beam = input instanceof FXBeamPower ? (FXBeamPower) input : null;
        if (beam == null || !beam.isAlive()) {
            beam = new FXBeamPower(world, px, py, pz, tx, ty, tz, red, green, blue, 8, false, amount);
            beam.setPulse(pulse, red, green, blue);
            ParticleEngine.addEffect(world, beam);
        } else {
            beam.updateBeam(px, py, pz, tx, ty, tz);
            beam.setPulse(pulse, red, green, blue);
        }
        return beam;
    }

    @Override
    public void bolt(World world, double x, double y, double z, double tx, double ty, double tz, int color, int speed) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(6, speed * 2));
        if (amount <= 0) return;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        ParticleEngine.addEffect(world,
                new FXLightningBolt(world, x, y, z, tx, ty, tz, red, green, blue, Math.max(4, speed), amount));
    }

    @Override
    public void bolt(World world, Entity sourceEntity, Entity targetedEntity) {
        if (world == null || !world.isRemote || sourceEntity == null || targetedEntity == null) return;
        FXLightningBolt bolt = new FXLightningBolt(world, sourceEntity, targetedEntity, world.rand.nextLong(), 4);
        bolt.defaultFractal();
        bolt.setType(0);
        bolt.finalizeBolt();
    }

    @Override
    public void nodeBolt(World world, float x, float y, float z, Entity target) {
        if (world == null || !world.isRemote || target == null) return;
        FXLightningBolt bolt = new FXLightningBolt(world, x, y, z, target.posX, target.posY, target.posZ, world.rand.nextLong(), 10, 4.0F, 5);
        bolt.defaultFractal();
        bolt.setType(3);
        bolt.finalizeBolt();
    }

    @Override
    public void nodeBolt(World world, float x, float y, float z, float tx, float ty, float tz) {
        if (world == null || !world.isRemote) return;
        FXLightningBolt bolt = new FXLightningBolt(world, x, y, z, tx, ty, tz, world.rand.nextLong(), 10, 4.0F, 5);
        bolt.defaultFractal();
        bolt.setType(0);
        bolt.finalizeBolt();
    }

    @Override
    public void sourceStreamFX(World world, double sx, double sy, double sz, float tx, float ty, float tz, int color) {
        if (world == null || !world.isRemote) return;
        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        FXWispArcing fx = new FXWispArcing(world, tx, ty, tz, sx, sy, sz, 0.1F, red, green, blue);
        fx.setGravity(0.0F);
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void essentiaTrailFx(World world, int x, int y, int z, int tx, int ty, int tz, int count, int color, float scale) {
        if (world == null || !world.isRemote) return;
        ParticleEngine.addEffect(world, new FXEssentiaTrail(
                world,
                x + 0.5D, y + 0.5D, z + 0.5D,
                tx + 0.5D, ty + 0.5D, tz + 0.5D,
                count, color, scale));
    }

    @Override
    public void visDrainFx(World world, BlockPos from, BlockPos to, int color) {
        if (world == null || !world.isRemote || from == null || to == null) return;
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        double sx = to.getX() + 0.4D + world.rand.nextFloat() * 0.2F;
        double sy = to.getY() + 0.4D + world.rand.nextFloat() * 0.2F;
        double sz = to.getZ() + 0.4D + world.rand.nextFloat() * 0.2F;
        double tx = from.getX() + world.rand.nextFloat();
        double ty = from.getY() + world.rand.nextFloat();
        double tz = from.getZ() + world.rand.nextFloat();
        FXVisSparkle sparkle = new FXVisSparkle(world, sx, sy, sz, tx, ty, tz);
        sparkle.setRBGColorF(red, green, blue);
        ParticleEngine.addEffect(world, sparkle);
    }

    @Override
    public void blockRunes(World world, double x, double y, double z, float red, float green, float blue, int duration, float gravity) {
        if (world == null || !world.isRemote) return;
        FXBlockRunes runes = new FXBlockRunes(world, x, y, z, red, green, blue, duration);
        runes.setGravity(gravity);
        ParticleEngine.addEffect(world, runes);
    }

    @Override
    public void arcLightning(World world,
                             double x, double y, double z,
                             double tx, double ty, double tz,
                             float red, float green, float blue,
                             float height) {
        if (world == null || !world.isRemote) return;
        FXSparkle sparkle = new FXSparkle(world, tx, ty, tz, 3.0F, 6, 2);
        sparkle.setGravity(0.0F);
        sparkle.setRBGColorF(red, green, blue);
        ParticleEngine.addEffect(world, sparkle);
        ParticleEngine.addEffect(world, new FXArc(world, x, y, z, tx, ty, tz, red, green, blue, height));
    }

    @Override
    public void drawInfusionParticles1(World world,
                                       double x, double y, double z,
                                       int tx, int ty, int tz,
                                       Item item, int meta) {
        if (world == null || !world.isRemote || item == null) return;
        ParticleEngine.addEffect(world, new FXBoreParticles(
                world,
                x, y, z,
                tx + 0.5D, ty - 0.5D, tz + 0.5D,
                item, meta));
    }

    @Override
    public void drawInfusionParticles2(World world,
                                       double x, double y, double z,
                                       int tx, int ty, int tz,
                                       IBlockState state) {
        if (world == null || !world.isRemote || state == null) return;
        ParticleEngine.addEffect(world, new FXBoreParticles(
                world,
                x, y, z,
                tx + 0.5D, ty - 0.5D, tz + 0.5D,
                state));
    }

    @Override
    public void drawInfusionParticles3(World world, double x, double y, double z, int tx, int ty, int tz) {
        if (world == null || !world.isRemote) return;
        FXBoreSparkle sparkle = new FXBoreSparkle(world, x, y, z, tx + 0.5D, ty - 0.5D, tz + 0.5D);
        sparkle.setRBGColorF(0.4F + world.rand.nextFloat() * 0.2F, 0.2F, 0.6F + world.rand.nextFloat() * 0.3F);
        ParticleEngine.addEffect(world, sparkle);
    }

    @Override
    public void drawInfusionParticles4(World world, double x, double y, double z, int tx, int ty, int tz) {
        if (world == null || !world.isRemote) return;
        FXBoreSparkle sparkle = new FXBoreSparkle(world, x, y, z, tx + 0.5D, ty - 0.5D, tz + 0.5D);
        sparkle.setRBGColorF(0.2F, 0.6F + world.rand.nextFloat() * 0.3F, 0.3F);
        ParticleEngine.addEffect(world, sparkle);
    }

    @Override
    public void burst(World world, double x, double y, double z, float scale) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(6, (int) (scale * 12.0f)));
        if (amount <= 0) return;
        ParticleEngine.addEffect(world, new FXBurst(world, x, y, z, scale, amount));
    }

    @Override
    public void sonicFX(World world, Entity source, int age) {
        if (world == null || !world.isRemote || source == null) return;
        ParticleEngine.addEffect(world, new FXSonic(world, source, Math.max(8, age)));
    }

    @Override
    public void shieldRunesFX(World world, Entity source, int age, float yaw, float pitch) {
        if (world == null || !world.isRemote || source == null) return;
        ParticleEngine.addEffect(world, new FXShieldRunes(world, source, Math.max(8, age), yaw, pitch));
    }

    @Override
    public void zapFX(World world, Entity source, Entity target) {
        if (world == null || !world.isRemote || source == null || target == null) return;
        FXLightningBolt bolt = new FXLightningBolt(
                world,
                source.posX,
                source.getEntityBoundingBox().minY + source.height * 0.5D,
                source.posZ,
                target.posX,
                target.getEntityBoundingBox().minY + target.height * 0.5D,
                target.posZ,
                world.rand.nextLong(),
                6,
                0.5F,
                8);
        bolt.defaultFractal();
        bolt.setType(2);
        bolt.setWidth(0.125F);
        bolt.finalizeBolt();
    }

    @Override
    public void focusShockBolt(World world, EntityLivingBase source, double tx, double ty, double tz) {
        if (world == null || !world.isRemote || source == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer clientPlayer = mc == null ? null : mc.player;
        double sx = source.posX;
        double sy = source.posY;
        double sz = source.posZ;
        if (clientPlayer == null || source.getEntityId() != clientPlayer.getEntityId()) {
            sy = source.getEntityBoundingBox().minY + source.height * 0.5D + 0.25D;
        }
        sx += -MathHelper.cos((float) (source.rotationYaw / 180.0F * Math.PI)) * 0.06F;
        sy -= 0.06D;
        sz += -MathHelper.sin((float) (source.rotationYaw / 180.0F * Math.PI)) * 0.06F;
        if (clientPlayer == null || source.getEntityId() != clientPlayer.getEntityId()) {
            sy = source.getEntityBoundingBox().minY + source.height * 0.5D + 0.25D;
        }
        Vec3d look = source.getLook(1.0F);
        sx += look.x * 0.3D;
        sy += look.y * 0.3D;
        sz += look.z * 0.3D;

        FXLightningBolt bolt = new FXLightningBolt(world, sx, sy, sz, tx, ty, tz, world.rand.nextLong(), 6, 0.5F, 8);
        bolt.defaultFractal();
        bolt.setType(2);
        bolt.setWidth(0.125F);
        bolt.finalizeBolt();
    }

    @Override
    public void wispFX3(World world, double x, double y, double z, double tx, double ty, double tz, float size, int count, boolean flag, float speed) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(2, (int) (size * 18.0f)));
        if (amount <= 0) return;
        for (int i = 0; i < amount; i++) {
            double px = x + (world.rand.nextFloat() - 0.5f) * 0.25f;
            double py = y + (world.rand.nextFloat() - 0.5f) * 0.25f;
            double pz = z + (world.rand.nextFloat() - 0.5f) * 0.25f;
            ParticleEngine.addEffect(world, new FXWisp(world, px, py, pz, tx, ty, tz, size, flag, speed));
        }
    }

    @Override
    public void wispFX2(World world, double x, double y, double z, float size, int type, boolean shrink, boolean clip, float gravity) {
        if (world == null || !world.isRemote) return;
        FXWisp fx = new FXWisp(world, x, y, z, size, type);
        fx.setGravity(gravity);
        fx.shrink = shrink;
        fx.setNoClip(clip);
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void wispFXEG(World world, double x, double y, double z, Entity target) {
        if (world == null || !world.isRemote || target == null) return;
        int amount = particleCount(1);
        if (amount <= 0) return;
        for (int i = 0; i < amount; i++) {
            ParticleEngine.addEffect(world, new FXWispEG(world, x, y, z, target));
        }
    }

    @Override
    public void taintLandFX(Entity entity) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            float angle = world.rand.nextFloat() * ((float) Math.PI * 2.0F);
            float radius = world.rand.nextFloat() * 0.5F + 0.5F;
            float offsetX = MathHelper.sin(angle) * 0.5F * radius;
            float offsetZ = MathHelper.cos(angle) * 0.5F * radius;
            double y = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) * 0.5D;

            FXBreaking fx = new FXBreaking(world, entity.posX + offsetX, y, entity.posZ + offsetZ, Items.SLIME_BALL);
            fx.setRBGColorF(0.1F, 0.0F, 0.1F);
            fx.setAlphaF(0.4F);
            fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
            ParticleEngine.addEffect(world, fx);
        }
    }

    @Override
    public void slimeJumpFX(Entity entity, int size) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        int amount = particleCount(Math.max(1, size + 1));
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            float angle = world.rand.nextFloat() * ((float) Math.PI * 2.0F);
            float radius = world.rand.nextFloat() * 0.5F + 0.5F;
            float offsetX = MathHelper.sin(angle) * size * 0.5F * radius;
            float offsetZ = MathHelper.cos(angle) * size * 0.5F * radius;
            double y = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) * 0.5D;

            FXBreaking fx = new FXBreaking(world, entity.posX + offsetX, y, entity.posZ + offsetZ, Items.SLIME_BALL);
            fx.setRBGColorF(0.7F, 0.0F, 1.0F);
            fx.setAlphaF(0.4F);
            fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
            ParticleEngine.addEffect(world, fx);
        }
    }

    @Override
    public Object swarmParticleFX(World world, Entity targetedEntity, float speed, float turnSpeed, float particleGravity) {
        if (world == null || !world.isRemote || targetedEntity == null) return null;
        int amount = particleCount(1);
        if (amount <= 0) return null;

        FXSwarm swarm = null;
        for (int i = 0; i < amount; i++) {
            swarm = new FXSwarm(
                    world,
                    targetedEntity.posX + (world.rand.nextFloat() - world.rand.nextFloat()) * 2.0F,
                    targetedEntity.posY + (world.rand.nextFloat() - world.rand.nextFloat()) * 2.0F,
                    targetedEntity.posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * 2.0F,
                    targetedEntity,
                    0.8F + world.rand.nextFloat() * 0.2F,
                    world.rand.nextFloat() * 0.4F,
                    1.0F - world.rand.nextFloat() * 0.2F,
                    speed,
                    turnSpeed,
                    particleGravity);
            ParticleEngine.addEffect(world, swarm);
        }
        return swarm;
    }

    @Override
    public void splooshFX(Entity entity) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        float angle = world.rand.nextFloat() * ((float) Math.PI * 2.0F);
        float radius = world.rand.nextFloat() * 0.5F + 0.5F;
        float offsetX = MathHelper.sin(angle) * 1.0F * radius;
        float offsetZ = MathHelper.cos(angle) * 1.0F * radius;

        FXBreaking fx = new FXBreaking(
                world,
                entity.posX + offsetX,
                entity.posY + world.rand.nextFloat() * entity.height,
                entity.posZ + offsetZ,
                Items.SLIME_BALL);
        if (world.rand.nextBoolean()) {
            fx.setRBGColorF(0.6F, 0.0F, 0.3F);
            fx.setAlphaF(0.4F);
        } else {
            fx.setRBGColorF(0.3F, 0.0F, 0.3F);
            fx.setAlphaF(0.6F);
        }
        fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void taintsplosionFX(Entity entity) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        double motionX = Math.random() * 2.0D - 1.0D;
        double motionY = Math.random() * 2.0D - 1.0D;
        double motionZ = Math.random() * 2.0D - 1.0D;
        double speed = (Math.random() + Math.random() + 1.0D) * 0.15D;
        double length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        if (length > 1.0E-6D) {
            motionX = motionX / length * speed;
            motionY = motionY / length * speed;
            motionZ = motionZ / length * speed;
        }
        FXBreaking fx = new FXBreaking(
                world,
                entity.posX,
                entity.posY + world.rand.nextFloat() * entity.height,
                entity.posZ,
                motionX,
                motionY,
                motionZ,
                Items.SLIME_BALL);
        if (world.rand.nextBoolean()) {
            fx.setRBGColorF(0.6F, 0.0F, 0.3F);
            fx.setAlphaF(0.4F);
        } else {
            fx.setRBGColorF(0.3F, 0.0F, 0.3F);
            fx.setAlphaF(0.6F);
        }
        fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void golemFishingSplashFX(Entity entity, int kind) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        int amount = particleCount(kind == 2 ? 12 : (kind == 1 ? 2 : 1));
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            float angle = world.rand.nextFloat() * ((float) Math.PI * 2.0F);
            float radius = kind == 1
                    ? 0.25F + world.rand.nextFloat() * 0.35F
                    : world.rand.nextFloat() * 0.2F;
            double px = entity.posX + MathHelper.sin(angle) * radius;
            double py = entity.posY + 0.1D + world.rand.nextFloat() * (kind == 2 ? 0.4D : 0.2D);
            double pz = entity.posZ + MathHelper.cos(angle) * radius;
            double mx = (world.rand.nextFloat() - world.rand.nextFloat()) * (kind == 2 ? 0.05F : 0.02F);
            double my = 0.02D + world.rand.nextFloat() * (kind == 2 ? 0.04D : 0.02D);
            double mz = (world.rand.nextFloat() - world.rand.nextFloat()) * (kind == 2 ? 0.05F : 0.02F);

            FXBubble bubble = new FXBubble(world, px, py, pz, mx, my, mz, kind == 2 ? 6 : 4);
            bubble.setRGB(0.8F, 0.9F, 1.0F);
            bubble.setBubbleSpeed(0.003D + (kind == 2 ? 0.002D : 0.001D));
            ParticleEngine.addEffect(world, bubble);
        }
    }

    @Override
    public void bottleTaintBreak(World world, double x, double y, double z) {
        if (world == null || !world.isRemote) return;
        Item bottle = ConfigItems.itemBottleTaint != null ? ConfigItems.itemBottleTaint : Items.SPLASH_POTION;
        for (int i = 0; i < 8; i++) {
            ParticleEngine.addEffect(world, new FXBreaking(
                    world,
                    x,
                    y,
                    z,
                    world.rand.nextGaussian() * 0.15D,
                    world.rand.nextDouble() * 0.2D,
                    world.rand.nextGaussian() * 0.15D,
                    bottle));
        }
        world.playSound(x, y, z, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, 0.9F + world.rand.nextFloat() * 0.1F, false);
    }

    @Override
    public void spark(float x, float y, float z, float size, float red, float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc == null ? null : mc.world;
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;
        for (int i = 0; i < amount; i++) {
            FXSpark fx = new FXSpark(world, x, y, z, size);
            fx.setRBGColorF(red, green, blue);
            fx.setAlphaF(alpha);
            ParticleEngine.addEffect(world, fx);
        }
    }

    @Override
    public void drawGenericParticles(World world, double x, double y, double z,
                                     double mx, double my, double mz,
                                     float red, float green, float blue, float alpha,
                                     boolean loop, int start, int num, int inc, int age, int delay, float scale) {
        int step = Math.max(1, Math.abs(inc));
        int amount = particleCount(Math.max(1, num / step));
        drawGenericParticles(world, x, y, z, mx, my, mz, red, green, blue, alpha,
                loop, start, num, inc, age, delay, scale, amount);
    }

    @Override
    public void drawGenericParticles(World world, double x, double y, double z,
                                     double mx, double my, double mz,
                                     float red, float green, float blue, float alpha,
                                     boolean loop, int start, int num, int inc, int age, int delay, float scale,
                                     int count) {
        if (world == null || !world.isRemote) return;
        if (count <= 0) return;

        for (int i = 0; i < count; i++) {
            ParticleEngine.addEffect(world, new FXGeneric(
                    world,
                    x + (world.rand.nextFloat() - 0.5f) * 0.15f,
                    y + (world.rand.nextFloat() - 0.5f) * 0.15f,
                    z + (world.rand.nextFloat() - 0.5f) * 0.15f,
                    mx + (world.rand.nextFloat() - 0.5f) * 0.01f,
                    my + (world.rand.nextFloat() - 0.5f) * 0.01f,
                    mz + (world.rand.nextFloat() - 0.5f) * 0.01f,
                    red, green, blue, alpha,
                    loop, start, num, inc, age, delay, scale));
        }
    }

    @Override
    public void boreDigFx(World world,
                          double x, double y, double z,
                          double tx, double ty, double tz,
                          IBlockState state,
                          @Nullable Item item,
                          int meta) {
        if (world == null || !world.isRemote) return;
        if (state != null) {
            ParticleEngine.addEffect(world, new FXBoreParticles(world, x, y, z, tx, ty, tz, state));
        } else if (item != null) {
            ParticleEngine.addEffect(world, new FXBoreParticles(world, x, y, z, tx, ty, tz, item, meta));
        }
    }

    @Override
    public void drawVentParticles(World world, double x, double y, double z,
                                  double mx, double my, double mz, int color) {
        drawVentParticles(world, x, y, z, mx, my, mz, color, 1.0F);
    }

    @Override
    public void drawVentParticles(World world, double x, double y, double z,
                                  double mx, double my, double mz, int color, float scale) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        for (int i = 0; i < amount; i++) {
            FXVent fx = new FXVent(
                    world,
                    x + (world.rand.nextFloat() - 0.5f) * 0.05f,
                    y + (world.rand.nextFloat() - 0.5f) * 0.05f,
                    z + (world.rand.nextFloat() - 0.5f) * 0.05f,
                    mx + (world.rand.nextFloat() - 0.5f) * 0.01f,
                    my + (world.rand.nextFloat() - 0.5f) * 0.01f,
                    mz + (world.rand.nextFloat() - 0.5f) * 0.01f,
                    red, green, blue);
            fx.setAlphaF(0.4F);
            fx.setScale(scale);
            ParticleEngine.addEffect(world, fx);
        }
    }

    @Override
    public void sparkle(float x, float y, float z, float scale, int type, float speed) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc == null ? null : mc.world;
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(1, (int) (scale * 4.0f)));
        if (amount <= 0) return;
        for (int i = 0; i < amount; i++) {
            ParticleEngine.addEffect(world, new FXSparkle(world, x, y, z, scale, type, speed));
        }
    }

    @Override
    public void sparkle(float x, float y, float z, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc == null ? null : mc.world;
        if (world == null || !world.isRemote) return;
        int amount = particleCount(2);
        if (amount <= 0 || world.rand.nextInt(6) >= amount) return;
        FXSparkle fx = new FXSparkle(world, x, y, z, 1.5F, color, 6.0F);
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void smokeSpiral(World world, double x, double y, double z, float radius, int start, int miny, int color) {
        if (world == null || !world.isRemote) return;
        FXSmokeSpiral fx = new FXSmokeSpiral(world, x, y, z, radius, start, miny);
        Color tint = new Color(color);
        fx.setRBGColorF(normalizeColor(tint.getRed()), normalizeColor(tint.getGreen()), normalizeColor(tint.getBlue()));
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public int particleCount(int base) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) {
            return Math.max(1, base);
        }
        int setting = mc.gameSettings.particleSetting;
        if (setting >= 2) {
            return 0;
        }
        if (setting == 1) {
            return Math.max(1, base);
        }
        return Math.max(1, base * 2);
    }

    @Override
    public void crucibleFroth(World world, float x, float y, float z) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            FXBubble bubble = new FXBubble(
                    world,
                    x,
                    y,
                    z,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    0.02f + world.rand.nextFloat() * 0.01f,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    8);
            bubble.setFroth();
            ParticleEngine.addEffect(world, bubble);
            if (world.rand.nextInt(4) == 0) {
                ParticleEngine.addEffect(world, new FXSmokeDrift(
                        world,
                        x,
                        y + 0.03f,
                        z,
                        (world.rand.nextFloat() - 0.5f) * 0.002f,
                        0.004f + world.rand.nextFloat() * 0.003f,
                        (world.rand.nextFloat() - 0.5f) * 0.002f,
                        7));
            }
        }
    }

    @Override
    public void crucibleFrothDown(World world, float x, float y, float z) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            FXBubble bubble = new FXBubble(
                    world,
                    x,
                    y,
                    z,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    -0.02f - world.rand.nextFloat() * 0.02f,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    14);
            bubble.setFroth2();
            ParticleEngine.addEffect(world, bubble);
        }
    }

    @Override
    public void crucibleBubble(World world, float x, float y, float z, float red, float green, float blue) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            FXBubbleAlt bubble = new FXBubbleAlt(
                    world,
                    x,
                    y,
                    z,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    0.015f + world.rand.nextFloat() * 0.01f,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    10);
            bubble.setRGB(red, green, blue);
            ParticleEngine.addEffect(world, bubble);
        }
    }

    @Override
    public void crucibleBoilSound(World world, int x, int y, int z) {
        if (world == null) return;
        world.playSound(
                x + 0.5, y + 0.5, z + 0.5,
                TCSounds.BUBBLE,
                SoundCategory.BLOCKS,
                0.2f,
                1.0f + world.rand.nextFloat() * 0.4f,
                false
        );
    }

    @Override
    public void crucibleBoil(World world, int x, int y, int z, TileCrucible crucible, int type) {
        if (world == null || !world.isRemote || crucible == null) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            FXBubble bubble = new FXBubble(
                    world,
                    x + 0.2f + world.rand.nextFloat() * 0.6f,
                    y + 0.1f + crucible.getFluidHeight(),
                    z + 0.2f + world.rand.nextFloat() * 0.6f,
                    0.0,
                    0.0,
                    0.0,
                    3);
            if (crucible.aspects == null || crucible.aspects.size() <= 0) {
                bubble.setRGB(1.0f, 1.0f, 1.0f);
            } else {
                Aspect[] aspects = crucible.aspects.getAspects();
                if (aspects != null && aspects.length > 0) {
                    Color tint = new Color(aspects[world.rand.nextInt(aspects.length)].getColor());
                    bubble.setRGB(normalizeColor(tint.getRed()), normalizeColor(tint.getGreen()), normalizeColor(tint.getBlue()));
                }
            }
            bubble.setBubbleSpeed(0.003D * type);
            ParticleEngine.addEffect(world, bubble);
        }
    }

    @Override
    public void startScan(Entity entity, BlockPos pos, long expireAtMs, int radius) {
        RenderEventHandler.startScan(entity, pos, expireAtMs, radius);
    }

    private static Color decodeColor(int color) {
        if (color < 0 || color > 0xFFFFFF) {
            return new Color(0xCCCCFF);
        }
        return new Color(color);
    }

    private static float normalizeColor(int channel) {
        float c = channel / 255.0f;
        return c <= 0.01f ? 0.02f : c;
    }

    private static int clampColor(float value) {
        if (value <= 0.0F) return 0;
        if (value >= 1.0F) return 255;
        return (int) (value * 255.0F);
    }
}
