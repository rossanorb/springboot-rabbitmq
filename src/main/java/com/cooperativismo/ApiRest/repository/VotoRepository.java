package com.cooperativismo.ApiRest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooperativismo.ApiRest.models.Voto;

public interface VotoRepository extends JpaRepository<Voto, Long>{
	List<Voto> findByVotoAndPautaId(Boolean voto, Long PautaId);
	List<Voto> findByPautaId(Long pautaId );
	List<Voto> findByPautaIdAndAssociadoId(Long pautaId, Long AssociadoId);
}
