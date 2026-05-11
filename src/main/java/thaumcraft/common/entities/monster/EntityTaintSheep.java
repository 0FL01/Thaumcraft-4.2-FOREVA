package thaumcraft.common.entities.monster;

public class EntityTaintSheep extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob, net.minecraftforge.common.IShearable {
    public EntityTaintSheep(net.minecraft.world.World world) { super(world); }

    @Override public boolean isShearable(net.minecraft.item.ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos) { return false; }
    @Override public java.util.List<net.minecraft.item.ItemStack> onSheared(net.minecraft.item.ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos, int fortune) { return new java.util.ArrayList<>(); }
}
