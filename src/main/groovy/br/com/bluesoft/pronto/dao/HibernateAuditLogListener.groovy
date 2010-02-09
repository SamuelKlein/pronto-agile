package br.com.bluesoft.pronto.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEvent;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import br.com.bluesoft.pronto.annotations.Label;
import br.com.bluesoft.pronto.model.Ticket;
import br.com.bluesoft.pronto.model.TicketLog;
import br.com.bluesoft.pronto.service.Seguranca;
import br.com.bluesoft.pronto.util.DateUtil;

public final class HibernateAuditLogListener implements PreDeleteEventListener, PreInsertEventListener, PreUpdateEventListener, PreLoadEventListener, Initializable {

	public final void initialize(final Configuration cfg) {}

	
	public final boolean onPreDelete(final PreDeleteEvent event) {
		return false;
	}

	
	public final boolean onPreInsert(final PreInsertEvent event) {
		return false;
	}

	public final boolean onPreUpdate(final PreUpdateEvent event) {

		StatelessSession session = null;

		try {

			if (!event.getEntity().getClass().equals(Ticket.class)) {
				return false;
			}

			final Serializable entityId = event.getPersister().hasIdentifierProperty() ? event.getPersister().getIdentifier(event.getEntity(), event.getPersister().guessEntityMode(event.getEntity())) : null;
			final Date transTime = new Date();
			final EntityMode entityMode = event.getPersister().guessEntityMode(event.getEntity());
			Object oldPropValue = null;
			Object newPropValue = null;

			session = event.getPersister().getFactory().openStatelessSession()

			final Object existingEntity = session.get(event.getEntity().getClass(), entityId);

			if (existingEntity != null) {

				for (final String propertyName : event.getPersister().getPropertyNames()) {

					String campo = propertyName;
					final boolean temLabel = Ticket.class.getDeclaredField(propertyName).isAnnotationPresent(Label.class);
					if (temLabel) {
						campo = Ticket.class.getDeclaredField(propertyName).getAnnotation(Label.class).value();
					}

					newPropValue = event.getPersister().getPropertyValue(event.getEntity(), propertyName, entityMode);
					if (newPropValue != null) {
						final boolean ehUmaCollection = newPropValue instanceof Collection;
						if (!ehUmaCollection) {
							oldPropValue = event.getPersister().getPropertyValue(existingEntity, propertyName, entityMode);

							final String oldValue = makeString(oldPropValue);
							final String newValue = makeString(newPropValue);

							if (!oldValue.equals(newValue)) {
								final TicketLog history = new TicketLog();
								history.setTicket((Ticket) event.getEntity());
								history.setData(transTime);
								history.setUsuario(Seguranca.getUsuario().getUsername());
								history.setCampo(campo);
								history.setValorAntigo(oldValue);
								history.setValorNovo(newValue);
								history.setOperacao(TicketLog.ALTERACAO);
								if (history.isDiferente()) {
									session.insert(history);
								}
							}
						}
					}
				}
			}
			
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return false;
	}

	private String makeString(final Object x) {

		String str = String.valueOf(x);

		if (x instanceof Date) {
			str = DateUtil.toString((Date) x);
		}

		if (x == null || x.equals("null")) {
			return "em branco";
		} else {
			return str;
		}
	}

	public final void onPreLoad(final PreLoadEvent event) {}
}
