package com.imss.sivimss.notasremision.util;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ConvertirGenericoTest {
    @Test
    public void testConvertInstanceOfObject_ReturnO() {
        Object actual = ConvertirGenerico.convertInstanceOfObject(null);
        assertNull(actual);

        Object actual2 = ConvertirGenerico.convertInstanceOfObject("hola");
        assertEquals("hola",actual2);

        Exception exception = assertThrows(ClassCastException.class, () -> {
            Object o = new String();
            Object actual3 = ConvertirGenerico.convertInstanceOfObject((Integer) o);
        });
        assertNotNull(exception);
    }
}
