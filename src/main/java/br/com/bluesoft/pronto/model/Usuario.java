package br.com.bluesoft.pronto.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import br.com.bluesoft.pronto.core.Papel;

@Entity
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int usuarioKey;

	private String username;

	private String password;

	private String nome;

	private String email;

	@ManyToMany
	@JoinTable(name = "USUARIO_PAPEL", joinColumns = { @JoinColumn(name = "USUARIO_KEY") }, inverseJoinColumns = { @JoinColumn(name = "PAPEL_KEY") })
	private Set<Papel> papeis;

	public int getUsuarioKey() {
		return usuarioKey;
	}

	public void setUsuarioKey(final int usuarioKey) {
		this.usuarioKey = usuarioKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public Set<Papel> getPapeis() {
		return papeis;
	}

	public void setPapeis(Set<Papel> papeis) {
		this.papeis = papeis;
	}
}
