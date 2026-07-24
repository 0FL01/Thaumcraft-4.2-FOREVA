package thaumcraft.common.lib.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.common.config.ConfigEntities;

public class ComponentBankerHome extends StructureVillagePieces.Village {

    private boolean isTallHouse;
    private int tablePosition;

    public ComponentBankerHome() {}

    public ComponentBankerHome(StructureVillagePieces.Start start, int type, Random random, StructureBoundingBox bb, EnumFacing facing) {
        super(start, type);
        this.setCoordBaseMode(facing);
        this.boundingBox = bb;
        this.isTallHouse = random.nextBoolean();
        this.tablePosition = random.nextInt(3);
    }

    /**
     * Builds the banker home component.
     */
    public static ComponentBankerHome buildComponent(StructureVillagePieces.Start start, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 4, 6, 5, facing);
        return (!canVillageGoDeeper(bb) || StructureComponent.findIntersecting(pieces, bb) != null) ? null : new ComponentBankerHome(start, p5, random, bb, facing);
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setInteger("T", this.tablePosition);
        tag.setBoolean("C", this.isTallHouse);
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager tm) {
        super.readStructureFromNBT(tag, tm);
        this.tablePosition = tag.getInteger("T");
        this.isTallHouse = tag.getBoolean("C");
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bb) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(world, bb);
            if (this.averageGroundLvl < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
        }

        IBlockState cobblestone = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
        IBlockState planks = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
        IBlockState log = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
        IBlockState fence = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
        IBlockState stoneStairs = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState()
                .withProperty(BlockStairs.FACING, EnumFacing.NORTH));

        // Hollow interior
        this.fillWithBlocks(world, bb, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        // Base floor (cobblestone perimeter)
        this.fillWithBlocks(world, bb, 0, 0, 0, 3, 0, 4, cobblestone, cobblestone, false);
        // Dirt floor inside
        this.fillWithBlocks(world, bb, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);

        // Roof
        if (this.isTallHouse) {
            this.fillWithBlocks(world, bb, 1, 4, 1, 2, 4, 3, log, log, false);
        } else {
            this.fillWithBlocks(world, bb, 1, 5, 1, 2, 5, 3, log, log, false);
        }

        // Roof edge
        this.setBlockState(world, log, 1, 4, 0, bb);
        this.setBlockState(world, log, 2, 4, 0, bb);
        this.setBlockState(world, log, 1, 4, 4, bb);
        this.setBlockState(world, log, 2, 4, 4, bb);
        this.setBlockState(world, log, 0, 4, 1, bb);
        this.setBlockState(world, log, 0, 4, 2, bb);
        this.setBlockState(world, log, 0, 4, 3, bb);
        this.setBlockState(world, log, 3, 4, 1, bb);
        this.setBlockState(world, log, 3, 4, 2, bb);
        this.setBlockState(world, log, 3, 4, 3, bb);

        // Front and back wall supports
        this.fillWithBlocks(world, bb, 0, 1, 0, 0, 3, 0, log, log, false);
        this.fillWithBlocks(world, bb, 3, 1, 0, 3, 3, 0, log, log, false);
        this.fillWithBlocks(world, bb, 0, 1, 4, 0, 3, 4, log, log, false);
        this.fillWithBlocks(world, bb, 3, 1, 4, 3, 3, 4, log, log, false);

        // Side walls (planks)
        this.fillWithBlocks(world, bb, 0, 1, 1, 0, 3, 3, planks, planks, false);
        this.fillWithBlocks(world, bb, 3, 1, 1, 3, 3, 3, planks, planks, false);
        this.fillWithBlocks(world, bb, 1, 1, 0, 2, 3, 0, planks, planks, false);
        this.fillWithBlocks(world, bb, 1, 1, 4, 2, 3, 4, planks, planks, false);

        // Windows
        this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 0, 2, 2, bb);
        this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 3, 2, 2, bb);

        // Table (fence + pressure plate)
        if (this.tablePosition > 0) {
            this.setBlockState(world, fence, this.tablePosition, 1, 3, bb);
            this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), this.tablePosition, 2, 3, bb);
        }

        // Door placement
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 1, 0, bb);
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 2, 0, bb);
        this.createVillageDoor(world, bb, random, 1, 1, 0, EnumFacing.NORTH);

        // Step outside door
        if (this.getBlockStateFromPos(world, 1, 0, -1, bb).getMaterial() == Material.AIR
                && this.getBlockStateFromPos(world, 1, -1, -1, bb).getMaterial() != Material.AIR) {
            this.setBlockState(world, stoneStairs, 1, 0, -1, bb);
        }

        // Clear above and fill below
        for (int z = 0; z < 5; ++z) {
            for (int x = 0; x < 4; ++x) {
                this.clearCurrentPositionBlocksUpwards(world, x, 6, z, bb);
                this.replaceAirAndLiquidDownwards(world, cobblestone, x, -1, z, bb);
            }
        }

        // Spawn a banker villager
        this.spawnVillagers(world, bb, 1, 1, 2, 1);

        return true;
    }

    @Override
    protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof) {
        return ConfigEntities.PROF_BANKER;
    }
}
