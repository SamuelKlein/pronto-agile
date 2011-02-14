package br.com.bluesoft.pronto.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import br.com.bluesoft.pronto.core.Backlog;
import br.com.bluesoft.pronto.core.KanbanStatus;
import br.com.bluesoft.pronto.core.TipoDeTicket;
import br.com.bluesoft.pronto.model.Classificacao;
import br.com.bluesoft.pronto.model.Script;
import br.com.bluesoft.pronto.model.Sprint;
import br.com.bluesoft.pronto.model.Ticket;
import br.com.bluesoft.pronto.model.TicketOrdem;
import br.com.bluesoft.pronto.model.Usuario;
import br.com.bluesoft.pronto.service.GeradorDeLogDeTicket;
import br.com.bluesoft.pronto.service.Seguranca;
import br.com.bluesoft.pronto.util.DateUtil

@Repository
public class TicketDao extends DaoHibernate {
	
	@Override
	public Ticket obter(final Integer ticketKey) {
		
		final StringBuilder hql = new StringBuilder();
		hql.append("select distinct t from Ticket t ");
		hql.append("left join fetch t.sprint ");
		hql.append("left join fetch t.pai ");
		hql.append("left join fetch t.tipoDeTicket ");
		hql.append("left join fetch t.backlog ");
		hql.append("left join fetch t.kanbanStatus ");
		hql.append("left join fetch t.reporter ");
		hql.append("where t.ticketKey = :ticketKey");
		
		return (Ticket) getSession().createQuery(hql.toString()).setInteger("ticketKey", ticketKey).uniqueResult();
	}
	
	@Override
	public Ticket obterPorStatus(final Integer ticketKey, final Integer kanbanStatusKey) {

		final StringBuilder hql = new StringBuilder();
		hql.append("select distinct t from Ticket t ");
		hql.append("left join fetch t.sprint ");
		hql.append("left join fetch t.pai ");
		hql.append("left join fetch t.tipoDeTicket ");
		hql.append("left join fetch t.backlog ");
		hql.append("left join fetch t.kanbanStatus ");
		hql.append("left join fetch t.reporter ");
		hql.append("where t.ticketKey = :ticketKey ");
		hql.append("and t.kanbanStatus.kanbanStatusKey = :kanbanStatusKey");

		return (Ticket) getSession().createQuery(hql.toString()).setInteger("ticketKey", ticketKey).setInteger("kanbanStatusKey", kanbanStatusKey).uniqueResult();
	}
	
	@Override
	public Ticket obterComUsuariosEnvolvidos(final Integer ticketKey) {
		
		String hql = """ 
			select distinct t from Ticket t 
			left join fetch t.sprint 
			left join fetch t.pai 
			left join fetch t.tipoDeTicket 
			left join fetch t.backlog 
			left join fetch t.kanbanStatus 
			left join fetch t.reporter 
			left join t.desenvolvedores 
			left join t.testadores 
			where t.ticketKey = :ticketKey
		"""
		
		return (Ticket) getSession()
		.createQuery(hql)
		.setInteger("ticketKey", ticketKey)
		.uniqueResult();
	}
	
	public Ticket obterComDependecias(final Integer ticketKey) {
		
		String hql = """ 
			select distinct t from Ticket t 
			left join fetch t.sprint 
			left join fetch t.pai 
			left join fetch t.tipoDeTicket 
			left join fetch t.backlog 
			left join fetch t.kanbanStatus 
			left join fetch t.reporter 
			left join fetch t.filhos 
			left join t.desenvolvedores 
			left join t.testadores 
			left join t.comentarios 
			left join t.logs 
			where t.ticketKey = :ticketKey
		"""
		
		return (Ticket) getSession()
		.createQuery(hql)
		.setInteger("ticketKey", ticketKey)
		.uniqueResult();
	}
	
	public TicketDao() {
		super(Ticket.class);
	}
	
	@Override
	public void salvar(final Ticket... tickets) {
		prepararParaSalvar(tickets)
		super.salvar(tickets)
		session.flush()
		session.clear()
		defineValores(tickets)
		session.flush()
	}
	
	
	private void prepararParaSalvar(final Ticket... tickets) {
		for (final Ticket ticket : tickets) {
			if (ticket.getScript() != null && ticket.getScript().getScriptKey() == 0) {
				ticket.setScript(null);
			}
			
			if(ticket.ticketKey == 0) {
				ticket.prioridade = 999999
			}
			
			if (ticket.ticketKey > 0) {
				Ticket antigo = obter(ticket.ticketKey)
				def logs = GeradorDeLogDeTicket.gerarLogs(antigo, ticket)
				ticket.logs.addAll(logs)
			}
		}
		session.clear()
	}
	
	private void defineValores(final Ticket... tickets) {
		
		for (Ticket ticket : tickets) {
			
			ticket = obter(ticket.ticketKey)
			
			ticket.dataDaUltimaAlteracao = DateUtil.getTimestampSemMilissegundos(new Timestamp(new Date().getTime()))
			
			if (ticket.getReporter() == null) {
				ticket.setReporter(Seguranca.getUsuario());
			}
			
			if (ticket.getScript() != null && ticket.getScript().getScriptKey() == 0) {
				ticket.setScript(null);
			}
			
			// Se um ticket tiver filhos, atualizar os dados dos filhos que devem ser sempre iguais aos do pai.
			if (ticket.temFilhos()) {
				ticket.setEsforco(ticket.getSomaDoEsforcoDosFilhos());
				
				for (Ticket filho : ticket.getFilhos()) {
					if (!filho.isImpedido() && !filho.isLixo()) {
						filho.setBacklog(ticket.getBacklog());
					}
					filho.setCliente(ticket.getCliente());
					filho.setSolicitador(ticket.getSolicitador());
					filho.setSprint(ticket.getSprint());
					filho.setTipoDeTicket((TipoDeTicket) getSession().get(TipoDeTicket.class, TipoDeTicket.TAREFA));
				}
				
				if (ticket.isTodosOsFilhosProntos()) {
					if (ticket.getDataDePronto() == null) {
						ticket.setDataDePronto(new Date());
					}
				} else {
					ticket.setDataDePronto(null);
				}
			}
			
			// Se o ticket pai estiver impedido ou na lixeira, o ticket filho deve permancer da mesma forma.
			if (ticket.temPai()) {
				
				final Ticket pai = ticket.pai
				
				if (pai.isLixo() || pai.isImpedido()) {
					ticket.setBacklog(pai.getBacklog())
				} else {
					if (!ticket.isLixo() && !ticket.isImpedido()) {
						ticket.setBacklog(pai.getBacklog())
					}
				}
				
				ticket.setSprint(pai.getSprint())
				ticket.setTipoDeTicket((TipoDeTicket) getSession().get(TipoDeTicket.class, TipoDeTicket.TAREFA))
				
				if (ticket.isDone() && pai.isTodosOsFilhosProntos()) {
					if (pai.getDataDePronto() == null) {
						pai.setDataDePronto(new Date())
						pai.setKanbanStatus((KanbanStatus) getSession().get(KanbanStatus.class, KanbanStatus.DONE))
						super.getSession().update(pai)
					}
				} else {
					if (pai.isEmAndamento()) {
						pai.setKanbanStatus((KanbanStatus) getSession().get(KanbanStatus.class, KanbanStatus.DOING))
					} else {
						pai.setKanbanStatus((KanbanStatus) getSession().get(KanbanStatus.class, KanbanStatus.TO_DO))
					}
					pai.setDataDePronto(null)
				}
			}
			
			// Tarefa nao tem valor de Negocio
			if (ticket.isTarefa()) {
				ticket.setValorDeNegocio(0)
			}
			
			// Grava sysdate na cria��o
			if (ticket.getDataDeCriacao() == null) {
				ticket.setDataDeCriacao(new Date())
			}
			
			// Se o status for pronto tem que ter data de pronto.
			if (ticket.getKanbanStatus().getKanbanStatusKey() == KanbanStatus.DONE && ticket.getDataDePronto() == null) {
				ticket.setDataDePronto(new Date())
			}
			
			getSession().saveOrUpdate(ticket)
		}
	}
	
	public List<Ticket> buscar(String busca, final Integer kanbanStatusKey, final Integer clienteKey, final TicketOrdem ordem, final Classificacao classificacao, String sprintNome, final Boolean ignorarLixeira) {
		
		final StringBuilder hql = new StringBuilder();
		hql.append(" select distinct t from Ticket t   ");
		hql.append(" left join fetch t.sprint          ");
		hql.append(" left join fetch t.reporter        ");
		hql.append(" left join fetch t.tipoDeTicket as tipoDeTicket ");
		hql.append(" left join fetch t.backlog as b    ");
		hql.append(" left join fetch t.kanbanStatus as kanbanStatus ");
		hql.append(" left join fetch t.filhos          ");
		hql.append(" left join fetch t.cliente as cliente  ");
		hql.append(" where 1=1 ");
		
		if (busca!=null) {
			hql.append(" and upper(t.titulo) like :query ");
		}
		
		if (kanbanStatusKey != null) {
			if (kanbanStatusKey == -1) {
				hql.append(" and t.dataDePronto is null ");
			} else if (kanbanStatusKey > 0) {
				hql.append(" and t.kanbanStatus.kanbanStatusKey = :kanbanStatusKey ");
			}
		}
		
		if (clienteKey != null && clienteKey > 0) {
			hql.append(" and t.cliente.clienteKey = :clienteKey ");
		}
		
		if (sprintNome != null && sprintNome.length() > 0) {
			hql.append(" and upper(t.sprint.nome) like :sprintNome ");
		}
		
		if (ignorarLixeira != null && ignorarLixeira) {
			hql.append(" and b.backlogKey <> :backlogKey ");
		}
		
		hql.append(buildOrdem(ordem, classificacao));
		
		
		final Query query = getSession().createQuery(hql.toString())

		if (busca!=null) {
			query.setString("query", '%' + busca.toUpperCase() + '%')
		}
		
		if (kanbanStatusKey != null && kanbanStatusKey > 0) {
			query.setInteger("kanbanStatusKey", kanbanStatusKey)
		}
		
		if (clienteKey != null && clienteKey > 0) {
			query.setInteger("clienteKey", clienteKey)
		}
		
		if (sprintNome != null && sprintNome.length() > 0) {
			query.setString("sprintNome", '%' + sprintNome.toUpperCase() + '%')
		}
		
		if (ignorarLixeira != null && ignorarLixeira) {
			query.setInteger("backlogKey", Backlog.LIXEIRA)
		}
		
		return query.list();
	}
	
	private String buildOrdem(final TicketOrdem ordem, final Classificacao classificacao) {
		String hqlOrdem = null;
		if (ordem != null) {
			switch (ordem) {
				case TicketOrdem.BACKLOG:
					hqlOrdem = "b.descricao";
					break;
				case TicketOrdem.CODIGO:
					hqlOrdem = "t.ticketKey";
					break;
				case TicketOrdem.CLIENTE:
					hqlOrdem = "cliente.nome";
					break;
				case TicketOrdem.ESFORCO:
					hqlOrdem = "t.esforco";
					break;
				case TicketOrdem.STATUS:
					hqlOrdem = "kanbanStatus.descricao";
					break;
				case TicketOrdem.TIPO:
					hqlOrdem = "tipoDeTicket.descricao";
					break;
				case TicketOrdem.VALOR_DE_NEGOCIO:
					hqlOrdem = "t.valorDeNegocio";
					break;
				case TicketOrdem.PRIORIDADE_DO_CLIENTE:
					hqlOrdem = "cliente.nome, t.prioridadeDoCliente";
					break;
				default:
					hqlOrdem = "t.titulo";
					break;
			}
		} else {
			hqlOrdem = "t.titulo ";
		}
		final String hqlClassificacao = classificacao == null || classificacao == Classificacao.ASCENDENTE ? " asc" : " desc";
		return " order by " + hqlOrdem + hqlClassificacao;
	}
	
	public List<Ticket> listarPorSprint(final int sprintKey) {
		
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" where t.sprint.sprintKey = :sprintKey");
		builder.append(" order by t.valorDeNegocio desc, t.esforco desc");
		
		return getSession().createQuery(builder.toString()).setInteger("sprintKey", sprintKey).list();
	}
	
	public List<Ticket> listarNaoConcluidosPorSprint(final int sprintKey) {
		
		final List<Ticket> ticketsDoSprint = listarPorSprint(sprintKey);
		final List<Ticket> ticketsNaoConcluidos = new ArrayList<Ticket>();
		
		for (final Ticket ticket : ticketsDoSprint) {
			if (ticket.getKanbanStatus().getKanbanStatusKey() != KanbanStatus.DONE) {
				ticketsNaoConcluidos.add(ticket);
			}
		}
		
		return ticketsNaoConcluidos;
	}
	
	public List<Ticket> listarPorBacklog(final int backlogKey) {
		
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" where t.backlog.backlogKey = :backlogKey");
		builder.append(" order by t.valorDeNegocio desc, t.esforco desc");
		
		return getSession().createQuery(builder.toString()).setInteger("backlogKey", backlogKey).list();
	}
	
	public List<Usuario> listarDesenvolvedoresDoTicket(final int ticketKey) {
		final String hql = "select t.desenvolvedores from Ticket t where t.ticketKey = :ticketKey";
		return getSession().createQuery(hql).setInteger("ticketKey", ticketKey).list();
	}
	
	public List<Usuario> listarTestadoresDoTicket(final int ticketKey) {
		final String hql = "select t.testadores from Ticket t where t.ticketKey = :ticketKey";
		return getSession().createQuery(hql).setInteger("ticketKey", ticketKey).list();
	}
	
	public List<Ticket> listarEstoriasEDefeitosDoProductBacklog() {
		return listarEstoriasEDefeitosPorBacklog(Backlog.PRODUCT_BACKLOG);
	}

	public List<Ticket> listarEstoriasEDefeitosPorBacklog(final int backlogKey) {
		return listarEstoriasEDefeitosPorBacklog(backlogKey, null, null);
	}
		
	public List<Ticket> listarEstoriasEDefeitosPorBacklog(final int backlogKey, Integer categoriaKey, Integer kanbanStatusKey) {
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" left join fetch t.filhos f ");
		builder.append(" left join fetch t.pai p");
		builder.append(" left join fetch t.kanbanStatus ");
		builder.append(" left join fetch t.tipoDeTicket ");
		builder.append(" where t.backlog.backlogKey = :backlogKey");
		builder.append(" and t.tipoDeTicket.tipoDeTicketKey in (:tipos)");
		if (categoriaKey && categoriaKey > 0) {
			builder.append(" and t.categoria.categoriaKey = :categoriaKey ");
		}
		
		if (kanbanStatusKey && kanbanStatusKey != 0) {
			if (kanbanStatusKey && kanbanStatusKey > 0) {
				builder.append(" and t.kanbanStatus.kanbanStatusKey = :kanbanStatusKey ");
			} else {
				builder.append(" and t.kanbanStatus.kanbanStatusKey != :kanbanStatusKey ");
			}
		}
		
		def query = getSession().createQuery(builder.toString())
		query.setInteger("backlogKey", backlogKey)
		query.setParameterList("tipos", [ TipoDeTicket.ESTORIA, TipoDeTicket.DEFEITO, TipoDeTicket.IDEIA ])
		if (categoriaKey && categoriaKey > 0) {
			query.setInteger 'categoriaKey', categoriaKey
		}
		if (kanbanStatusKey && kanbanStatusKey != 0) {
			if (kanbanStatusKey > 0) {
				query.setInteger 'kanbanStatusKey', kanbanStatusKey
			} else {
				query.setInteger 'kanbanStatusKey', KanbanStatus.DONE
			}
		}
		
		final List<Ticket> lista = query.list();
		
		
		final List<Comparator> comparators = new ArrayList<Comparator>();
		comparators.add(new ReverseComparator(new BeanComparator("valorDeNegocio")));
		comparators.add(new BeanComparator("prioridade"));
		comparators.add(new ReverseComparator(new BeanComparator("esforco")));
		comparators.add(new BeanComparator("ticketKey"));
		final ComparatorChain comparatorChain = new ComparatorChain(comparators);
		
		Collections.sort(lista, comparatorChain);
		
		return lista;
	}

	public List<Ticket> listarEstoriasEDefeitosPorSprint(final int sprintKey) {
		return listarEstoriasEDefeitosPorSprint(sprintKey, null, null)
	}
	
	public List<Ticket> listarEstoriasEDefeitosPorSprint(final int sprintKey, final Integer categoriaKey, Integer kanbanStatusKey) {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(" select distinct t from Ticket t");
		builder.append(" left join fetch t.filhos f ");
		builder.append(" left join fetch t.sprint ");
		builder.append(" left join fetch t.tipoDeTicket ");
		builder.append(" left join fetch t.backlog ");
		builder.append(" left join fetch t.kanbanStatus ");
		builder.append(" left join fetch t.reporter ");
		builder.append(" where t.sprint.sprintKey = :sprintKey");
		builder.append(" and t.tipoDeTicket.tipoDeTicketKey in (:tipos)");
		if (categoriaKey && categoriaKey > 0) {
			builder.append(" and t.categoria.categoriaKey = :categoriaKey ");
		}
		if (kanbanStatusKey && kanbanStatusKey != 0) {
			if (kanbanStatusKey && kanbanStatusKey > 0) {
				builder.append(" and t.kanbanStatus.kanbanStatusKey = :kanbanStatusKey ");
			} else {
				builder.append(" and t.kanbanStatus.kanbanStatusKey != :kanbanStatusKey ");
			}
		}
		def query = getSession().createQuery(builder.toString())
		query.setInteger("sprintKey", sprintKey)
		query.setParameterList("tipos", [ TipoDeTicket.ESTORIA, TipoDeTicket.DEFEITO ])
		if (categoriaKey && categoriaKey > 0) {
			query.setInteger 'categoriaKey', categoriaKey
		}
		if (kanbanStatusKey && kanbanStatusKey != 0) {
			if (kanbanStatusKey > 0) {
				query.setInteger 'kanbanStatusKey', kanbanStatusKey
			} else {
				query.setInteger 'kanbanStatusKey', KanbanStatus.DONE
			}
		}
		
		final List<Ticket> lista = query.list();
		
		final List<Comparator> comparators = new ArrayList<Comparator>();
		comparators.add(new ReverseComparator(new BeanComparator("valorDeNegocio")));
		comparators.add(new BeanComparator("prioridade"));
		comparators.add(new ReverseComparator(new BeanComparator("esforco")));
		comparators.add(new BeanComparator("ticketKey"));
		final ComparatorChain comparatorChain = new ComparatorChain(comparators);
		
		Collections.sort(lista, comparatorChain);
		
		return lista;
	}
	
	public List<Ticket> listarTarefasEmBacklogsDiferentesDasEstoriasPorBacklog(final int backlogKey) {
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" left join fetch t.filhos f ");
		builder.append(" left join fetch t.pai ");
		builder.append(" left join fetch t.sprint ");
		builder.append(" left join fetch t.tipoDeTicket ");
		builder.append(" left join fetch t.backlog ");
		builder.append(" left join fetch t.kanbanStatus ");
		builder.append(" left join fetch t.reporter ");
		builder.append(" where t.backlog.backlogKey = :backlogKey");
		builder.append(" and t.tipoDeTicket.tipoDeTicketKey = :tipoTarefa");
		builder.append(" and t.pai.backlog.backlogKey != t.backlog.backlogKey");
		builder.append(" order by t.valorDeNegocio desc, t.esforco desc");
		def query = getSession().createQuery(builder.toString())
		query.setInteger("backlogKey", backlogKey)
		query.setInteger("tipoTarefa", TipoDeTicket.TAREFA)
		return query.list();
	}
	
	public List<Ticket> listarTicketsQueNaoEstaoNoBranchMaster() {
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" left join fetch t.filhos f ");
		builder.append(" left join fetch t.pai p");
		builder.append(" where t.filhos is empty  and t.branch is not null and t.branch != 'master' and t.branch != ''");
		builder.append(" order by t.branch, t.titulo");
		return getSession().createQuery(builder.toString()).list();
	}
	
	public List<Ticket> listarPorCliente(final int clienteKey) {
		final StringBuilder builder = new StringBuilder();
		builder.append(" select distinct t from Ticket t");
		builder.append(" left join fetch t.filhos f ");
		builder.append(" left join fetch t.pai p");
		builder.append(" where t.backlog.backlogKey != 4 and t.pai is null");
		builder.append(" and t.cliente.clienteKey = :clienteKey");
		builder.append(" order by t.prioridadeDoCliente");
		final Query query = getSession().createQuery(builder.toString());
		query.setInteger("clienteKey", clienteKey);
		return query.list();
	}
	
	public void alterarPrioridadeDoCliente(final int clienteKey, final Integer[] tickets) {
		
		final String sql = "update ticket set prioridade_do_cliente = :prioridade where ticket_key =:ticketKey and cliente_key = :clienteKey";
		int prioridade = 0;
		if (tickets != null) {
			for (int i = 0; i < tickets.length; i++) {
				if (tickets[i] != null) {
					getSession().createSQLQuery(sql).setInteger("ticketKey", tickets[i]).setInteger("clienteKey", clienteKey).setInteger("prioridade", ++prioridade).executeUpdate();
				}
			}
		}
	}
	
	
	void priorizar(Integer[] tickets, int valor) {
		final String sql = "update ticket set prioridade = :prioridade, valor_de_negocio = :valor where ticket_key =:ticketKey";
		int prioridade = 0;
		if (tickets != null) {
			for (int i = 0; i < tickets.length; i++) {
				if (tickets[i] != null) {
					getSession().createSQLQuery(sql)
							.setInteger("ticketKey", tickets[i])
							.setInteger("prioridade", ++prioridade)
							.setInteger('valor',valor)
							.executeUpdate();
				}
			}
		}
	}
	
	void priorizarTarefas(Integer[] tickets) {
		final String sql = "update ticket set prioridade = :prioridade where ticket_key =:ticketKey";
		int prioridade = 0;
		if (tickets != null) {
			for (int i = 0; i < tickets.length; i++) {
				if (tickets[i] != null) {
					getSession().createSQLQuery(sql)
							.setInteger("ticketKey", tickets[i])
							.setInteger("prioridade", ++prioridade)
							.executeUpdate();
				}
			}
		}
	}
	
	
	public void moverParaSprintAtual(final List<Ticket> ticketsParaMover, final Sprint sprintAtual) {
		for (final Ticket ticket : ticketsParaMover) {
			ticket.setSprint(sprintAtual);
		}
	}
	
	public Date obterDataDaUltimaAlteracaoDoTicket(int ticketKey) {
		Query query = session.createQuery('select t.dataDaUltimaAlteracao from Ticket t where t.ticketKey = :ticketKey')
		query.setInteger 'ticketKey', ticketKey
		return query.uniqueResult()
	}
	
	public Map<Integer,Integer> listarKanbanStatusDosTicketsDoSprint(int sprintKey){
		def query = session.createQuery('select t.ticketKey, t.kanbanStatus.kanbanStatusKey from Ticket t where t.sprint.sprintKey = :sprintKey');
		query.setInteger 'sprintKey', sprintKey
		def mapa = [:]
		def list = query.list()
		list.each {
			mapa[it[0] as Integer] = it[1] as Integer
		}
		return mapa
	}
	
	Integer obterTicketKeyIntegradoComZendesk(int zendeskTicketKey){
		def query = session.createSQLQuery('select izd.ticket_key from integracao_zendesk izd where izd.zendesk_ticket_key = :zendeskTicketKey')
		query.setInteger('zendeskTicketKey',zendeskTicketKey)
		return query.uniqueResult() as Integer
	}
	
	void inserirTicketKeyIntegradoComZendesk(int ticketKey, int zendeskTicketKey){
		def query = session.createSQLQuery(' INSERT INTO integracao_zendesk( ticket_key, zendesk_ticket_key ) VALUES (:ticketKey, :zendeskTicketKey) ')
		query.setInteger('ticketKey', ticketKey)
		query.setInteger('zendeskTicketKey', zendeskTicketKey)
		query.executeUpdate()
	}
	
	void excluirVinculoComZendesk(int ticketKey){
		def query = session.createSQLQuery('DELETE FROM integracao_zendesk WHERE ticket_key = :ticketKey')
		query.setInteger('ticketKey', ticketKey)
		query.executeUpdate()
	}
	
	void relacionarComZendesk(ticketKey, zendeskTicketKey) {
		def sql = 'insert into integracao_zendesk (ticket_key, zendesk_ticket_key) values (:ticketKey, :zendeskTicketKey)'
		def query = session.createSQLQuery(sql)
		query.setInteger 'ticketKey', ticketKey
		query.setInteger 'zendeskTicketKey', zendeskTicketKey
		query.executeUpdate()
	}
	
	public Integer obterNumeroDoTicketNoZendesk(Integer ticketKey) {
		def sql = 'select zendesk_ticket_key from integracao_zendesk izd where izd.ticket_key = :ticketKey'
		def query = session.createSQLQuery(sql)
		query.setInteger 'ticketKey', ticketKey
		return query.uniqueResult() as Integer
	}
}
