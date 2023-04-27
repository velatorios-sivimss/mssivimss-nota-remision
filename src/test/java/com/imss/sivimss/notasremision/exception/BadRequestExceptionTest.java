package com.imss.sivimss.notasremision.exception;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.imss.sivimss.notasremision.util.UtUtils.createInstance;
import static com.imss.sivimss.notasremision.util.UtUtils.setField;
import static org.junit.Assert.assertNull;

public final class BadRequestExceptionTest {
    @Test
    public void testGetEstado_ReturnCodigo() throws Exception {
        BadRequestException badRequestException = ((BadRequestException) createInstance("com.imss.sivimss.notasremision.exception.BadRequestException"));
        setField(badRequestException, "com.imss.sivimss.notasremision.exception.BadRequestException", "codigo", null);

        HttpStatus actual = badRequestException.getEstado();

        assertNull(actual);
    }

    @Test
    public void testGetMensaje_ReturnMensaje() throws Exception {
        BadRequestException badRequestException = ((BadRequestException) createInstance("com.imss.sivimss.notasremision.exception.BadRequestException"));
        setField(badRequestException, "com.imss.sivimss.notasremision.exception.BadRequestException", "mensaje", null);

        String actual = badRequestException.getMensaje();

        assertNull(actual);
    }
}
