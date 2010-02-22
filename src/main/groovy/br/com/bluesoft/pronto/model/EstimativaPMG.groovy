package br.com.bluesoft.pronto.model

public enum EstimativaPMG {
	
	P("Pequeno"), M("M�dio"), G("Grande")
	
	private TipoEstimativa(String descricao) {
		this.descricao = descricao;
	}
	
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}
	
	public String getString() {
		return this.toString();
	}
	
}
