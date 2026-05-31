package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.init.Biomes;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class TileEtherealBloom extends TileEntity implements ITickable {
    public int counter = 0;
    public int growthCounter = 0;

    @Override
    public void update() {
        if (this.world == null) return;

        if (this.counter == 0) {
            this.counter = this.world.rand.nextInt(100);
        }
        ++this.counter;

        if (!this.world.isRemote && this.counter % 20 == 0) {
            int x = this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
            int z = this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
            int tx = this.pos.getX() + x;
            int tz = this.pos.getZ() + z;
            BlockPos target = new BlockPos(tx, 0, tz);
            Biome current = this.world.getBiome(target);
            int currentId = Biome.getIdForBiome(current);

            if (isBloomTargetBiome(current, currentId)
                    && this.getDistanceSq((double) tx + 0.5D, this.pos.getY(), (double) tz + 0.5D) <= 81.0D) {
                Biome[] generated = this.world.getBiomeProvider().getBiomes(null, tx, tz, 1, 1);
                if (generated != null && generated.length > 0 && generated[0] != null) {
                    Biome biome = generated[0];
                    if (ThaumcraftWorldGenerator.biomeTaint != null && biome == ThaumcraftWorldGenerator.biomeTaint) {
                        biome = Biomes.PLAINS;
                    }
                    Utils.setBiomeAt(this.world, tx, tz, biome);
                }
            }
        }

        if (this.world.isRemote && this.growthCounter == 0) {
            this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                    TCSounds.ROOTS, SoundCategory.BLOCKS, 1.0F, 0.6F, false);
        }
        ++this.growthCounter;
    }

    private static boolean isBloomTargetBiome(Biome biome, int biomeId) {
        return biomeId == Config.biomeTaintID
                || biomeId == Config.biomeEerieID
                || biomeId == Config.biomeMagicalForestID
                || isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint)
                || isSameBiome(biome, ThaumcraftWorldGenerator.biomeEerie)
                || isSameBiome(biome, ThaumcraftWorldGenerator.biomeMagicalForest);
    }

    private static boolean isSameBiome(Biome first, Biome second) {
        return first == second || first != null && second != null
                && Biome.getIdForBiome(first) == Biome.getIdForBiome(second);
    }
}
