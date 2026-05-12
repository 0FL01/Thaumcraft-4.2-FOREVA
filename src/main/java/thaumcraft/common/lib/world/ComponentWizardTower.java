package thaumcraft.common.lib.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;

public class ComponentWizardTower extends StructureVillagePieces.Village {

    private int averageGroundLevel = -1;

    public ComponentWizardTower() {}

    public ComponentWizardTower(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox bb, EnumFacing facing) {
        super(start, type);
        this.setCoordBaseMode(facing);
        this.boundingBox = bb;
    }

    /**
     * Builds the wizard tower component.
     */
    public static ComponentWizardTower buildComponent(StructureVillagePieces.Start start, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 12, 5, facing);
        return (!canVillageGoDeeper(bb) || StructureComponent.findIntersecting(pieces, bb) != null) ? null : new ComponentWizardTower(start, p5, random, bb, facing);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bb) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, bb);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 12 - 1, 0);
        }

        // Hollow interior
        this.fillWithBlocks(world, bb, 2, 1, 2, 4, 11, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        // Base floor
        this.fillWithBlocks(world, bb, 2, 0, 2, 4, 0, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        // Mid floor (y=5)
        this.fillWithBlocks(world, bb, 2, 5, 2, 4, 5, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        // Roof (y=10)
        this.fillWithBlocks(world, bb, 2, 10, 2, 4, 10, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);

        // Wall pillars / corners (cobblestone vertical strips)
        this.fillWithBlocks(world, bb, 1, 0, 2, 1, 11, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 0, 1, 4, 11, 1, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 5, 0, 2, 5, 11, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 0, 5, 4, 11, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);

        // Fence posts at outer corners (bottom)
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 0, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 0, 5, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 0, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 0, 5, bb);

        // Fence posts at outer corners (mid)
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 5, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 5, 5, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 5, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 5, 5, bb);

        // Fence posts at outer corners (roof)
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 10, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 10, 5, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 10, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 10, 5, bb);

        // Fence gate at front (z=1)
        this.setBlockState(world, Blocks.OAK_FENCE_GATE.getDefaultState(), 3, 7, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE_GATE.getDefaultState(), 3, 8, 1, bb);
        this.setBlockState(world, Blocks.OAK_FENCE_GATE.getDefaultState(), 3, 7, 5, bb);
        this.setBlockState(world, Blocks.OAK_FENCE_GATE.getDefaultState(), 3, 8, 5, bb);
        this.setBlockState(world, Blocks.OAK_FENCE_GATE.getDefaultState(), 3, 2, 5, bb);
        this.setBlockState(world, Blocks.OAK_FENCE_GATE.getDefaultState(), 3, 3, 5, bb);

        // Ladder going up
        IBlockState ladderState = Blocks.LADDER.getDefaultState();
        for (int y = 1; y <= 9; ++y) {
            this.setBlockState(world, ladderState, 4, y, 3, bb);
        }
        // Trapdoor at top of ladder
        this.setBlockState(world, Blocks.TRAPDOOR.getDefaultState(), 4, 10, 3, bb);

        // Bookshelf
        this.setBlockState(world, Blocks.BOOKSHELF.getDefaultState(), 3, 5, 3, bb);

        // Chest with loot
        this.placeChestWithLoot(world, bb, random, 2, 6, 2);

        // Door placement
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 3, 1, 1, bb);
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 3, 2, 1, bb);
        this.generateDoor(world, bb, random, 3, 1, 1, this.getCoordBaseMode(), Blocks.OAK_DOOR);

        // Step (double stone slab) outside door
        if (this.getBlockStateFromPos(world, 3, 0, 0, bb).getMaterial() == net.minecraft.block.material.Material.AIR
                && this.getBlockStateFromPos(world, 3, -1, 0, bb).getMaterial() != net.minecraft.block.material.Material.AIR) {
            this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 3, 0, 0, bb);
        }

        // Clear above the structure
        for (int z = 0; z < 12; ++z) {
            for (int x = 0; x < 5; ++x) {
                this.clearCurrentPositionBlocksUpwards(world, x, 12, z, bb);
                this.replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), x, -1, z, bb);
            }
        }

        // Spawn a wizard villager
        this.spawnVillagers(world, bb, 7, 1, 1, 1);

        return true;
    }

    /**
     * Places a chest with wizard tower loot.
     */
    private void placeChestWithLoot(World world, StructureBoundingBox bb, Random random, int x, int y, int z) {
        BlockPos pos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
        if (bb.isVecInside(pos) && world.getBlockState(pos).getBlock() != Blocks.CHEST) {
            this.setBlockState(world, Blocks.CHEST.getDefaultState(), x, y, z, bb);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityChest) {
                TileEntityChest chest = (TileEntityChest) te;
                // Fill with random loot items
                int count = 4 + random.nextInt(6); // 4-9 items
                for (int i = 0; i < count; i++) {
                    ItemStack stack = getRandomTowerLoot(random);
                    if (!stack.isEmpty()) {
                        int slot = random.nextInt(chest.getSizeInventory());
                        chest.setInventorySlotContents(slot, stack);
                    }
                }
            }
        }
    }

    /**
     * Returns a random loot item for the wizard tower chest.
     */
    private static ItemStack getRandomTowerLoot(Random random) {
        int roll = random.nextInt(100);
        if (roll < 3)  return new ItemStack(Items.BOOK, 1 + random.nextInt(3));              // 3% Book
        if (roll < 13) return new ItemStack(Items.PAPER, 1 + random.nextInt(5));             // 10% Paper
        if (roll < 18) return new ItemStack(Items.EMERALD, 1 + random.nextInt(3));           // 5% Emerald
        if (roll < 23) return new ItemStack(Items.FILLED_MAP);                               // 5% Empty Map
        if (roll < 26) return new ItemStack(Items.ENDER_PEARL);                              // 3% Ender Pearl
        if (roll < 46) return new ItemStack(ConfigItems.itemResource, 1 + random.nextInt(3), 9); // 20% TC Resource (meta 9)
        if (roll < 51) return new ItemStack(ConfigItems.itemResource, 1, 0);                 // 5% TC Resource (meta 0)
        if (roll < 56) return new ItemStack(ConfigItems.itemResource, 1, 1);                 // 5% TC Resource (meta 1)
        if (roll < 61) return new ItemStack(ConfigItems.itemResource, 1 + random.nextInt(2), 2); // 5% TC Resource (meta 2)
        if (roll < 81) return new ItemStack(ConfigItems.itemThaumonomicon);                  // 20% Thaumonomicon
        return ItemStack.EMPTY;
    }

    @Override
    protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof) {
        return ConfigEntities.PROF_WIZARD;
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager tm) {
        super.readStructureFromNBT(tag, tm);
    }
}
