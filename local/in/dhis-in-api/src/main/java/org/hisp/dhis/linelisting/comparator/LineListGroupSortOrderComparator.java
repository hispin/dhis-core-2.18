package org.hisp.dhis.linelisting.comparator;

import java.util.Comparator;

import org.hisp.dhis.linelisting.LineListGroup;

public class LineListGroupSortOrderComparator
    implements Comparator<LineListGroup>
{
    public int compare( LineListGroup lineListGroup0, LineListGroup lineListGroup1 )
    {
        return lineListGroup0.getSortOrder().compareTo( lineListGroup1.getSortOrder() );
    }
}
