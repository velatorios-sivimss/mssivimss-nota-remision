package com.imss.sivimss.notasremision.service.impl;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.imss.sivimss.notasremision.util.ProviderServiceRestTemplate;
import com.imss.sivimss.notasremision.service.NotasRemisionService;
import com.imss.sivimss.notasremision.util.DatosRequest;
import com.imss.sivimss.notasremision.util.Response;

@Service
public class NotasRemisionServiceImpl implements NotasRemisionService {
	
	@Value("${endpoints.dominio-paginado}")
	private String urlGenericoPaginado;
	
	@Value("${endpoints.dominio-consulta}")
	private String urlGenericoConsulta;
	
	@Value("${endpoints.dominio-crear}")
	private String urlGenericoCrear;
	
	@Value("${endpoints.dominio-actualizar}")
	private String urlGenericoActualizar;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public Response<?> consultarODS(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<?> buscarODS(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<?> detalleODS(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Response<?> existeNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<?> generarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<?> cancelarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
