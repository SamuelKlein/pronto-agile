package br.com.bluesoft.pronto.controller;

import java.util.Date;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.bluesoft.pronto.core.KanbanStatus;
import br.com.bluesoft.pronto.dao.SprintDao;
import br.com.bluesoft.pronto.dao.TicketDao;
import br.com.bluesoft.pronto.model.Sprint;
import br.com.bluesoft.pronto.model.Ticket;
import br.com.bluesoft.pronto.service.Seguranca;

@Controller
public class KanbanController {

	private static final String VIEW_KANBAN = "/kanban/kanban.kanban.jsp";

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SprintDao sprintDao;

	@Autowired
	private TicketDao ticketDao;

	@RequestMapping("/kanban/kanban.action")
	public String index(final Model model) {
		final Sprint sprintAtual = sprintDao.getSprintAtualComTickets();

		if (sprintAtual == null) {
			return LoginController.VIEW_BEM_VINDO;
		}

		model.addAttribute("sprint", sprintAtual);
		model.addAttribute("tickets", sprintAtual.getTicketsParaOKanban());

		return VIEW_KANBAN;
	}

	@RequestMapping("/kanban/mover.action")
	public String mover(final Model model, final int ticketKey, final int kanbanStatusKey) {

		final Ticket ticket = (Ticket) sessionFactory.getCurrentSession().get(Ticket.class, ticketKey);
		ticket.setKanbanStatus((KanbanStatus) sessionFactory.getCurrentSession().get(KanbanStatus.class, kanbanStatusKey));

		if (kanbanStatusKey == KanbanStatus.DONE) {
			ticket.setDataDePronto(new Date());
			if (Seguranca.getUsuario().isTestador()) {
				ticket.addTestador(Seguranca.getUsuario());
			}
		} else {
			ticket.setDataDePronto(null);
			if (kanbanStatusKey == KanbanStatus.DOING && Seguranca.getUsuario().isDesenvolvedor()) {
				ticket.addDesenvolvedor(Seguranca.getUsuario());
			}
		}

		ticketDao.salvar(ticket);

		return null;
	}

}
