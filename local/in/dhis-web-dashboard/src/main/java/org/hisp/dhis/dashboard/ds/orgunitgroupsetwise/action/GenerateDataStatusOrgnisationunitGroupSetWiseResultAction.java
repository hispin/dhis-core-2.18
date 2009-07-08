/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.dashboard.ds.orgunitgroupsetwise.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dashboard.util.DBConnection;
import org.hisp.dhis.dashboard.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetStore;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

import com.opensymphony.xwork.Action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class GenerateDataStatusOrgnisationunitGroupSetWiseResultAction implements Action
{
    
	
        // ---------------------------------------------------------------
        // Dependencies
        // ---------------------------------------------------------------
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
        
        private Map<OrganisationUnitGroup, Map<Integer, Integer>> ougMapDataStatusResult;
        
        public Map<OrganisationUnitGroup, Map<Integer, Integer>> getOugMapDataStatusResult() 
        {
			return ougMapDataStatusResult;
		}

		private Map<OrganisationUnit,List<Integer>> ouMapDataStatusResult;       
        
        private Collection<Period> periodList;
        
        private List<OrganisationUnit> orgUnitList;

        public List<OrganisationUnit> getOrgUnitList()
        {
            return orgUnitList;
        }

        private List<OrganisationUnitGroup> orgUnitGroupList;

        public List<OrganisationUnitGroup> getOrgUnitGroupList()
        {
            return orgUnitGroupList;
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
   
        
        private String selectedButton;
        
        public void setselectedButton( String selectedButton)
        {
            this.selectedButton = selectedButton;
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

        private List<String> orgUnitListCB;

        public void setOrgUnitListCB( List<String> orgUnitListCB )
        {
            this.orgUnitListCB = orgUnitListCB;
        }

        private String selectedDataSets;

        public void setSelectedDataSets( String selectedDataSets )
        {
            this.selectedDataSets = selectedDataSets;
        }

        public String getSelectedDataSets()
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
        
        private DataSet selDataSet;
        
        public DataSet getSelDataSet()
        {
            return selDataSet;
        }
        
        private OrganisationUnitGroup selectedOrgUnitGroup;
        
        public OrganisationUnitGroup getSelectedOrgUnitGroup()
        {
            return selectedOrgUnitGroup;
        }
        

        private Connection con = null;
        String orgUnitInfo;
        String periodInfo;
        String deInfo;
        int orgUnitCount;
        
        

        // ---------------------------------------------------------------
        // Action Implementation
        // ---------------------------------------------------------------
        
        @SuppressWarnings("unchecked")
		public String execute() throws Exception
        {
        
        con = dbConnection.openConnection();
        con.setAutoCommit(false);
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        String query = "";

        orgUnitCount = 0;
              
        // Intialization
        ouMapDataStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        ougMapDataStatusResult = new HashMap<OrganisationUnitGroup, Map<Integer, Integer>>();
        
        results = new ArrayList<Integer>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        //DataSet Related Info
        dataSetList = new ArrayList<DataSet>();

        deInfo = "-1";
        DataSet dSet = dataSetStore.getDataSet( Integer.parseInt( selectedDataSets ) );
        selDataSet = dSet;
        for(DataElement de : dSet.getDataElements())
              deInfo += "," + de.getId();
        
        // Period Related Info
        Period startPeriod = periodStore.getPeriod( sDateLB );
        Period endPeriod = periodStore.getPeriod( eDateLB );
        
        selectedPeriodList = new ArrayList<Period>(periodStore.getIntersectingPeriods( startPeriod.getStartDate(), endPeriod.getEndDate() ));
        
        periodInfo = "-1";
        for(Period p : selectedPeriodList)            
            periodInfo +=  "," + p.getId();
       
                                                 
        //DataSet ds;
        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        PeriodType dataSetPeriodType;
        periodList = new ArrayList<Period>();
              
        dataElements = selDataSet.getDataElements();
        
        int dataSetMemberCount1 = 0;
        for( DataElement de1 : dataElements )
        {
            dataSetMemberCount1 += de1.getCategoryCombo().getOptionCombos().size();
        }
        
        deInfo = getDEInfo(dataElements);
                    
        dataSetPeriodType = selDataSet.getPeriodType();
        
        periodList = periodStore.getIntersectingPeriodsByPeriodType( dataSetPeriodType,
            startPeriod.getStartDate(), endPeriod.getEndDate() );

        // Dataset source
        Set<Source> dSetSource = selDataSet.getSources();
        
       // OrgUnit Group Info 
        
        orgUnitGroupList = new ArrayList<OrganisationUnitGroup>(orgUnitListCB.size());
        
        Iterator<String> orgUnitListCBIterator = orgUnitListCB.iterator();
        while ( orgUnitListCBIterator.hasNext() )
         {               
            OrganisationUnitGroup og = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt((String) orgUnitListCBIterator.next()));
            orgUnitGroupList.add( og ); 
            
         }
              
        System.out.println("orgUnitGroupList:" + orgUnitGroupList);
        
        //--------------------------------------------------------------------------------
        selectedOrgUnitGroup = new OrganisationUnitGroup();
        
        int gcount = 0;
        Iterator<OrganisationUnitGroup> orgGroupItr = orgUnitGroupList.iterator();
        while( orgGroupItr.hasNext())
        {            
            selectedOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup(( orgGroupItr.next()).getId());
      
            // OrgUnit Related Info
            
            orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
                                      
            Iterator<OrganisationUnit> orgUnitsIterator = orgUnits.iterator();
            while ( orgUnitsIterator.hasNext() )
             {
                 OrganisationUnit o = (OrganisationUnit) orgUnitsIterator.next();
                 orgUnitList.add( o );             
             }
            
            
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
            }
            System.out.println("After :"+orgUnitList.size());
            

            
            Iterator orgUnitListIterator = orgUnitList.iterator();
            OrganisationUnit o;
            Set<Source> dso = new HashSet<Source>();
            Iterator periodIterator = null;
            dso = selDataSet.getSources();
            Map<Integer,Integer> ougResult =  new HashMap<Integer,Integer>();
            for(int i=0; i<periodList.size(); i++ )
            {
            	ougResult.put( i, 0 );
            }
            
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
                int pcount =0;
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
                        
                        query = "SELECT COUNT(*) FROM datavalue WHERE dataelementid IN ("+deInfo+") AND sourceid IN ("+orgUnitInfo+") AND periodid IN ("+periodInfo+")";
                        ps1 = con.prepareStatement(query);                       
                        rs1 = ps1.executeQuery();
                        
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
                        int orgr = ougResult.get(pcount) + (int) dataStatusPercentatge;
                        ougResult.put(pcount, orgr);
                        pcount++;
                        continue;
                    }
                    
                    orgUnitInfo = ""+ o.getId();                 
                    query = "SELECT COUNT(*) FROM datavalue WHERE dataelementid IN ("+deInfo+") AND sourceid IN ("+orgUnitInfo+") AND periodid IN ("+periodInfo+")";
                    ps1 = con.prepareStatement(query);                   
                    rs1 = ps1.executeQuery();
                   
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

                    dsResults.add( ( int ) dataStatusPercentatge );
                    
                    int orgr = ougResult.get(pcount) + (int) dataStatusPercentatge;
                    ougResult.put(pcount, orgr);
                    
                    pcount++;
                }// Period loop end
                ouMapDataStatusResult.put( o, dsResults );
            }// Orgunit loop end
         
            for(int i=0; i<periodList.size(); i++ )
            {
            	double tem1 = (double)	ougResult.get(i)/orgUnitList.size();
            	ougResult.put(i, (int) tem1);
            }
            
            System.out.println("Result Size of "+selectedOrgUnitGroup.getName()+" is : "+ougResult.size());
            ougMapDataStatusResult.put(selectedOrgUnitGroup, ougResult);
            
            gcount++;
            
        } // OrgUnitGroup loop end          
	        
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
                con.setAutoCommit(true);
                
                if(rs1 != null) rs1.close();
                if(ps1 != null) ps1.close();

                if(con != null) con.close();
                //dashBoardService.deleteDataTable( dataTableName );
            }
            catch( Exception e )
            {
                System.out.println("Exception while closing DB Connections : "+e.getMessage());
            }
        }// finally block end
 
        return SUCCESS;
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

        public Map<OrganisationUnit, List<Integer>> getOuMapDataStatusResult() 
     	{
            return ouMapDataStatusResult;
        }

        public Collection<Period> getPeriodList()
        {
            return periodList;
        }
}
