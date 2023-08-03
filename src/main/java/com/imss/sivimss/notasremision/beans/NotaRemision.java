package com.imss.sivimss.notasremision.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

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
	
	public NotaRemision(Integer id, Integer idOrden) {
		this.id = id;
		this.idOrden = idOrden;
	}
	
	public DatosRequest ultimoFolioNota(DatosRequest request) throws UnsupportedEncodingException {
		String query = "SELECT IFNULL(MAX(NUM_FOLIO),0) AS folio FROM SVT_NOTA_REMISION";
		
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	
	public DatosRequest serviciosNotaRem(DatosRequest request) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT pq.DES_PAQUETE AS nomPaquete, ar.DES_ARTICULO AS nomServicio, dcp.CAN_DET_PRESUP AS cantidad \n");
		query.append("FROM SVC_CARACTERISTICAS_PRESUPUESTO cp \n");
		query.append("JOIN SVT_PAQUETE pq ON (cp.ID_PAQUETE = pq.ID_PAQUETE) \n");
		query.append("JOIN SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO dcp ON (cp.ID_CARACTERISTICAS_PRESUPUESTO = dcp.ID_CARACTERISTICAS_PRESUPUESTO) \n");
		query.append("JOIN SVT_INVENTARIO_ARTICULO ia ON (dcp.ID_INVE_ARTICULO = ia.ID_INVE_ARTICULO) \n");
		query.append("JOIN SVT_ARTICULO ar ON (ia.ID_ARTICULO = ar.ID_ARTICULO) \n");
		query.append("WHERE dcp.ID_INVE_ARTICULO IS NOT NULL AND cp.ID_ORDEN_SERVICIO = " + this.idOrden + " \n");
		query.append("UNION \n");
		query.append("SELECT pq.DES_PAQUETE AS nomPaquete, sv.DES_SERVICIO AS nomServicio, dcp.CAN_DET_PRESUP AS cantidad \n");
		query.append("FROM SVC_CARACTERISTICAS_PRESUPUESTO cp \n");
		query.append("JOIN SVT_PAQUETE pq ON (cp.ID_PAQUETE = pq.ID_PAQUETE) \n");
		query.append("JOIN SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO dcp ON (cp.ID_CARACTERISTICAS_PRESUPUESTO = dcp.ID_CARACTERISTICAS_PRESUPUESTO) \n");
		query.append("JOIN SVT_SERVICIO sv ON (dcp.ID_SERVICIO = sv.ID_SERVICIO) \n");
		query.append("WHERE dcp.ID_SERVICIO IS NOT NULL AND cp.ID_ORDEN_SERVICIO = " + this.idOrden);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest detalleNotaRem(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT nr.NUM_FOLIO AS folioNota, DATE_FORMAT(nr.FEC_ALTA,'" + formatoFecha + "') AS fechaNota, \n");
		query.append("os.CVE_FOLIO AS folioODS, vel.DES_VELATORIO AS nomVelatorio, \n");
		query.append("CONCAT(IFNULL(domv.DES_CALLE,''),' ',IFNULL(domv.NUM_EXTERIOR,''),' ',IFNULL(domv.DES_COLONIA,'')) AS dirVelatorio, \n");
		query.append("CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append("par.DES_PARENTESCO AS parFinado, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomSolicitante, \n");
		query.append("CONCAT(IFNULL(domc.DES_CALLE,''),' ',IFNULL(domc.NUM_EXTERIOR,''),' ',IFNULL(domc.DES_COLONIA,'')) AS dirSolicitante, \n");
		query.append("prc.CVE_CURP AS curpSolicitante, vel.DES_VELATORIO AS velatorioOrigen, IFNULL(cvn.DES_FOLIO,0) AS folioConvenio, ");
		query.append("DATE_FORMAT(IFNULL(cvn.FEC_INICIO,0),'" + formatoFecha + "') AS fechaConvenio, \n");
		query.append("IFNULL(notr.DES_MOTIVO,'') AS motivo  \n");
		query.append("FROM SVT_NOTA_REMISION nr \n");
		query.append("JOIN SVC_ORDEN_SERVICIO os ON (nr.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVT_NOTA_REMISION notr ON (os.ID_ORDEN_SERVICIO = notr.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = fin.ID_VELATORIO) \n");
		query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
		query.append("LEFT JOIN SVT_DOMICILIO domv ON (vel.ID_DOMICILIO = domv.ID_DOMICILIO) \n");
		query.append("LEFT JOIN SVC_PARENTESCO par ON (os.ID_PARENTESCO = par.ID_PARENTESCO) \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON (con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE) \n");
		query.append("JOIN SVT_CONVENIO_PF cvn ON (cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF) \n");
		query.append("LEFT JOIN SVT_DOMICILIO domc ON (con.ID_DOMICILIO = domc.ID_DOMICILIO) \n");
		query.append("WHERE nr.ID_NOTAREMISION = " + this.id);
		System.out.print(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().remove("idNota");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
    public DatosRequest generarNotaRem(String ultimoFolio) throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_NOTA_REMISION");
		q.agregarParametroValues("NUM_FOLIO",  "'" + String.format("%06d", Integer.parseInt(ultimoFolio) + 1) + "'");
		q.agregarParametroValues("ID_ORDEN_SERVICIO","'" + this.idOrden + "'");
		q.agregarParametroValues("ID_ESTATUS", "2");
		q.agregarParametroValues("FEC_ALTA", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("ID_USUARIO_ALTA", "'" + this.idUsuarioAlta + "'");
		
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
    
    public DatosRequest obtenTipoPrevision(DatosRequest request) throws UnsupportedEncodingException {
    	StringBuilder query =  new StringBuilder("SELECT ID_TIPO_PREVISION AS idTipoPrevision, con.ID_CONTRATANTE AS idContratante, \n");
    	query.append("cnv.ID_CONVENIO_PF AS idConvenio, cpc.ID_CONTRATANTE_PAQUETE_CONVENIO_PF AS idContratantePaquete, fin.ID_PERSONA AS idPersona \n");
    	query.append("FROM SVC_ORDEN_SERVICIO os \n");
    	query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpc ON cpc.ID_CONTRATANTE = os.ID_CONTRATANTE \n");
    	query.append("JOIN SVT_CONVENIO_PF cnv ON cnv.ID_CONVENIO_PF = cpc.ID_CONVENIO_PF \n");
    	query.append("JOIN SVC_FINADO fin ON fin.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO \n");
    	query.append("LEFT JOIN SVC_CONTRATANTE con ON con.ID_PERSONA = fin.ID_PERSONA \n");
    	query.append("WHERE os.ID_ORDEN_SERVICIO = " + this.idOrden);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
    }
    
    public DatosRequest actualizaEstatusCrear(LlavesTablasUpd llavesTablasUpd) throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	
    	StringBuilder query = new StringBuilder("");
    	if (llavesTablasUpd.getIsContratante()) {
    	    query.append("UPDATE SVT_CONVENIO_PF SET ID_ESTATUS_CONVENIO = 4, ID_USUARIO_MODIFICA = " + this.idUsuarioAlta);
    	    query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONVENIO_PF = " + llavesTablasUpd.getIdConvenio() + ";$$");
    	    query.append("UPDATE SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SET IND_ACTIVO = 0, ID_USUARIO_MODIFICA = " + this.idUsuarioAlta);
    	    query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRATANTE_PAQUETE_CONVENIO_PF = " + llavesTablasUpd.getIdContratantePaquete() + ";$$");
    	} if (llavesTablasUpd.getIdTipoPrevision() == 1) {
    		query.append("UPDATE SVT_CONTRATANTE_BENEFICIARIOS SET IND_SINIESTROS = 1, IND_ACTIVO = 0, ID_USUARIO_MODIFICA = " + this.idUsuarioAlta);
    	    query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRATANTE_PAQUETE_CONVENIO_PF = " + llavesTablasUpd.getIdContratantePaquete());
    	    query.append(" AND ID_PERSONA = " + llavesTablasUpd.getIdPersona() + ";$$");
    	}
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
    	parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("separador", "$$");
		request.setDatos(parametro);
		
    	return request;
    }
    
    public DatosRequest actualizaEstatusCancelar(LlavesTablasUpd llavesTablasUpd) throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	
    	StringBuilder query = new StringBuilder("");
    	if (llavesTablasUpd.getIsContratante()) {
    	    query.append("UPDATE SVT_CONVENIO_PF SET ID_ESTATUS_CONVENIO = 2, ID_USUARIO_MODIFICA = " + this.idUsuarioModifica);
    	    query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONVENIO_PF = " + llavesTablasUpd.getIdConvenio() + ";$$");
    	    query.append("UPDATE SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SET IND_ACTIVO = 1, ID_USUARIO_MODIFICA = " + this.idUsuarioModifica);
    	    query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRATANTE_PAQUETE_CONVENIO_PF = " + llavesTablasUpd.getIdContratantePaquete() + ";$$");
    	} else if (llavesTablasUpd.getIdTipoPrevision() == 1) {
    		query.append("UPDATE SVT_CONTRATANTE_BENEFICIARIOS SET IND_SINIESTROS = 0, IND_ACTIVO = 1, ID_USUARIO_MODIFICA = " + this.idUsuarioModifica);
    	    query.append(", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_CONTRATANTE_PAQUETE_CONVENIO_PF = " + llavesTablasUpd.getIdContratantePaquete());
    	    query.append(" AND ID_PERSONA = " + llavesTablasUpd.getIdPersona() + ";$$");
    	}
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
    	parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("separador", "$$");
		request.setDatos(parametro);
		
    	return request;
    }

    public DatosRequest cancelarNotaRem() throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_NOTA_REMISION");
		q.agregarParametroValues("ID_ESTATUS", "3");
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("DES_MOTIVO", "'" + this.motivo + "'");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + this.idUsuarioModifica + "'");
		q.addWhere("ID_NOTAREMISION = " + this.id);
		
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
    
    public Map<String, Object> imprimirNotaRem(FormatoNotaDto formatoDto,String nombrePdfNotaRem){
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
		envioDatos.put("convenio", formatoDto.getFolioConvenio());
		envioDatos.put("fechaConvenio", formatoDto.getFechaConvenio());
		envioDatos.put("condicion", " AND cp.ID_ORDEN_SERVICIO = " +  this.idOrden);
		envioDatos.put("tipoReporte", formatoDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfNotaRem);
		
		return envioDatos;
    }
    
}
