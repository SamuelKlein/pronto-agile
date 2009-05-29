package br.com.bluesoft.pronto.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
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

import br.com.bluesoft.pronto.model.Ticket;
import br.com.bluesoft.pronto.model.TicketLog;

public final class HibernateAuditLogListener implements PreDeleteEventListener, PreInsertEventListener, PreUpdateEventListener, PreLoadEventListener, Initializable {

	private static final long serialVersionUID = 1L;

	@Override
	public final void initialize(final Configuration cfg) {

	}

	@Override
	public final boolean onPreDelete(final PreDeleteEvent event) {

		return false;
	}

	@Override
	public final boolean onPreInsert(final PreInsertEvent event) {

		try {

			if (!event.getEntity().getClass().equals(Ticket.class)) {
				return false;
			}

			final Date transTime = new Date(); // new
			// Date(event.getSource().getTimestamp());
			final EntityMode entityMode = event.getPersister().guessEntityMode(event.getEntity());
			Object newPropValue = null;

			// need to have a separate session for audit save
			StatelessSession session = event.getPersister().getFactory().openStatelessSession();
			session.beginTransaction();

			for (String propertyName : event.getPersister().getPropertyNames()) {
				newPropValue = event.getPersister().getPropertyValue(event.getEntity(), propertyName, entityMode);
				// because we are performing an insert we only need to be
				// concerned will non-null values
				if (newPropValue != null) {
					// collections will fire their own events
					if (!(newPropValue instanceof Collection)) {
						TicketLog history = new TicketLog();
						history.setTicket((Ticket) event.getEntity());
						history.setData(transTime);
						history.setUsuario("andrefaria");
						history.setCampo(null);
						history.setValorAntigo(null);
						history.setValorNovo(String.valueOf(newPropValue));
						history.setOperacao(TicketLog.INCLUSAO);
						session.insert(history);
					}
				}
			}

			session.getTransaction().commit();
			session.close();
		} catch (HibernateException e) {
		}
		return false;
	}

	@Override
	public final boolean onPreUpdate(PreUpdateEvent event) {
		try {

			if (!event.getEntity().getClass().equals(Ticket.class)) {
				return false;
			}

			final Serializable entityId = event.getPersister().hasIdentifierProperty() ? event.getPersister().getIdentifier(event.getEntity(), event.getPersister().guessEntityMode(event.getEntity())) : null;
			final Date transTime = new Date(); // new
			// Date(event.getSource().getTimestamp());
			final EntityMode entityMode = event.getPersister().guessEntityMode(event.getEntity());
			Object oldPropValue = null;
			Object newPropValue = null;

			// need to have a separate session for audit save
			StatelessSession session = event.getPersister().getFactory().openStatelessSession();
			session.beginTransaction();

			// get the existing entity from session so that we can extract
			// existing property values
			Object existingEntity = session.get(event.getEntity().getClass(), entityId);

			// cycle through property names, extract corresponding property
			// values and insert new entry in audit trail
			for (String propertyName : event.getPersister().getPropertyNames()) {
				newPropValue = event.getPersister().getPropertyValue(event.getEntity(), propertyName, entityMode);
				// because we are performing an insert we only need to be
				// concerned will non-null values
				if (newPropValue != null) {
					// collections will fire their own events
					if (!(newPropValue instanceof Collection)) {
						oldPropValue = event.getPersister().getPropertyValue(existingEntity, propertyName, entityMode);

						String oldValue = String.valueOf(oldPropValue);
						String newValue = String.valueOf(newPropValue);

						if (!oldValue.equals(newValue)) {
							TicketLog history = new TicketLog();
							history.setTicket((Ticket) event.getEntity());
							history.setData(transTime);
							history.setUsuario("andrefaria");
							history.setCampo(propertyName);
							history.setValorAntigo(oldValue);
							history.setValorNovo(newValue);
							history.setOperacao(TicketLog.ALTERACAO);
							session.insert(history);
						}
					}
				}
			}

			session.getTransaction().commit();
			session.close();
		} catch (HibernateException e) {
		}
		return false;
	}

	@Override
	public final void onPreLoad(final PreLoadEvent event) {

	}
}