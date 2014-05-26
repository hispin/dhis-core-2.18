package org.hisp.dhis.light.interpretation.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class PostInterpretation
    implements Action
{
    private static final Log log = LogFactory.getLog( PostInterpretation.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private InterpretationService interpretationService;

    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String interpretation;

    public void setInterpretation( String interpretation )
    {
        this.interpretation = interpretation;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Chart c = chartService.getChart( id );

        Interpretation i = new Interpretation( c, null, interpretation );

        i.setUser( currentUserService.getCurrentUser() );

        interpretationService.saveInterpretation( i );

        return SUCCESS;
    }
}
