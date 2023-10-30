package com.imss.sivimss.notasremision.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.notasremision.util.QueryHelper;
import com.imss.sivimss.notasremision.model.request.FormatoNotaDto;
import com.imss.sivimss.notasremision.model.request.LlavesTablasUpd;
import com.imss.sivimss.notasremision.util.AppConstantes;
import com.imss.sivimss.notasremision.util.DatosRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class NotaRemision {

	private Integer id;
	private String numFolio;
	private Integer idOrden;
	private Byte idEstatus;
	private String motivo;
	private Integer idUsuarioAlta;
	private Integer idUsuarioModifica;
	private static final Logger logg = LoggerFactory.getLogger(NotaRemision.class);

	public NotaRemision(Integer id, Integer idOrden) {
		this.id = id;
		this.idOrden = idOrden;
	}

	public DatosRequest ultimoFolioNota(DatosRequest request) {
		String query = "SELECT \r\n"
				+ "IFNULL(\r\n"
				+ "MAX(\r\n"
				+ "CAST( NUM_FOLIO AS double)\r\n"
				+ ")\r\n"
				+ ",0) AS folio \r\n"
				+ "FROM SVT_NOTA_REMISION";
		logg.info(query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest serviciosNotaRem(DatosRequest request) {
		StringBuilder query = new StringBuilder(
				"SELECT pq.REF_PAQUETE_DESCRIPCION AS nomPaquete, ar.REF_ARTICULO AS nomServicio, dcp.CAN_DET_PRESUP AS cantidad \n");
		query.append("FROM SVC_CARAC_PRESUPUESTO cp \n");
		query.append("JOIN SVT_PAQUETE pq ON (cp.ID_PAQUETE = pq.ID_PAQUETE) \n");
		query.append("JOIN SVC_DETALLE_CARAC_PRESUP dcp ON (cp.ID_CARAC_PRESUPUESTO = dcp.ID_CARAC_PRESUPUESTO) \n");
		query.append("JOIN SVT_INVENTARIO_ARTICULO ia ON (dcp.ID_INVE_ARTICULO = ia.ID_INVE_ARTICULO) \n");
		query.append("JOIN SVT_ARTICULO ar ON (ia.ID_ARTICULO = ar.ID_ARTICULO) \n");
		query.append("WHERE dcp.ID_INVE_ARTICULO IS NOT NULL AND cp.ID_ORDEN_SERVICIO = " + this.idOrden + " \n");
		query.append("UNION \n");
		query.append(
				"SELECT pq.REF_PAQUETE_DESCRIPCION AS nomPaquete, sv.DES_SERVICIO AS nomServicio, dcp.CAN_DET_PRESUP AS cantidad \n");
		query.append("FROM SVC_CARAC_PRESUPUESTO cp \n");
		query.append("JOIN SVT_PAQUETE pq ON (cp.ID_PAQUETE = pq.ID_PAQUETE) \n");
		query.append("JOIN SVC_DETALLE_CARAC_PRESUP dcp ON (cp.ID_CARAC_PRESUPUESTO = dcp.ID_CARAC_PRESUPUESTO) \n");
		query.append("JOIN SVT_SERVICIO sv ON (dcp.ID_SERVICIO = sv.ID_SERVICIO) \n");
		query.append("WHERE dcp.ID_SERVICIO IS NOT NULL AND cp.ID_ORDEN_SERVICIO = " + this.idOrden);
		logg.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest detalleNotaRem(DatosRequest request, String formatoFecha) {
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "nr.NUM_FOLIO AS folioNota,\r\n"
				+ "DATE_FORMAT(nr.FEC_ALTA,'");
		query.append(formatoFecha);
		query.append("') AS fechaNota,\r\n"
				+ "os.CVE_FOLIO AS folioODS,\r\n"
				+ "vel.DES_VELATORIO AS nomVelatorio,\r\n"
				+ "CONCAT(\r\n"
				+ "IFNULL(domv.REF_CALLE,''),' ',IFNULL(domv.NUM_EXTERIOR,''),' ',IFNULL(domv.REF_COLONIA,'')\r\n"
				+ ") AS dirVelatorio,\r\n"
				+ "CONCAT(\r\n"
				+ "prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO\r\n"
				+ ") AS nomFinado,\r\n"
				+ "IFNULL(par.DES_PARENTESCO, ' ') AS parFinado,\r\n"
				+ "CONCAT(\r\n"
				+ "prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO\r\n"
				+ ") AS nomSolicitante,\r\n"
				+ "CONCAT(\r\n"
				+ "IFNULL(domc.REF_CALLE,''),' ',IFNULL(domc.NUM_EXTERIOR,''),' ',IFNULL(domc.REF_COLONIA,'')\r\n"
				+ ") AS dirSolicitante,\r\n"
				+ "prc.CVE_CURP AS curpSolicitante,\r\n"
				+ "vel.DES_VELATORIO AS velatorioOrigen,\r\n"
				+ "IFNULL(cvn.DES_FOLIO,0) AS folioConvenio,\r\n"
				+ "DATE_FORMAT(IFNULL(cvn.FEC_INICIO,0),'");
		query.append(formatoFecha);
		query.append("') AS fechaConvenio,\r\n"
				+ "DATE_FORMAT(IFNULL(os.FEC_ALTA,0),'");
		query.append(formatoFecha);
		query.append("') AS fechaODS,\r\n"
				+ "IFNULL(nr.REF_MOTIVO,'') AS motivo\r\n"
				+ "FROM\r\n"
				+ "SVT_NOTA_REMISION nr\r\n"
				+ "JOIN SVC_ORDEN_SERVICIO os ON (nr.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO)\r\n"
				+ "JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO)\r\n"
				+ "LEFT JOIN SVT_CONVENIO_PF cvn ON (cvn.ID_CONVENIO_PF = fin.ID_CONTRATO_PREVISION)\r\n"
				+ "JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO)\r\n"
				+ "LEFT JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA)\r\n"
				+ "LEFT JOIN SVT_DOMICILIO domv ON (vel.ID_DOMICILIO = domv.ID_DOMICILIO)\r\n"
				+ "LEFT JOIN SVC_PARENTESCO par ON (os.ID_PARENTESCO = par.ID_PARENTESCO)\r\n"
				+ "LEFT JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE)\r\n"
				+ "LEFT JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA)\r\n"
				+ "LEFT JOIN SVT_DOMICILIO domc ON (con.ID_DOMICILIO = domc.ID_DOMICILIO)\r\n");
		query.append(" WHERE nr.ID_NOTAREMISION = " + this.id);
		logg.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().remove("idNota");
		request.getDatos().put(AppConstantes.QUERY, encoded);

		return request;
	}

	public DatosRequest generarNotaRem(Integer ultimoFolio) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_NOTA_REMISION");
		q.agregarParametroValues("NUM_FOLIO", "'" + String.format("%06d", ultimoFolio + 1) + "'");
		q.agregarParametroValues("ID_ORDEN_SERVICIO", "'" + this.idOrden + "'");
		q.agregarParametroValues("IND_ESTATUS", "2");
		q.agregarParametroValues("FEC_ALTA", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("ID_USUARIO_ALTA", "'" + this.idUsuarioAlta + "'");
		logg.info(q.toString());
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}

	public DatosRequest actNota(String estatus, Integer idNota, Integer idUsuario) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		QueryHelper q = new QueryHelper("UPDATE SVT_NOTA_REMISION");
		q.agregarParametroValues("IND_ESTATUS", estatus);
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + idUsuario + "'");
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.addWhere("ID_NOTAREMISION = " + idNota);
		logg.info(q.toString());
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}
	
	public DatosRequest actNotaFolio(String estatus, Integer idNota, Integer idUsuario, Integer ultimoFolio) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		QueryHelper q = new QueryHelper("UPDATE SVT_NOTA_REMISION");
		q.agregarParametroValues("IND_ESTATUS", estatus);
		q.agregarParametroValues("NUM_FOLIO", "'" + String.format("%06d", ultimoFolio + 1) + "'");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + idUsuario + "'");
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.addWhere("ID_NOTAREMISION = " + idNota);
		logg.info(q.toString());
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}
	
	
	public DatosRequest obtenTipoPrevision(DatosRequest request) {
		StringBuilder query = new StringBuilder(
				"SELECT cnv.ID_TIPO_PREVISION AS idTipoPrevision, con.ID_CONTRATANTE AS idContratante,  cnv.ID_CONVENIO_PF AS idConvenio,"
						+
						" cpc.ID_CONTRA_PAQ_CONVENIO_PF  AS idContratantePaquete," +
						" fin.ID_PERSONA AS idPersona,");
		query.append(" fin.ID_TIPO_ORDEN AS idTipoOrden, sps.ID_PLAN_SFPA AS idConvenioSFPA");
		query.append(" FROM SVC_ORDEN_SERVICIO os \n");
		query.append(" LEFT JOIN SVC_FINADO fin ON fin.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO \n");
		query.append(" LEFT JOIN SVT_CONVENIO_PF cnv ON cnv.ID_CONVENIO_PF = fin.ID_CONTRATO_PREVISION \n");
		query.append(" LEFT JOIN SVT_CONTRA_PAQ_CONVENIO_PF cpc ON cpc.ID_CONVENIO_PF = cnv.ID_CONVENIO_PF\n");
		query.append(" LEFT JOIN SVT_PLAN_SFPA sps ON sps.ID_PLAN_SFPA = fin.ID_CONTRATO_PREVISION_PA \n");
		query.append(" LEFT JOIN SVC_CONTRATANTE con ON con.ID_PERSONA = fin.ID_PERSONA");
		query.append(" WHERE fin.ID_TIPO_ORDEN IN (2,4) ");
		query.append(" AND os.ID_ORDEN_SERVICIO = " + this.idOrden);
		query.append(" GROUP BY os.ID_ORDEN_SERVICIO");
		logg.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest actualizaEstatusCrear(LlavesTablasUpd llavesTablasUpd) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		StringBuilder query = new StringBuilder("");
		Integer tipoOrden = llavesTablasUpd.getIdTipoOrden();

		if (tipoOrden == 2) {
			query.append(
					"UPDATE SVT_CONVENIO_PF SET ID_ESTATUS_CONVENIO = 4, ID_USUARIO_MODIFICA = "
							+ this.idUsuarioAlta);
			query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONVENIO_PF = "
					+ llavesTablasUpd.getIdConvenio() + ";$$");
			query.append(" UPDATE SVT_CONTRA_PAQ_CONVENIO_PF SET IND_ACTIVO = 0, ID_USUARIO_MODIFICA = "
					+ this.idUsuarioAlta);
			query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRA_PAQ_CONVENIO_PF  = "
					+ llavesTablasUpd.getIdContratantePaquete() + ";$$");

			if (llavesTablasUpd.getIdTipoPrevision() == 1) {
				query.append(
						"UPDATE SVT_CONTRATANTE_BENEFICIARIOS SET IND_SINIESTROS = 1, IND_ACTIVO = 0, ID_USUARIO_MODIFICA = "
								+ this.idUsuarioAlta);
				query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRA_PAQ_CONVENIO_PF  = "
						+ llavesTablasUpd.getIdContratantePaquete());
				query.append(" AND ID_PERSONA = " + llavesTablasUpd.getIdPersona() + ";$$");
			}

		} else if (tipoOrden == 4) {
			query.append("update SVT_PLAN_SFPA SET ID_ESTATUS_PLAN_SFPA=4 ,");
			query.append(" ID_USUARIO_MODIFICA=" + this.idUsuarioAlta);
			query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() ");
			query.append("  WHERE ID_PLAN_SFPA =" + llavesTablasUpd.getIdConvenioSFPA() + ";$$");
		}

		logg.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		parametro.put("separador", "$$");
		request.setDatos(parametro);

		return request;
	}

	public DatosRequest actualizaEstatusCancelar(LlavesTablasUpd llavesTablasUpd) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		StringBuilder query = new StringBuilder("");
		Integer tipoOrden = llavesTablasUpd.getIdTipoOrden();

		if (tipoOrden == 2 && llavesTablasUpd.getIdContratante() > 0) {
			query.append("UPDATE SVT_CONVENIO_PF SET ID_ESTATUS_CONVENIO = 2, ID_USUARIO_MODIFICA = "
					+ this.idUsuarioModifica);
			query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONVENIO_PF = "
					+ llavesTablasUpd.getIdConvenio() + ";$$");
			query.append("UPDATE SVT_CONTRA_PAQ_CONVENIO_PF SET IND_ACTIVO = 1, ID_USUARIO_MODIFICA = "
					+ this.idUsuarioModifica);
			query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRA_PAQ_CONVENIO_PF  = "
					+ llavesTablasUpd.getIdContratantePaquete() + ";$$");
			if (llavesTablasUpd.getIdTipoPrevision() == 1) {
				query.append(
						"UPDATE SVT_CONTRATANTE_BENEFICIARIOS SET IND_SINIESTROS = 0, IND_ACTIVO = 1, ID_USUARIO_MODIFICA = "
								+ this.idUsuarioModifica);
				query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRA_PAQ_CONVENIO_PF  = "
						+ llavesTablasUpd.getIdContratantePaquete());
				query.append(" AND ID_PERSONA = " + llavesTablasUpd.getIdPersona() + ";$$");
			}
		} else if (tipoOrden == 4) {
			query.append("update SVT_PLAN_SFPA SET ID_ESTATUS_PLAN_SFPA=4 ,");
			query.append(" ID_USUARIO_MODIFICA=" + this.idUsuarioAlta);
			query.append(" , FEC_ACTUALIZACION = CURRENT_TIMESTAMP() ");
			query.append(" WHERE ID_PLAN_SFPA =" + llavesTablasUpd.getIdConvenioSFPA() + ";$$");
		}

		logg.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		parametro.put("separador", "$$");
		request.setDatos(parametro);

		return request;
	}

	public DatosRequest cancelarNotaRem() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_NOTA_REMISION");
		q.agregarParametroValues("IND_ESTATUS", "3");
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("REF_MOTIVO", "'" + this.motivo + "'");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + this.idUsuarioModifica + "'");
		q.addWhere("ID_NOTAREMISION = " + this.id);
		logg.info(q.toString());
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

	public Map<String, Object> imprimirNotaRem(FormatoNotaDto formatoDto, String nombrePdfNotaRem) {
		Map<String, Object> envioDatos = new HashMap<>();

		envioDatos.put("nomVelatorio", formatoDto.getNomVelatorio());
		envioDatos.put("folioNota", formatoDto.getFolioNota());
		envioDatos.put("dirVelatorio", formatoDto.getDirVelatorio());
		envioDatos.put("nomSolicitante", formatoDto.getNomSolicitante());
		envioDatos.put("curpSolicitante", formatoDto.getCurpSolicitante());
		envioDatos.put("dirSolicitante", formatoDto.getDirSolicitante());
		envioDatos.put("velatorioOrigen", formatoDto.getVelatorioOrigen());
		envioDatos.put("nomFinado", formatoDto.getNomFinado());
		envioDatos.put("parFinado", formatoDto.getParFinado());
		envioDatos.put("folioODS", formatoDto.getFolioODS());
		envioDatos.put("fechaODS", formatoDto.getFechaODS());
		envioDatos.put("folioConvenio", formatoDto.getFolioConvenio());
		envioDatos.put("fechaConvenio", formatoDto.getFechaConvenio());
		envioDatos.put("condicion", " AND cp.ID_ORDEN_SERVICIO = " + this.idOrden);
		envioDatos.put("tipoReporte", formatoDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfNotaRem);

		return envioDatos;
	}

}
