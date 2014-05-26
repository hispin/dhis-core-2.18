package org.hisp.dhis.light.interpretation.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;

import com.opensymphony.xwork2.Action;

public class PostInterpretationComment
    implements Action
{
    private static final Log log = LogFactory.getLog( PostInterpretationComment.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private InterpretationService interpretationService;

    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private int interpretationId;

    public void setInterpretationId( int interpretationId )
    {
        this.interpretationId = interpretationId;
    }

    private Interpretation interpretation;

    public void setInterpretation( Interpretation interpretation )
    {
        this.interpretation = interpretation;
    }

    public Interpretation getInterpretation()
    {
        return interpretation;
    }

    private String comment;

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        setInterpretation( interpretationService.getInterpretation( interpretationId ) );

        interpretationService.addInterpretationComment( interpretation.getUid(), comment );

        return SUCCESS;
    }
}
