package br.com.bluesoft.pronto.core

import javax.persistence.Cacheable;
import javax.persistence.Entity
import javax.persistence.Id

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Cacheable
@org.hibernate.annotations.Entity(mutable = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "eternal")
class KanbanStatus implements Comparable {
	
	public static final int TO_DO = 1
	public static final int DOING = 2
	public static final int TESTING = 31
	public static final int DONE = 100
	
	@Id
	int kanbanStatusKey
	
	String descricao
	
	boolean fixo
	
	int ordem
	
	KanbanStatus() {
		
	}
	
	KanbanStatus( int kanbanStatusKey) {
		this.kanbanStatusKey = kanbanStatusKey
	}
	
	KanbanStatus( int kanbanStatusKey,  String descricao) {
		this.kanbanStatusKey = kanbanStatusKey
		this.descricao = descricao
	}
	
	String toString() {
		return descricao
	}
	
	int compareTo(def outro) {
		this.ordem.compareTo outro.ordem  
	}
	
}