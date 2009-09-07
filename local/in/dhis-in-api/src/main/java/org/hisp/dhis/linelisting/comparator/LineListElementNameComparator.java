package org.hisp.dhis.linelisting.comparator;

import java.util.Comparator;

import org.hisp.dhis.linelisting.LineListElement;

public class LineListElementNameComparator
    implements Comparator<LineListElement>
{

    public int compare( LineListElement lineElement0, LineListElement lineElement1 )
    {
        // TODO Auto-generated method stub
        return lineElement0.getName().compareTo( lineElement1.getName() );
    }

}
