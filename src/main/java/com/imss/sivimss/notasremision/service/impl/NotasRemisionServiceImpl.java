package com.imss.sivimss.notasremision.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.imss.sivimss.notasremision.model.request.FormatoNotaDto;
import com.imss.sivimss.notasremision.model.request.NotaRemisionDto;
import com.imss.sivimss.notasremision.model.response.ODSGeneradaResponse;
import com.imss.sivimss.notasremision.service.NotasRemisionService;
import com.imss.sivimss.notasremision.util.DatosRequest;
import com.imss.sivimss.notasremision.util.Response;
import com.imss.sivimss.notasremision.util.MensajeResponseUtil;

@Service
public class NotasRemisionServiceImpl implements NotasRemisionService {
	
	@Value("${endpoints.dominio}")
	private String urlDominioGenerico;
	
	private static final String PAGINADO = "paginado";
	
	private static final String CONSULTA = "consulta";
	
	private static final String ACTUALIZAR = "actualizar";
	
	@Value("${endpoints.generico-reportes}")
	private String urlReportes;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	private static final String NOMBREPDFNOTAREM = "reportes/generales/FormatoNotaRemision.jrxml";
	
	private static final String NOMBREPDFREPORTE = "reportes/generales/ReporteODSNotas.jrxml";
	
	private static final String INFONOENCONTRADA = "45";
	
	private static final String ERROR_DESCARGA = "64";
	
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

		return providerRestTemplate.consumirServicio(ordenServicio.obtenerODS(request, busqueda, formatoFecha).getDatos(), urlDominioGenerico + PAGINADO, 
				authentication);
	}
	
	@Override
	public Response<?> listadoODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		OrdenServicio ordenServicio = new OrdenServicio();
		List<ODSGeneradaResponse> ODSResponse;
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		Response<?> response = providerRestTemplate.consumirServicio(ordenServicio.listadoODS(busqueda).getDatos(), urlDominioGenerico + CONSULTA, 
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
		
		Response<?> response = providerRestTemplate.consumirServicio(ordenServicio.buscarODS(request, busqueda, formatoFecha).getDatos(), urlDominioGenerico + PAGINADO,
				authentication);
		ArrayList datos1 = (ArrayList) ((LinkedHashMap) response.getDatos()).get("content");
		if (datos1.isEmpty()) {
			response.setMensaje(INFONOENCONTRADA);
	    }
		
		return response;
	}

	@Override
	public Response<?> detalleODS(DatosRequest request, Authentication authentication) throws IOException {
        OrdenServicio ordenServicio = new OrdenServicio();
		
		return providerRestTemplate.consumirServicio(ordenServicio.detalleODS(request).getDatos(), urlDominioGenerico + CONSULTA, 
				authentication);
	}
	
	@Override
	public Response<?> existeNotaRem(DatosRequest request, Authentication authentication) throws IOException {
        OrdenServicio ordenServicio = new OrdenServicio();
		
		return providerRestTemplate.consumirServicio(ordenServicio.existeNotaRem(request).getDatos(), urlDominioGenerico + CONSULTA, 
				authentication);
	}
	
	@Override
	public Response<?> detalleNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdNota() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		NotaRemision notaRemision  = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());
		
		return providerRestTemplate.consumirServicio(notaRemision.detalleNotaRem(request, formatoFecha).getDatos(), urlDominioGenerico + CONSULTA, 
				authentication);
	}
	
	@Override
	public Response<?> serviciosNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdOrden() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		NotaRemision notaRemision  = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());
		
		return providerRestTemplate.consumirServicio(notaRemision.serviciosNotaRem(request).getDatos(), urlDominioGenerico + CONSULTA, 
				authentication);
	}

	@Override
	public Response<?> generarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		if (notaDto.getIdOrden() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
	
		NotaRemision notaRemision  = new NotaRemision(0, notaDto.getIdOrden());
		notaRemision.setIdUsuarioAlta(usuarioDto.getIdUsuario());
		Response<?> request1 = providerRestTemplate.consumirServicio(notaRemision.ultimoFolioNota(request).getDatos(), urlDominioGenerico + CONSULTA,
				authentication);
		ArrayList<LinkedHashMap> datos1 = (ArrayList) request1.getDatos();
		String ultimoFolio = datos1.get(0).get("folio").toString();
		
		return providerRestTemplate.consumirServicio(notaRemision.generarNotaRem(ultimoFolio).getDatos(), urlDominioGenerico + "crear", authentication);
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
		
		return providerRestTemplate.consumirServicio(notaRemision.cancelarNotaRem().getDatos(), urlDominioGenerico + ACTUALIZAR, authentication);
	}
	
	@Override
	public Response<?> descargarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdNota() == null || notaDto.getIdOrden() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		NotaRemision notaRemision  = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());
		
		Response<?> response1 = providerRestTemplate.consumirServicio(notaRemision.detalleNotaRem(request, formatoFecha).getDatos(), urlDominioGenerico + CONSULTA, 
				authentication);
		ArrayList<LinkedHashMap> datos1 = (ArrayList) response1.getDatos();
		
		FormatoNotaDto formatoNotaDto = new FormatoNotaDto();
		formatoNotaDto.setTipoReporte(notaDto.getTipoReporte());
		if (datos1.size() > 0) {
			formatoNotaDto.setNomVelatorio(datos1.get(0).get("nomVelatorio").toString());
			formatoNotaDto.setFolioNota(datos1.get(0).get("folioNota").toString());
			formatoNotaDto.setDirVelatorio(datos1.get(0).get("dirVelatorio").toString());
			formatoNotaDto.setNomSolicitante(datos1.get(0).get("nomSolicitante").toString());
			formatoNotaDto.setDirSolicitante(datos1.get(0).get("dirSolicitante").toString());
			formatoNotaDto.setCurpSolicitante(datos1.get(0).get("curpSolicitante").toString());
			formatoNotaDto.setVelatorioOrigen(datos1.get(0).get("velatorioOrigen").toString());
			formatoNotaDto.setNomFinado(datos1.get(0).get("nomFinado").toString());
			formatoNotaDto.setParFinado(datos1.get(0).get("parFinado").toString());
			formatoNotaDto.setFolioODS(datos1.get(0).get("folioODS").toString());
		}
		Map<String, Object> envioDatos = notaRemision.imprimirNotaRem(formatoNotaDto, NOMBREPDFNOTAREM);
		Response<?> response = providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
		
		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}
	
	@Override
	public Response<?> descargarDocumento(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto reporteDto = gson.fromJson(datosJson, BusquedaDto.class);
		
		Map<String, Object> envioDatos = new OrdenServicio().generarReporte(reporteDto, NOMBREPDFREPORTE, formatoFecha);
		Response<?> response =  providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
	
		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

}
