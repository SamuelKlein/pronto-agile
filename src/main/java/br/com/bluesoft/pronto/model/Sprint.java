package br.com.bluesoft.pronto.model;

import java.sql.Blob;
import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Sprint {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int sprintKey;

	private String nome;

	private Date dataInicial;

	private Date dataFinal;

	private Blob imagem;

	private boolean fechado;

	private boolean atual;

	@OneToMany(mappedBy = "sprint")
	private List<Ticket> tickets;

	public int getSprintKey() {
		return sprintKey;
	}

	public void setSprintKey(final int sprintKey) {
		this.sprintKey = sprintKey;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(final Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(final Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Blob getImagem() {
		return imagem;
	}

	public void setImagem(final Blob imagem) {
		this.imagem = imagem;
	}

	@Override
	public String toString() {
		return this.getNome();
	}

	public boolean isFechado() {
		return fechado;
	}

	public void setFechado(boolean fechado) {
		this.fechado = fechado;
	}

	public boolean isAtual() {
		return atual;
	}

	public void setAtual(boolean atual) {
		this.atual = atual;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public int getEsforcoTotal() {
		int total = 0;
		for (Ticket ticket : tickets) {
			if (ticket.isDefeito() || ticket.isEstoria()) {
				total += ticket.getEsforco();
			}
		}
		return total;
	}
	
	public int getValorDeNegocioTotal() {
		int total = 0;
		for (Ticket ticket : tickets) {
			if (ticket.isDefeito() || ticket.isEstoria()) {
				total += ticket.getValorDeNegocio();
			}
		}
		return total;
	}
}
