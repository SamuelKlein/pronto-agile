package br.com.bluesoft.pronto.controller;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.bluesoft.pronto.model.Usuario;
import br.com.bluesoft.pronto.service.Seguranca;

@Controller
public class LoginController {

	private static final String ACTION_KANBAN = "/kanban/kanban.action";
	static final String VIEW_BEM_VINDO = "/bemVindo.jsp";

	@Autowired
	private SessionFactory sessionFactory;

	public static Boolean initialized = false;

	@RequestMapping("/start.action")
	public String start(final HttpSession httpSession) {
		if (httpSession.getAttribute("usuarioLogado") != null) {
			return "redirect:" + ACTION_KANBAN;
		} else {
			return "/login/login.login.jsp";
		}
	}

	@RequestMapping("/login.action")
	public String login(final Model model, final HttpSession httpSession, final String username, final String password) {
		final String md5 = Seguranca.encrypt(password);
		final Usuario usuario = (Usuario) sessionFactory.getCurrentSession().createQuery("select distinct u from Usuario u inner join fetch u.papeis where u.username = :username and u.password = :password").setString("username", username).setString("password", md5).uniqueResult();
		if (usuario == null) {
			model.addAttribute("erro", "Usu�rio e/ou senha inv�lidos!");
			return "/start.action";
		} else {
			httpSession.setAttribute("usuarioLogado", usuario);
			return "redirect:" + ACTION_KANBAN;

		}

	}

	@RequestMapping("/logout.action")
	public String login(final Model model, final HttpSession httpSession) {
		httpSession.removeAttribute("usuarioLogado");
		return "/login/login.login.jsp";
	}

}
