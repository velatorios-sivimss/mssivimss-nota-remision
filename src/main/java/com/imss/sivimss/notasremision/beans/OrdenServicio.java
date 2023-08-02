package com.imss.sivimss.notasremision.beans;

import java.io.UnsupportedEncodingException;
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

	public DatosRequest obtenerODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
		if (busqueda.getIdOficina() > 1) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
			if (busqueda.getIdOficina() == 3) {
				query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
		} 
        
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
	public DatosRequest listadoODS(BusquedaDto busqueda) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO, os.CVE_FOLIO \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_VELATORIO vel ON (os.ID_VELATORIO = vel.ID_VELATORIO) \n");
		query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO = 2 ");
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
	
    public DatosRequest buscarODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
    	StringBuilder query = new StringBuilder("");
    	StringBuilder busquedaCancelada = busquedaCancelada(formatoFecha);
    	StringBuilder busquedaGeneradas = busquedaGeneradas(formatoFecha);
    	StringBuilder busquedaSinNota = busquedaSinNota(formatoFecha);
    	
    	
    	//Busqueda por dos fechas, regresa las notas con estatus Generada
    	if (busqueda.getIdDelegacion() != null && busqueda.getIdVelatorio() != null && busqueda.getFecIniODS() != null && busqueda.getFecFinODS() != null) {
    		query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    		query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    		query.append(" AND DATE(nr.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFecIniODS() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFecFinODS() + "','" + formatoFecha + "')");
    		busquedaGeneradas.append(query);
    		
    		logger.info(busquedaGeneradas.toString());
        	String encoded = DatatypeConverter.printBase64Binary(busquedaGeneradas.toString().getBytes("UTF-8"));
    		request.getDatos().put(AppConstantes.QUERY, encoded);
        	
    		return request;
		}
    	else if (busqueda.getIdDelegacion() != null && busqueda.getIdVelatorio() == null && busqueda.getFecIniODS() != null && busqueda.getFecFinODS() != null) {
    		query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    		query.append(" AND DATE(nr.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFecIniODS() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFecFinODS() + "','" + formatoFecha + "')");
    		busquedaGeneradas.append(query);
    		
    		logger.info(busquedaGeneradas.toString());
    		String encoded = DatatypeConverter.printBase64Binary(busquedaGeneradas.toString().getBytes("UTF-8"));
    		request.getDatos().put(AppConstantes.QUERY, encoded);
        	
    		return request;
      	}
    	else if (busqueda.getIdDelegacion() == null && busqueda.getIdVelatorio() == null && busqueda.getFecIniODS() != null && busqueda.getFecFinODS() != null) {
    		query.append(" AND DATE(nr.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFecIniODS() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFecFinODS() + "','" + formatoFecha + "')");
    		busquedaGeneradas.append(query);
    		
    		logger.info(busquedaGeneradas.toString());
    		String encoded = DatatypeConverter.printBase64Binary(busquedaGeneradas.toString().getBytes("UTF-8"));
    		request.getDatos().put(AppConstantes.QUERY, encoded);
        	
    		return request;
		}
    	// Busqueda General
    	else {
    		if (busqueda.getIdVelatorio() != null) {
    			query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    		}
        	if (busqueda.getIdDelegacion() != null) {
        		query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
        	}

        	if (busqueda.getFolioODS() != null) {
        	    query.append(" AND os.CVE_FOLIO = '" + busqueda.getFolioODS() +"' ");
        	}
        	if (busqueda.getFecIniODS() != null) {
        	    query.append(" AND DATE(nr.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFecIniODS() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFecFinODS() + "','" + formatoFecha + "')");
        	}
        	
        	busquedaCancelada.append(query);
        	busquedaSinNota.append(query);
        	 
         	StringBuilder queryCompleto = new StringBuilder("");
        	queryCompleto.append("SELECT  * FROM ( ");
        	queryCompleto.append(busquedaCancelada);
        	queryCompleto.append(" UNION ALL ");
        	queryCompleto.append(busquedaSinNota);
        	queryCompleto.append(" ) datos  ORDER BY datos.id");
        	
        	logger.info(queryCompleto.toString());
        	String encoded = DatatypeConverter.printBase64Binary(queryCompleto.toString().getBytes("UTF-8"));
    		request.getDatos().put(AppConstantes.QUERY, encoded);
        	
    		return request;
    	}
    	

	}
    
    public DatosRequest detalleODS(DatosRequest request) throws UnsupportedEncodingException {
    	String idODS = request.getDatos().get("id").toString();
		StringBuilder query =  new StringBuilder("SELECT os.CVE_FOLIO AS folioODS, vel.DES_VELATORIO AS nomVelatorio, \n");
		query.append("CONCAT(IFNULL(domv.DES_CALLE,''),' ',IFNULL(domv.NUM_EXTERIOR,''),' ',IFNULL(domv.DES_COLONIA,'')) AS dirVelatorio, \n");
		query.append("CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append("par.DES_PARENTESCO AS parFinado, vel. NOM_RESPO_SANITARIO AS nomResponsable, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomSolicitante, \n");
		query.append("CONCAT(IFNULL(domc.DES_CALLE,''),' ',IFNULL(domc.NUM_EXTERIOR,''),' ',IFNULL(domc.DES_COLONIA,'')) AS dirSolicitante, \n");
		query.append("prc.CVE_CURP AS curpSolicitante, vel.DES_VELATORIO AS velatorioOrigen, \n");
		query.append("IFNULL(cvn.DES_FOLIO,0) AS folioConvenio, IFNULL(cvn.FEC_INICIO,0) AS fechaConvenio, \n");
		query.append("(SELECT LPAD(IFNULL(MAX(NUM_FOLIO+1),1),6,'0') FROM SVT_NOTA_REMISION) AS folioNota \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON (con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE) \n");
		query.append("JOIN SVT_CONVENIO_PF cvn ON (cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF) \n");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
		query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO) \n");
		query.append("LEFT JOIN SVT_DOMICILIO domv ON (vel.ID_DOMICILIO = domv.ID_DOMICILIO) \n");
		query.append("LEFT JOIN SVC_PARENTESCO par ON (os.ID_PARENTESCO = par.ID_PARENTESCO) \n");
		query.append("LEFT JOIN SVT_DOMICILIO domc ON (con.ID_DOMICILIO = domc.ID_DOMICILIO) \n");
		query.append("WHERE os.ID_ORDEN_SERVICIO = " + idODS);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().remove("id");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
    
    public DatosRequest existeNotaRem(DatosRequest request) throws UnsupportedEncodingException {
    	String idODS = request.getDatos().get("id").toString();
		String query = "SELECT COUNT(NUM_FOLIO) AS valor FROM SVT_NOTA_REMISION WHERE ID_ORDEN_SERVICIO = " + idODS;
		
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
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
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
    
    private StringBuilder busquedaCancelada(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT DISTINCT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS, IFNULL(cvn.DES_FOLIO,0) AS folioConvenio, os.ID_CONTRATANTE AS idContratante, ");
    	query.append("CONCAT(IFNULL(prc.NOM_PERSONA,' '),IFNULL(prc.NOM_PRIMER_APELLIDO,' '),IFNULL(prc.NOM_SEGUNDO_APELLIDO,' ')) AS nomContratante, \n");
    	query.append("fin.ID_FINADO AS idFinado,  \n");
    	query.append("CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado,  \n");
    	query.append("IFNULL(nr.ID_ESTATUS,1) AS estatus, IFNULL(nr.ID_NOTAREMISION,0) AS idNota,  \n");
    	query.append("IFNULL(nr.ID_NOTAREMISION,0) AS idCancelada, 0 AS total \n");
    	query.append("FROM SVC_ORDEN_SERVICIO os \n");
    	query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
    	query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
    	query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON (con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE) \n");
    	query.append("JOIN SVT_CONVENIO_PF cvn ON (cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF) \n");
    	query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
    	query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
    	query.append("JOIN SVT_NOTA_REMISION nr ON (os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO) AND nr.ID_ESTATUS =3 \n");
    	query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO) \n");
    	query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO > 1 \n");
    	return query;
    }
    
    private StringBuilder busquedaGeneradas(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT DISTINCT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, \n");
    	query.append("DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS,  \n");
    	query.append("IFNULL(cvn.DES_FOLIO,0) AS folioConvenio, os.ID_CONTRATANTE AS idContratante, \n");
    	query.append("CONCAT(IFNULL(prc.NOM_PERSONA,' '),IFNULL(prc.NOM_PRIMER_APELLIDO,' '),IFNULL(prc.NOM_SEGUNDO_APELLIDO,' ')) AS nomContratante,  \n");
    	query.append("fin.ID_FINADO AS idFinado, CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
    	query.append("IFNULL(nr.ID_ESTATUS,1) AS estatus, IFNULL(nr.ID_NOTAREMISION,0) AS idNota,  \n");
    	query.append("IFNULL(nr.ID_NOTAREMISION,0) AS idCancelada, (SELECT COUNT(rn.ID_ORDEN_SERVICIO) FROM SVT_NOTA_REMISION rn WHERE rn.ID_ORDEN_SERVICIO=os.ID_ORDEN_SERVICIO ) AS total \n");
    	query.append("FROM SVC_ORDEN_SERVICIO os \n");
    	query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
    	query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
    	query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON (con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE) \n");
    	query.append("JOIN SVT_CONVENIO_PF cvn ON (cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF) \n");
    	query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
    	query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
    	query.append("JOIN SVT_NOTA_REMISION nr ON (os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO) \n");
    	query.append("AND nr.ID_ESTATUS =2 \n");
    	query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO) \n");
    	query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO > 1 \n");

    	return query;
    }
    
    private StringBuilder busquedaSinNota(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT DISTINCT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, \n");
    	query.append("DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS,  \n");
    	query.append("IFNULL(cvn.DES_FOLIO,0) AS folioConvenio, os.ID_CONTRATANTE AS idContratante,  \n");
    	query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
    	query.append("fin.ID_FINADO AS idFinado, CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
    	query.append("IFNULL(nr.ID_ESTATUS,1) AS estatus, IFNULL(nr.ID_NOTAREMISION,0) AS idNota,  \n");
    	query.append("IFNULL(nr.ID_NOTAREMISION,0) AS idCancelada, (SELECT COUNT(rn.ID_ORDEN_SERVICIO) FROM SVT_NOTA_REMISION rn WHERE rn.ID_ORDEN_SERVICIO=os.ID_ORDEN_SERVICIO ) AS total \n");
    	query.append("FROM SVC_ORDEN_SERVICIO os \n");
    	query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
    	query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
    	query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON (con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE) \n");
    	query.append("JOIN SVT_CONVENIO_PF cvn ON (cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF) \n");
    	query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
    	query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
    	query.append("LEFT JOIN SVT_NOTA_REMISION nr ON (os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO) \n");
    	query.append("AND nr.ID_ESTATUS =2 \n");
    	query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO) \n");
    	query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO > 1 \n");
    	
    	return query;
    }
    
    private StringBuilder armaQuery(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS, \n");
		query.append("IFNULL(cvn.DES_FOLIO,0) AS folioConvenio, os.ID_CONTRATANTE AS idContratante, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("fin.ID_FINADO AS idFinado, CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append("IFNULL(nr.ID_ESTATUS,1) AS estatus, IFNULL(nr.ID_NOTAREMISION,0) AS idNota, IFNULL(nrc.ID_NOTAREMISION,0) AS idCancelada \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON (con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE) \n");
		query.append("JOIN SVT_CONVENIO_PF cvn ON (cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF) \n");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
		query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO \n");
		query.append("LEFT JOIN SVT_NOTA_REMISION nr ON (os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO) \n");
		query.append("LEFT JOIN SVT_NOTA_REMISION nrc ON (os.ID_ORDEN_SERVICIO = nrc.ID_ORDEN_SERVICIO AND nrc.ID_ESTATUS = 3) \n");
		query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO > 1 ");
		
		return query;
    }
    
    public Map<String, Object> generarReporte(BusquedaDto reporteDto,String nombrePdfReportes, String formatoFecha){
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");
		if (reporteDto.getIdVelatorio() != null) {
			condicion.append(" AND os.ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
		}
		if (reporteDto.getIdDelegacion() != null) {
    		condicion.append(" AND vel.ID_DELEGACION = ").append(reporteDto.getIdDelegacion());
    	}
		if (reporteDto.getFolioODS() != null) {
    	    condicion.append(" AND os.CVE_FOLIO = '" + reporteDto.getFolioODS() +"' ");
    	}
    	if (reporteDto.getFecIniODS() != null) {
    	    condicion.append(" AND DATE(nr.FEC_ALTA) BETWEEN STR_TO_DATE('" + reporteDto.getFecIniODS() + "','" + formatoFecha + "') AND STR_TO_DATE('" + reporteDto.getFecFinODS() + "','" + formatoFecha + "')");
    	}
		
		envioDatos.put("condicion", condicion.toString());
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		
		return envioDatos;
	}
    
}
