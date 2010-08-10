package org.hisp.dhis.patient.action.caseaggregation;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class CaseAggregationOptionCaptureAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private int programStageId;

    public void setId( String stringId )
    {
        String id_split[] = new String[3];
        id_split = stringId.split( ":" );
        id_split[1] = id_split[1].replace( "]", "" ).trim();
        id_split[1] = id_split[1].replace( ".", ":" );
        id_split = id_split[1].split( ":" );
        programStageId = Integer.parseInt( id_split[1] );
    }

    private Set<DataElementCategoryOptionCombo> optionCombos;

    public Set<DataElementCategoryOptionCombo> getOptionCombos()
    {
        return optionCombos;
    }

    private DataElement de;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        optionCombos = new HashSet<DataElementCategoryOptionCombo>();
        de = dataElementService.getDataElement( programStageId );
        optionCombos = de.getCategoryCombo().getOptionCombos();

        return SUCCESS;
    }

}
