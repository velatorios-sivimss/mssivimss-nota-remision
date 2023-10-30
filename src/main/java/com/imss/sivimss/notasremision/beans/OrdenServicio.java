package com.imss.sivimss.notasremision.beans;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.notasremision.model.request.BusquedaDto;
import com.imss.sivimss.notasremision.util.AppConstantes;
import com.imss.sivimss.notasremision.util.DatosRequest;
import com.imss.sivimss.notasremision.util.QueryHelper;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrdenServicio {

	private Integer id;
	private String fechaODS;
	private String folioODS;
	private String folioConvenio;
	private Integer idContratante;
	private String nomContratante;
	private Integer idFinado;
	private String nomFinado;
	private Integer estatus;

	private static final Logger logger = LoggerFactory.getLogger(OrdenServicio.class);

	public DatosRequest obtenerODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha)
			throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
		if (busqueda.getIdOficina() > 1) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
			if (busqueda.getIdOficina() == 3) {
				query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
		}

		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);

		return request;
	}

	public DatosRequest listadoODS(BusquedaDto busqueda) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO, os.CVE_FOLIO  ");
		query.append("FROM SVC_ORDEN_SERVICIO os  ");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO)  ");
		query.append("JOIN SVC_VELATORIO vel ON (os.ID_VELATORIO = vel.ID_VELATORIO)  ");
		query.append("INNER JOIN SVT_CONVENIO_PF cvn ON ( cvn.ID_CONVENIO_PF = fin.ID_CONTRATO_PREVISION   )\r\n"
				+ "INNER JOIN SVC_CONTRATANTE con ON ( con.ID_CONTRATANTE = os.ID_CONTRATANTE)\r\n"
				+ "INNER JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA )\r\n"
				+ "INNER JOIN SVC_PERSONA prf ON ( fin.ID_PERSONA = prf.ID_PERSONA ) ");
		query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO = 2 ");
		query.append("AND fin.ID_TIPO_ORDEN in(2,4) ");

		if (busqueda.getIdDelegacion() != null) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
			if (busqueda.getIdVelatorio() != null) {
				query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
		}

		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

	public DatosRequest buscarODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha)
			throws UnsupportedEncodingException {
		StringBuilder query = busqueda(formatoFecha);
		
		if (busqueda.getIdVelatorio() != null) {
			query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
		}
		
		if (busqueda.getIdDelegacion() != null) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
		}

		if (busqueda.getFolioODS() != null) {
			query.append(" AND os.CVE_FOLIO = '" + busqueda.getFolioODS() + "' ");
		}
		
		if (busqueda.getFecIniODS() != null && busqueda.getFecFinODS() != null) {
			query.append( " AND nr.FEC_ALTA BETWEEN '" + busqueda.getFecIniODS() + "' AND '" + busqueda.getFecFinODS() + "' " );
			
			query.append(" AND nr.IND_ESTATUS in (2, 3)");
			logger.info("busqueda generadas");
			logger.info(query.toString());
			String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
			request.getDatos().put(AppConstantes.QUERY, encoded);
			
		}else {
			
			logger.info("busqueda por ODS general");
			logger.info(query.toString());
			String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
			request.getDatos().put(AppConstantes.QUERY, encoded);
			
		}
		
			return request;

	}

	private StringBuilder busqueda(String formatoFecha) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT \r\n"
				+ "os.ID_ORDEN_SERVICIO AS id,\r\n"
				+ "nr.NUM_FOLIO AS folioNota,\r\n"
				+ "DATE_FORMAT(os.FEC_ALTA, '");
		query.append(formatoFecha);
		query.append("') AS fechaODS,\r\n"
				+ "os.CVE_FOLIO AS folioODS,\r\n"
				+ "cvn.DES_FOLIO AS folioConvenio,\r\n"
				+ "os.ID_CONTRATANTE AS idContratante,\r\n"
				+ "CONCAT(\r\n"
				+ "IFNULL(prc.NOM_PERSONA, ''), ' ',\r\n"
				+ "IFNULL(prc.NOM_PRIMER_APELLIDO, ''), ' ',\r\n"
				+ "IFNULL(prc.NOM_SEGUNDO_APELLIDO, '')\r\n"
				+ ") AS nomContratante,\r\n"
				+ "fin.ID_FINADO AS idFinado,\r\n"
				+ "CONCAT(\r\n"
				+ "prf.NOM_PERSONA,\r\n"
				+ "' ',\r\n"
				+ "prf.NOM_PRIMER_APELLIDO,\r\n"
				+ "' ',\r\n"
				+ "prf.NOM_SEGUNDO_APELLIDO\r\n"
				+ ") AS nomFinado,\r\n"
				+ "IFNULL(nr.IND_ESTATUS, 1) AS estatus,\r\n"
				+ "IFNULL(nr.ID_NOTAREMISION, 0) AS idNota,\r\n"
				+ "IFNULL(nr.ID_NOTAREMISION, 0) AS idCancelada,\r\n"
				+ "( \r\n"
				+ "SELECT COUNT(ID_NOTAREMISION)\r\n"
				+ "FROM\r\n"
				+ "SVT_NOTA_REMISION\r\n"
				+ "WHERE\r\n"
				+ "ID_ORDEN_SERVICIO = id\r\n"
				+ "AND\r\n"
				+ "IND_ESTATUS = 3\r\n"
				+ ") AS total,\r\n"
				+ "IFNULL(ENR.DES_ESTATUS, 'Sin nota') AS DesEstatus,\r\n"
				+ "os.ID_VELATORIO AS idVelatorio,\r\n"
				+ "vel.ID_DELEGACION AS idDelegacion\r\n"
				+ "FROM\r\n"
				+ "SVC_ORDEN_SERVICIO os\r\n"
				+ "INNER JOIN SVC_FINADO fin ON ( os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO )\r\n"
				+ "INNER JOIN SVT_CONVENIO_PF cvn ON ( cvn.ID_CONVENIO_PF = fin.ID_CONTRATO_PREVISION   )\r\n"
				+ "INNER JOIN SVC_CONTRATANTE con ON ( con.ID_CONTRATANTE = os.ID_CONTRATANTE)\r\n"
				+ "INNER JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA )\r\n"
				+ "INNER JOIN SVC_PERSONA prf ON ( fin.ID_PERSONA = prf.ID_PERSONA )\r\n"
				+ "JOIN SVC_VELATORIO vel ON (os.ID_VELATORIO = vel.ID_VELATORIO)\r\n"
				+ "LEFT JOIN SVT_NOTA_REMISION nr ON ( os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO )\r\n"
				+ "LEFT JOIN SVC_ESTATUS_NOTA_REM ENR ON ( ENR.ID_ESTATUS_NOTA_REM = nr.IND_ESTATUS )\r\n"
				+ "WHERE os.ID_ESTATUS_ORDEN_SERVICIO IN (2,6)\r\n"
				+ "AND fin.ID_TIPO_ORDEN in(2) ");
		
		return query;
	}
	
	public DatosRequest detalleODS(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idODS = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "os.CVE_FOLIO AS folioODS,\r\n"
				+ "DATE_FORMAT(IFNULL(os.FEC_ALTA,0),'");
		query.append(formatoFecha);
		query.append("') AS fechaODS,\r\n"
				+ "vel.DES_VELATORIO AS nomVelatorio, \r\n"
				+ "CONCAT(IFNULL(domv.REF_CALLE,''),' ',IFNULL(domv.NUM_EXTERIOR,''),' ',IFNULL(domv.REF_COLONIA,'')) AS dirVelatorio, \r\n"
				+ "CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \r\n"
				+ "IFNULL(par.DES_PARENTESCO, ' ') AS parFinado, \r\n"
				+ "vel. NOM_RESPO_SANITARIO AS nomResponsable, \r\n"
				+ "CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomSolicitante, \r\n"
				+ "CONCAT(IFNULL(domc.REF_CALLE,''),' ',IFNULL(domc.NUM_EXTERIOR,''),' ',IFNULL(domc.REF_COLONIA,'')) AS dirSolicitante, \r\n"
				+ "prc.CVE_CURP AS curpSolicitante,\r\n"
				+ "vel.DES_VELATORIO AS velatorioOrigen, \r\n"
				+ "IFNULL(cvn.DES_FOLIO,0) AS folioConvenio,\r\n"
				+ "DATE_FORMAT(IFNULL(cvn.FEC_INICIO,0),'");
		query.append(formatoFecha);
		query.append("') AS fechaConvenio,\r\n"
				+ "(\r\n"
				+ "SELECT LPAD\r\n"
				+ "(\r\n"
				+ "IFNULL(\r\n"
				+ "MAX(\r\n"
				+ "CAST( NUM_FOLIO AS double)+1\r\n"
				+ "),1\r\n"
				+ "),6,'0'\r\n"
				+ ") FROM SVT_NOTA_REMISION) AS folioNota \r\n"
				+ "FROM SVC_ORDEN_SERVICIO os \r\n"
				+ "INNER JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO)\r\n"
				+ "LEFT JOIN SVT_DOMICILIO domv ON (vel.ID_DOMICILIO = domv.ID_DOMICILIO) \r\n"
				+ "INNER JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO)\r\n"
				+ "LEFT JOIN SVC_PARENTESCO par ON (os.ID_PARENTESCO = par.ID_PARENTESCO)\r\n"
				+ "LEFT JOIN SVT_CONVENIO_PF cvn ON (cvn.ID_CONVENIO_PF = fin.ID_CONTRATO_PREVISION) \r\n"
				+ "LEFT JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA)\r\n"
				+ "JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \r\n"
				+ "LEFT JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA)\r\n"
				+ "LEFT JOIN SVT_DOMICILIO domc ON (con.ID_DOMICILIO = domc.ID_DOMICILIO)");
		
		query.append("WHERE os.ID_ORDEN_SERVICIO = " + idODS);

		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().remove("id");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest existeNotaRem(DatosRequest request) throws UnsupportedEncodingException {
		String idODS = request.getDatos().get("id").toString();
		String query = "SELECT COUNT(NUM_FOLIO) AS valor FROM SVT_NOTA_REMISION WHERE ID_ORDEN_SERVICIO = " + idODS;

		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().remove("id");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest actualizaEstatus(String estatus) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVC_ORDEN_SERVICIO");
		q.agregarParametroValues("ID_ESTATUS_ORDEN_SERVICIO", estatus);
		q.addWhere("ID_ORDEN_SERVICIO = " + this.id);

		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

	private StringBuilder armaQuery(String formatoFecha) {
		StringBuilder query = new StringBuilder(
				"SELECT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha
						+ "') AS fechaODS, \n");
		query.append("IFNULL(cvn.DES_FOLIO,0) AS folioConvenio, os.ID_CONTRATANTE AS idContratante, \n");
		query.append(
				"CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append(
				"fin.ID_FINADO AS idFinado, CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append(
				"IFNULL(nr.IND_ESTATUS,1) AS estatus, IFNULL(nr.ID_NOTAREMISION,0) AS idNota, IFNULL(nrc.ID_NOTAREMISION,0) AS idCancelada \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("LEFT JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("JOIN SVT_CONTRA_PAQ_CONVENIO_PF cpcf ON (con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE) \n");
		query.append("  JOIN SVT_CONVENIO_PF cvn ON (cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF) \n");
		query.append("LEFT JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("LEFT JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
		query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO \n");
		query.append("LEFT JOIN SVT_NOTA_REMISION nr ON (os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO) \n");
		query.append(
				"LEFT JOIN SVT_NOTA_REMISION nrc ON (os.ID_ORDEN_SERVICIO = nrc.ID_ORDEN_SERVICIO AND nrc.ID_ESTATUS = 3)  ");
		query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO =2  ");
		query.append("AND fin.ID_TIPO_ORDEN in(2,4)");

		return query;
	}

	public Map<String, Object> generarReporte(BusquedaDto reporteDto, String nombrePdfReportes, String formatoFecha) {
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");
		if (reporteDto.getIdVelatorio() != null) {
			condicion.append(" AND os.ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
		}
		if (reporteDto.getIdDelegacion() != null) {
			condicion.append(" AND vel.ID_DELEGACION = ").append(reporteDto.getIdDelegacion());
		}
		if (reporteDto.getFolioODS() != null) {
			condicion.append(" AND os.CVE_FOLIO = '" + reporteDto.getFolioODS() + "' ");
		}
		if (reporteDto.getFecIniODS() != null) {
			condicion.append(" AND DATE(nr.FEC_ALTA) BETWEEN STR_TO_DATE('" + reporteDto.getFecIniODS() + "','"
					+ formatoFecha + "') AND STR_TO_DATE('" + reporteDto.getFecFinODS() + "','" + formatoFecha + "')");
		}

		envioDatos.put("condicion", condicion.toString());
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		if (reporteDto.getTipoReporte().equals("xls")) {
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}

		return envioDatos;
	}

}
