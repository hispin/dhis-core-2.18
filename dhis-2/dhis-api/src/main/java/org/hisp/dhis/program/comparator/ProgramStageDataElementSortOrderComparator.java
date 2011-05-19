package org.hisp.dhis.program.comparator;

import java.util.Comparator;

import org.hisp.dhis.program.ProgramStageDataElement;

public class ProgramStageDataElementSortOrderComparator
    implements Comparator<ProgramStageDataElement>
{

    @Override
    public int compare( ProgramStageDataElement programStageDataElement0,
        ProgramStageDataElement programStageDataElement1 )
    {
        if ( programStageDataElement0.getDataElement().getSortOrder() == null
            || programStageDataElement0.getDataElement().getSortOrder() == 0 )
        {
            return programStageDataElement0.getDataElement().getName()
                .compareTo( programStageDataElement1.getDataElement().getName() );
        }
        if ( programStageDataElement1.getDataElement().getSortOrder() == null
            || programStageDataElement1.getDataElement().getSortOrder() == 0 )
        {
            return programStageDataElement0.getDataElement().getName()
                .compareTo( programStageDataElement1.getDataElement().getName() );
        }
        return programStageDataElement0.getDataElement().getSortOrder()
            - programStageDataElement1.getDataElement().getSortOrder();
    }

}
