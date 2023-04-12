package com.imss.sivimss.notasremision.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusquedaDto {

	private Integer idOficina;
	private Integer idNivel;
	private Integer idDelegacion;
	private Integer idVelatorio;
	private String folioODS;
	private String fecIniODS;
	private String fecFinODS;
	
}
