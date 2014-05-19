package org.hisp.dhis.light.interpretation.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;

import com.opensymphony.xwork2.Action;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class PostInterpretationComment
    implements Action
{
    /**
     * 
     */
    private static final Log log = LogFactory.getLog( GetInterpretations.class );

    /**
     * 
     */
    public PostInterpretationComment()
    {
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    /**
     * 
     */
    private InterpretationService interpretationService;

    /**
     * @return the interpretationService
     */
    public InterpretationService getInterpretationService()
    {
        return interpretationService;
    }

    /**
     * @param interpretationService the interpretationService to set
     */
    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    /**
     * 
     */
    private int interpretationId;

    /**
     * @return the interpretationId
     */
    public int getInterpretationId()
    {
        return interpretationId;
    }

    /**
     * @param interpretationId the interpretationId to set
     */
    public void setInterpretationId( int interpretationId )
    {
        this.interpretationId = interpretationId;
    }

    /**
     * 
     */
    private Interpretation interpretation;

    /**
     * @return the interpretation
     */
    public Interpretation getInterpretation()
    {
        return interpretation;
    }

    /**
     * @param interpretation the interpretation to set
     */
    public void setInterpretation( Interpretation interpretation )
    {
        this.interpretation = interpretation;
    }

    /**
     * 
     */
    private String comment;

    /**
     * 
     * @return
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * 
     * @param comment
     */
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
        setInterpretation( interpretationService.getInterpretation( getInterpretationId() ) );
        interpretationService.addInterpretationComment( getInterpretation().getUid(), getComment() );
        return SUCCESS;
    }
}
