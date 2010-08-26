package org.hisp.dhis.mobile.db;

import org.hisp.dhis.mobile.model.AbstractModel;

public class OrgUnitRecordFilter
    extends AbstractModelRecordFilter
{

    public OrgUnitRecordFilter( AbstractModel model )
    {
        super( model );
    }

    public boolean matches( byte[] suspect )
    {
        return super.matches( suspect );
    }

}
