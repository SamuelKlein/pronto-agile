package br.com.bluesoft.pronto.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.bluesoft.pronto.core.Papel;
import br.com.bluesoft.pronto.model.Usuario;

@Repository
public class UsuarioDao extends DaoHibernate<Usuario, String> {

	public UsuarioDao() {
		super(Usuario.class);
	}

	public int obterQuantidadeDeUsuariosCadastrados() {
		return getSession().createCriteria(Usuario.class).list().size();
	}

	public String obterPassword(final String username) {
		return (String) getSession().createQuery("select password from Usuario u where u.username = :username").setString("username", username).uniqueResult();

	}

	public List<Usuario> listarTestadores() {
		return listarUsuariosPorPapel(Papel.TESTADOR);
	}

	public List<Usuario> listarDesenvolvedores() {
		return listarUsuariosPorPapel(Papel.DESENVOLVEDOR);
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> listarUsuariosPorPapel(final int papelKey) {
		final String hql = "select distinct u from Usuario u inner join fetch u.papeis p where p.papelKey = :papel order by u.username";
		return getSession().createQuery(hql).setInteger("papel", papelKey).list();
	}
}
