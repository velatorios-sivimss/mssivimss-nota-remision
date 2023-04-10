package com.imss.sivimss.notasremision.beans;

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
	
	public DatosRequest obtenerODS(DatosRequest request) {
		
		return request;
	}
	
    public DatosRequest buscarODS(DatosRequest request) {
		
		return request;
	}
    
    public DatosRequest detalleODS(DatosRequest request) {
		
		return request;
	}
    
    public DatosRequest existeNotaRem(DatosRequest request) {
		
  		return request;
  	}
    
    public DatosRequest generarNotaRem(DatosRequest request) {
		
		return request;
	}

    public DatosRequest cancelarNotaRem(DatosRequest request) {
		
		return request;
	}
    
}
