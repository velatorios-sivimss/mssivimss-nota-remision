package com.imss.sivimss.notasremision.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.imss.sivimss.notasremision.model.request.LlavesTablasUpd;
import com.imss.sivimss.notasremision.model.request.NotaRemisionDto;
import com.imss.sivimss.notasremision.model.response.BeneficiarioResponse;
import com.imss.sivimss.notasremision.model.response.ODSGeneradaResponse;
import com.imss.sivimss.notasremision.service.NotasRemisionService;
import com.imss.sivimss.notasremision.util.DatosRequest;
import com.imss.sivimss.notasremision.util.Response;
import com.imss.sivimss.notasremision.model.request.ActualizarMultiRequest;
import com.imss.sivimss.notasremision.util.LogUtil;
import com.imss.sivimss.notasremision.util.MensajeResponseUtil;
import com.imss.sivimss.notasremision.util.NotaUtil;

@Service
public class NotasRemisionServiceImpl implements NotasRemisionService {

	@Value("${endpoints.dominio}")
	private String urlDominioGenerico;

	private static final String PAGINADO = "/paginado";

	private static final String CONSULTA = "/consulta";

	private static final String ACTUALIZAR = "/actualizar";

	private static final String CREAR = "/crear";

	private static final String MULTIPLE = "/insertarMultiple";
	private static final String ACTUALIZAR_MULTIPLES = "/actualizar/multiples";
	
	@Value("${endpoints.generico-reportes}")
	private String urlReportes;

	@Value("${formato_fecha}")
	private String formatoFecha;

	private static final String NOMBREPDFNOTAREM = "reportes/generales/FormatoNotaRemision.jrxml";

	private static final String NOMBREPDFREPORTE = "reportes/generales/ReporteODSNotas.jrxml";

	private static final String INFONOENCONTRADA = "45";

	private static final String ERROR_DESCARGA = "64";

	private static final String CONCLUIDA = "6";

	private static final String GENERADA = "2";

	private static final String ALTA = "alta";

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private LogUtil logUtil;

	private static final Logger log = LoggerFactory.getLogger(NotasRemisionServiceImpl.class);

	@Override
	public Response<?> consultarODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		OrdenServicio ordenServicio = new OrdenServicio();

		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);

		try {
			return providerRestTemplate.consumirServicio(
					ordenServicio.buscarODS(request, busqueda, formatoFecha).getDatos(), urlDominioGenerico + PAGINADO,
					authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
		}
	}

	@Override
	public Response<?> listadoODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		OrdenServicio ordenServicio = new OrdenServicio();
		List<ODSGeneradaResponse> ODSResponse;

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		Response<?> response = providerRestTemplate.consumirServicio(ordenServicio.listadoODS(busqueda).getDatos(),
				urlDominioGenerico + CONSULTA,
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
		Response<?> response = new Response(false, 200, "Exito");
		try {
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
			BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
			OrdenServicio ordenServicio = new OrdenServicio();

			response = providerRestTemplate.consumirServicio(
					ordenServicio.buscarODS(request, busqueda, formatoFecha).getDatos(), urlDominioGenerico + PAGINADO,
					authentication);
			ArrayList datos1 = (ArrayList) ((LinkedHashMap) response.getDatos()).get("content");
			if (datos1.isEmpty()) {
				response.setMensaje(INFONOENCONTRADA);
			}

		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return response;

	}

	@Override
	public Response<?> detalleODS(DatosRequest request, Authentication authentication) throws IOException {
		OrdenServicio ordenServicio = new OrdenServicio();

		try {
			return providerRestTemplate.consumirServicio(ordenServicio.detalleODS(request, formatoFecha).getDatos(),
					urlDominioGenerico + CONSULTA,
					authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			throw new IOException("Error al ejectuar el Query", e.getCause());
		}
	}

	@Override
	public Response<?> existeNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		OrdenServicio ordenServicio = new OrdenServicio();

		return providerRestTemplate.consumirServicio(ordenServicio.existeNotaRem(request).getDatos(),
				urlDominioGenerico + CONSULTA,
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
		NotaRemision notaRemision = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());

		try {
			return providerRestTemplate.consumirServicio(notaRemision.detalleNotaRem(request, formatoFecha).getDatos(),
					urlDominioGenerico + CONSULTA,
					authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
		}
	}

	@Override
	public Response<?> serviciosNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdOrden() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		NotaRemision notaRemision = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());

		return providerRestTemplate.consumirServicio(notaRemision.serviciosNotaRem(request).getDatos(),
				urlDominioGenerico + CONSULTA,
				authentication);
	}

	@Override
	public Response<?> generarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + "generando nota de remision", authentication);
		
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		if (notaDto.getIdOrden() == null) {
			log.error("fallo");
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
					this.getClass().getPackage().toString(), "","No se envio idOrden", authentication);
			
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		try {
			OrdenServicio ordenServicio = new OrdenServicio();
			ordenServicio.setId(notaDto.getIdOrden());
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
					this.getClass().getPackage().toString(), "",CONSULTA +" " + "ods actualizada", authentication);
			
			NotaRemision notaRemision = new NotaRemision(0, notaDto.getIdOrden());
			notaRemision.setIdUsuarioAlta(usuarioDto.getIdUsuario());
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
					this.getClass().getPackage().toString(), "",CONSULTA +" " + "obteniendo datos tipo prevision y psfpa", authentication);
			
			Response<?> request1 = providerRestTemplate.consumirServicio(
					notaRemision.obtenTipoPrevision(request).getDatos(), urlDominioGenerico + CONSULTA, authentication);
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
					this.getClass().getPackage().toString(), "",CONSULTA +" " + "informacion extraida", authentication);
			
			
			ArrayList<LinkedHashMap> datos1 = (ArrayList<LinkedHashMap>) request1.getDatos();
			
			LlavesTablasUpd llavesTablasUpd = new LlavesTablasUpd();
			llavesTablasUpd.setIdContratante( (Integer) datos1.get(0).get("idContratante") );
			llavesTablasUpd.setIdContratantePaquete( (Integer) datos1.get(0).get("idContratantePaquete") );
			llavesTablasUpd.setIdConvenioPF( (Integer) datos1.get(0).get("idConvenioPF") );
			llavesTablasUpd.setIdConvenioSFPA( (Integer) datos1.get(0).get("idConvenioSFPA") );
			llavesTablasUpd.setIdPerContratante( (Integer) datos1.get(0).get("idPerContratante") );
			llavesTablasUpd.setIdPerFinado( (Integer) datos1.get(0).get("idPerFinado") );
			llavesTablasUpd.setIdTipoOrden( (Integer) datos1.get(0).get("idTipoOrden") );
			llavesTablasUpd.setIdTipoPrevision( (Integer) datos1.get(0).get("idTipoPrevision") );
			String estatus = (String) datos1.get(0).get("idEstatusConvenio");
			llavesTablasUpd.setIdEstatusConvenio( Integer.parseInt(estatus) );
			
			request1 = providerRestTemplate.consumirServicio(
					notaRemision.obtBeneficiarios(request, llavesTablasUpd.getIdContratantePaquete()).getDatos(), urlDominioGenerico + CONSULTA, authentication);
			
			List<BeneficiarioResponse> beneficiarios =  Arrays.asList(modelMapper.map(request1.getDatos(), BeneficiarioResponse[].class));
			
			// Actualiza estatus de la ODS
			log.info("actualizando estatus ods");
			providerRestTemplate.consumirServicio(ordenServicio.actualizaEstatus(CONCLUIDA).getDatos(),
					urlDominioGenerico + ACTUALIZAR, authentication);

			// Actualizar estatus de convenio

			log.info("extraccion de informacion correcta");
			DatosRequest request2 = notaRemision.actualizaEstatusCrear(llavesTablasUpd, beneficiarios);
			
			if( request2 != null) {
				providerRestTemplate.consumirServicio(request2.getDatos(),
						urlDominioGenerico + MULTIPLE, authentication);
			}
			
			log.info("se actualizo en estatus");

			// Registro de nota de remisión
			request1 = providerRestTemplate.consumirServicio(notaRemision.ultimoFolioNota(request).getDatos(),
					urlDominioGenerico + CONSULTA,
					authentication);
			log.info("se registro la nota de remision");
			datos1 = (ArrayList<LinkedHashMap>) request1.getDatos();
			String ultimoFolio = datos1.get(0).get("folio").toString();
			Double folioD = Double.parseDouble(ultimoFolio);
			Integer folioI = folioD.intValue();
			
			Response<?> salida;
			
			if( notaDto.getIdNota() == null || notaDto.getIdNota().equals(0)) {
				salida = providerRestTemplate.consumirServicio(
						notaRemision.generarNotaRem(folioI).getDatos(),
						urlDominioGenerico + CREAR, authentication);
			}else {
				salida = providerRestTemplate.consumirServicio(
						notaRemision.actNotaFolio(GENERADA, notaDto.getIdNota(), usuarioDto.getIdUsuario(), folioI).getDatos(),
						urlDominioGenerico + CREAR, authentication);
			}
			
			log.info("{}", salida);
			return salida;
		} catch (Exception e) {
			log.error(e.getMessage());
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), e.getMessage(), ALTA, authentication);
			return null;
		}
	}

	@Override
	public Response<?> cancelarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		ActualizarMultiRequest actualizarMultiRequest = new ActualizarMultiRequest();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		NotaUtil notaUtil = new NotaUtil();
		String encoded;
		String query;
		ArrayList<String> querys = new ArrayList<>();
		Response<Object> response;
		
		if (notaDto.getIdNota() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		
		NotaRemision notaRemision = new NotaRemision(0, notaDto.getIdOrden());
		notaRemision.setIdUsuarioAlta(usuarioDto.getIdUsuario());
		Response<?> request1 = providerRestTemplate.consumirServicio(
				notaRemision.obtenTipoPrevision(request).getDatos(), urlDominioGenerico + CONSULTA, authentication);
		log.info("informacion extraida");
		ArrayList<LinkedHashMap> datos1 = (ArrayList<LinkedHashMap>) request1.getDatos();
		
		LlavesTablasUpd llavesTablasUpd = new LlavesTablasUpd();
		llavesTablasUpd.setIdContratante( (Integer) datos1.get(0).get("idContratante") );
		llavesTablasUpd.setIdContratantePaquete( (Integer) datos1.get(0).get("idContratantePaquete") );
		llavesTablasUpd.setIdConvenioPF( (Integer) datos1.get(0).get("idConvenioPF") );
		llavesTablasUpd.setIdConvenioSFPA( (Integer) datos1.get(0).get("idConvenioSFPA") );
		llavesTablasUpd.setIdPerContratante( (Integer) datos1.get(0).get("idPerContratante") );
		llavesTablasUpd.setIdPerFinado( (Integer) datos1.get(0).get("idPerFinado") );
		llavesTablasUpd.setIdTipoOrden( (Integer) datos1.get(0).get("idTipoOrden") );
		llavesTablasUpd.setIdTipoPrevision( (Integer) datos1.get(0).get("idTipoPrevision") );
		String estatus = (String) datos1.get(0).get("idEstatusConvenio");
		llavesTablasUpd.setIdEstatusConvenio( Integer.parseInt(estatus) );
		
		request1 = providerRestTemplate.consumirServicio(
				notaRemision.obtBeneficiarios(request, llavesTablasUpd.getIdContratantePaquete()).getDatos(), urlDominioGenerico + CONSULTA, authentication);
		
		List<BeneficiarioResponse> beneficiarios =  Arrays.asList(modelMapper.map(request1.getDatos(), BeneficiarioResponse[].class));
		
		querys =  notaRemision.actualizaEstatusCancelar(llavesTablasUpd, beneficiarios);
		
		
		/**
		 * Se crea Query para actualizar la Orden de Servicio a Generada
		 */
		query = notaUtil.actEstatusOds( GENERADA, notaDto.getIdOrden());
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		querys.add( encoded );

		/**
		 * Se crea Query para cancelar la Nota de Remision
		 */
		query = notaUtil.cancelarNotaRem(notaDto.getMotivo(), usuarioDto.getIdUsuario(), notaDto.getIdNota() );
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		querys.add( encoded );
		
		/**
		 * Se crea Query para crear la nueva Nota de Remision
		 */
		query = notaUtil.generarNotaRem(notaDto.getIdOrden(), usuarioDto.getIdUsuario() );
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		querys.add( encoded );
		
		actualizarMultiRequest.setUpdates(querys);
		
		response = providerRestTemplate.consumirServicioActMult(actualizarMultiRequest, urlDominioGenerico + ACTUALIZAR_MULTIPLES, 
				authentication);
		
		return response;
	}

	@Override
	public Response<?> descargarNotaRem(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		NotaRemisionDto notaDto = gson.fromJson(datosJson, NotaRemisionDto.class);
		if (notaDto.getIdNota() == null || notaDto.getIdOrden() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}

		NotaRemision notaRemision = new NotaRemision(notaDto.getIdNota(), notaDto.getIdOrden());
		FormatoNotaDto formatoNotaDto = new FormatoNotaDto();
		if (notaDto.getIdNota() > 0) {
			Response<?> response1 = providerRestTemplate.consumirServicio(
					notaRemision.detalleNotaRem(request, formatoFecha).getDatos(), urlDominioGenerico + CONSULTA,
					authentication);
			ArrayList<LinkedHashMap> datos1 = (ArrayList<LinkedHashMap>) response1.getDatos();

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
				formatoNotaDto.setFolioConvenio(datos1.get(0).get("folioConvenio").toString());
				
				if( datos1.get(0).get("fechaConvenio") == null) {
					formatoNotaDto.setFechaConvenio( " " );
				}else {
					formatoNotaDto.setFechaConvenio(datos1.get(0).get("fechaConvenio").toString());
				}
				
				if( datos1.get(0).get("fechaODS") == null) {
					formatoNotaDto.setFechaODS( " " );
				}else {
					formatoNotaDto.setFechaODS(datos1.get(0).get("fechaODS").toString());
				}
				
			}
		} else {
			formatoNotaDto = gson.fromJson(datosJson, FormatoNotaDto.class);
		}
		
		Map<String, Object> envioDatos = notaRemision.imprimirNotaRem(formatoNotaDto, NOMBREPDFNOTAREM);
		Response<Object> response = providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);

		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

	@Override
	public Response<Object> descargarDocumento(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto reporteDto = gson.fromJson(datosJson, BusquedaDto.class);
		Map<String, Object> envioDatos = new OrdenServicio().buscarODS(request, reporteDto, formatoFecha).getDatos();
		String query = queryDecoded(envioDatos);
		envioDatos.put("condicion", query);
		envioDatos.put("rutaNombreReporte", NOMBREPDFREPORTE);
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		if (reporteDto.getTipoReporte().equals("xls")) {
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}
		Response<Object> response =  providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
	
		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

	private String queryDecoded (Map<String, Object> envioDatos ) {
		return new String(DatatypeConverter.parseBase64Binary(envioDatos.get(AppConstantes.QUERY).toString()));
	}
}
