package com.imss.sivimss.notasremision.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.notasremision.util.DatosRequest;
import com.imss.sivimss.notasremision.util.Response;

public interface NotasRemisionService {
	
	Response<?> consultarODS(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> buscarODS(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> detalleODS(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> existeNotaRem(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> generarNotaRem(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> cancelarNotaRem(DatosRequest request, Authentication authentication) throws IOException;

}
