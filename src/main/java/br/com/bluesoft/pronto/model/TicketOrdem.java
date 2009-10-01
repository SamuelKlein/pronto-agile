package br.com.bluesoft.pronto.model;

public enum TicketOrdem {

	/**/CODIGO("C�digo"),
	/**/TITULO("T�tulo"),
	/**/TIPO("Tipo"),
	/**/CLIENTE("Cliente"),
	/**/BACKLOG("Backlog"),
	/**/VALOR_DE_NEGOCIO("Valor de Neg�cio"),
	/**/ESFORCO("Esfor�o"),
	/**/STATUS("Status");

	private String descricao;

	private TicketOrdem(final String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
