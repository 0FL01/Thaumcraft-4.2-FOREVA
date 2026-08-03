package thaumcraft.api.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GolemInfoApiContractTest {
    @Test
    public void interfaceIsExactReadOnlyScalarContract() {
        Map<String, Class<?>> methods = new HashMap<>();
        for (Method method : IGolemInfo.class.getDeclaredMethods()) {
            methods.put(method.getName() + descriptor(method), method.getReturnType());
            assertFalse("Golem info API must not expose mutators", method.getName().startsWith("set"));
            assertFalse("Golem info API must not expose arrays", method.getReturnType().isArray());
            assertFalse("Golem info API must not expose common implementation types",
                    method.getReturnType().getName().startsWith("thaumcraft.common."));
        }

        assertEquals(4, methods.size());
        assertEquals(byte.class, methods.get("getCore()"));
        assertEquals(int.class, methods.get("getGolemTypeId()"));
        assertEquals(boolean.class, methods.get("isAdvancedGolem()"));
        assertEquals(int.class, methods.get("getUpgradeAmount(int)"));
        assertTrue(IGolemInfo.class.isAssignableFrom(EntityGolemBase.class));
    }

    @Test
    public void constantsRemainStableAndMatchCurrentGolemTypes() throws Exception {
        assertEquals(-1, GolemIds.CORE_NONE);
        assertSequentialByteConstants("CORE_", 0, 11);
        assertEquals(EnumGolemType.values().length, 8);
        assertEquals(EnumGolemType.STRAW.ordinal(), GolemIds.TYPE_STRAW);
        assertEquals(EnumGolemType.WOOD.ordinal(), GolemIds.TYPE_WOOD);
        assertEquals(EnumGolemType.TALLOW.ordinal(), GolemIds.TYPE_TALLOW);
        assertEquals(EnumGolemType.CLAY.ordinal(), GolemIds.TYPE_CLAY);
        assertEquals(EnumGolemType.FLESH.ordinal(), GolemIds.TYPE_FLESH);
        assertEquals(EnumGolemType.STONE.ordinal(), GolemIds.TYPE_STONE);
        assertEquals(EnumGolemType.IRON.ordinal(), GolemIds.TYPE_IRON);
        assertEquals(EnumGolemType.THAUMIUM.ordinal(), GolemIds.TYPE_THAUMIUM);
        assertSequentialIntConstants("UPGRADE_", 0, 5);
    }

    private static String descriptor(Method method) {
        StringBuilder descriptor = new StringBuilder("(");
        for (Class<?> parameter : method.getParameterTypes()) {
            descriptor.append(parameter == int.class ? "int" : parameter.getName());
            descriptor.append(',');
        }
        if (descriptor.charAt(descriptor.length() - 1) == ',') {
            descriptor.setLength(descriptor.length() - 1);
        }
        return descriptor.append(')').toString();
    }

    private static void assertSequentialByteConstants(String prefix, int first, int last) throws Exception {
        boolean[] seen = new boolean[last - first + 1];
        for (Field field : GolemIds.class.getDeclaredFields()) {
            if (!field.getName().startsWith(prefix) || field.getName().equals("CORE_NONE")) continue;
            assertTrue(Modifier.isPublic(field.getModifiers()));
            assertTrue(Modifier.isStatic(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
            int value = field.getByte(null);
            assertTrue(value >= first && value <= last);
            seen[value - first] = true;
        }
        for (boolean present : seen) assertTrue(present);
    }

    private static void assertSequentialIntConstants(String prefix, int first, int last) throws Exception {
        boolean[] seen = new boolean[last - first + 1];
        for (Field field : GolemIds.class.getDeclaredFields()) {
            if (!field.getName().startsWith(prefix)) continue;
            int value = field.getInt(null);
            assertTrue(value >= first && value <= last);
            seen[value - first] = true;
        }
        for (boolean present : seen) assertTrue(present);
    }
}
