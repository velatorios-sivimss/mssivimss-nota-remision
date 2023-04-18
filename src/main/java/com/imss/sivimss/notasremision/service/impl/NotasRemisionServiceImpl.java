package com.imss.sivimss.notasremision.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.imss.sivimss.notasremision.util.ProviderServiceRestTemplate;
import com.google.gson.Gson;
import com.imss.sivimss.notasremision.util.AppConstantes;
import com.imss.sivimss.notasremision.util.ConvertirGenerico;
import com.imss.sivimss.notasremision.beans.OrdenServicio;
import com.imss.sivimss.notasremision.exception.BadRequestException;
import com.imss.sivimss.notasremision.model.request.UsuarioDto;
import com.imss.sivimss.notasremision.beans.NotaRemision;
import com.imss.sivimss.notasremision.model.request.BusquedaDto;
import com.imss.sivimss.notasremision.model.request.NotaRemisionDto;
import com.imss.sivimss.notasremision.model.response.ODSGeneradaResponse;
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
		Gson gson = new Gson();
		OrdenServicio ordenServicio = new OrdenServicio();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		return providerRestTemplate.consumirServicio(ordenServicio.obtenerODS(request, busqueda).getDatos(), urlGenericoPaginado, 
				authentication);
		
	}
	
	@Override
	public Response<?> listadoODS(DatosRequest request, Authentication authentication) throws IOException {
		OrdenServicio ordenServicio = new OrdenServicio();
		List<ODSGeneradaResponse> ODSResponse;
		
		Response<?> response = providerRestTemplate.consumirServicio(ordenServicio.listadoODS().getDatos(), urlGenericoConsulta, 
				authentication);
		
		if (response.getCodigo() == 200) {
			ODSResponse = Arrays.asList(modelMapper.map(response.getDatos(), ODSGeneradaResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(ODSResponse));
		}
		return response;
	}

	@Override
	public Response<?> buscarODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		OrdenServicio ordenServicio = new OrdenServicio();
		
		return providerRestTemplate.consumirServicio(ordenServicio.buscarODS(request, busqueda).getDatos(), urlGenericoPaginado,
				authentication);
	}

	@Override
	public Response<?> detalleODS(DatosRequest request, Authentication authentication) throws IOException {
        OrdenServicio ordenServicio = new OrdenServicio();
		
		return providerRestTemplate.consumirServicio(ordenServicio.detalleODS(request).getDatos(), urlGenericoConsulta, 
				authentication);
	}
	
	@Override
	public Response<?> existeNotaRem(DatosRequest request, Authentication authentication) throws IOException {
        OrdenServicio ordenServicio = new OrdenServicio();
		
		return providerRestTemplate.consumirServicio(ordenServicio.existeNotaRem(request).getDatos(), urlGenericoConsulta, 
				authentication);
	}
	
	@Override
	public Response<?> detalleNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdNota() == null || notaDto.getIdOrden() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		NotaRemision notaRemision  = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());
		
		return providerRestTemplate.consumirServicio(notaRemision.detalleNotaRem(request).getDatos(), urlGenericoConsulta, 
				authentication);
	}
	
	@Override
	public Response<?> serviciosNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdNota() == null || notaDto.getIdOrden() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		NotaRemision notaRemision  = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());
		
		return providerRestTemplate.consumirServicio(notaRemision.serviciosNotaRem(request).getDatos(), urlGenericoConsulta, 
				authentication);
	}

	@Override
	public Response<?> generarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		if (notaDto.getIdNota() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		NotaRemision notaRemision  = new NotaRemision(0, notaDto.getIdOrden());
		notaRemision.setIdUsuarioAlta(usuarioDto.getIdUsuario());
		
		Response<?> request1 = providerRestTemplate.consumirServicio(notaRemision.ultimoFolioNota(request).getDatos(), urlGenericoConsulta,
				authentication);
		ArrayList<LinkedHashMap> datos1 = (ArrayList) request1.getDatos();
		String ultimoFolio = datos1.get(0).get("folio").toString();
		
		return providerRestTemplate.consumirServicio(notaRemision.generarNotaRem(ultimoFolio).getDatos(), urlGenericoCrear, authentication);
	}

	@Override
	public Response<?> cancelarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdNota() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		NotaRemision notaRemision  = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());
		notaRemision.setMotivo(notaDto.getMotivo());
		notaRemision.setIdUsuarioModifica(usuarioDto.getIdUsuario());
		
		return providerRestTemplate.consumirServicio(notaRemision.cancelarNotaRem().getDatos(), urlGenericoActualizar, authentication);
	}

}
