package com.imss.sivimss.notasremision.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeneficiarioResponse {

	private Integer idPerBeneficiario;
	private Integer idConBenef;
	private Boolean activo;
	
}
