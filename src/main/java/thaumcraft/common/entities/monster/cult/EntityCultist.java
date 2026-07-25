package thaumcraft.common.entities.monster.cult;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/** TC6 package alias for cultist entities. */
public class EntityCultist extends thaumcraft.common.entities.monster.EntityCultist {

    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("thaumcraft", "cultist"));

    public EntityCultist(World world) {
        super(world);
    }
}
