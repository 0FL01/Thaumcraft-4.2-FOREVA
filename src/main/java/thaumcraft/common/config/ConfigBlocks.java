package thaumcraft.common.config;

import net.minecraft.block.Block;
import thaumcraft.common.blocks.*;
import thaumcraft.common.blocks.ItemBlocks.*;

public class ConfigBlocks {

    // Block instances
    public static BlockJar blockJar;
    public static BlockCrystal blockCrystal;
    public static BlockTable blockTable;
    public static BlockStoneDevice blockStoneDevice;
    public static BlockWoodenDevice blockWoodenDevice;
    public static BlockMetalDevice blockMetalDevice;
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
                .setRegistryName("thaumcraft", "jar")
                .setTranslationKey("thaumcraft.jar");

        blockCrystal = (BlockCrystal) new BlockCrystal()
                .setRegistryName("thaumcraft", "crystal")
                .setTranslationKey("thaumcraft.crystal");

        blockTable = (BlockTable) new BlockTable()
                .setRegistryName("thaumcraft", "table")
                .setTranslationKey("thaumcraft.table");

        blockStoneDevice = (BlockStoneDevice) new BlockStoneDevice()
                .setRegistryName("thaumcraft", "stone_device")
                .setTranslationKey("thaumcraft.stone_device");

        blockWoodenDevice = (BlockWoodenDevice) new BlockWoodenDevice()
                .setRegistryName("thaumcraft", "wooden_device")
                .setTranslationKey("thaumcraft.wooden_device");

        blockMetalDevice = (BlockMetalDevice) new BlockMetalDevice()
                .setRegistryName("thaumcraft", "metal_device")
                .setTranslationKey("thaumcraft.metal_device");

        blockMagicalLog = (BlockMagicalLog) new BlockMagicalLog()
                .setRegistryName("thaumcraft", "magical_log")
                .setTranslationKey("thaumcraft.magical_log");

        blockMagicalLeaves = (BlockMagicalLeaves) new BlockMagicalLeaves()
                .setRegistryName("thaumcraft", "magical_leaves")
                .setTranslationKey("thaumcraft.magical_leaves");

        blockCustomOre = (BlockCustomOre) new BlockCustomOre()
                .setRegistryName("thaumcraft", "custom_ore")
                .setTranslationKey("thaumcraft.custom_ore");

        blockCustomPlant = (BlockCustomPlant) new BlockCustomPlant()
                .setRegistryName("thaumcraft", "custom_plant")
                .setTranslationKey("thaumcraft.custom_plant");

        blockCosmeticSolid = (BlockCosmeticSolid) new BlockCosmeticSolid()
                .setRegistryName("thaumcraft", "cosmetic_solid")
                .setTranslationKey("thaumcraft.cosmetic_solid");

        blockCosmeticOpaque = (BlockCosmeticOpaque) new BlockCosmeticOpaque()
                .setRegistryName("thaumcraft", "cosmetic_opaque")
                .setTranslationKey("thaumcraft.cosmetic_opaque");

        blockTaint = (BlockTaint) new BlockTaint()
                .setRegistryName("thaumcraft", "taint")
                .setTranslationKey("thaumcraft.taint");

        blockTaintFibres = (BlockTaintFibres) new BlockTaintFibres()
                .setRegistryName("thaumcraft", "taint_fibres")
                .setTranslationKey("thaumcraft.taint_fibres");

        blockAiry = (BlockAiry) new BlockAiry()
                .setRegistryName("thaumcraft", "airy")
                .setTranslationKey("thaumcraft.airy");

        blockFluxGoo = (BlockFluxGoo) new BlockFluxGoo()
                .setRegistryName("thaumcraft", "flux_goo")
                .setTranslationKey("thaumcraft.flux_goo");

        blockFluxGas = (BlockFluxGas) new BlockFluxGas()
                .setRegistryName("thaumcraft", "flux_gas")
                .setTranslationKey("thaumcraft.flux_gas");

        blockManaPod = (BlockManaPod) new BlockManaPod()
                .setRegistryName("thaumcraft", "mana_pod")
                .setTranslationKey("thaumcraft.mana_pod");

        blockEldritch = (BlockEldritch) new BlockEldritch()
                .setRegistryName("thaumcraft", "eldritch")
                .setTranslationKey("thaumcraft.eldritch");

        blockEldritchNothing = (BlockEldritchNothing) new BlockEldritchNothing()
                .setRegistryName("thaumcraft", "eldritch_nothing")
                .setTranslationKey("thaumcraft.eldritch_nothing");

        blockEldritchPortal = (BlockEldritchPortal) new BlockEldritchPortal()
                .setRegistryName("thaumcraft", "eldritch_portal")
                .setTranslationKey("thaumcraft.eldritch_portal");

        blockStairsEldritch = (BlockStairsEldritch) new BlockStairsEldritch()
                .setRegistryName("thaumcraft", "stairs_eldritch")
                .setTranslationKey("thaumcraft.stairs_eldritch");

        blockHole = (BlockHole) new BlockHole()
                .setRegistryName("thaumcraft", "hole")
                .setTranslationKey("thaumcraft.hole");

        blockWarded = (BlockWarded) new BlockWarded()
                .setRegistryName("thaumcraft", "warded")
                .setTranslationKey("thaumcraft.warded");

        // ItemBlock instances (cast needed because setRegistryName returns Item)
        blockMagicalLeavesItem = (BlockMagicalLeavesItem) new BlockMagicalLeavesItem(blockMagicalLeaves)
                .setRegistryName("thaumcraft", "magical_leaves");

        blockCustomOreItem = (BlockCustomOreItem) new BlockCustomOreItem(blockCustomOre)
                .setRegistryName("thaumcraft", "custom_ore");

        blockCustomPlantItem = (BlockCustomPlantItem) new BlockCustomPlantItem(blockCustomPlant)
                .setRegistryName("thaumcraft", "custom_plant");

        blockCosmeticSolidItem = (BlockCosmeticSolidItem) new BlockCosmeticSolidItem(blockCosmeticSolid)
                .setRegistryName("thaumcraft", "cosmetic_solid");

        blockCosmeticOpaqueItem = (BlockCosmeticOpaqueItem) new BlockCosmeticOpaqueItem(blockCosmeticOpaque)
                .setRegistryName("thaumcraft", "cosmetic_opaque");

        blockTaintItem = (BlockTaintItem) new BlockTaintItem(blockTaint)
                .setRegistryName("thaumcraft", "taint");

        blockTaintFibresItem = (BlockTaintFibresItem) new BlockTaintFibresItem(blockTaintFibres)
                .setRegistryName("thaumcraft", "taint_fibres");

        blockAiryItem = (BlockAiryItem) new BlockAiryItem(blockAiry)
                .setRegistryName("thaumcraft", "airy");

        blockFluxGooItem = (BlockFluxGooItem) new BlockFluxGooItem(blockFluxGoo)
                .setRegistryName("thaumcraft", "flux_goo");

        blockFluxGasItem = (BlockFluxGasItem) new BlockFluxGasItem(blockFluxGas)
                .setRegistryName("thaumcraft", "flux_gas");

        blockCrystalItem = (BlockCrystalItem) new BlockCrystalItem(blockCrystal)
                .setRegistryName("thaumcraft", "crystal");
    }

    public static Block[] getAllBlocks() {
        return new Block[]{
                blockJar,
                blockCrystal,
                blockTable,
                blockStoneDevice,
                blockWoodenDevice,
                blockMetalDevice,
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
        registry.register(new BlockEldritchItem(blockEldritch)
                .setRegistryName("thaumcraft", "eldritch"));
        registry.register(new net.minecraft.item.ItemBlock(blockEldritchNothing)
                .setRegistryName("thaumcraft", "eldritch_nothing"));
        registry.register(new net.minecraft.item.ItemBlock(blockEldritchPortal)
                .setRegistryName("thaumcraft", "eldritch_portal"));
        registry.register(new net.minecraft.item.ItemBlock(blockStairsEldritch)
                .setRegistryName("thaumcraft", "stairs_eldritch"));
    }
}
