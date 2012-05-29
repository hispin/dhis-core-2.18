package org.hisp.dhis.dashboard.interpretation.action;

import java.util.List;

import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;

import com.opensymphony.xwork2.Action;

public class GetInterpretationsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private InterpretationService interpretationService;

    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Interpretation> interpretations;
    
    public List<Interpretation> getInterpretations()
    {
        return interpretations;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        interpretations = interpretationService.getInterpretations( 0, 10 );
        
        return SUCCESS;
    }
}
