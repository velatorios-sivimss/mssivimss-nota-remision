package com.imss.sivimss.notasremision.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusquedaDto {

	private Integer idOficina;
	private Integer idDelegacion;
	private Integer idVelatorio;
	private String folioODS;
	private String fecIniODS;
	private String fecFinODS;
	private String tipoReporte;
	
}
