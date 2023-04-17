package com.imss.sivimss.notasremision.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.notasremision.util.QueryHelper;
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
	
    public DatosRequest generarNotaRem(String ultimoFolio) {
    	DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_NOTA_REMISION");
		q.agregarParametroValues("NUM_FOLIO",  "'" + String.format("%06d", Integer.parseInt(ultimoFolio) + 1) + "'");
		q.agregarParametroValues("ID_ORDEN_SERVICIO","'" + this.idOrden + "'");
		q.agregarParametroValues("ID_ESTATUS", "1");
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
		q.agregarParametroValues("ID_ESTATUS", "2");
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + this.idUsuarioModifica + "'");
		q.addWhere("ID_NOTAREMISION = " + this.id);
		
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
    
}
