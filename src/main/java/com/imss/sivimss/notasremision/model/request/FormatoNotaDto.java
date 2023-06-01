package com.imss.sivimss.notasremision.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormatoNotaDto {
	
	// Velatorio
	private String nomVelatorio;
	private String folioNota;
	private String dirVelatorio;
	
	// Solicitante
	private String nomSolicitante;
	private String dirSolicitante;
	private String curpSolicitante;
	private String velatorioOrigen;

	// Finado
	private String nomFinado;
	private String parFinado;
	private String folioODS;
	
	// Convenio
	private String folioConvenio;
	private String fechaConvenio;
	
	private String tipoReporte;
	
}
