package org.hisp.dhis.dataanalyser.tr.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementGroupNameComparator;
import org.hisp.dhis.dataelement.comparator.DataElementNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

public class GenerateTargetAnalysisFormAction
implements Action
{
    //--------------------------------------------------------------------------
    //Dependencies
    //--------------------------------------------------------------------------
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitGroupService orgUnitGroupService;
    
    public void setOrgUnitGroupService( OrganisationUnitGroupService orgUnitGroupService )
    {
        this.orgUnitGroupService = orgUnitGroupService;
    }
    
    //--------------------------------------------------------------------------
    //Input/Output
    //--------------------------------------------------------------------------
    
    private List<DataElement> dataElements;
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

   private List<DataElementGroup> dataElementGroups;

   public List<DataElementGroup> getDataElementGroups()
   {
      return dataElementGroups;
   }
   
   private List<OrganisationUnitGroup> orgUnitGroups;

   public List<OrganisationUnitGroup> getOrgUnitGroups()
   {
       return orgUnitGroups;
   }
   
   private List<Period> monthlyPeriods;
   
   public List<Period> getMonthlyPeriods()
   {
       return monthlyPeriods;
   }
   
   private SimpleDateFormat simpleDateFormat;

   public SimpleDateFormat getSimpleDateFormat()
   {
       return simpleDateFormat;
   }
   
    //--------------------------------------------------------------------------
    //Action Implementation
    //--------------------------------------------------------------------------

    public String execute()
    {
        dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        // take only those dataElement which are VALUE_TYPE_INT and DOMAIN_TYPE_AGGREGATE
        Iterator<DataElement> alldeIterator = dataElements.iterator();
        while ( alldeIterator.hasNext() )
        {
            DataElement dataElement = alldeIterator.next();
            if ( !dataElement.getDomainType().equalsIgnoreCase( DataElement.DOMAIN_TYPE_AGGREGATE ) )
            {
                alldeIterator.remove();
            }
        }
        
        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        Collections.sort( dataElements, new DataElementNameComparator() );
        Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );
        
        orgUnitGroups = new ArrayList<OrganisationUnitGroup> ( orgUnitGroupService.getAllOrganisationUnitGroups() );
        
        monthlyPeriods = new ArrayList<Period> ( periodService.getPeriodsByPeriodType( new MonthlyPeriodType() ));
        Iterator<Period> periodIterator = monthlyPeriods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        Collections.sort( monthlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        return SUCCESS;
    }

}
