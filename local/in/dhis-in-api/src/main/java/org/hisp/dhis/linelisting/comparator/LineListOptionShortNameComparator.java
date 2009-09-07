package org.hisp.dhis.linelisting.comparator;

import java.util.Comparator;

import org.hisp.dhis.linelisting.LineListOption;

public class LineListOptionShortNameComparator
    implements Comparator<LineListOption>
{

    public int compare( LineListOption lineListOption0, LineListOption lineListOption1 )
    {
        // TODO Auto-generated method stub
        return lineListOption0.getShortName().compareTo( lineListOption1.getShortName() );
    }

}
