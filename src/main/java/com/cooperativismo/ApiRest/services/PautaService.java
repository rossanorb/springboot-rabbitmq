package com.cooperativismo.ApiRest.services;

import java.util.List;

import com.cooperativismo.ApiRest.models.Pauta;

public interface PautaService {

	public List<Pauta> findAll();

	public Pauta create(Pauta pauta);

	public Pauta find(Long id);

	public Pauta update(Long id, Pauta pauta);

	public Boolean secaoBloqueada(Long id);
	
	public void Contabiliza(Pauta pauta);
	
	public String autorizaCFP(Long id);
	
}
