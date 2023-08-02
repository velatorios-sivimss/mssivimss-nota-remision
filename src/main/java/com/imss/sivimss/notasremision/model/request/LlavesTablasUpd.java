package com.imss.sivimss.notasremision.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlavesTablasUpd {
	
	private Integer idTipoPrevision;
	private Boolean isContratante;
	private Integer idConvenio;
    private Integer idContratantePaquete;
    private Integer idPersona;
	
}
