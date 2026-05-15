package thaumcraft.common.lib.world.dim;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.Config;

public class WorldProviderOuter extends WorldProvider {
    public WorldProviderOuter() {
        super();
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionManager.getProviderType(Config.dimensionOuterId);
    }

    @Override
    public String getSaveFolder() {
        return "DIM_OUTERLANDS";
    }

    public String getWelcomeMessage() {
        return "Entering The Outer Lands";
    }

    public String getDepartMessage() {
        return "Leaving The Outer Lands";
    }

    @Override
    protected void init() {
        this.biomeProvider = new BiomeProviderSingle(
                thaumcraft.common.lib.world.ThaumcraftWorldGenerator.biomeEldritchLands);
        this.hasSkyLight = false;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkProviderOuter(this.world, this.world.getSeed(), false);
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.0f;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        return new Vec3d(0.0, 0.0, 0.0);
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean doesWaterVaporize() {
        return true;
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        BlockPos top = this.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        return this.world.getBlockState(top).getMaterial().blocksMovement();
    }

    @Override
    public BlockPos getSpawnPoint() {
        return new BlockPos(0, 60, 0);
    }

    @Override
    public int getAverageGroundLevel() {
        return 50;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z) {
        return true;
    }

    @Override
    public boolean shouldMapSpin(String entity, double x, double y, double z) {
        return true;
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
        return false;
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        return false;
    }

    @Override
    public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk) {
        return false;
    }

    @Override
    public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {
        return false;
    }

    @Override
    public float getCloudHeight() {
        return -999.0f;
    }
}
