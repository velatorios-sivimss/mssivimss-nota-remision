package com.imss.sivimss.notasremision.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlavesTablasUpd {

	private Integer idTipoPrevision;
	private Integer idContratante;
	private Integer idConvenioPF;
	private Integer idContratantePaquete;
	private Integer idPerContratante;
	private Integer idPerFinado;
	private Integer idTipoOrden;
	private Integer idConvenioSFPA;
	private Integer idEstatusConvenio;

}
