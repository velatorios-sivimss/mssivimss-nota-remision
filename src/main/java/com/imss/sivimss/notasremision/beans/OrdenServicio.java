package com.imss.sivimss.notasremision.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.notasremision.util.AppConstantes;
import com.imss.sivimss.notasremision.model.request.BusquedaDto;
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
	private Boolean conNota;
	
	private static final String fechaCotejo =  "DATE_FORMAT(inf.FEC_CORTEJO,'%d/%m/%Y')";
	
	public DatosRequest obtenerODS(DatosRequest request) {
		StringBuilder query = armaQuery();
		query.append(" ORDER BY os.ID_ORDEN_SERVICIO DESC");
        
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
	public DatosRequest listadoODS() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query = "SELECT ID_ORDEN_SERVICIO, CVE_FOLIO FROM SVC_ORDEN_SERVICIO";
		
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
    public DatosRequest buscarODS(DatosRequest request, BusquedaDto busqueda) {
		
    	StringBuilder query = armaQuery();
    	query.append("WHERE 1 = 1");
    	if (busqueda.getFolioODS() != null) {
    	    query.append(" AND os.CVE_FOLIO = '" + busqueda.getFolioODS() +"' ");
    	}
    	if (busqueda.getFecIniODS() != null) {
    	    query.append(" AND " + fechaCotejo + " BETWEEN '" + busqueda.getFecIniODS() + "' AND '" + busqueda.getFecFinODS() + "' \n");
    	}
    	query.append(" ORDER BY os.ID_ORDEN_SERVICIO DESC");
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
		return request;
	}
    
    public DatosRequest detalleODS(DatosRequest request) {
    	String idODS = request.getDatos().get("id").toString();
		StringBuilder query = armaQuery();
		query.append("WHERE os.ID_ORDEN_SERVICIO = " + idODS);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().remove("id");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
    
    public DatosRequest existeNotaRem(DatosRequest request) {
    	String idODS = request.getDatos().get("id").toString();
		String query = "SELECT COUNT(NUM_FOLIO) AS valor FROM SVT_NOTA_REMISION WHERE ID_ORDEN_SERVICIO = " + idODS;
		
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().remove("id");
		request.getDatos().put(AppConstantes.QUERY, encoded);
  		return request;
  	}
    
    public DatosRequest generarNotaRem(DatosRequest request) {
		
		return request;
	}

    public DatosRequest cancelarNotaRem(DatosRequest request) {
		
		return request;
	}
    
    private StringBuilder armaQuery() {
    	StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, " + fechaCotejo + " AS fechaODS, \n");
		query.append("0 AS folioConvenio, os.ID_CONTRATANTE AS idContratante, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("fin.ID_FINADO AS idFinado, CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append("os.CVE_ESTATUS AS estatus, IFNULL(nr.ID_NOTA_REMISION,0) AS conNota \n");
		query.append("FROM svc_orden_servicio os \n");
		query.append("LEFT JOIN svc_informacion_servicio inf ON (os.ID_ORDEN_SERVICIO = inf.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN svc_contratante con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN svc_persona prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("JOIN svc_finado fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN svc_persona prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
		query.append("LEFT JOIN svt_nota_remision nr ON (os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO) \n");
		
		return query;
    }
    
}
