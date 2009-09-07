package org.hisp.dhis.linelisting.comparator;

import java.util.Comparator;

import org.hisp.dhis.linelisting.LineListGroup;

public class LineListGroupShortNameComparator
    implements Comparator<LineListGroup>
{
    public int compare( LineListGroup lineListGroup0, LineListGroup lineListGroup1 )
    {
        return lineListGroup0.getShortName().compareTo( lineListGroup1.getShortName() );
    }
}
