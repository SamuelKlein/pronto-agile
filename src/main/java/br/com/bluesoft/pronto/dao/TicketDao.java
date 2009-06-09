package br.com.bluesoft.pronto.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.bluesoft.pronto.core.Backlog;
import br.com.bluesoft.pronto.core.TipoDeTicket;
import br.com.bluesoft.pronto.model.Ticket;
import br.com.bluesoft.pronto.model.Usuario;

@Repository
public class TicketDao extends DaoHibernate<Ticket, Integer> {

	public TicketDao() {
		super(Ticket.class);
	}

	@Override
	public void salvar(final Ticket ticket) {

		// Se um ticket tiver filhos, atualizar os dados dos filhos que devem ser sempre iguais aos do pai.
		if (ticket.temFilhos()) {
			for (final Ticket filho : ticket.getFilhos()) {

				if (!filho.isImpedido() || !filho.isLixo()) {
					filho.setBacklog(ticket.getBacklog());
				}

				filho.setCliente(ticket.getCliente());
				filho.setSolicitador(ticket.getSolicitador());
				filho.setSprint(ticket.getSprint());
				filho.setTipoDeTicket((TipoDeTicket) getSession().get(TipoDeTicket.class, TipoDeTicket.TAREFA));
			}
		}

		// Se o ticket pai estiver impedido ou na lixeira, o ticket filho deve permancer da mesma forma.
		if (ticket.temPai()) {
			final Ticket pai = ticket.getPai();
			if (pai.isLixo() || pai.isImpedido()) {
				ticket.setBacklog(pai.getBacklog());
			} else {
				if (!ticket.isLixo() && !ticket.isImpedido()) {
					ticket.setBacklog(pai.getBacklog());
				}
			}
			ticket.setSprint(pai.getSprint());
			ticket.setTipoDeTicket((TipoDeTicket) getSession().get(TipoDeTicket.class, TipoDeTicket.TAREFA));
		}

		super.salvar(ticket);
	}

	@SuppressWarnings("unchecked")
	public List<Ticket> buscar(final String busca) {
		final String hql = "from Ticket t where upper(t.titulo) like :query";
		final Query query = getSession().createQuery(hql).setString("query", '%' + busca.toUpperCase() + '%');
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Ticket> listarPorSprint(final int sprintKey) {

		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" where t.sprint.sprintKey = :sprintKey");
		builder.append(" and t.backlog.backlogKey = :backlogKey");
		builder.append(" order by t.valorDeNegocio desc, t.esforco desc");

		return getSession().createQuery(builder.toString()).setInteger("sprintKey", sprintKey).setInteger("backlogKey", Backlog.SPRINT_BACKLOG).list();

	}

	@SuppressWarnings("unchecked")
	public List<Ticket> listarPorBacklog(final int backlogKey) {

		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" where t.backlog.backlogKey = :backlogKey");
		builder.append(" order by t.valorDeNegocio desc, t.esforco desc");

		return getSession().createQuery(builder.toString()).setInteger("backlogKey", backlogKey).list();

	}

	@SuppressWarnings("unchecked")
	public List<Usuario> listarDesenvolvedoresDoTicket(final int ticketKey) {
		final String hql = "select t.desenvolvedores from Ticket t where t.ticketKey = :ticketKey";
		return getSession().createQuery(hql).setInteger("ticketKey", ticketKey).list();
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> listarTestadoresDoTicket(final int ticketKey) {
		final String hql = "select t.testadores from Ticket t where t.ticketKey = :ticketKey";
		return getSession().createQuery(hql).setInteger("ticketKey", ticketKey).list();
	}

	public List<Ticket> listarEstoriasEDefeitosDoProductBacklog() {
		return listarEstoriasEDefeitosPorBacklog(Backlog.PRODUCT_BACKLOG);
	}

	@SuppressWarnings("unchecked")
	public List<Ticket> listarEstoriasEDefeitosPorBacklog(final int backlogKey) {
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" where t.backlog.backlogKey = :backlogKey");
		builder.append(" and t.tipoDeTicket.tipoDeTicketKey in (:tipos)");
		builder.append(" order by t.valorDeNegocio desc, t.esforco desc");
		return getSession().createQuery(builder.toString()).setInteger("backlogKey", backlogKey).setParameterList("tipos", new Integer[] { TipoDeTicket.ESTORIA, TipoDeTicket.DEFEITO }).list();
	}

	@SuppressWarnings("unchecked")
	public List<Ticket> listarEstoriasEDefeitosPorSprint(final int sprintKey) {
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" where t.sprint.sprintKey = :sprintKey");
		builder.append(" and t.tipoDeTicket.tipoDeTicketKey in (:tipos)");
		builder.append(" order by t.valorDeNegocio desc, t.esforco desc");
		return getSession().createQuery(builder.toString()).setInteger("sprintKey", sprintKey).setParameterList("tipos", new Integer[] { TipoDeTicket.ESTORIA, TipoDeTicket.DEFEITO }).list();
	}

}
