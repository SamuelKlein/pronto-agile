package br.com.bluesoft.pronto.controller;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.bluesoft.pronto.core.Backlog;
import br.com.bluesoft.pronto.core.KanbanStatus;
import br.com.bluesoft.pronto.core.Papel;
import br.com.bluesoft.pronto.core.TipoDeTicket;
import br.com.bluesoft.pronto.model.Sprint;
import br.com.bluesoft.pronto.model.Ticket;
import br.com.bluesoft.pronto.model.Usuario;
import br.com.bluesoft.pronto.service.Seguranca;

@Controller
public class LoginController {

	private static final String ACTION_KANBAN = "/kanban/kanban.action";

	@Autowired
	private Seguranca seguranca;

	@Autowired
	private SessionFactory sessionFactory;

	public static Boolean initialized = false;

	@RequestMapping("/start.action")
	public String start() {

		synchronized (initialized) {

			if (!initialized) {
				Usuario usuario = new Usuario();
				usuario.setEmail("andrefaria@bluesoft.com.br");
				usuario.setNome("Andr� Faria Gomes");
				usuario.setUsername("andrefaria");
				usuario.setPassword(seguranca.md5("8437"));
				sessionFactory.getCurrentSession().save(usuario);
				sessionFactory.getCurrentSession().flush();

				usuario = new Usuario();
				usuario.setEmail("junior@bluesoft.com.br");
				usuario.setNome("Luiz dos Santos Faias Jr.");
				usuario.setUsername("junior");
				usuario.setPassword(seguranca.md5(""));
				sessionFactory.getCurrentSession().save(usuario);
				sessionFactory.getCurrentSession().flush();

				TipoDeTicket tipoDeTicket = new TipoDeTicket(1, "Id�ia");
				sessionFactory.getCurrentSession().save(tipoDeTicket);
				tipoDeTicket = new TipoDeTicket(2, "Est�ria");
				sessionFactory.getCurrentSession().save(tipoDeTicket);
				tipoDeTicket = new TipoDeTicket(3, "Defeito");
				sessionFactory.getCurrentSession().save(tipoDeTicket);
				tipoDeTicket = new TipoDeTicket(5, "Impedimento");
				sessionFactory.getCurrentSession().save(tipoDeTicket);
				tipoDeTicket = new TipoDeTicket(6, "Tarefa");
				sessionFactory.getCurrentSession().save(tipoDeTicket);
				sessionFactory.getCurrentSession().flush();

				Papel papel = new Papel(Papel.PRODUCT_OWNER, "Product Owner");
				sessionFactory.getCurrentSession().save(papel);
				papel = new Papel(Papel.TESTADOR, "Testador");
				sessionFactory.getCurrentSession().save(papel);
				papel = new Papel(Papel.SCRUM_MASTER, "Scrum Master");
				sessionFactory.getCurrentSession().save(papel);
				papel = new Papel(Papel.SUPORTE, "Suporte");
				sessionFactory.getCurrentSession().save(papel);
				papel = new Papel(Papel.DESENVOLVEDOR, "Desenvolvedor");
				sessionFactory.getCurrentSession().save(papel);
				sessionFactory.getCurrentSession().flush();

				Sprint sprint = new Sprint();
				sprint.setNome("Canad�");
				sessionFactory.getCurrentSession().save(sprint);
				sprint = new Sprint();
				sprint.setNome("Alemanha");
				sprint.setAtual(true);
				sessionFactory.getCurrentSession().save(sprint);
				sessionFactory.getCurrentSession().flush();

				sessionFactory.getCurrentSession().save(new Backlog(Backlog.IDEIAS, "Id�ias"));
				sessionFactory.getCurrentSession().save(new Backlog(Backlog.IMPEDIMENTOS, "Impedimentos"));
				sessionFactory.getCurrentSession().save(new Backlog(Backlog.LIXEIRA, "Lixeira"));
				Backlog productBacklog = new Backlog(Backlog.PRODUCT_BACKLOG, "Product Backlog");
				sessionFactory.getCurrentSession().save(productBacklog);
				Backlog sprintBacklog = new Backlog(Backlog.SPRINT_BACKLOG, "Sprint Backlog");
				sessionFactory.getCurrentSession().save(sprintBacklog);
				sessionFactory.getCurrentSession().flush();

				sessionFactory.getCurrentSession().save(new KanbanStatus(KanbanStatus.TO_DO, "To Do"));
				sessionFactory.getCurrentSession().save(new KanbanStatus(KanbanStatus.DOING, "Doing"));
				sessionFactory.getCurrentSession().save(new KanbanStatus(KanbanStatus.TO_TEST, "To Test"));
				sessionFactory.getCurrentSession().save(new KanbanStatus(KanbanStatus.TESTING, "Testing"));
				sessionFactory.getCurrentSession().save(new KanbanStatus(KanbanStatus.DONE, "Done"));

				sessionFactory.getCurrentSession().flush();

				Ticket cadastro = new Ticket();
				cadastro.setTitulo("Cadastro de Produtos");
				cadastro.setTipoDeTicket(new TipoDeTicket(2));
				cadastro.setSolicitador("Alberto");
				cadastro.setCliente("Chama");
				cadastro.setBacklog(productBacklog);
				cadastro.setValorDeNegocio(100);
				cadastro.setEsforco(13);
				cadastro.setReporter(usuario);
				cadastro.setSprint(null);
				sessionFactory.getCurrentSession().save(cadastro);
				sessionFactory.getCurrentSession().flush();

				Ticket consulta = new Ticket();
				consulta.setTitulo("Consulta de Cheques");
				consulta.setTipoDeTicket(new TipoDeTicket(2));
				consulta.setSolicitador("Luiz");
				consulta.setCliente("Pedreira");
				consulta.setBacklog(sprintBacklog);
				consulta.setSprint(sprint);
				consulta.setValorDeNegocio(100);
				consulta.setEsforco(13);
				consulta.setReporter(usuario);
				sessionFactory.getCurrentSession().save(consulta);
				
				Ticket relatorio = new Ticket();
				relatorio.setTitulo("Relatorio de Vendas");
				relatorio.setTipoDeTicket(new TipoDeTicket(2));
				relatorio.setSolicitador("Luiz");
				relatorio.setCliente("Pedreira");
				relatorio.setBacklog(sprintBacklog);
				relatorio.setSprint(sprint);
				relatorio.setValorDeNegocio(100);
				relatorio.setEsforco(13);
				relatorio.setReporter(usuario);
				sessionFactory.getCurrentSession().save(relatorio);
				
				Ticket cobranca = new Ticket();
				cobranca.setTitulo("Cobran�a");
				cobranca.setTipoDeTicket(new TipoDeTicket(2));
				cobranca.setSolicitador("Luiz");
				cobranca.setCliente("Pedreira");
				cobranca.setBacklog(sprintBacklog);
				cobranca.setSprint(sprint);
				cobranca.setValorDeNegocio(100);
				cobranca.setEsforco(13);
				cobranca.setReporter(usuario);
				sessionFactory.getCurrentSession().save(cobranca);
				sessionFactory.getCurrentSession().flush();

				initialized = true;
			}

		}

		return "/login/login.login.jsp";

	}

	@RequestMapping("/login.action")
	public String login(final Model model, final HttpSession httpSession, final String username, final String password) {

		final String md5 = seguranca.md5(password);
		final Usuario usuario = (Usuario) sessionFactory.getCurrentSession().createQuery("from Usuario u where u.username = :username and u.password = :password").setString("username", username).setString("password", md5).uniqueResult();
		if (usuario == null) {
			model.addAttribute("mensagem", "Usu�rio e/ou senha inv�lidos!");
			return "/start.action";
		} else {
			httpSession.setAttribute("usuario", usuario);
			return ACTION_KANBAN;

		}

	}

	@RequestMapping("/logout.action")
	public String login(final Model model, final HttpSession httpSession) {
		httpSession.removeAttribute("usuario");
		return "/login/login.login.jsp";
	}
}
