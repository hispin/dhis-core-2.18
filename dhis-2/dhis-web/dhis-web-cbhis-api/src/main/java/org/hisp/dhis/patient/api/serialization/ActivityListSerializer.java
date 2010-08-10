package org.hisp.dhis.patient.api.serialization;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.patient.api.model.ActivityPlanItem;

public class ActivityListSerializer
    implements JavaObjectSerializer<List<ActivityPlanItem>>
{
    private Log log = LogFactory.getLog( this.getClass() );

    @Override
    public void serialize( OutputStream os, List<ActivityPlanItem> activities )
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream( os );

        if ( activities == null || activities.isEmpty() )
        {
            dos.writeByte( 0 );
            return;
        }

        dos.writeByte( activities.size() );
        for ( ActivityPlanItem activity : activities )
            serializeActivity( activity, dos );

    }

    @Override
    public List<ActivityPlanItem> deSerialize( InputStream is )
    {
        return null;
    }

    protected void serializeActivity( ActivityPlanItem activity, OutputStream os )
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream( os );
        dos.writeInt( activity.getBeneficiary().getId() );
    }

}
