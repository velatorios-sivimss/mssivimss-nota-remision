package com.imss.sivimss.notasremision.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.notasremision.util.AppConstantes;
import com.imss.sivimss.notasremision.model.request.BusquedaDto;
import com.imss.sivimss.notasremision.util.DatosRequest;
import com.imss.sivimss.notasremision.util.QueryHelper;

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
	
	private static final String FECHA_COTEJO =  "DATE_FORMAT(inf.FEC_CORTEJO,'%d/%m/%Y')";
	
	private static final String ORDENAMIENTO = " ORDER BY os.ID_ORDEN_SERVICIO DESC";
	
	public DatosRequest obtenerODS(DatosRequest request, BusquedaDto busqueda) {
		StringBuilder query = armaQuery();
		if (busqueda.getIdOficina() > 1) {
			query.append(" WHERE vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
			if (busqueda.getIdOficina() == 3) {
				query.append(" AND fin.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
		} 
		query.append(ORDENAMIENTO);
        
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
	public DatosRequest listadoODS(BusquedaDto busqueda) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO, os.CVE_FOLIO \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("LEFT JOIN SVC_VELATORIO vel ON (fin.ID_VELATORIO = vel.ID_VELATORIO) \n");
		query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO = 2 ");
		if (busqueda.getIdOficina() > 1) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
			if (busqueda.getIdOficina() == 3) {
				query.append(" AND fin.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
		} 
		query.append(ORDENAMIENTO);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
    public DatosRequest buscarODS(DatosRequest request, BusquedaDto busqueda) {
		
    	StringBuilder query = armaQuery();
    	query.append("WHERE 1 = 1");
    	if (busqueda.getIdNivel() > 1 && busqueda.getIdVelatorio() != null) {
			query.append(" AND fin.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
		}

    	if (busqueda.getFolioODS() != null) {
    	    query.append(" AND os.CVE_FOLIO = '" + busqueda.getFolioODS() +"' ");
    	}
    	if (busqueda.getFecIniODS() != null) {
    	    query.append(" AND " + FECHA_COTEJO + " BETWEEN '" + busqueda.getFecIniODS() + "' AND '" + busqueda.getFecFinODS() + "' \n");
    	}
    	query.append(ORDENAMIENTO);
    	
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
    
    private StringBuilder armaQuery() {
    	StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, " + FECHA_COTEJO + " AS fechaODS, \n");
		query.append("0 AS folioConvenio, os.ID_CONTRATANTE AS idContratante, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("fin.ID_FINADO AS idFinado, CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append("IFNULL(nr.ID_ESTATUS,1) AS estatus \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("LEFT JOIN SVC_INFORMACION_SERVICIO inf ON (os.ID_ORDEN_SERVICIO = inf.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
		query.append("LEFT JOIN SVT_NOTA_REMISION nr ON (os.ID_ORDEN_SERVICIO = nr.ID_ORDEN_SERVICIO) \n");
		query.append("LEFT JOIN SVC_VELATORIO vel ON (fin.ID_VELATORIO = vel.ID_VELATORIO) \n");
		
		return query;
    }
    
    public DatosRequest actualizaEstatus(String estatus) {
    	DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVC_ORDEN_SERVICIO");
		q.agregarParametroValues("ID_ESTATUS_ORDEN_SERVICIO", estatus);
		q.addWhere("ID_ORDEN_SERVICIO = " + this.id);
		
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
    
    public Map<String, Object> generarReporte(BusquedaDto reporteDto,String nombrePdfReportes){
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");
		if (reporteDto.getIdVelatorio() != null) {
			condicion.append(" AND fin.ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
		}
		if (reporteDto.getFolioODS() != null) {
    	    condicion.append(" AND os.CVE_FOLIO = '" + reporteDto.getFolioODS() +"' ");
    	}
    	if (reporteDto.getFecIniODS() != null) {
    	    condicion.append(" AND " + FECHA_COTEJO + " BETWEEN '" + reporteDto.getFecIniODS() + "' AND '" + reporteDto.getFecFinODS() + "' \n");
    	}
		
		envioDatos.put("condicion", condicion.toString());
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		
		return envioDatos;
	}
    
}
