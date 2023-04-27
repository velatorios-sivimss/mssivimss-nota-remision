package com.imss.sivimss.notasremision.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.notasremision.util.QueryHelper;
import com.imss.sivimss.notasremision.model.request.BusquedaDto;
import com.imss.sivimss.notasremision.model.request.FormatoNotaDto;
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
	
	public DatosRequest ultimoFolioNota(DatosRequest request) {
		String query = "SELECT IFNULL(MAX(NUM_FOLIO),0) AS folio FROM SVT_NOTA_REMISION";
		
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	
	public DatosRequest serviciosNotaRem(DatosRequest request) {
		StringBuilder query = new StringBuilder("SELECT pq.NOM_PAQUETE AS nomPaquete, ar.DES_ARTICULO AS nomServicio, dcp.CAN_CANTIDAD AS cantidad \n");
		query.append("FROM SVC_CARACTERISTICAS_PRESUPUESTO cp \n");
		query.append("JOIN SVT_PAQUETE pq ON (cp.ID_PAQUETE = pq.ID_PAQUETE) \n");
		query.append("JOIN SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO dcp ON (cp.ID_CARACTERISTICAS_PRESUPUESTO = dcp.ID_CARACTERISTICAS_PRESUPUESTO) \n");
		query.append("JOIN SVT_ARTICULO ar ON (dcp.ID_ARTICULO = ar.ID_ARTICULO) \n");
		query.append("WHERE dcp.ID_ARTICULO IS NOT NULL AND cp.ID_ORDEN_SERVICIO = " + this.idOrden + " \n");
		query.append("UNION \n");
		query.append("SELECT pq.NOM_PAQUETE AS nomPaquete, sv.NOM_SERVICIO AS nomServicio, dcp.CAN_CANTIDAD AS cantidad \n");
		query.append("FROM SVC_CARACTERISTICAS_PRESUPUESTO cp \n");
		query.append("JOIN SVT_PAQUETE pq ON (cp.ID_PAQUETE = pq.ID_PAQUETE) \n");
		query.append("JOIN SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO dcp ON (cp.ID_CARACTERISTICAS_PRESUPUESTO = dcp.ID_CARACTERISTICAS_PRESUPUESTO) \n");
		query.append("JOIN SVT_SERVICIO sv ON (dcp.ID_SERVICIO = sv.ID_SERVICIO) \n");
		query.append("WHERE dcp.ID_SERVICIO IS NOT NULL AND cp.ID_ORDEN_SERVICIO = " + this.idOrden);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest detalleNotaRem(DatosRequest request) {
		StringBuilder query = new StringBuilder("SELECT nr.NUM_FOLIO AS folioNota, os.CVE_FOLIO AS folioODS, vel.NOM_VELATORIO AS nomVelatorio, \n");
		query.append("IFNULL(CONCAT(domv.DES_CALLE,' ',domv.NUM_EXTERIOR,' ',domv.DES_COLONIA),'') AS dirVelatorio, \n");
		query.append("CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append("par.DES_PARENTESCO AS parFinado, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomSolicitante, \n");
		query.append("CONCAT(domc.DES_CALLE,' ',domc.NUM_EXTERIOR,' ',domc.DES_COLONIA) AS dirSolicitante, \n");
		query.append("prc.CVE_CURP AS curpSolicitante, vel.NOM_VELATORIO AS velatorioOrigen \n");
		query.append("FROM SVT_NOTA_REMISION nr \n");
		query.append("JOIN SVC_ORDEN_SERVICIO os ON (nr.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = fin.ID_VELATORIO) \n");
		query.append("JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
		query.append("LEFT JOIN SVT_DOMICILIO domv ON (vel.ID_DOMICILIO = domv.ID_DOMICILIO) \n");
		query.append("LEFT JOIN SVC_PARENTESCO par ON (os.ID_PARENTESCO = par.ID_PARENTESCO) \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("LEFT JOIN SVT_DOMICILIO domc ON (con.ID_DOMICILIO = domc.ID_DOMICILIO) \n");
		query.append("WHERE nr.ID_NOTAREMISION = " + this.id);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().remove("idNota");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
    public DatosRequest generarNotaRem(String ultimoFolio) {
    	DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_NOTA_REMISION");
		q.agregarParametroValues("NUM_FOLIO",  "'" + String.format("%06d", Integer.parseInt(ultimoFolio) + 1) + "'");
		q.agregarParametroValues("ID_ORDEN_SERVICIO","'" + this.idOrden + "'");
		q.agregarParametroValues("ID_ESTATUS", "2");
		q.agregarParametroValues("FEC_ALTA", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("ID_USUARIO_ALTA", "'" + this.idUsuarioAlta + "'");
		
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}

    public DatosRequest cancelarNotaRem() {
    	DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_NOTA_REMISION");
		q.agregarParametroValues("ID_ESTATUS", "3");
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("DES_MOTIVO", "'" + this.motivo + "'");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + this.idUsuarioModifica + "'");
		q.addWhere("ID_NOTAREMISION = " + this.id);
		
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
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
		envioDatos.put("condicion", " AND cp.ID_ORDEN_SERVICIO = " +  this.id);
		envioDatos.put("tipoReporte", "pdf");
		envioDatos.put("rutaNombreReporte", nombrePdfNotaRem);
		
		return envioDatos;
    }
    
}
