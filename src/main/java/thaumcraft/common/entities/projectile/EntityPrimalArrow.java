package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityPrimalArrow extends EntityArrow implements IProjectile, IEntityAdditionalSpawnData {
    public EntityPrimalArrow(World world) { super(world); }
    public EntityPrimalArrow(World world, EntityLivingBase shooter) {
        super(world, shooter);
    }

    @Override
    protected ItemStack getArrowStack() { return ItemStack.EMPTY; }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result == null) return;
        super.onHit(result);
        // Primal aspect effects applied after vanilla arrow damage (Phase 3)
        // Types: 0=Air, 1=Fire, 2=Water, 3=Earth, 4=Order, 5=Entropy
    }

    @Override public void writeSpawnData(ByteBuf buf) {}
    @Override public void readSpawnData(ByteBuf buf) {}
}
