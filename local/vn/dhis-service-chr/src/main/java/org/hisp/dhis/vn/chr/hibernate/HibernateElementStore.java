package org.hisp.dhis.vn.chr.hibernate;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementStore;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.hibernate.HibernateSessionManager;

public class HibernateElementStore implements ElementStore {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private HibernateSessionManager hibernateSessionManager;

	// -----------------------------------------------------------------------------------------------
	// Getter && Setter
	// -----------------------------------------------------------------------------------------------
	public void setHibernateSessionManager(
			HibernateSessionManager hibernateSessionManager) {
		this.hibernateSessionManager = hibernateSessionManager;
	}

	// -----------------------------------------------------------------------------------------------
	// Implements
	// -----------------------------------------------------------------------------------------------

	public int addElement(Element element) {
		
		Session session = hibernateSessionManager.getCurrentSession();

		String name = element.getName().toLowerCase();
		
		element.setName(name);
		
		return (Integer) session.save(element);
	}

	public void deleteElement(int id) {
		
		Session session = hibernateSessionManager.getCurrentSession();

		session.delete(getElement(id));		
	}

	@SuppressWarnings("unchecked")
	public Collection<Element> getAllElements() {
		
		Session session = hibernateSessionManager.getCurrentSession();

		Criteria criteria = session.createCriteria(Element.class);

		return criteria.list();
	}

	public Element getElement(int id) {

		Session session = hibernateSessionManager.getCurrentSession();

		return (Element) session.get(Element.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Element> getElementsByForm(Form form) {

		Session session = hibernateSessionManager.getCurrentSession();

		Criteria criteria = session.createCriteria(Element.class);

		criteria.add(Restrictions.eq("form", form));

		return criteria.list();
	}
    
	@SuppressWarnings("unchecked")
	public Collection<Element> getElementsByEgroup(Egroup egroup)
    {
		Session session = hibernateSessionManager.getCurrentSession();

		Criteria criteria = session.createCriteria(Element.class);

		criteria.add(Restrictions.eq("egroup", egroup));

		return criteria.list();
    }

	public void updateElement(Element element) {
		
		Session session = hibernateSessionManager.getCurrentSession();
		
		session.update(element);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Element> getElementsByNoEgroup(){
		
		Session session = hibernateSessionManager.getCurrentSession();

		Criteria criteria = session.createCriteria(Element.class);

		criteria.add(Restrictions.eq("egroup", null));

		return criteria.list();
	}
	
	public Element getElement(String name){
		
		Session session = hibernateSessionManager.getCurrentSession();

		Criteria criteria = session.createCriteria(Element.class);

		criteria.add(Restrictions.eq("name", name));
		
		return (Element)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Element> getElementsByFormLink(int formId){
	
		Session session = hibernateSessionManager.getCurrentSession();

		Criteria criteria = session.createCriteria(Element.class);

		criteria.add(Restrictions.eq("formLink", formId));
		
		return criteria.list();
	}
}
