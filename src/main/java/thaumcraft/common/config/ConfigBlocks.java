package thaumcraft.common.config;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.common.blocks.*;
import thaumcraft.common.blocks.ItemBlocks.*;
import thaumcraft.common.tiles.*;

import java.util.Locale;

public class ConfigBlocks {

    // Block instances
    public static BlockJar blockJar;
    public static BlockCrystal blockCrystal;
    public static BlockTable blockTable;
    public static BlockStoneDevice blockStoneDevice;
    public static BlockWoodenDevice blockWoodenDevice;
    public static BlockMetalDevice blockMetalDevice;
    public static BlockTube blockTube;
    public static BlockMirror blockMirror;
    public static BlockEssentiaReservoir blockEssentiaReservoir;
    public static BlockMagicalLog blockMagicalLog;
    public static BlockMagicalLeaves blockMagicalLeaves;
    public static BlockCustomOre blockCustomOre;
    public static BlockCustomPlant blockCustomPlant;
    public static BlockCosmeticSolid blockCosmeticSolid;
    public static BlockCosmeticOpaque blockCosmeticOpaque;
    public static BlockTaint blockTaint;
    public static BlockTaintFibres blockTaintFibres;
    public static BlockAiry blockAiry;
    public static BlockFluxGoo blockFluxGoo;
    public static BlockFluxGas blockFluxGas;
    public static BlockManaPod blockManaPod;
    public static BlockEldritch blockEldritch;
    public static BlockEldritchNothing blockEldritchNothing;
    public static BlockEldritchPortal blockEldritchPortal;
    public static BlockStairsEldritch blockStairsEldritch;
    public static BlockHole blockHole;
    public static BlockWarded blockWarded;

    // ItemBlock instances
    public static BlockMagicalLeavesItem blockMagicalLeavesItem;
    public static BlockCustomOreItem blockCustomOreItem;
    public static BlockCustomPlantItem blockCustomPlantItem;
    public static BlockCosmeticSolidItem blockCosmeticSolidItem;
    public static BlockCosmeticOpaqueItem blockCosmeticOpaqueItem;
    public static BlockTaintItem blockTaintItem;
    public static BlockTaintFibresItem blockTaintFibresItem;
    public static BlockAiryItem blockAiryItem;
    public static BlockFluxGooItem blockFluxGooItem;
    public static BlockFluxGasItem blockFluxGasItem;
    public static BlockCrystalItem blockCrystalItem;

    public static void init() {
        blockJar = (BlockJar) new BlockJar()
                .setRegistryName("thaumcraft", legacyPath("blockJar"))
                .setTranslationKey("thaumcraft.jar");

        blockCrystal = (BlockCrystal) new BlockCrystal()
                .setRegistryName("thaumcraft", legacyPath("blockCrystal"))
                .setTranslationKey("thaumcraft.crystal");

        blockTable = (BlockTable) new BlockTable()
                .setRegistryName("thaumcraft", legacyPath("blockTable"))
                .setTranslationKey("thaumcraft.table");

        blockStoneDevice = (BlockStoneDevice) new BlockStoneDevice()
                .setRegistryName("thaumcraft", legacyPath("blockStoneDevice"))
                .setTranslationKey("thaumcraft.stone_device");

        blockWoodenDevice = (BlockWoodenDevice) new BlockWoodenDevice()
                .setRegistryName("thaumcraft", legacyPath("blockWoodenDevice"))
                .setTranslationKey("thaumcraft.wooden_device");

        blockMetalDevice = (BlockMetalDevice) new BlockMetalDevice()
                .setRegistryName("thaumcraft", legacyPath("blockMetalDevice"))
                .setTranslationKey("thaumcraft.metal_device");

        blockTube = (BlockTube) new BlockTube()
                .setRegistryName("thaumcraft", legacyPath("blockTube"))
                .setTranslationKey("thaumcraft.tube");

        blockMirror = (BlockMirror) new BlockMirror()
                .setRegistryName("thaumcraft", legacyPath("blockMirror"))
                .setTranslationKey("thaumcraft.mirror");

        blockEssentiaReservoir = (BlockEssentiaReservoir) new BlockEssentiaReservoir()
                .setRegistryName("thaumcraft", legacyPath("blockEssentiaReservoir"))
                .setTranslationKey("thaumcraft.essentia_reservoir");

        blockMagicalLog = (BlockMagicalLog) new BlockMagicalLog()
                .setRegistryName("thaumcraft", legacyPath("blockMagicalLog"))
                .setTranslationKey("thaumcraft.magical_log");

        blockMagicalLeaves = (BlockMagicalLeaves) new BlockMagicalLeaves()
                .setRegistryName("thaumcraft", legacyPath("blockMagicalLeaves"))
                .setTranslationKey("thaumcraft.magical_leaves");

        blockCustomOre = (BlockCustomOre) new BlockCustomOre()
                .setRegistryName("thaumcraft", legacyPath("blockCustomOre"))
                .setTranslationKey("thaumcraft.custom_ore");

        blockCustomPlant = (BlockCustomPlant) new BlockCustomPlant()
                .setRegistryName("thaumcraft", legacyPath("blockCustomPlant"))
                .setTranslationKey("thaumcraft.custom_plant");

        blockCosmeticSolid = (BlockCosmeticSolid) new BlockCosmeticSolid()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticSolid"))
                .setTranslationKey("thaumcraft.cosmetic_solid");

        blockCosmeticOpaque = (BlockCosmeticOpaque) new BlockCosmeticOpaque()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticOpaque"))
                .setTranslationKey("thaumcraft.cosmetic_opaque");

        blockTaint = (BlockTaint) new BlockTaint()
                .setRegistryName("thaumcraft", legacyPath("blockTaint"))
                .setTranslationKey("thaumcraft.taint");

        blockTaintFibres = (BlockTaintFibres) new BlockTaintFibres()
                .setRegistryName("thaumcraft", legacyPath("blockTaintFibres"))
                .setTranslationKey("thaumcraft.taint_fibres");

        blockAiry = (BlockAiry) new BlockAiry()
                .setRegistryName("thaumcraft", legacyPath("blockAiry"))
                .setTranslationKey("thaumcraft.airy");

        blockFluxGoo = (BlockFluxGoo) new BlockFluxGoo()
                .setRegistryName("thaumcraft", legacyPath("blockFluxGoo"))
                .setTranslationKey("thaumcraft.flux_goo");

        blockFluxGas = (BlockFluxGas) new BlockFluxGas()
                .setRegistryName("thaumcraft", legacyPath("blockFluxGas"))
                .setTranslationKey("thaumcraft.flux_gas");

        blockManaPod = (BlockManaPod) new BlockManaPod()
                .setRegistryName("thaumcraft", legacyPath("blockManaPod"))
                .setTranslationKey("thaumcraft.mana_pod");

        blockEldritch = (BlockEldritch) new BlockEldritch()
                .setRegistryName("thaumcraft", legacyPath("blockEldritch"))
                .setTranslationKey("thaumcraft.eldritch");

        blockEldritchNothing = (BlockEldritchNothing) new BlockEldritchNothing()
                .setRegistryName("thaumcraft", legacyPath("blockEldritchNothing"))
                .setTranslationKey("thaumcraft.eldritch_nothing");

        blockEldritchPortal = (BlockEldritchPortal) new BlockEldritchPortal()
                .setRegistryName("thaumcraft", legacyPath("blockPortalEldritch"))
                .setTranslationKey("thaumcraft.eldritch_portal");

        blockStairsEldritch = (BlockStairsEldritch) new BlockStairsEldritch()
                .setRegistryName("thaumcraft", legacyPath("blockStairsEldritch"))
                .setTranslationKey("thaumcraft.stairs_eldritch");

        blockHole = (BlockHole) new BlockHole()
                .setRegistryName("thaumcraft", legacyPath("blockHole"))
                .setTranslationKey("thaumcraft.hole");

        blockWarded = (BlockWarded) new BlockWarded()
                .setRegistryName("thaumcraft", legacyPath("blockWarded"))
                .setTranslationKey("thaumcraft.warded");

        // ItemBlock instances (cast needed because setRegistryName returns Item)
        blockMagicalLeavesItem = (BlockMagicalLeavesItem) new BlockMagicalLeavesItem(blockMagicalLeaves)
                .setRegistryName(blockMagicalLeaves.getRegistryName());

        blockCustomOreItem = (BlockCustomOreItem) new BlockCustomOreItem(blockCustomOre)
                .setRegistryName(blockCustomOre.getRegistryName());

        blockCustomPlantItem = (BlockCustomPlantItem) new BlockCustomPlantItem(blockCustomPlant)
                .setRegistryName(blockCustomPlant.getRegistryName());

        blockCosmeticSolidItem = (BlockCosmeticSolidItem) new BlockCosmeticSolidItem(blockCosmeticSolid)
                .setRegistryName(blockCosmeticSolid.getRegistryName());

        blockCosmeticOpaqueItem = (BlockCosmeticOpaqueItem) new BlockCosmeticOpaqueItem(blockCosmeticOpaque)
                .setRegistryName(blockCosmeticOpaque.getRegistryName());

        blockTaintItem = (BlockTaintItem) new BlockTaintItem(blockTaint)
                .setRegistryName(blockTaint.getRegistryName());

        blockTaintFibresItem = (BlockTaintFibresItem) new BlockTaintFibresItem(blockTaintFibres)
                .setRegistryName(blockTaintFibres.getRegistryName());

        blockAiryItem = (BlockAiryItem) new BlockAiryItem(blockAiry)
                .setRegistryName(blockAiry.getRegistryName());

        blockFluxGooItem = (BlockFluxGooItem) new BlockFluxGooItem(blockFluxGoo)
                .setRegistryName(blockFluxGoo.getRegistryName());

        blockFluxGasItem = (BlockFluxGasItem) new BlockFluxGasItem(blockFluxGas)
                .setRegistryName(blockFluxGas.getRegistryName());

        blockCrystalItem = (BlockCrystalItem) new BlockCrystalItem(blockCrystal)
                .setRegistryName(blockCrystal.getRegistryName());
    }

    public static Block[] getAllBlocks() {
        return new Block[]{
                blockJar,
                blockCrystal,
                blockTable,
                blockStoneDevice,
                blockWoodenDevice,
                blockMetalDevice,
                blockTube,
                blockMirror,
                blockEssentiaReservoir,
                blockMagicalLog,
                blockMagicalLeaves,
                blockCustomOre,
                blockCustomPlant,
                blockCosmeticSolid,
                blockCosmeticOpaque,
                blockTaint,
                blockTaintFibres,
                blockAiry,
                blockFluxGoo,
                blockFluxGas,
                blockManaPod,
                blockEldritch,
                blockEldritchNothing,
                blockEldritchPortal,
                blockStairsEldritch,
                blockHole,
                blockWarded
        };
    }

    public static void registerItemBlocks(net.minecraftforge.registries.IForgeRegistry<net.minecraft.item.Item> registry) {
        registry.registerAll(
            blockMagicalLeavesItem,
            blockCustomOreItem,
            blockCustomPlantItem,
            blockCosmeticSolidItem,
            blockCosmeticOpaqueItem,
            blockTaintItem,
            blockTaintFibresItem,
            blockAiryItem,
            blockFluxGooItem,
            blockFluxGasItem,
            blockCrystalItem
        );
        registry.register(new BlockMetadataItem(blockTable)
                .setRegistryName(blockTable.getRegistryName()));
        registry.register(new BlockMetadataItem(blockStoneDevice)
                .setRegistryName(blockStoneDevice.getRegistryName()));
        registry.register(new BlockWoodenDeviceItem(blockWoodenDevice)
                .setRegistryName(blockWoodenDevice.getRegistryName()));
        registry.register(new BlockMetadataItem(blockMetalDevice)
                .setRegistryName(blockMetalDevice.getRegistryName()));
        registry.register(new BlockTubeItem(blockTube)
                .setRegistryName(blockTube.getRegistryName()));
        registry.register(new BlockMirrorItem(blockMirror)
                .setRegistryName(blockMirror.getRegistryName()));
        registry.register(new BlockEssentiaReservoirItem(blockEssentiaReservoir)
                .setRegistryName(blockEssentiaReservoir.getRegistryName()));
        registry.register(new BlockMetadataItem(blockMagicalLog)
                .setRegistryName(blockMagicalLog.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockManaPod)
                .setRegistryName(blockManaPod.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockHole)
                .setRegistryName(blockHole.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockWarded)
                .setRegistryName(blockWarded.getRegistryName()));
        registry.register(new BlockEldritchItem(blockEldritch)
                .setRegistryName(blockEldritch.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockEldritchNothing)
                .setRegistryName(blockEldritchNothing.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockEldritchPortal)
                .setRegistryName(blockEldritchPortal.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockStairsEldritch)
                .setRegistryName(blockStairsEldritch.getRegistryName()));
    }

    public static void registerTileEntities() {
        for (TileRegistration entry : TILE_REGISTRATIONS) {
            GameRegistry.registerTileEntity(entry.tileClass, legacyLocation(entry.legacyToken));
        }
    }

    public static String legacyPath(String legacyToken) {
        return legacyToken.toLowerCase(Locale.ROOT);
    }

    private static ResourceLocation legacyLocation(String legacyToken) {
        return new ResourceLocation("thaumcraft", legacyPath(legacyToken));
    }

    private static final TileRegistration[] TILE_REGISTRATIONS = new TileRegistration[]{
            new TileRegistration(TileJarFillable.class, "TileJar"),
            new TileRegistration(TileJarBrain.class, "TileJarBrain"),
            new TileRegistration(TileJarNode.class, "TileJarNode"),
            new TileRegistration(TileJarFillableVoid.class, "TileJarVoid"),
            new TileRegistration(TileCrystal.class, "TileCrystal"),
            new TileRegistration(TileEldritchCrystal.class, "TileEldritchCrystal"),
            new TileRegistration(TileNode.class, "TileNode"),
            new TileRegistration(TileTable.class, "TileTable"),
            new TileRegistration(TileMagicWorkbench.class, "TileMagicWorkbench"),
            new TileRegistration(TileArcaneWorkbench.class, "TileArcaneWorkbench"),
            new TileRegistration(TileDeconstructionTable.class, "TileDeconstructionTable"),
            new TileRegistration(TileResearchTable.class, "TileResearchTable"),
            new TileRegistration(TilePedestal.class, "TilePedestal"),
            new TileRegistration(TileWandPedestal.class, "TileWandPedestal"),
            new TileRegistration(TileAlchemyFurnace.class, "TileAlchemyFurnace"),
            new TileRegistration(TileInfusionMatrix.class, "TileInfusionStone"),
            new TileRegistration(TileInfusionPillar.class, "TileInfusionPillar"),
            new TileRegistration(TileNodeStabilizer.class, "TileNodeStabilizer"),
            new TileRegistration(TileNodeConverter.class, "TileNodeConverter"),
            new TileRegistration(TileSpa.class, "TileSpa"),
            new TileRegistration(TileFocalManipulator.class, "TileFocalManipulator"),
            new TileRegistration(TileFluxScrubber.class, "TileFluxScrubber"),
            new TileRegistration(TileCrucible.class, "TileCrucible"),
            new TileRegistration(TileManaPod.class, "TileManaPod"),
            new TileRegistration(TileArcaneBore.class, "TileArcaneBore"),
            new TileRegistration(TileArcaneBoreBase.class, "TileArcaneBoreBase"),
            new TileRegistration(TileArcaneFurnace.class, "TileArcaneFurnace"),
            new TileRegistration(TileArcaneFurnaceNozzle.class, "TileArcaneFurnaceNozzle"),
            new TileRegistration(TileBellows.class, "TileBellows"),
            new TileRegistration(TileTube.class, "TileTube"),
            new TileRegistration(TileTubeValve.class, "TileTubeValve"),
            new TileRegistration(TileTubeFilter.class, "TileTubeFilter"),
            new TileRegistration(TileTubeBuffer.class, "TileTubeBuffer"),
            new TileRegistration(TileTubeRestrict.class, "TileTubeRestrict"),
            new TileRegistration(TileTubeOneway.class, "TileTubeOneway"),
            new TileRegistration(TileCentrifuge.class, "TileCentrifuge"),
            new TileRegistration(TileEssentiaReservoir.class, "TileEssentiaReservoir"),
            new TileRegistration(TileMirror.class, "TileMirror"),
            new TileRegistration(TileMirrorEssentia.class, "TileMirrorEssentia"),
            new TileRegistration(TileVisRelay.class, "TileVisRelay"),
            new TileRegistration(TileMagicWorkbenchCharger.class, "TileMagicWorkbenchCharger"),
            new TileRegistration(TileOwned.class, "TileOwned"),
            new TileRegistration(TileArcanePressurePlate.class, "TileArcanePressurePlate"),
            new TileRegistration(TileBanner.class, "TileBanner"),
            new TileRegistration(TileSensor.class, "TileSensor"),
            new TileRegistration(TileLifter.class, "TileLifter"),
            new TileRegistration(TileHole.class, "TileHole"),
            new TileRegistration(TileWarded.class, "TileWarded"),
            new TileRegistration(TileGrate.class, "TileGrate"),
            new TileRegistration(TileAlembic.class, "TileSiphon"),
            new TileRegistration(TileArcaneLamp.class, "TileArcaneLamp"),
            new TileRegistration(TileArcaneLampGrowth.class, "TileArcaneLampGrowth"),
            new TileRegistration(TileThaumatorium.class, "TileThaumatorium"),
            new TileRegistration(TileThaumatoriumTop.class, "TileThaumatoriumTop"),
            new TileRegistration(TileEtherealBloom.class, "TilePurifyTotem"),
            new TileRegistration(TileNodeEnergized.class, "TileNodeEnergized"),
            new TileRegistration(TileWardingStone.class, "TileWardingStone"),
            new TileRegistration(TileWardingStoneFence.class, "TileWardingStoneFence"),
            new TileRegistration(TileNitor.class, "TileNitor"),
            new TileRegistration(TileEldritchPortal.class, "TileEldritchPortal"),
            new TileRegistration(TileEldritchNothing.class, "TileEldritchNothing"),
            new TileRegistration(TileEldritchLock.class, "TileEldritchLock"),
            new TileRegistration(TileEldritchCrabSpawner.class, "TileEldritchCrabSpawner"),
            new TileRegistration(TileEldritchAltar.class, "TileEldritchAltar"),
            new TileRegistration(TileEldritchCap.class, "TileEldritchCap"),
            new TileRegistration(TileEldritchObelisk.class, "TileEldritchObelisk"),
            new TileRegistration(TileEldritchTrap.class, "TileEldritchTrap")
    };

    private static final class TileRegistration {
        private final Class<? extends TileEntity> tileClass;
        private final String legacyToken;

        private TileRegistration(Class<? extends TileEntity> tileClass, String legacyToken) {
            this.tileClass = tileClass;
            this.legacyToken = legacyToken;
        }
    }
}
