package thaumcraft.common.lib.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
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

        // Hollow interior
        this.fillWithBlocks(world, bb, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        // Base floor (cobblestone perimeter)
        this.fillWithBlocks(world, bb, 0, 0, 0, 3, 0, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        // Dirt floor inside
        this.fillWithBlocks(world, bb, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);

        // Roof (oak slabs)
        if (this.isTallHouse) {
            this.fillWithBlocks(world, bb, 1, 4, 1, 2, 4, 3, Blocks.OAK_STAIRS.getDefaultState(), Blocks.OAK_STAIRS.getDefaultState(), false);
        } else {
            this.fillWithBlocks(world, bb, 1, 5, 1, 2, 5, 3, Blocks.OAK_STAIRS.getDefaultState(), Blocks.OAK_STAIRS.getDefaultState(), false);
        }

        // Roof edge fence posts
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 4, 0, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 4, 0, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 4, 4, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 4, 4, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 4, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 4, 2, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 4, 3, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 3, 4, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 3, 4, 2, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 3, 4, 3, bb);

        // Front and back walls (fence)
        this.fillWithBlocks(world, bb, 0, 1, 0, 0, 3, 0, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 3, 1, 0, 3, 3, 0, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 0, 1, 4, 0, 3, 4, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 3, 1, 4, 3, 3, 4, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);

        // Side walls (planks)
        this.fillWithBlocks(world, bb, 0, 1, 1, 0, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 3, 1, 1, 3, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 1, 1, 0, 2, 3, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 1, 1, 4, 2, 3, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);

        // Glass panes (windows)
        this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, bb);
        this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 3, 2, 2, bb);

        // Table (fence + pressure plate)
        if (this.tablePosition > 0) {
            this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), this.tablePosition, 1, 3, bb);
            this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), this.tablePosition, 2, 3, bb);
        }

        // Door placement
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 1, 0, bb);
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 2, 0, bb);
        this.generateDoor(world, bb, random, 1, 1, 0, this.getCoordBaseMode(), Blocks.OAK_DOOR);

        // Step outside door
        if (this.getBlockStateFromPos(world, 1, 0, -1, bb).getMaterial() == Material.AIR
                && this.getBlockStateFromPos(world, 1, -1, -1, bb).getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 1, 0, -1, bb);
        }

        // Clear above and fill below
        for (int z = 0; z < 5; ++z) {
            for (int x = 0; x < 4; ++x) {
                this.clearCurrentPositionBlocksUpwards(world, x, 6, z, bb);
                this.replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), x, -1, z, bb);
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
