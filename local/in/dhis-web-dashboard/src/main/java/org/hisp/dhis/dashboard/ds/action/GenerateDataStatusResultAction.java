package org.hisp.dhis.dashboard.ds.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hisp.dhis.dashboard.util.DBConnection;
import org.hisp.dhis.dashboard.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetStore;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

import com.opensymphony.xwork.Action;

public class GenerateDataStatusResultAction
    implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    private DataSetStore dataSetStore;

    public void setDataSetStore( DataSetStore dataSetStore )
    {
        this.dataSetStore = dataSetStore;
    }

    public DataSetStore getDataSetStore()
    {
        return dataSetStore;
    }

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    private DBConnection dbConnection;
    
    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------
    
    private Map<OrganisationUnit,List<Integer>> ouMapDataStatusResult;
    
    
    private Collection<Period> periodList;
    
    

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Integer> results;

    public List<Integer> getResults()
    {
        return results;
    }

    private Map<DataSet, Map<OrganisationUnit, List<Integer>>> dataStatusResult;

    public Map<DataSet, Map<OrganisationUnit, List<Integer>>> getDataStatusResult()
    {
        return dataStatusResult;
    }

    private Map<DataSet, Collection<Period>> dataSetPeriods;

    public Map<DataSet, Collection<Period>> getDataSetPeriods()
    {
        return dataSetPeriods;
    }

    List<Period> selectedPeriodList;

    public List<Period> getSelectedPeriodList()
    {
        return selectedPeriodList;
    }

    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }

    private int maxOULevel;
    
    public int getMaxOULevel()
    {
        return maxOULevel;
    }

    
    // ---------------------------------------------------------------
    // Input Parameters
    // ---------------------------------------------------------------

    private String dsId;

    public void setDsId( String dsId )
    {
        this.dsId = dsId;
    }
    
    private String selectedButton;
    
    public void setselectedButton( String selectedButton)
    {
    	this.selectedButton = selectedButton;
    }

    private String ouId;
    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String immChildOption;

    public void setImmChildOption( String immChildOption )
    {
        this.immChildOption = immChildOption;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    public int getSDateLB()
    {
        return sDateLB;
    }
    
    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    public int getEDateLB()
    {
        return eDateLB;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private List<String> selectedDataSets;

    public void setSelectedDataSets( List<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    public List<String> getSelectedDataSets()
    {
        return selectedDataSets;
    }

    private int minOULevel;
            
    public int getMinOULevel()
    {
        return minOULevel;
    }
    
    private int number;
    
    public int getNumber()
    {
    	return number;
    }

    private Connection con = null;
    String orgUnitInfo;
    String periodInfo;
    String deInfo;
    int orgUnitCount;
    private String dataTableName;
    
    private DataSet selDataSet;
 
    public DataSet getSelDataSet() {
		return selDataSet;
	}

    List<String> periodNameList;
    
	public List<String> getPeriodNameList() {
		return periodNameList;
	}

	// ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute()
        throws Exception
    {
        //con = (new DBConnection()).openConnection();
        //con = dbConnection.openConnection();
        con = sessionManager.getCurrentSession().connection();
        con.setAutoCommit(false);
        
        //CallableStatement callStat = null;
        //callStat = con.prepareCall("{call test1(?,?,?,?)}");
        
        orgUnitCount = 0;
        dataTableName = "";
        
        // Intialization
        periodNameList = new ArrayList<String>();
        ouMapDataStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        results = new ArrayList<Integer>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        
        if(immChildOption!= null && immChildOption.equalsIgnoreCase( "yes" ))
        {
            orgUnitListCB = new ArrayList<String>();
            orgUnitListCB.add( ouId );
            
            facilityLB = "immChildren";
            
            selectedDataSets = new ArrayList<String>();
            selectedDataSets.add( dsId );
        }
        
        // DataSet Related Info
        dataSetList = new ArrayList<DataSet>();

        deInfo = "-1";
        for(String ds : selectedDataSets)
        {
            DataSet dSet = dataSetStore.getDataSet( Integer.parseInt( ds ) );
            selDataSet = dSet;
            for(DataElement de : dSet.getDataElements())
                deInfo += "," + de.getId();
        }
        
        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );            
            orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
        	int number;
        	
        	selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        	
        	number = selectedOrgUnit.getChildren().size();
        	System.out.println("The total number of children are :"+number);


                orgUnitList = new ArrayList<OrganisationUnit>();
                
                Iterator<String> orgUnitIterator = orgUnitListCB.iterator();                
                while ( orgUnitIterator.hasNext() )
                {
                    OrganisationUnit o = organisationUnitService.getOrganisationUnit( Integer.parseInt( (String) orgUnitIterator.next() ) );
                    orgUnitList.add( o );
                    List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( o.getChildren() );
                    Collections.sort( organisationUnits, new OrganisationUnitShortNameComparator() );
                    orgUnitList.addAll( organisationUnits );
                }                
 
        }
        else
        {
            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            OrganisationUnit o;
            while ( orgUnitIterator.hasNext() )
            {
                o = organisationUnitService.getOrganisationUnit( Integer
                    .parseInt( orgUnitIterator.next() ) );
                orgUnitList.add( o );
            }
        }

   
        Set<Source> dSetSource = selDataSet.getSources();
        /*
        List<OrganisationUnit> dataSetOrgUnit = new ArrayList<OrganisationUnit>();
        for( Source s1 : dSetSource )
        {
        	int sid = s1.getId();
        	OrganisationUnit ou = organisationUnitService.getOrganisationUnit( sid );
        	dataSetOrgUnit.add( ou );
        }
       */
        System.out.println("Before :"+orgUnitList.size());
        
        orgUnitInfo = "-1";
        Iterator<OrganisationUnit> ouIt = orgUnitList.iterator();
        while( ouIt.hasNext() )
        {
        	OrganisationUnit ou = ouIt.next();
        	
        	orgUnitCount = 0;
        	if(!dSetSource.contains(ou))
        	{
        		getDataSetAssignedOrgUnitCount( ou, dSetSource );
        	
	        	if( orgUnitCount > 0 )
	        	{
	        		orgUnitInfo += "," + ou.getId();
	        		getOrgUnitInfo(ou);
	        	}
	        	else
	        	{
	        		ouIt.remove();
	        	}
        	}
        	else
        	{
        		orgUnitInfo += "," + ou.getId();
        	}
        }
        System.out.println("After :"+orgUnitList.size());
        
        // Period Related Info
        Period startPeriod = periodStore.getPeriod( sDateLB );
        Period endPeriod = periodStore.getPeriod( eDateLB );
                        
        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        //selectedPeriodList = new ArrayList<Period>(periodStore.getIntersectingPeriods( startPeriod.getStartDate(), endPeriod.getEndDate() ));
        periodList = new ArrayList<Period>( periodStore.getIntersectingPeriodsByPeriodType( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ));
        
        periodInfo = "-1";
        for(Period p : periodList)            
            periodInfo +=  "," + p.getId();

       dataTableName = createDataTable(orgUnitInfo, deInfo, periodInfo);
        
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        String query = "";
        query = "SELECT COUNT(*) FROM "+ dataTableName +" WHERE dataelementid IN (?) AND sourceid IN (?) AND periodid IN (?)";
        ps1 = con.prepareStatement(query);
 
        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        dataElements = selDataSet.getDataElements();
            
       int dataSetMemberCount1 = 0;
            for( DataElement de1 : dataElements )
            {
                dataSetMemberCount1 += de1.getCategoryCombo().getOptionCombos().size();
            }
            
            deInfo = getDEInfo(dataElements);
                        
            
            Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
            OrganisationUnit o;
            Set<Source> dso = new HashSet<Source>();
            Iterator periodIterator;
            dso = selDataSet.getSources();
            
            while ( orgUnitListIterator.hasNext() )
            {
                
                o = (OrganisationUnit) orgUnitListIterator.next();
                orgUnitInfo = ""+o.getId();
                
                if(maxOULevel < organisationUnitService.getLevelOfOrganisationUnit( o ))
                    maxOULevel = organisationUnitService.getLevelOfOrganisationUnit( o );
                
                if(minOULevel > organisationUnitService.getLevelOfOrganisationUnit( o ))
                    minOULevel = organisationUnitService.getLevelOfOrganisationUnit( o );
                    
                
                periodIterator = periodList.iterator();
                
                Period p;
                
                double dataStatusPercentatge;
                List<Integer> dsResults = new ArrayList<Integer>();
                while ( periodIterator.hasNext() )
                {                    
                    p = (Period) periodIterator.next();
                    periodInfo = ""+p.getId();
                                                                               
                    if ( dso == null )
                    {
                    	dsResults.add( -1 );
                        continue;
                    }
                    else if(!dso.contains( o ))
                    {                        
                        orgUnitInfo = "-1";
                        orgUnitCount = 0;
                        getOrgUnitInfo(o, dso);
                        
                        query = "SELECT COUNT(*) FROM "+ dataTableName +" WHERE dataelementid IN ("+deInfo+") AND sourceid IN ("+orgUnitInfo+") AND periodid IN ("+periodInfo+")";
                        rs1 = ps1.executeQuery(query);
                   
                        if(rs1.next())
                        {
                            try
                            {
                                dataStatusPercentatge = ((double) rs1.getInt( 1 ) / (double) (dataSetMemberCount1*orgUnitCount))*100.0;    
                            }
                            catch(Exception e)
                            {
                                dataStatusPercentatge = 0.0;
                            }
                        }                            
                        else
                            dataStatusPercentatge = 0.0;
                        
                        if(dataStatusPercentatge > 100.0) dataStatusPercentatge = 100;
                        
                        dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                        
                        dsResults.add( ( int ) dataStatusPercentatge );
                        continue;
                    }
                    
                    orgUnitInfo = ""+ o.getId();
                    
                    query = "SELECT COUNT(*) FROM "+ dataTableName +" WHERE dataelementid IN ("+deInfo+") AND sourceid IN ("+orgUnitInfo+") AND periodid IN ("+periodInfo+")";
                    rs1 = ps1.executeQuery(query);

                    System.out.println("Sourceid is"+ orgUnitInfo);
                    if(rs1.next())
                    {
                        try
                        {
                            dataStatusPercentatge = ((double) rs1.getInt( 1 ) / (double) dataSetMemberCount1)*100.0;    
                        }
                        catch(Exception e)
                        {
                            dataStatusPercentatge = 0.0;
                        }
                    }                            
                    else
                        dataStatusPercentatge = 0.0;
                   
                    if(dataStatusPercentatge > 100.0) dataStatusPercentatge = 100;
                    
                    dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                    
                    System.out.println("Sourceid is"+ orgUnitInfo+ "DataStatus is"+dataStatusPercentatge+ "period is"+ periodInfo);

                    dsResults.add( ( int ) dataStatusPercentatge );                    
                }
                
                ouMapDataStatusResult.put( o, dsResults );
             }
 
        // For Level Names
        String ouLevelNames[] = new String[organisationUnitService.getNumberOfOrganisationalLevels()+1];
        for(int i = 0; i < ouLevelNames.length; i++)
        {
            ouLevelNames[i] = "Level"+i;            
        }
        
        List<OrganisationUnitLevel> ouLevels = new ArrayList<OrganisationUnitLevel>(organisationUnitService.getOrganisationUnitLevels());        
        for( OrganisationUnitLevel ouL : ouLevels )
        {
        	ouLevelNames[ouL.getLevel()] = ouL.getName();
        }
                       
        levelNames = new ArrayList<String>();
        int count1 = minOULevel;
        while ( count1 <= maxOULevel )
        {
            levelNames.add( ouLevelNames[count1] );
            count1++;
        }
		
        try
        {
            
        }
        finally
        {
            try
            {
                //dashBoardService.deleteDataTable( dataTableName );
                deleteDataTable( dataTableName );
                
                con.setAutoCommit(true);
                
                if(rs1 != null) rs1.close();
                if(ps1 != null) ps1.close();

                //if(con != null) con.close();
                //dashBoardService.deleteDataTable( dataTableName );
            }
            catch( Exception e )
            {
                System.out.println("Exception while closing DB Connections : "+e.getMessage());
            }
        }// finally block end
        
        periodNameList = dashBoardService.getPeriodNamesByPeriodType( dataSetPeriodType, periodList );
        
        if( selectedButton.equalsIgnoreCase("SummaryStatus") )
        {
        	return "summarystatus";
        }
        
        return "detailedstatus";
    }

    
	public void getDataSetAssignedOrgUnitCount( OrganisationUnit organisationUnit, Set<Source> dso )
	{
        Collection<OrganisationUnit> children = organisationUnit.getChildren();

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            if(dso.contains( child ))
            {                
                orgUnitCount++;
            }            
            getDataSetAssignedOrgUnitCount( child, dso );
        } 
	
	}

    public String createDataTable(String orgUnitInfo, String deInfo, String periodInfo)
    {        
        Statement st1 = null;
        Statement st2 = null;
        
        String dataTableName = "_datastatus_" + UUID.randomUUID().toString();
        dataTableName = dataTableName.replaceAll( "-", "" );
                
        String query = "DROP TABLE IF EXISTS "+dataTableName;
        
        try
        {
            st1 = con.createStatement();
            st2 = con.createStatement();
            
            st1.executeUpdate( query );
                        
            System.out.println("Table "+dataTableName+" dropped Successfully (if exists) ");
            
            query = "CREATE table "+ dataTableName +" AS " +
                    " SELECT sourceid,dataelementid,periodid,value FROM datavalue " + 
                    " WHERE dataelementid in (" + deInfo + ") AND " + 
                    " sourceid in (" + orgUnitInfo + ") AND " +
                    " periodid in (" + periodInfo + ")";
                                        
            st2.executeUpdate( query );
            
            System.out.println("Table "+dataTableName+" created Successfully");
        } // try block end
        catch ( Exception e )
        {
            System.out.println("SQL Exception : "+e.getMessage());     
            return null;
        }
        finally
        {
            try
            {
                if(st1 != null) st1.close();
                if(st2 != null) st2.close();                
            }
            catch( Exception e )
            {
                System.out.println("SQL Exception : "+e.getMessage());
                return null;
            }
        }// finally block end

        return dataTableName;                
    }
    
    public void deleteDataTable(String dataTableName)
    {
        
        Statement st1 = null;        
                       
        String query = "DROP TABLE IF EXISTS "+dataTableName;
        
        try
        {
            st1 = con.createStatement();            
            st1.executeUpdate( query );
            System.out.println("Table "+dataTableName+" dropped Successfully");
        } // try block end
        catch ( Exception e )
        {
            System.out.println("SQL Exception : "+e.getMessage());                        
        }
        finally
        {
            try
            {                
                if(st1 != null) st1.close();
            }
            catch( Exception e )
            {
                System.out.println("SQL Exception : "+e.getMessage());                
            }
        }// finally block end        
    }


    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        Collection<OrganisationUnit> children = orgUnit.getChildren();

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end


    private void getOrgUnitInfo(OrganisationUnit organisationUnit)
    {                
        Collection<OrganisationUnit> children = organisationUnit.getChildren();

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            orgUnitInfo += "," + child.getId();
            getOrgUnitInfo( child);
        }               
    }

    private void getOrgUnitInfo(OrganisationUnit organisationUnit, Set<Source> dso)
    {                
        Collection<OrganisationUnit> children = organisationUnit.getChildren();

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            if(dso.contains( child ))
            {
                orgUnitInfo += "," + child.getId();
                orgUnitCount++;
            }            
            getOrgUnitInfo( child, dso );
        }               
    }

    
    private String getDEInfo(Collection<DataElement> dataElements)
    {
        StringBuffer deInfo = new StringBuffer( "-1" );
                        
        for(DataElement de : dataElements)
        {
            deInfo.append( "," ).append( de.getId() );
        }
        return deInfo.toString();
    }

	public Map<OrganisationUnit, List<Integer>> getOuMapDataStatusResult() {
		return ouMapDataStatusResult;
	}

	public Collection<Period> getPeriodList() {
		return periodList;
	}

}// class end
