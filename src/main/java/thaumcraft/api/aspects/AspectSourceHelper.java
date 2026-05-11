package thaumcraft.api.aspects;

import net.minecraftforge.fml.common.FMLLog;
import java.lang.reflect.Method;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;

public class AspectSourceHelper {
    static Method drainEssentia;
    static Method findEssentia;

    public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range) {
        try {
            if (drainEssentia == null) {
                Class<?> fake = Class.forName("thaumcraft.common.lib.events.EssentiaHandler");
                drainEssentia = fake.getMethod("drainEssentia", TileEntity.class, Aspect.class, EnumFacing.class, Integer.TYPE);
            }
            return (Boolean)drainEssentia.invoke(null, tile, aspect, direction, range);
        }
        catch (Exception ex) {
            FMLLog.warning((String)"[Thaumcraft API] Could not invoke thaumcraft.common.lib.events.EssentiaHandler method drainEssentia", (Object[])new Object[0]);
            return false;
        }
    }

    public static boolean findEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range) {
        try {
            if (findEssentia == null) {
                Class<?> fake = Class.forName("thaumcraft.common.lib.events.EssentiaHandler");
                findEssentia = fake.getMethod("findEssentia", TileEntity.class, Aspect.class, EnumFacing.class, Integer.TYPE);
            }
            return (Boolean)findEssentia.invoke(null, tile, aspect, direction, range);
        }
        catch (Exception ex) {
            FMLLog.warning((String)"[Thaumcraft API] Could not invoke thaumcraft.common.lib.events.EssentiaHandler method findEssentia", (Object[])new Object[0]);
            return false;
        }
    }
}

