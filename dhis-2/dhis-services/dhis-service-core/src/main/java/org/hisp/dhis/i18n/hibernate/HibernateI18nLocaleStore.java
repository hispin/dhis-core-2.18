package org.hisp.dhis.i18n.hibernate;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nLocaleStore;
import org.hisp.dhis.i18n.locale.I18nLocale;

public class HibernateI18nLocaleStore
    extends HibernateIdentifiableObjectStore<I18nLocale>
    implements I18nLocaleStore
{
    @Override
    public I18nLocale getI18nLocaleByLocale( String language, String country )
    {
        return (I18nLocale) getCriteria( Restrictions.eq( "language", language ), Restrictions.eq( "country", country ) )
            .uniqueResult();
    }

}
