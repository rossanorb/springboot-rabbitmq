package com.cooperativismo.ApiRest.resources.v1;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.cooperativismo.ApiRest.models.Voto;
import com.cooperativismo.ApiRest.services.PautaService;
import com.cooperativismo.ApiRest.services.VotoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "API REST - Model Voto")
@RestController
@RequestMapping("/v1/votos")
public class VotoResource {
	
	@Autowired
	private VotoService votoService;
	
	@Autowired
	private PautaService pautaService;
	
	public VotoResource(VotoService votoService, PautaService pautaService)
	{
		this.votoService = votoService;
		this.pautaService = pautaService;
	}
	
	@ApiOperation(value = "Vota na pauta")
	@PostMapping
	@ResponseBody
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<?>  create(@Valid @RequestBody Voto voto, Errors errors) throws IOException {
		if(!errors.hasErrors()) {	
			
			String result = this.pautaService.autorizaCFP(voto.getAssociado_id());
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode  = objectMapper.readValue(result,  JsonNode.class);
			
			String status = jsonNode.get("status").textValue();
			
			if( status.trim().equals("UNABLE_TO_VOTE") ) {
				Erros erro = new Erros("CPF n�o est� autorizado");
				System.out.println( "CPF n�o est� autorizado" );
				return ResponseEntity.badRequest().body(erro);
			}
			
			Boolean votou = this.votoService.jaVotou(voto);
			
			if(votou) {
				Erros erro = new Erros("Voc� j� votou");
				return ResponseEntity.badRequest().body(erro);				
			}
			
			Boolean secaoBloqueada = this.pautaService.secaoBloqueada(voto.getPauta_id());
			
			if(secaoBloqueada) {
				Erros erro = new Erros("Se��o fechada");
				return ResponseEntity.badRequest().body(erro);
			}
			
			Voto votoCreated = this.votoService.create(voto);
			return new ResponseEntity<Voto>(votoCreated, HttpStatus.CREATED);
			
		}
		
		return this.getErrors(errors);
	}
	
	private ResponseEntity<?> getErrors(Errors errors) {
		return ResponseEntity
				.badRequest()
				.body(errors
						.getAllErrors()
						.stream()
						.map(msg -> msg.getDefaultMessage())
						.collect(Collectors.joining("\n")));			
	}	
	
	
}
