package com.imss.sivimss.notasremision.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown  = true)
public class ODSGeneradaResponse {

	@JsonProperty(value = "id")
	private Integer ID_ORDEN_SERVICIO;
	
	@JsonProperty(value = "nombre")
	private String CVE_FOLIO;
	
}
