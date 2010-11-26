package org.hisp.dhis.web.api.model.comparator;

import java.util.Comparator;

import org.hisp.dhis.web.api.model.Activity;

public class ActivityComparator implements Comparator<Activity>
{

    @Override
    public int compare( Activity act1, Activity act2 )
    {
        return act2.getBeneficiary().getFullName().compareToIgnoreCase( act1.getBeneficiary().getFullName() );
    }
}
