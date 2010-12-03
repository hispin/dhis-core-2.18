package org.hisp.dhis.mobile.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CheckDataStatusJob  extends QuartzJobBean
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private SmsService smsService;
    
    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------

    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException 
    {
        System.out.println("CheckDataStatus Job Started at : "+new Date() );
        
        List<OrganisationUnit> rootOrgUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getRootOrganisationUnits() );        
        PeriodType dailyPeriodType = new DailyPeriodType();        
        
        Date curDate = new Date();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Period period = dailyPeriodType.createPeriod( curDate );
        
        if( period == null )
        {
            period = reloadPeriodForceAdd( period );
        }
        
        int count = 1;
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getDataSetsByPeriodType( dailyPeriodType ) ); 
        for( DataSet dataSet : dataSetList )
        {
            List<String> phoneNumbers = new ArrayList<String>();
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            
            for( OrganisationUnit rootOrgUnit : rootOrgUnits )
            {
                orgUnitList.addAll( reportService.getDataNotSentOrgUnits( dataSet, period, rootOrgUnit ) );
            }
            
            String groupName = "datastatusgroup"+count;
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                List<User> users = new ArrayList<User>( userStore.getUsersByOrganisationUnit( orgUnit ) );
                for( User user : users )
                {
                    if( user.getPhoneNumber() != null && !user.getPhoneNumber().trim().equalsIgnoreCase( "" ) )
                        phoneNumbers.add( user.getPhoneNumber() );
                }
            }
            
            String message = "YOU HAVE NOT SUBMIT UR REPORT FOR "+dataSet.getName()+" FOR "+simpleDateFormat.format( curDate )+"; PLEASE SUBMIT.";
            
            smsService.sendMessageToGroup( groupName, phoneNumbers, message );
            
            count++;
        }
        
        System.out.println("CheckDataStatus Job Ended at : "+new Date() );
    }

    // -------------------------------------------------------------------------
    // Support methods for reloading periods
    // -------------------------------------------------------------------------
    private final Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }
    
}
