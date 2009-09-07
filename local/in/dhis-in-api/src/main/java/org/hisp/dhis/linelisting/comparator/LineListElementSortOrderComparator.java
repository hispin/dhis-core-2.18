package org.hisp.dhis.linelisting.comparator;

import java.util.Comparator;

import org.hisp.dhis.linelisting.LineListElement;

public class LineListElementSortOrderComparator
    implements Comparator<LineListElement>
{

    public int compare( LineListElement lineListElement0, LineListElement lineListElement1 )
    {
        // TODO Auto-generated method stub
        return lineListElement0.getSortOrder().compareTo( lineListElement1.getSortOrder() );
    }

}
