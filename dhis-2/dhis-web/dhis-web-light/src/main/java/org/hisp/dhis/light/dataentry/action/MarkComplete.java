package org.hisp.dhis.light.dataentry.action;

import com.opensymphony.xwork2.Action;
import org.apache.commons.lang.Validate;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;

import java.util.Date;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class MarkComplete
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public Integer getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    public String getPeriodId()
    {
        return periodId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public Integer getDataSetId()
    {
        return dataSetId;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        Validate.notNull( organisationUnitId );
        Validate.notNull( periodId );
        Validate.notNull( dataSetId );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        Period period = periodService.getPeriodByExternalId( periodId );

        DataSet dataSet = dataSetService.getDataSet( dataSetId );

        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet, period,
            organisationUnit );

        if ( registration == null )
        {
            registration = new CompleteDataSetRegistration();
            registration.setDataSet( dataSet );
            registration.setPeriod( period );
            registration.setSource( organisationUnit );
            registration.setDate( new Date() );
            registration.setStoredBy( currentUserService.getCurrentUsername() );

            registrationService.saveCompleteDataSetRegistration( registration );
        }

        return SUCCESS;
    }
}
