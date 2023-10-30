package com.imss.sivimss.notasremision.util;

public class NotaUtil {

	public String actEstatusOds(String estatus, Integer idOds){
		
		QueryHelper q = new QueryHelper("UPDATE SVC_ORDEN_SERVICIO");
		q.agregarParametroValues("ID_ESTATUS_ORDEN_SERVICIO", estatus);
		q.addWhere("ID_ORDEN_SERVICIO = " + idOds);
			
		return q.obtenerQueryActualizar();
	}
	
	public String cancelarNotaRem(String motivo, Integer idUsuario, Integer idNotaRemision) {
		
		QueryHelper q = new QueryHelper("UPDATE SVT_NOTA_REMISION");
		q.agregarParametroValues("IND_ESTATUS", "3");
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("REF_MOTIVO", "'" + motivo + "'");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + idUsuario + "'");
		q.addWhere("ID_NOTAREMISION = " + idNotaRemision);
		
		return q.obtenerQueryActualizar();
		
	}
	
	public String generarNotaRem(Integer idOds, Integer idUsuario) {
		
		QueryHelper q = new QueryHelper("INSERT INTO SVT_NOTA_REMISION");
		q.agregarParametroValues("ID_ORDEN_SERVICIO", "'" + idOds + "'");
		q.agregarParametroValues("IND_ESTATUS", "1");
		q.agregarParametroValues("FEC_ALTA", "CURRENT_TIMESTAMP()");
		q.agregarParametroValues("ID_USUARIO_ALTA", "'" + idUsuario + "'");
		
		return q.obtenerQueryInsertar();
		
	}
	
}
