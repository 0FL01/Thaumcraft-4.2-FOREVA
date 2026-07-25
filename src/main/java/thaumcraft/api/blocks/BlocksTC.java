package thaumcraft.api.blocks;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Minimal Thaumcraft 6 block compatibility surface for 1.12 addons.
 *
 * <p>This port keeps TC4's block model, so several TC6 fields intentionally
 * point at the same metadata-driven TC4 block. The public field names and
 * descriptors are the binary contract 1.12 addons link against.</p>
 */
public final class BlocksTC {

    public static Block shimmerleaf;
    public static Block cinderpearl;
    public static Block vishroom;

    public static Block crystalAir;
    public static Block crystalFire;
    public static Block crystalWater;
    public static Block crystalEarth;
    public static Block crystalOrder;
    public static Block crystalEntropy;
    public static Block crystalTaint;
    public static Block lootCrateCommon;
    public static Block lootCrateRare;
    public static Block lootCrateUncommon;
    public static Block lootUrnCommon;
    public static Block lootUrnRare;
    public static Block lootUrnUncommon;
    public static HashMap<EnumDyeColor, Block> nitor;

    private BlocksTC() {
    }

    public static void init() {
        // TC4 stores shimmerleaf/cinderpearl/vishroom as blockCustomPlant
        // metadata 2/3/5. TC6 exposes them as separate Block fields.
        shimmerleaf = ConfigBlocks.blockCustomPlant;
        cinderpearl = ConfigBlocks.blockCustomPlant;
        vishroom = ConfigBlocks.blockCustomPlant;

        // TC4 stores primal crystals as blockCrystal metadata 0..5. TC6 exposes
        // one Block field per primal. Map all fields to the registered TC4 block
        // so binary integrations can register/use a non-null block reference.
        crystalAir = ConfigBlocks.blockCrystal;
        crystalFire = ConfigBlocks.blockCrystal;
        crystalWater = ConfigBlocks.blockCrystal;
        crystalEarth = ConfigBlocks.blockCrystal;
        crystalOrder = ConfigBlocks.blockCrystal;
        crystalEntropy = ConfigBlocks.blockCrystal;
        crystalTaint = ConfigBlocks.blockCrystal;

        lootCrateCommon = ConfigBlocks.blockLootCrate;
        lootCrateRare = ConfigBlocks.blockLootCrate;
        lootCrateUncommon = ConfigBlocks.blockLootCrate;
        lootUrnCommon = ConfigBlocks.blockLootUrn;
        lootUrnRare = ConfigBlocks.blockLootUrn;
        lootUrnUncommon = ConfigBlocks.blockLootUrn;

        nitor = new HashMap<>();
        for (EnumDyeColor color : EnumDyeColor.values()) {
            nitor.put(color, ConfigBlocks.blockAiry);
        }
    }
}
