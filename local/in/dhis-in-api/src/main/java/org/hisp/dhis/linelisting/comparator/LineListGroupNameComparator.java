package org.hisp.dhis.linelisting.comparator;

import java.util.Comparator;

import org.hisp.dhis.linelisting.LineListGroup;

public class LineListGroupNameComparator
    implements Comparator<LineListGroup>
{
    public int compare (LineListGroup lineListGroup0, LineListGroup lineListGroup1)
    {
        return lineListGroup0.getName().compareTo( lineListGroup1.getName() );
    }
}
