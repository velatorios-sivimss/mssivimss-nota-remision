package com.imss.sivimss.notasremision.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppConstantesTest {
    @Test
    public void constructorTest() throws Exception {
        Constructor constructor=AppConstantes.class.getDeclaredConstructor();
        assertTrue("AppConstantes class", Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        });
        assertNotNull(exception);
    }
    @Test
    public void appConstantesTest() throws Exception {
        assertNotNull(AppConstantes.SUPERVISOR);
        assertNotNull(AppConstantes.DATOS);
        assertNotNull(AppConstantes.QUERY);
        assertNotNull(AppConstantes.STATUSEXCEPTION);
        assertNotNull(AppConstantes.EXPIREDJWTEXCEPTION);
        assertNotNull(AppConstantes.MALFORMEDJWTEXCEPTION);
        assertNotNull(AppConstantes.UNSUPPORTEDJWTEXCEPTION);
        assertNotNull(AppConstantes.ILLEGALARGUMENTEXCEPTION);
        assertNotNull(AppConstantes.SIGNATUREEXCEPTION);
        assertNotNull(AppConstantes.FORBIDDENEXCEPTION);
        assertNotNull(AppConstantes.EXPIREDJWTEXCEPTION_MENSAJE);
        assertNotNull(AppConstantes.MALFORMEDJWTEXCEPTION_MENSAJE);
        assertNotNull(AppConstantes.UNSUPPORTEDJWTEXCEPTION_MENSAJE);
        assertNotNull(AppConstantes.ILLEGALARGUMENTEXCEPTION_MENSAJE);
        assertNotNull(AppConstantes.SIGNATUREEXCEPTION_MENSAJE);
        assertNotNull(AppConstantes.FORBIDDENEXCEPTION_MENSAJE);
    }
}
