package org.hisp.dhis.design.comparator;

import java.util.Comparator;

import org.hisp.dhis.design.Design;

public class DesignComparator
    implements Comparator<Design>
{
    public int compare( Design design1, Design design2 )
    {
        return design1.getName().compareTo( design2.getName() );
    }
}
