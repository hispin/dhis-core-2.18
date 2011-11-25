/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.common.hibernate;

import java.util.Collection;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.GenericDimensionalObjectStore;
import org.hisp.dhis.concept.Concept;

/**
 *
 * @author bobj
 */
public class HibernateDimensionalObjectStore<T> extends HibernateIdentifiableObjectStore
  implements GenericDimensionalObjectStore
{

    @Override
    public Collection<T> getByConcept(Concept concept) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("concept", concept));
        return criteria.list();
    }
    
}
