package com.imss.sivimss.notasremision.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicioDto {
	
	private String nomPaquete;
	private String nomServicio;
	private String cantidad;

}
