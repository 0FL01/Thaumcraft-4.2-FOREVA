package thaumcraft.client;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.IReloadableResourceManager;
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
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
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
import thaumcraft.client.fx.particles.FXEssentiaTrail;
import thaumcraft.client.fx.particles.FXGeneric;
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
import thaumcraft.client.renderers.item.ItemEssentiaReservoirRenderer;
import thaumcraft.client.renderers.item.ItemJarRenderer;
import thaumcraft.client.renderers.item.LifterItemColor;
import thaumcraft.client.renderers.item.ItemTrunkSpawnerRenderer;
import thaumcraft.client.renderers.item.ItemMetalDeviceRenderer;
import thaumcraft.client.renderers.item.ItemNodeRenderer;
import thaumcraft.client.renderers.item.ItemThaumometerRenderer;
import thaumcraft.client.renderers.item.ItemCrystalRenderer;
import thaumcraft.client.renderers.item.ItemStoneDeviceRenderer;
import thaumcraft.client.renderers.item.ItemTableRenderer;
import thaumcraft.client.renderers.item.ItemTubeRenderer;
import thaumcraft.client.renderers.item.ItemWandRenderer;
import thaumcraft.client.renderers.item.ItemWoodenDeviceRenderer;
import thaumcraft.client.renderers.item.WandRenderCalibration;
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
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.client.lib.RenderEventHandler;
import thaumcraft.client.lib.ItemAspectTooltipHandler;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.blocks.BlockCandle;
import thaumcraft.common.blocks.BlockMagicalLeaves;
import thaumcraft.common.blocks.BlockTaint;
import thaumcraft.common.blocks.BlockTaintFibres;
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
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvanced;
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
        registerItemColorHandlers();
        setupTileRenderers();
    }

    @Override
    public void registerEntityRenders() {
        setupEntityRenderers();
    }

    @Override
    public void registerModelLocations() {
        setupItemRenderers();
        setupBlockRenderers();
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
            if (item == ConfigItems.itemEssence) {
                ModelResourceLocation emptyPhialModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemessence_empty"), "inventory");
                ModelResourceLocation filledPhialModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemessence"), "inventory");
                ModelLoader.setCustomModelResourceLocation(item, 0, emptyPhialModel);
                for (int meta = 1; meta < 64; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, filledPhialModel);
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
            if (item == ConfigItems.itemRingRunic) {
                ModelResourceLocation lesserModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemringrunic_lesser"), "inventory");
                ModelResourceLocation normalModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemringrunic"), "inventory");
                ModelResourceLocation chargedModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemringrunic_charged"), "inventory");
                ModelResourceLocation regenModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemringrunic_regen"), "inventory");
                ModelLoader.setCustomModelResourceLocation(item, 0, lesserModel);
                ModelLoader.setCustomModelResourceLocation(item, 1, normalModel);
                ModelLoader.setCustomModelResourceLocation(item, 2, chargedModel);
                ModelLoader.setCustomModelResourceLocation(item, 3, regenModel);
                for (int meta = 4; meta < 64; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, normalModel);
                }
                continue;
            }
            if (item == ConfigItems.itemAmuletRunic) {
                ModelResourceLocation normalModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemamuletrunic"), "inventory");
                ModelResourceLocation emergencyModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemamuletrunic_emergency"), "inventory");
                ModelLoader.setCustomModelResourceLocation(item, 0, normalModel);
                ModelLoader.setCustomModelResourceLocation(item, 1, emergencyModel);
                for (int meta = 2; meta < 64; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, normalModel);
                }
                continue;
            }
            if (item == ConfigItems.itemGirdleRunic) {
                ModelResourceLocation normalModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemgirdlerunic"), "inventory");
                ModelResourceLocation kineticModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemgirdlerunic_kinetic"), "inventory");
                ModelLoader.setCustomModelResourceLocation(item, 0, normalModel);
                ModelLoader.setCustomModelResourceLocation(item, 1, kineticModel);
                for (int meta = 2; meta < 64; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta, normalModel);
                }
                continue;
            }
            if (item == ConfigItems.itemNugget) {
                String[] nuggetModels = {
                    "itemnugget_nuggetiron", "itemnugget_nuggetcopper", "itemnugget_nuggettin", "itemnugget_nuggetsilver", "itemnugget_nuggetlead",
                    "itemnugget_nuggetquicksilver", "itemnugget_nuggetthaumium", "itemnugget_nuggetvoid",
                    "", "", "", "", "", "", "", "",
                    "itemnugget_clusteriron", "itemnugget_clustercopper", "itemnugget_clustertin", "itemnugget_clustersilver", "itemnugget_clusterlead",
                    "itemnugget_clustercinnabar", "", "", "", "", "", "", "", "", "",
                    "itemnugget_clustergold"
                };
                ModelResourceLocation fallback = new ModelResourceLocation(registryName, "inventory");
                for (int meta = 0; meta < 64; meta++) {
                    if (meta < nuggetModels.length && !nuggetModels[meta].isEmpty()) {
                        ModelLoader.setCustomModelResourceLocation(item, meta,
                                new ModelResourceLocation(new ResourceLocation("thaumcraft", nuggetModels[meta]), "inventory"));
                    } else {
                        ModelLoader.setCustomModelResourceLocation(item, meta, fallback);
                    }
                }
                continue;
            }
            if (item == ConfigItems.itemEldritchObject) {
                String[] eldritchModels = {
                    "itemeldritchobject_eldritch_object", "itemeldritchobject_crimson_rites",
                    "itemeldritchobject_eldritch_object_2", "itemeldritchobject_eldritch_object_3",
                    "itemeldritchobject_ob_placer"
                };
                for (int meta = 0; meta < eldritchModels.length; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", eldritchModels[meta]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                for (int meta = eldritchModels.length; meta < 64; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", eldritchModels[0]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                continue;
            }
            if (item == ConfigItems.itemGolemPlacer) {
                String[] placerModels = {
                    "itemgolemplacer_straw", "itemgolemplacer_wood", "itemgolemplacer_tallow",
                    "itemgolemplacer_clay", "itemgolemplacer_flesh", "itemgolemplacer_stone",
                    "itemgolemplacer_iron", "itemgolemplacer_thaumium"
                };
                for (int meta = 0; meta < placerModels.length; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", placerModels[meta]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                for (int meta = placerModels.length; meta < 64; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", placerModels[0]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                continue;
            }
            if (item == ConfigItems.itemGolemCore) {
                ModelResourceLocation coreFallback = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "itemgolemcore_blank"), "inventory");
                String[] coreModels = new String[101];
                coreModels[0] = "itemgolemcore_fill";
                coreModels[1] = "itemgolemcore_empty";
                coreModels[2] = "itemgolemcore_gather";
                coreModels[3] = "itemgolemcore_harvest";
                coreModels[4] = "itemgolemcore_guard";
                coreModels[5] = "itemgolemcore_decanting";
                coreModels[6] = "itemgolemcore_alchemy";
                coreModels[7] = "itemgolemcore_chop";
                coreModels[8] = "itemgolemcore_use";
                coreModels[9] = "itemgolemcore_butcher";
                coreModels[10] = "itemgolemcore_sorting";
                coreModels[11] = "itemgolemcore_fishing";
                coreModels[100] = "itemgolemcore_blank";
                for (int meta = 0; meta < 64; meta++) {
                    if (coreModels[meta] != null) {
                        ModelLoader.setCustomModelResourceLocation(item, meta,
                                new ModelResourceLocation(new ResourceLocation("thaumcraft", coreModels[meta]), "inventory"));
                    } else {
                        ModelLoader.setCustomModelResourceLocation(item, meta, coreFallback);
                    }
                }
                ModelLoader.setCustomModelResourceLocation(item, 100,
                        new ModelResourceLocation(new ResourceLocation("thaumcraft", "itemgolemcore_blank"), "inventory"));
                continue;
            }
            if (item == ConfigItems.itemGolemUpgrade) {
                String[] upgradeModels = {
                    "itemgolemupgrade_air", "itemgolemupgrade_earth", "itemgolemupgrade_fire",
                    "itemgolemupgrade_water", "itemgolemupgrade_order", "itemgolemupgrade_entropy"
                };
                for (int meta = 0; meta < upgradeModels.length; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", upgradeModels[meta]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                for (int meta = upgradeModels.length; meta < 64; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", upgradeModels[0]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                continue;
            }
            if (item == ConfigItems.itemGolemDecoration) {
                String[] decoModels = {
                    "itemgolemdecoration_tophat", "itemgolemdecoration_spectacles", "itemgolemdecoration_bowtie",
                    "itemgolemdecoration_fez", "itemgolemdecoration_dart", "itemgolemdecoration_visor",
                    "itemgolemdecoration_armor", "itemgolemdecoration_mace"
                };
                for (int meta = 0; meta < decoModels.length; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", decoModels[meta]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                for (int meta = decoModels.length; meta < 64; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", decoModels[0]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                continue;
            }
            if (item == ConfigItems.itemResource) {
                String[] resourceModels = {
                    "itemresource_alumentum", "itemresource_nitor", "itemresource_thaumiumingot",
                    "itemresource_quicksilver", "itemresource_tallow", "itemresource_brain",
                    "itemresource_amber", "itemresource_cloth", "itemresource_filter",
                    "itemresource_knowledgefragment", "itemresource_mirrorglass", "itemresource_taint_slime",
                    "itemresource_taint_tendril", "itemresource_label", "itemresource_dust",
                    "itemresource_charm", "itemresource_voidingot", "itemresource_voidseed", "itemresource_coin"
                };
                for (int meta = 0; meta < resourceModels.length; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", resourceModels[meta]), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                for (int meta = resourceModels.length; meta < 64; meta++) {
                    ModelResourceLocation model = new ModelResourceLocation(
                            new ResourceLocation("thaumcraft", "itemresource_alumentum"), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, meta, model);
                }
                continue;
            }
            if (item == ConfigItems.itemWandCasting) {
                final ModelResourceLocation wandModel = new ModelResourceLocation(
                        new ResourceLocation("thaumcraft", "wandcasting_tesr"), "inventory");
                ModelLoader.setCustomMeshDefinition(item, stack -> wandModel);
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
            if (item == ConfigItems.itemThaumometer) {
                for (int meta = 0; meta < 64; meta++) {
                    registerBuiltinItemModel(item, meta, "itemthaumometer_tesr");
                }
                continue;
            }
            if (item == ConfigItems.itemWandRod) {
                String[] wandRods = {"greatwood","obsidian","silverwood","ice","quartz","reed","blaze","bone"};
                for (int meta = 0; meta < wandRods.length; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta,
                            new ModelResourceLocation(new ResourceLocation("thaumcraft", "wandrod_rod_" + wandRods[meta]), "inventory"));
                }
                String[] staffRods = {"greatwood","obsidian","silverwood","ice","quartz","reed","blaze","bone"};
                for (int meta = 0; meta < staffRods.length; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta + 50,
                            new ModelResourceLocation(new ResourceLocation("thaumcraft", "wandrod_staff_" + staffRods[meta]), "inventory"));
                }
                ModelLoader.setCustomModelResourceLocation(item, 100,
                        new ModelResourceLocation(new ResourceLocation("thaumcraft", "wandrod_staff_primal"), "inventory"));
                continue;
            }
            if (item == ConfigItems.itemWandCap) {
                String[] capModels = {
                    "wandcap_iron", "wandcap_gold", "wandcap_thaumium", "wandcap_copper",
                    "wandcap_silver", "wandcap_silver_inert", "wandcap_thaumium_inert",
                    "wandcap_void", "wandcap_void_inert"
                };
                for (int meta = 0; meta < capModels.length; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta,
                            new ModelResourceLocation(new ResourceLocation("thaumcraft", capModels[meta]), "inventory"));
                }
                continue;
            }
            if (item == ConfigItems.itemShard) {
                String[] shardModels = {
                    "", "", "", "", "", "", "itemshard_balanced"
                };
                ModelResourceLocation fallback = new ModelResourceLocation(registryName, "inventory");
                for (int meta = 0; meta < 64; meta++) {
                    if (meta < shardModels.length && !shardModels[meta].isEmpty()) {
                        ModelLoader.setCustomModelResourceLocation(item, meta,
                                new ModelResourceLocation(
                                        new ResourceLocation("thaumcraft", shardModels[meta]), "inventory"));
                    } else {
                        ModelLoader.setCustomModelResourceLocation(item, meta, fallback);
                    }
                }
                continue;
            }
            if (item == ConfigItems.itemPrimalArrow) {
                String[] arrowModels = {
                    "primalarrow_0", "primalarrow_1", "primalarrow_2",
                    "primalarrow_3", "primalarrow_4", "primalarrow_5"
                };
                for (int meta = 0; meta < arrowModels.length; meta++) {
                    ModelLoader.setCustomModelResourceLocation(item, meta,
                            new ModelResourceLocation(new ResourceLocation("thaumcraft", arrowModels[meta]), "inventory"));
                }
                continue;
            }
            ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
            for (int meta = 0; meta < 64; meta++) {
                ModelLoader.setCustomModelResourceLocation(item, meta, model);
            }
        }
    }

    private void registerItemColorHandlers() {
        Minecraft minecraft = Minecraft.getMinecraft();
        final int taintGrassColor = 0x6D4189;
        if (ConfigBlocks.blockMagicalLeaves != null && ConfigBlocks.blockMagicalLeavesItem != null) {
            minecraft.getBlockColors().registerBlockColorHandler(
                    (state, world, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        if (state.getValue(BlockMagicalLeaves.TYPE) == 1) {
                            return -1;
                        }
                        return world != null && pos != null
                                ? BiomeColorHelper.getFoliageColorAtPos(world, pos)
                                : ColorizerFoliage.getFoliageColorBasic();
                    },
                    ConfigBlocks.blockMagicalLeaves
            );
            minecraft.getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> tintIndex == 0
                            ? minecraft.getBlockColors().colorMultiplier(
                                    ConfigBlocks.blockMagicalLeaves.getStateFromMeta(stack.getMetadata()),
                                    null,
                                    null,
                                    tintIndex
                            )
                            : -1,
                    ConfigBlocks.blockMagicalLeavesItem
            );
        }
        if (ConfigBlocks.blockTaint != null) {
            minecraft.getBlockColors().registerBlockColorHandler(
                    (state, world, pos, tintIndex) -> {
                        if (tintIndex != 0 || state.getValue(BlockTaint.TYPE) != 1) {
                            return -1;
                        }
                        return world != null && pos != null
                                ? BiomeColorHelper.getGrassColorAtPos(world, pos)
                                : taintGrassColor;
                    },
                    ConfigBlocks.blockTaint
            );
            Item item = Item.getItemFromBlock(ConfigBlocks.blockTaint);
            if (item != Items.AIR) {
                minecraft.getItemColors().registerItemColorHandler(
                        (stack, tintIndex) -> tintIndex == 0 && stack.getMetadata() == 1 ? taintGrassColor : -1,
                        item
                );
            }
        }
        if (ConfigBlocks.blockTaintFibres != null) {
            minecraft.getBlockColors().registerBlockColorHandler(
                    (state, world, pos, tintIndex) -> tintIndex == 0
                            ? (world != null && pos != null
                                    ? BiomeColorHelper.getGrassColorAtPos(world, pos)
                                    : taintGrassColor)
                            : -1,
                    ConfigBlocks.blockTaintFibres
            );
            Item item = Item.getItemFromBlock(ConfigBlocks.blockTaintFibres);
            if (item != Items.AIR) {
                minecraft.getItemColors().registerItemColorHandler(
                        (stack, tintIndex) -> tintIndex == 0 ? taintGrassColor : -1,
                        item
                );
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
        if (ConfigItems.itemShard != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemShard.getColorFromItemStack(stack, tintIndex),
                    ConfigItems.itemShard
            );
        }
        if (ConfigBlocks.blockLifter != null) {
            Item item = Item.getItemFromBlock(ConfigBlocks.blockLifter);
            if (item != Items.AIR) {
                minecraft.getItemColors().registerItemColorHandler(new LifterItemColor(), item);
            }
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
        Item tableItem = Item.getItemFromBlock(ConfigBlocks.blockTable);
        if (tableItem != null) {
            tableItem.setTileEntityItemStackRenderer(new ItemTableRenderer());
        }
        Item reservoirItem = Item.getItemFromBlock(ConfigBlocks.blockEssentiaReservoir);
        if (reservoirItem != null) {
            reservoirItem.setTileEntityItemStackRenderer(new ItemEssentiaReservoirRenderer());
        }
        if (ConfigItems.itemWandCasting != null) {
            ConfigItems.itemWandCasting.setTileEntityItemStackRenderer(new ItemWandRenderer());
        }
        if (ConfigItems.itemTrunkSpawner != null) {
            ConfigItems.itemTrunkSpawner.setTileEntityItemStackRenderer(new ItemTrunkSpawnerRenderer());
        }
        if (ConfigItems.itemThaumometer != null) {
            ConfigItems.itemThaumometer.setTileEntityItemStackRenderer(new ItemThaumometerRenderer());
        }
    }

    private void setupEntityRenderers() {
        // Note: called from preInit. Minecraft.getMinecraft().getRenderItem() is NOT available yet.
        // RenderItem-dependent factories (RenderSnowball) retrieve it lazily at factory execution time.
        Set<Class<? extends Entity>> registered = new HashSet<>();

        registerEntityRenderer(EntitySpecialItem.class, RenderSpecialItem::new, registered);
        registerEntityRenderer(EntityPermanentItem.class, RenderSpecialItem::new, registered);
        registerEntityRenderer(EntityFollowingItem.class, RenderFollowingItem::new, registered);
        registerEntityRenderer(EntityItemGrate.class, RenderSpecialItem::new, registered);

        registerEntityRenderer(EntityDart.class, RenderDart::new, registered);
        registerEntityRenderer(EntityPrimalArrow.class, RenderPrimalArrow::new, registered);
        registerEntityRenderer(EntityBottleTaint.class,
                manager -> new RenderSnowball<>(manager, itemOrFallback(ConfigItems.itemBottleTaint, Items.SPLASH_POTION),
                        Minecraft.getMinecraft().getRenderItem()), registered);
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
        Item customPlantItem = Item.getItemFromBlock(ConfigBlocks.blockCustomPlant);
        String[] plantItemModels = {
            "blockcustomplant_item_0", "blockcustomplant_item_1", "blockcustomplant_item_2",
            "blockcustomplant_item_3", "blockcustomplant_item_4", "blockcustomplant_item_5"
        };
        for (int meta = 0; meta <= 5; meta++) {
            registerBuiltinItemModel(customPlantItem, meta, plantItemModels[meta]);
        }
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
        registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockEldritchPortal), 0, "blockportaleldritch");
        Item stoneDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockStoneDevice);
        for (int meta = 0; meta <= 14; meta++) {
            registerBlockItemModel(stoneDeviceItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(stoneDeviceItem, 2, "blockstonedevice_2_inventory");
        registerBuiltinItemModel(stoneDeviceItem, 9, "blockstonedevice_tesr");
        registerBuiltinItemModel(stoneDeviceItem, 10, "blockstonedevice_tesr");
        registerBuiltinItemModel(stoneDeviceItem, 11, "blockstonedevice_tesr");
        registerBuiltinItemModel(stoneDeviceItem, 13, "blockstonedevice_13_inventory");
        registerBuiltinItemModel(stoneDeviceItem, 14, "blockstonedevice_tesr");
        Item metalDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockMetalDevice);
        for (int meta = 0; meta <= 14; meta++) {
            registerBlockItemModel(metalDeviceItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(metalDeviceItem, 0, "blockmetaldevice_0_inventory");
        registerBuiltinItemModel(metalDeviceItem, 5, "blockmetaldevice_5_inventory");
        registerBuiltinItemModel(metalDeviceItem, 6, "blockmetaldevice_5_inventory");
        registerBuiltinItemModel(metalDeviceItem, 7, "blockmetaldevice_7_inventory");
        registerBuiltinItemModel(metalDeviceItem, 8, "blockmetaldevice_8_inventory");
        registerBuiltinItemModel(metalDeviceItem, 13, "blockmetaldevice_13_inventory");
        Item woodenDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockWoodenDevice);
        for (int meta = 0; meta <= 8; meta++) {
            registerBlockItemModel(woodenDeviceItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(woodenDeviceItem, 0, "blockwoodendevice_bellows_tesr");
        registerBuiltinItemModel(woodenDeviceItem, 4, "blockwoodendevice_tesr");
        registerBuiltinItemModel(woodenDeviceItem, 5, "blockwoodendevice_tesr");
        registerBuiltinItemModel(woodenDeviceItem, 8, "blockwoodendevice_banner_tesr");
        registerBuiltinItemModel(metalDeviceItem, 1, "blockmetaldevice_dynamic_tesr");
        registerBuiltinItemModel(metalDeviceItem, 2, "blockmetaldevice_dynamic_tesr");
        registerBuiltinItemModel(metalDeviceItem, 10, "blockmetaldevice_tesr");
        registerBuiltinItemModel(metalDeviceItem, 11, "blockmetaldevice_tesr");
        registerBuiltinItemModel(metalDeviceItem, 14, "blockmetaldevice_dynamic_tesr");
        Item tubeItem = Item.getItemFromBlock(ConfigBlocks.blockTube);
        for (int meta = 0; meta <= 7; meta++) {
            registerBlockItemModel(tubeItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(tubeItem, 1, "blocktube_tesr");
        registerBuiltinItemModel(tubeItem, 2, "blocktube_2_inventory");
        registerBuiltinItemModel(tubeItem, 7, "blocktube_tesr");
        Item tableItem = Item.getItemFromBlock(ConfigBlocks.blockTable);
        for (int meta = 0; meta <= 15; meta++) {
            registerBlockItemModel(tableItem, meta, "type=" + meta);
        }
        registerBuiltinItemModel(tableItem, 0, "blocktable_0_inventory");
        registerBuiltinItemModel(tableItem, 1, "blocktable_tesr");
        registerBuiltinItemModel(tableItem, 14, "blocktable_14_inventory");
        registerBuiltinItemModel(tableItem, 15, "blocktable_tesr");
        Item mirrorItem = Item.getItemFromBlock(ConfigBlocks.blockMirror);
        for (int meta = 0; meta <= 5; meta++) {
            registerBuiltinItemModel(mirrorItem, meta, meta == 1 ? "blockmirror_open" : "blockmirror");
        }
        for (int meta = 6; meta <= 11; meta++) {
            registerBuiltinItemModel(mirrorItem, meta,
                    meta == 7 ? "blockmirror_essentia_open" : "blockmirror_essentia");
        }
        Item arcaneFurnaceItem = Item.getItemFromBlock(ConfigBlocks.blockArcaneFurnace);
        for (int meta = 0; meta <= 10; meta++) {
            registerBlockItemModel(arcaneFurnaceItem, meta, "type=" + meta + ",facing=north");
        }
        registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockEssentiaReservoir), 0, "blockessentiareservoir_tesr");
        Item candleItem = Item.getItemFromBlock(ConfigBlocks.blockCandle);
        for (int meta = 0; meta < 16; meta++) {
            registerBlockItemModel(candleItem, meta, "type=" + meta);
        }
        Item oreItem = Item.getItemFromBlock(ConfigBlocks.blockCustomOre);
        for (int meta = 0; meta <= 7; meta++) {
            registerBlockItemModel(oreItem, meta, "type=" + meta);
        }
        Item cosmeticSolidItem = Item.getItemFromBlock(ConfigBlocks.blockCosmeticSolid);
        for (int meta = 0; meta <= 15; meta++) {
            registerBlockItemModel(cosmeticSolidItem, meta, "type=" + meta);
        }
        Item magicalLogItem = Item.getItemFromBlock(ConfigBlocks.blockMagicalLog);
        registerBlockItemModel(magicalLogItem, 0, "axis=y,type=0");
        registerBlockItemModel(magicalLogItem, 1, "axis=y,type=1");
        Item cosmeticOpaqueItem = Item.getItemFromBlock(ConfigBlocks.blockCosmeticOpaque);
        for (int meta = 0; meta <= 4; meta++) {
            registerBlockItemModel(cosmeticOpaqueItem, meta, "type=" + meta);
        }
        Item jarItem2 = Item.getItemFromBlock(ConfigBlocks.blockJar);
        for (int meta = 0; meta <= 3; meta++) {
            registerBuiltinItemModel(jarItem2, meta, "blockjar");
        }
        Item slabWoodItem = Item.getItemFromBlock(ConfigBlocks.blockSlabWood);
        registerBlockItemModel(slabWoodItem, 0, "half=bottom,variant=greatwood");
        registerBlockItemModel(slabWoodItem, 1, "half=bottom,variant=silverwood");
        Item slabStoneItem = Item.getItemFromBlock(ConfigBlocks.blockSlabStone);
        registerBlockItemModel(slabStoneItem, 0, "half=bottom,variant=arcane");
        registerBlockItemModel(slabStoneItem, 1, "half=bottom,variant=eldritch");
        registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsArcaneStone), 0,
                "blockstairsarcanestone");
        registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsGreatwood), 0,
                "blockstairsgreatwood");
        registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsSilverwood), 0,
                "blockstairssilverwood");
        registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsEldritch), 0,
                "blockstairseldritch");
        Item taintItem = Item.getItemFromBlock(ConfigBlocks.blockTaint);
        for (int meta = 0; meta <= 2; meta++) {
            registerBlockItemModel(taintItem, meta, "type=" + meta);
        }
        String[] taintFibreItemModels = {
            "blocktaintfibres_item_0", "blocktaintfibres_item_1",
            "blocktaintfibres_item_2", "blocktaintfibres_item_3",
            "blocktaintfibres_item_4"
        };
        Item taintFibresItem = Item.getItemFromBlock(ConfigBlocks.blockTaintFibres);
        for (int meta = 0; meta <= 4; meta++) {
            registerBuiltinItemModel(taintFibresItem, meta, taintFibreItemModels[meta]);
        }
        Item lifterItem = Item.getItemFromBlock(ConfigBlocks.blockLifter);
        registerBuiltinItemModel(lifterItem, 0, "blocklifter");
        Item lootCrateItem = Item.getItemFromBlock(ConfigBlocks.blockLootCrate);
        for (int meta = 0; meta <= 2; meta++) {
            registerBlockItemModel(lootCrateItem, meta, "type=" + meta);
        }
        Item manaPodItem = Item.getItemFromBlock(ConfigBlocks.blockManaPod);
        registerBuiltinItemModel(manaPodItem, 0, "blockmanapod");
        Item chestItem = Item.getItemFromBlock(ConfigBlocks.blockChestHungry);
        registerBuiltinItemModel(chestItem, 0, "blockchesthungry");
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

        ClientRegistry.bindTileEntitySpecialRenderer(TileAlchemyFurnaceAdvanced.class, new TileAlchemyFurnaceAdvancedRenderer());
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
    public boolean isShiftKeyDown() {
        return GuiScreen.isShiftKeyDown();
    }

    @Override
    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(new ClientTickEventsFML());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(ParticleEngine.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ItemAspectTooltipHandler());

        // Hot reload for wand/staff/sceptre render calibration (edit JSON, then F3+T).
        if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
                    .registerReloadListener(WandRenderCalibration::onResourceManagerReload);
        }
    }

    @Override
    public void scheduleClientTask(Runnable task) {
        if (task != null) {
            Minecraft.getMinecraft().addScheduledTask(task);
        }
    }

    @Override
    public void notifyThaumometerUnknownObject() {
        PlayerNotifications.addNotification(localizeOrFallback("tc.unknownobject", "Nothing can be learned from this."));
    }

    @Override
    public void notifyThaumometerDiscoveryError(@Nullable Aspect missingAspect) {
        String missing = missingAspect == null ? "?" : formatThaumometerAspectDescription(missingAspect);
        String text = localizeOrFallback("tc.discoveryerror", "To understand this you need to study %1$s.")
                .replace("%1$s", missing);
        PlayerNotifications.addNotification(text);
    }

    @Override
    public void notifyThaumometerAspectDiscovery(@Nullable Aspect aspect) {
        if (aspect == null) {
            return;
        }
        String text = localizeOrFallback("tc.addaspectdiscovery", "You have discovered %n!")
                .replace("%n", formatThaumometerAspectLabel(aspect));
        PlayerNotifications.addNotification("\u00a76" + text, aspect);
    }

    @Override
    public void notifyThaumometerAspectPool(@Nullable Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return;
        }
        String text = localizeOrFallback("tc.addaspectpool", "Gained %s research point(s) for %n")
                .replace("%s", Integer.toString(amount))
                .replace("%n", formatThaumometerAspectLabel(aspect));
        PlayerNotifications.addNotification(text, aspect);
        for (int a = 0; a < amount; ++a) {
            PlayerNotifications.addAspectNotification(aspect);
        }
    }

    private static String localizeOrFallback(String key, String fallback) {
        String localized = net.minecraft.client.resources.I18n.format(key);
        return key.equals(localized) ? fallback : localized;
    }

    private static String formatThaumometerAspectLabel(Aspect aspect) {
        return aspect.getName() + " (" + formatThaumometerAspectDescription(aspect) + ")";
    }

    private static String formatThaumometerAspectDescription(Aspect aspect) {
        String description = aspect.getLocalizedDescription();
        if (description == null || description.isEmpty()
                || description.equals("tc.aspect." + aspect.getTag())
                || description.equals("tc.aspect.help." + aspect.getTag())) {
            description = net.minecraft.client.resources.I18n.format("tc.aspect.help." + aspect.getTag());
        }
        if (description == null || description.isEmpty()
                || description.equals("tc.aspect." + aspect.getTag())
                || description.equals("tc.aspect.help." + aspect.getTag())) {
            return aspect.getName();
        }
        return description;
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
    public void refreshWardedBlockRender(World world, BlockPos pos) {
        if (world == null || !world.isRemote || pos == null) return;
        TileWardedRenderer.invalidate(world, pos);
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
        beam.setBlendMode(GL11.GL_ONE_MINUS_SRC_ALPHA);
        beam.setBeamWidth(2.5F);
        beam.setType(1);
        beam.setReverse(true);
        beam.setPulse(true);
        ParticleEngine.addEffect(world, beam);
    }

    @Override
    public void beamPulseGolemBossFX(World world, EntityLivingBase source, Entity target) {
        if (world == null || !world.isRemote || source == null || target == null) return;

        FXBeamGolemBoss beamA = new FXBeamGolemBoss(world, source, target, 0.07F, 0.376F, 0.325F, 20);
        beamA.setBlendMode(GL11.GL_ONE);
        beamA.setBeamWidth(3.0F);
        beamA.setType(2);
        beamA.setReverse(false);
        beamA.setPulse(true);
        ParticleEngine.addEffect(world, beamA);

        FXBeamGolemBoss beamB = new FXBeamGolemBoss(world, source, target, 1.0F, 0.5F, 0.5F, 20);
        beamB.setBlendMode(GL11.GL_ONE);
        beamB.setBeamWidth(1.5F);
        beamB.setType(1);
        beamB.setReverse(false);
        beamB.setPulse(true);
        ParticleEngine.addEffect(world, beamB);
    }

    @Override
    public void excavateFX(World world, BlockPos pos, EntityPlayer player, int progress) {
        if (world == null || !world.isRemote || pos == null || player == null) return;
        Minecraft.getMinecraft().renderGlobal.sendBlockBreakProgress(player.getEntityId(), pos, progress);
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
            ParticleEngine.addEffect(world, beam);
        } else {
            beam.updateBeam(tx, ty, tz);
            beam.setEndMod(endmod);
            beam.impact = impact;
        }
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
            ParticleEngine.addEffect(world, beam);
        } else {
            beam.updateBeam(tx, ty, tz);
            beam.setEndMod(endmod);
            beam.impact = impact;
        }
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
        boolean localPlayer = clientPlayer != null && source.getEntityId() == clientPlayer.getEntityId();
        double sx = source.posX;
        double sy = source.posY;
        double sz = source.posZ;
        if (localPlayer) {
            // In 1.7.10 player posY included the eye-level yOffset; in 1.12 it is feet-level.
            sy += source.getEyeHeight();
        } else {
            sy = source.getEntityBoundingBox().minY + source.height * 0.5D + 0.25D;
        }
        sx += -MathHelper.cos((float) (source.rotationYaw / 180.0F * Math.PI)) * 0.06F;
        sy -= 0.06D;
        sz += -MathHelper.sin((float) (source.rotationYaw / 180.0F * Math.PI)) * 0.06F;
        if (!localPlayer) {
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
    public void wispFX(World world, double x, double y, double z, float size, float red, float green, float blue) {
        if (world == null || !world.isRemote) return;
        FXWisp fx = new FXWisp(world, x, y, z, size, red, green, blue);
        fx.setGravity(0.02F);
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void wispFX3(World world, double x, double y, double z, double tx, double ty, double tz, float size, int count, boolean flag, float speed) {
        if (world == null || !world.isRemote) return;
        FXWisp fx = new FXWisp(world, x, y, z, tx, ty, tz, size, count);
        fx.setGravity(speed);
        fx.shrink = flag;
        ParticleEngine.addEffect(world, fx);
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
        float angle = world.rand.nextFloat() * ((float) Math.PI * 2.0F);
        float radius = world.rand.nextFloat() * 0.5F + 0.5F;
        float offsetX = MathHelper.sin(angle) * radius;
        float offsetZ = MathHelper.cos(angle) * radius;
        double y = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) * 0.5D;

        FXBreaking fx = new FXBreaking(world, entity.posX + offsetX, y, entity.posZ + offsetZ, Items.SLIME_BALL);
        fx.setRBGColorF(0.1F, 0.0F, 0.1F);
        fx.setAlphaF(0.4F);
        fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
        ParticleEngine.addEffect(world, fx);
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
        FXSwarm swarm = new FXSwarm(
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
        return swarm;
    }

    @Override
    public boolean isParticleAlive(Object particle) {
        return particle instanceof Particle && ((Particle) particle).isAlive();
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
            motionX = motionX / length * speed * 0.9640000000596046D;
            motionY = motionY / length * speed * 0.9640000000596046D + 0.10000000149011612D;
            motionZ = motionZ / length * speed * 0.9640000000596046D;
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
    public void tentacleAriseFX(Entity entity) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        BlockPos blockPos = new BlockPos(
                MathHelper.floor(entity.posX),
                MathHelper.floor(entity.posY) - 1,
                MathHelper.floor(entity.posZ));
        IBlockState state = world.getBlockState(blockPos);

        for (int i = 0; (float) i < 2.0F * entity.height; i++) {
            float angle = world.rand.nextFloat() * (float) Math.PI * entity.height;
            float radius = world.rand.nextFloat() * 0.5F + 0.5F;
            float offsetX = MathHelper.sin(angle) * entity.height * 0.25F * radius;
            float offsetZ = MathHelper.cos(angle) * entity.height * 0.25F * radius;

            FXBreaking fx = new FXBreaking(world, entity.posX + offsetX, entity.posY, entity.posZ + offsetZ, Items.SLIME_BALL);
            fx.setRBGColorF(0.4F, 0.0F, 0.4F);
            fx.setAlphaF(0.5F);
            fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
            ParticleEngine.addEffect(world, fx);

            if (!world.isAirBlock(blockPos)) {
                float digAngle = world.rand.nextFloat() * (float) Math.PI * entity.height;
                float digRadius = world.rand.nextFloat() * 0.5F + 0.5F;
                float digOffsetX = MathHelper.sin(digAngle) * entity.height * 0.25F * digRadius;
                float digOffsetZ = MathHelper.cos(digAngle) * entity.height * 0.25F * digRadius;
                ParticleEngine.addEffect(world, new TaintDiggingFX(
                        world,
                        entity.posX + digOffsetX,
                        entity.posY,
                        entity.posZ + digOffsetZ,
                        state).setBlockPos(blockPos));
            }
        }
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
        FXSpark fx = new FXSpark(world, x, y, z, size);
        fx.setRBGColorF(red, green, blue);
        fx.setAlphaF(alpha);
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void drawGenericParticles(World world, double x, double y, double z,
                                     double mx, double my, double mz,
                                     float red, float green, float blue, float alpha,
                                     boolean loop, int start, int num, int inc, int age, int delay, float scale) {
        if (world == null || !world.isRemote) return;
        ParticleEngine.addEffect(world, new FXGeneric(
                world,
                x, y, z,
                mx, my, mz,
                red, green, blue, alpha,
                loop, start, num, inc, age, delay, scale));
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
                    x, y, z,
                    mx, my, mz,
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
        FXVent fx = new FXVent(world, x, y, z, mx, my, mz, color);
        fx.setAlphaF(0.4F);
        fx.setScale(scale);
        ParticleEngine.addEffect(world, fx);
    }

    @Override
    public void sparkle(float x, float y, float z, float scale, int type, float speed) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc == null ? null : mc.world;
        if (world == null || !world.isRemote) return;
        if (world.rand.nextInt(6) >= particleCount(2)) return;
        FXSparkle fx = new FXSparkle(world, x, y, z, scale, type, 6.0F);
        fx.setGravity(speed);
        ParticleEngine.addEffect(world, fx);
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
                    0.0D,
                    0.0D,
                    0.0D,
                    4 + world.rand.nextInt(3));
            bubble.setRGB(0.5F, 0.5F, 0.7F);
            bubble.setParticle(64)
                    .setFinalParticles(65, 2)
                    .setScale(0.2F + world.rand.nextFloat() * 0.2F)
                    .setRandomMovementScale(0.001D, 0.001D, 0.001D)
                    .setGravity(0.1F);
            ParticleEngine.addEffect(world, bubble);
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
                    0.0D,
                    0.0D,
                    0.0D,
                    12 + world.rand.nextInt(12));
            bubble.setRGB(0.25F, 0.0F, 0.75F);
            bubble.setParticle(73)
                    .setFinalParticles(65, 2)
                    .setScale(0.4F + world.rand.nextFloat() * 0.2F)
                    .setAlpha(0.8F)
                    .setGravity(0.05F);
            ParticleEngine.addEffect(world, bubble);
        }
    }

    @Override
    public void crucibleBubble(World world, float x, float y, float z, float red, float green, float blue) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            FXBubble bubble = new FXBubble(
                    world,
                    x,
                    y,
                    z,
                    0.0D,
                    0.0D,
                    0.0D,
                    15 + world.rand.nextInt(10));
            bubble.setRGB(red, green, blue);
            bubble.setParticle(64)
                    .setFinalParticles(65, 2)
                    .setScale(0.3F + world.rand.nextFloat() * 0.3F)
                    .setRandomMovementScale(0.002D, 0.002D, 0.002D)
                    .setGravity(-0.001F);
            ParticleEngine.addEffect(world, bubble);
        }
    }

    @Override
    public void crucibleBoilSound(World world, int x, int y, int z) {
        if (world == null) return;
        world.playSound(
                x + 0.5, y + 0.5, z + 0.5,
                TCSounds.SPILL,
                SoundCategory.BLOCKS,
                0.2f,
                1.0f,
                false
        );
    }

    @Override
    public void crucibleBoil(World world, int x, int y, int z, TileCrucible crucible, int type) {
        if (world == null || !world.isRemote || crucible == null) return;

        for (int i = 0; i < 2; i++) {
            FXBubble bubble = new FXBubble(
                    world,
                    x + 0.2f + world.rand.nextFloat() * 0.6f,
                    y + 0.1f + crucible.getFluidHeight(),
                    z + 0.2f + world.rand.nextFloat() * 0.6f,
                    0.0,
                    0.0,
                    0.0,
                    (int) (7.0D + 8.0D / (world.rand.nextDouble() * 0.8D + 0.2D)));
            if (crucible.aspects == null || crucible.aspects.size() <= 0) {
                bubble.setRGB(1.0f, 1.0f, 1.0f);
            } else {
                Aspect[] aspects = crucible.aspects.getAspects();
                if (aspects != null && aspects.length > 0) {
                    Color tint = new Color(aspects[world.rand.nextInt(aspects.length)].getColor());
                    bubble.setRGB(normalizeColor(tint.getRed()), normalizeColor(tint.getGreen()), normalizeColor(tint.getBlue()));
                }
            }
            bubble.setParticle(64)
                    .setFinalParticles(65, 2)
                    .setScale(0.2F + world.rand.nextFloat() * 0.3F)
                    .setRandomMovementScale(0.001D, 0.001D, 0.001D)
                    .setGravity(-0.025F * type);
            ParticleEngine.addEffect(world, bubble);
        }
    }

    @Override
    public void startScan(Entity entity, BlockPos pos, long expireAtMs, int radius) {
        RenderEventHandler.startScan(entity, pos, expireAtMs, radius);
    }

    private static final class TaintDiggingFX extends ParticleDigging {
        private TaintDiggingFX(World world, double x, double y, double z, IBlockState state) {
            super(world, x, y, z, 0.0D, 0.0D, 0.0D, state);
        }
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
