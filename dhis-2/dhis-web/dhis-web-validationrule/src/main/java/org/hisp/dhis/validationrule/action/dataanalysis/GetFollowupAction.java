package org.hisp.dhis.validationrule.action.dataanalysis;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataanalysis.FollowupAnalysisService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.util.SessionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Halvdan Hoem Grelland
 */
public class GetFollowupAction
    implements Action
{
    private static final String KEY_ANALYSIS_DATA_VALUES = "analysisDataValues";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FollowupAnalysisService followupAnalysisService;

    public void setFollowupAnalysisService( FollowupAnalysisService followupAnalysisService )
    {
        this.followupAnalysisService = followupAnalysisService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<DeflatedDataValue> dataValues = new ArrayList<>();

    public Collection<DeflatedDataValue> getDataValues()
    {
        return dataValues;
    }

    private boolean maxExceeded;

    public boolean getMaxExceeded()
    {
        return maxExceeded;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        OrganisationUnit orgUnit = selectionTreeManager.getReloadedSelectedOrganisationUnit();

        if( orgUnit != null )
        {
            dataValues = followupAnalysisService.getFollowupDataValues( orgUnit, DataAnalysisService.MAX_OUTLIERS + 1 ); // +1 to detect overflow

            maxExceeded = dataValues.size() > DataAnalysisService.MAX_OUTLIERS;
        }
        else
        {
            dataValues = new ArrayList<>();

            maxExceeded = false;
        }

        SessionUtils.setSessionVar( KEY_ANALYSIS_DATA_VALUES, dataValues );

        return SUCCESS;
    }
}
