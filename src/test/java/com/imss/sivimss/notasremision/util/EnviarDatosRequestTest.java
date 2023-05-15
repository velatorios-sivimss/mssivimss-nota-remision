package com.imss.sivimss.notasremision.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnviarDatosRequestTest {
    @Test
    public void enviarDatosRequestTest() throws Exception {
        Map<String, Object> datos=new HashMap<>();
        datos.put("nombre","vacio");
        EnviarDatosRequest request=new EnviarDatosRequest(datos);
        assertNotNull(request);
    }
}
