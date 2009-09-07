package org.hisp.dhis.linelisting.comparator;

import java.util.Comparator;

import org.hisp.dhis.linelisting.LineListOption;

public class LineListSortOptionSortOrderComparator
    implements Comparator<LineListOption>
{

    public int compare( LineListOption lineListOption0, LineListOption lineListOption1 )
    {
        // TODO Auto-generated method stub
        return lineListOption0.getSortOrder().compareTo( lineListOption1.getSortOrder() );
    }

}
