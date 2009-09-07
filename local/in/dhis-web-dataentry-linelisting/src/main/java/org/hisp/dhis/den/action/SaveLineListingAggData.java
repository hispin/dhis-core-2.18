package org.hisp.dhis.den.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.den.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class SaveLineListingAggData implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private LLDataValueService lldataValueService;
    
	public void setLldataValueService(LLDataValueService lldataValueService) 
	{
		this.lldataValueService = lldataValueService;
	}


    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<Integer, String> llValueMap;
    public Map<Integer, String> getLlValueMap()
    {
        return llValueMap;
    }
 
    private Map<Integer,Integer> liDEMap;
    
    public Map<Integer, Integer> getLiDEMap()
    {
        return liDEMap;
    }

    private Map<String,String> lineListAggDes;
    
    private String lineListSex;
    private String lineListWeigh;
    private String lineListbreastFeeding;
    
    private String lineListDeathSex;
    private String lineListDeathAge;
    private String lineListDeathCause;
    
    private DataValue[] lineListSexValues;
    private DataValue[] lineListWeighValues;
    private DataValue[] lineListBFValues;
    
    private DataValue[] lineListDeathSexValues;
    private DataValue[] lineListDeathAgeValues;
    private DataValue[] lineListDeathCauseValues;
    
    private OrganisationUnit organisationUnit;
    private Period period;
    private DataSet dataSet;
    private String storedBy;
    
    private Map<String, String> lldeValueMap;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // Initialization
        lineListAggDes = new HashMap<String,String>(); 
        llValueMap = new HashMap<Integer,String>();
        liDEMap = new HashMap<Integer,Integer>();
        lldeValueMap = new HashMap<String, String>();
        
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        period = selectedStateManager.getSelectedPeriod();
        
        dataSet = selectedStateManager.getSelectedDataSet();

        storedBy = currentUserService.getCurrentUsername();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        if(dataSet.getName().equalsIgnoreCase( LLDataSets.LL_BIRTHS ))
        {
        	/*
            getLineListingDataElementsForLiveBirths();
            getLineListingDataForLiveBirths();
            
            updateLivebirhts(organisationUnit, period, lineListSexValues);
            updateWeighs(organisationUnit, period, lineListSexValues, lineListWeighValues);
            updateBreastFeeding(organisationUnit, period, lineListSexValues, lineListBFValues);
            */
        	
        	lldeValueMap = lldataValueService.processLineListBirths(organisationUnit, period);
        	List<String> aggDeList = new ArrayList<String>(lldeValueMap.keySet());
        	for(String aggde : aggDeList)
        	{
        		String aggDeVal = lldeValueMap.get(aggde);
        		saveData( organisationUnit, period, aggde, aggDeVal);
        	}
        	
        	System.out.println("LineListing Birth AggDataValues Saved");
        	
        }    
        else if(dataSet.getName().equalsIgnoreCase( LLDataSets.LL_DEATHS ))
        {    
            /*
        	getLineListingDataElementsForDeaths();
            getLineListingDataForDeaths();
            
            updateDeaths(organisationUnit, period, lineListDeathSexValues, lineListDeathAgeValues, lineListDeathCauseValues);
            */

        	lldeValueMap = lldataValueService.processLineListDeaths(organisationUnit, period);
        	List<String> aggDeList = new ArrayList<String>(lldeValueMap.keySet());
        	for(String aggde : aggDeList)
        	{
        		String aggDeVal = lldeValueMap.get(aggde);
        		saveData( organisationUnit, period, aggde, aggDeVal);
        	}
        	
        	System.out.println("LineListing Death AggDataValues Saved");
        }   
        else if(dataSet.getName().equalsIgnoreCase( LLDataSets.LL_MATERNAL_DEATHS ))
        {    

        	lldeValueMap = lldataValueService.processLineListMaternalDeaths(organisationUnit, period);
        	List<String> aggDeList = new ArrayList<String>(lldeValueMap.keySet());
        	for(String aggde : aggDeList)
        	{
        		String aggDeVal = lldeValueMap.get(aggde);
        		saveData( organisationUnit, period, aggde, aggDeVal);
        	}
        	
        	System.out.println("LineListing Maternal Death AggDataValues Saved");
        }   
                                                   
        return SUCCESS;
    }
    
    
    private void getLineListingDataForLiveBirths()
    {
        String[] lineListSexDes = lineListSex.split( "," );
        String[] lineListWeighDes = lineListWeigh.split( "," );
        String[] lineListBFDes = lineListbreastFeeding.split( "," );
        
        lineListSexValues = new DataValue[lineListSexDes.length];
        lineListWeighValues = new DataValue[lineListWeighDes.length];
        lineListBFValues = new DataValue[lineListBFDes.length];
        
        int count1 = 0;
        while(count1 < lineListWeighDes.length)
        {
            // Breast Feeding Data
            String deString3 = lineListBFDes[count1];            
            String partsOfdeString3[] = deString3.split( ":" );
                        
            int dataElementId3 = Integer.parseInt( partsOfdeString3[0] );
            int optionComboId3 = Integer.parseInt( partsOfdeString3[1] );                
                
            DataElement dataElement3 = dataElementService.getDataElement( dataElementId3 );                
            DataElementCategoryOptionCombo optionCombo3 = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId3 );

            if(dataElement3 == null || optionCombo3 == null)
            {
                continue;
            }
                
            DataValue dataValue3 = dataValueService.getDataValue( organisationUnit, dataElement3, period, optionCombo3 );
                
            lineListBFValues[count1] = dataValue3;

            // Weigh Data
            String deString1 = lineListWeighDes[count1];            
            String partsOfdeString1[] = deString1.split( ":" );
            System.out.println(deString1);
            System.out.println(partsOfdeString1[0]+" : "+partsOfdeString1[1]);
                        
            int dataElementId1 = Integer.parseInt( partsOfdeString1[0] );
            int optionComboId1 = Integer.parseInt( partsOfdeString1[1] );                
                
            DataElement dataElement1 = dataElementService.getDataElement( dataElementId1 );                
            DataElementCategoryOptionCombo optionCombo1 = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId1 );

            if(dataElement1 == null || optionCombo1 == null)
            {
                continue;
            }
                
            DataValue dataValue1 = dataValueService.getDataValue( organisationUnit, dataElement1, period, optionCombo1 );
                
            lineListWeighValues[count1] = dataValue1;

            // Sex Data
            String deString2 = lineListSexDes[count1];            
            String partsOfdeString2[] = deString2.split( ":" );
            
            int dataElementId2 = Integer.parseInt( partsOfdeString2[0] );
            int optionComboId2 = Integer.parseInt( partsOfdeString2[1] );                
                
            DataElement dataElement2 = dataElementService.getDataElement( dataElementId2 );                
            DataElementCategoryOptionCombo optionCombo2 = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId2 );

            if(dataElement2 == null || optionCombo2 == null)
            {
                continue;
            }
                
            DataValue dataValue2 = dataValueService.getDataValue( organisationUnit, dataElement2, period, optionCombo2 );
                
            lineListSexValues[count1] = dataValue2;
            
            count1++;
        }        
    }

    private void  getLineListingDataForDeaths()
    {
        String[] lineListDeathSexDes = lineListDeathSex.split( "," );
        String[] lineListDeathAgeDes = lineListDeathAge.split( "," );
        String[] lineListDeathCauseDes = lineListDeathCause.split( "," );
        
        lineListDeathSexValues = new DataValue[lineListDeathSexDes.length];
        lineListDeathAgeValues = new DataValue[lineListDeathAgeDes.length];
        lineListDeathCauseValues = new DataValue[lineListDeathCauseDes.length];
        
        int count1 = 0;
        while(count1 < lineListDeathSexDes.length)
        {
            // Cause of Death Data
            String deString3 = lineListDeathCauseDes[count1];            
            String partsOfdeString3[] = deString3.split( ":" );
                        
            int dataElementId3 = Integer.parseInt( partsOfdeString3[0] );
            int optionComboId3 = Integer.parseInt( partsOfdeString3[1] );                
                
            DataElement dataElement3 = dataElementService.getDataElement( dataElementId3 );                
            DataElementCategoryOptionCombo optionCombo3 = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId3 );

            if(dataElement3 == null || optionCombo3 == null)
            {
                continue;
            }
                
            DataValue dataValue3 = dataValueService.getDataValue( organisationUnit, dataElement3, period, optionCombo3 );
                
            lineListDeathCauseValues[count1] = dataValue3;

            // Age Data
            String deString1 = lineListDeathAgeDes[count1];            
            String partsOfdeString1[] = deString1.split( ":" );
            System.out.println(deString1);
            System.out.println(partsOfdeString1[0]+" : "+partsOfdeString1[1]);
                        
            int dataElementId1 = Integer.parseInt( partsOfdeString1[0] );
            int optionComboId1 = Integer.parseInt( partsOfdeString1[1] );                
                
            DataElement dataElement1 = dataElementService.getDataElement( dataElementId1 );                
            DataElementCategoryOptionCombo optionCombo1 = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId1 );

            if(dataElement1 == null || optionCombo1 == null)
            {
                continue;
            }
                
            DataValue dataValue1 = dataValueService.getDataValue( organisationUnit, dataElement1, period, optionCombo1 );
                
            lineListDeathAgeValues[count1] = dataValue1;

            // Sex Data
            String deString2 = lineListDeathSexDes[count1];            
            String partsOfdeString2[] = deString2.split( ":" );
            
            int dataElementId2 = Integer.parseInt( partsOfdeString2[0] );
            int optionComboId2 = Integer.parseInt( partsOfdeString2[1] );                
                
            DataElement dataElement2 = dataElementService.getDataElement( dataElementId2 );                
            DataElementCategoryOptionCombo optionCombo2 = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId2 );

            if(dataElement2 == null || optionCombo2 == null)
            {
                continue;
            }
                
            DataValue dataValue2 = dataValueService.getDataValue( organisationUnit, dataElement2, period, optionCombo2 );
                
            lineListDeathSexValues[count1] = dataValue2;
            
            count1++;
        }        
        
    }
    
    private void getLineListingDataElementsForDeaths()
    {
        String llMappingXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
        + "linelisting" + File.separator + "LineListingDeathDEs.xml";
        
        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                llMappingXMLFileName = newpath + File.separator + "dhis" + File.separator
                + "linelisting" + File.separator + "LineListingDeathDEs.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println("Line Listing DataElement Mapping XML File Not Found, Make sure the Path");
        }
        
            try
            {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse( new File( llMappingXMLFileName ) );
                                          
                NodeList mainTagList1 = doc.getElementsByTagName( "do5y" );
                Element mainTagElement1 = (Element) mainTagList1.item( 0 );
                NodeList textMainTagList1 = mainTagElement1.getChildNodes();
                String mainTagValue1 = ((Node) textMainTagList1.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "do5y", mainTagValue1 );

                NodeList mainTagList2 = doc.getElementsByTagName( "mdo5y" );
                Element mainTagElement2 = (Element) mainTagList2.item( 0 );
                NodeList textMainTagList2 = mainTagElement2.getChildNodes();
                String mainTagValue2 = ((Node) textMainTagList2.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "mdo5y", mainTagValue2 );
                
                NodeList mainTagList3 = doc.getElementsByTagName( "fdo5y" );
                Element mainTagElement3 = (Element) mainTagList3.item( 0 );
                NodeList textMainTagList3 = mainTagElement3.getChildNodes();
                String mainTagValue3 = ((Node) textMainTagList3.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "fdo5y", mainTagValue3 );
                
                NodeList mainTagList4 = doc.getElementsByTagName( "cdb5y" );
                Element mainTagElement4 = (Element) mainTagList4.item( 0 );
                NodeList textMainTagList4 = mainTagElement4.getChildNodes();
                String mainTagValue4 = ((Node) textMainTagList4.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cdb5y", mainTagValue4 );
                
                NodeList mainTagList5 = doc.getElementsByTagName( "cmdb5y" );
                Element mainTagElement5 = (Element) mainTagList5.item( 0 );
                NodeList textMainTagList5 = mainTagElement5.getChildNodes();
                String mainTagValue5 = ((Node) textMainTagList5.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cmdb5y", mainTagValue5 );
                
                NodeList mainTagList6 = doc.getElementsByTagName( "cfdb5y" );
                Element mainTagElement6 = (Element) mainTagList6.item( 0 );
                NodeList textMainTagList6 = mainTagElement6.getChildNodes();
                String mainTagValue6 = ((Node) textMainTagList6.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cfdb5y", mainTagValue6 );
                
                NodeList mainTagList7 = doc.getElementsByTagName( "cmdb12m" );
                Element mainTagElement7 = (Element) mainTagList7.item( 0 );
                NodeList textMainTagList7 = mainTagElement7.getChildNodes();
                String mainTagValue7 = ((Node) textMainTagList7.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cmdb12m", mainTagValue7 );
                
                NodeList mainTagList8 = doc.getElementsByTagName( "cfdb12m" );
                Element mainTagElement8 = (Element) mainTagList8.item( 0 );
                NodeList textMainTagList8 = mainTagElement8.getChildNodes();
                String mainTagValue8 = ((Node) textMainTagList8.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cfdb12m", mainTagValue8 );
                
                NodeList mainTagList9 = doc.getElementsByTagName( "cmdb1m" );
                Element mainTagElement9 = (Element) mainTagList9.item( 0 );
                NodeList textMainTagList9 = mainTagElement9.getChildNodes();
                String mainTagValue9 = ((Node) textMainTagList9.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cmdb1m", mainTagValue9 );
                
                NodeList mainTagList10 = doc.getElementsByTagName( "cfdb1m" );
                Element mainTagElement10 = (Element) mainTagList10.item( 0 );
                NodeList textMainTagList10 = mainTagElement10.getChildNodes();
                String mainTagValue10 = ((Node) textMainTagList10.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cfdb1m", mainTagValue10 );
                
                NodeList mainTagList11 = doc.getElementsByTagName( "cmdb1w" );
                Element mainTagElement11 = (Element) mainTagList11.item( 0 );
                NodeList textMainTagList11 = mainTagElement11.getChildNodes();
                String mainTagValue11 = ((Node) textMainTagList11.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cmdb1w", mainTagValue11 );
                                                                               
                NodeList mainTagList12 = doc.getElementsByTagName( "cfdb1w" );
                Element mainTagElement12 = (Element) mainTagList12.item( 0 );
                NodeList textMainTagList12 = mainTagElement12.getChildNodes();
                String mainTagValue12 = ((Node) textMainTagList12.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cfdb1w", mainTagValue12 );

                NodeList mainTagList13 = doc.getElementsByTagName( "cmdb1d" );
                Element mainTagElement13 = (Element) mainTagList13.item( 0 );
                NodeList textMainTagList13 = mainTagElement13.getChildNodes();
                String mainTagValue13 = ((Node) textMainTagList13.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cmdb1d", mainTagValue13 );

                NodeList mainTagList14 = doc.getElementsByTagName( "cfdb1d" );
                Element mainTagElement14 = (Element) mainTagList14.item( 0 );
                NodeList textMainTagList14 = mainTagElement14.getChildNodes();
                String mainTagValue14 = ((Node) textMainTagList14.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cfdb1d", mainTagValue14 );

                NodeList mainTagList15 = doc.getElementsByTagName( "cdnk" );
                Element mainTagElement15 = (Element) mainTagList15.item( 0 );
                NodeList textMainTagList15 = mainTagElement15.getChildNodes();
                String mainTagValue15 = ((Node) textMainTagList15.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "cdnk", mainTagValue15 );

                NodeList mainTagList16 = doc.getElementsByTagName( "mdo55y" );
                Element mainTagElement16 = (Element) mainTagList16.item( 0 );
                NodeList textMainTagList16 = mainTagElement16.getChildNodes();
                String mainTagValue16 = ((Node) textMainTagList16.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "mdo55y", mainTagValue16 );
                
                NodeList mainTagList17 = doc.getElementsByTagName( "fdo55y" );
                Element mainTagElement17 = (Element) mainTagList17.item( 0 );
                NodeList textMainTagList17 = mainTagElement17.getChildNodes();
                String mainTagValue17 = ((Node) textMainTagList17.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "fdo55y", mainTagValue17 );
                
                NodeList mainTagList18 = doc.getElementsByTagName( "mdo15y" );
                Element mainTagElement18 = (Element) mainTagList18.item( 0 );
                NodeList textMainTagList18 = mainTagElement18.getChildNodes();
                String mainTagValue18 = ((Node) textMainTagList18.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "mdo15y", mainTagValue18 );
                
                NodeList mainTagList19 = doc.getElementsByTagName( "fdo15y" );
                Element mainTagElement19 = (Element) mainTagList19.item( 0 );
                NodeList textMainTagList19 = mainTagElement19.getChildNodes();
                String mainTagValue19 = ((Node) textMainTagList19.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "fdo15y", mainTagValue19 );
                
                lineListDeathSex = mainTagElement1.getAttribute( "sex" );
                lineListDeathAge = mainTagElement1.getAttribute( "age" );
                lineListDeathCause = mainTagElement1.getAttribute( "causeofdeath" );
                
                System.out.println("Sex : "+lineListSex+ " Weigh : "+lineListWeigh);
                
            }// try block end
            catch ( SAXParseException err )
            {
                System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
                System.out.println( " " + err.getMessage() );
            }
            catch ( SAXException e )
            {
                Exception x = e.getException();
                ((x == null) ? e : x).printStackTrace();
            }
            catch ( Throwable t )
            {
                t.printStackTrace();
            }                                    

    }
    
    private void getLineListingDataElementsForLiveBirths()
    {
        String llMappingXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
        + "linelisting" + File.separator + "LineListingLiveBirthDEs.xml";
        
        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                llMappingXMLFileName = newpath + File.separator + "dhis" + File.separator
                + "linelisting" + File.separator + "LineListingLiveBirthDEs.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println("Line Listing DataElement Mapping XML File Not Found, Make sure the Path");
        }
        
            try
            {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse( new File( llMappingXMLFileName ) );
                                          
                NodeList mainTagList1 = doc.getElementsByTagName( "livebirth" );
                Element mainTagElement1 = (Element) mainTagList1.item( 0 );
                NodeList textMainTagList1 = mainTagElement1.getChildNodes();
                String mainTagValue1 = ((Node) textMainTagList1.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "livebirth", mainTagValue1 );

                NodeList mainTagList2 = doc.getElementsByTagName( "malebirth" );
                Element mainTagElement2 = (Element) mainTagList2.item( 0 );
                NodeList textMainTagList2 = mainTagElement2.getChildNodes();
                String mainTagValue2 = ((Node) textMainTagList2.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "malebirth", mainTagValue2 );
                
                NodeList mainTagList3 = doc.getElementsByTagName( "femalebirth" );
                Element mainTagElement3 = (Element) mainTagList3.item( 0 );
                NodeList textMainTagList3 = mainTagElement3.getChildNodes();
                String mainTagValue3 = ((Node) textMainTagList3.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "femalebirth", mainTagValue3 );
                
                NodeList mainTagList4 = doc.getElementsByTagName( "mlbweighted" );
                Element mainTagElement4 = (Element) mainTagList4.item( 0 );
                NodeList textMainTagList4 = mainTagElement4.getChildNodes();
                String mainTagValue4 = ((Node) textMainTagList4.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "mlbweighted", mainTagValue4 );
                
                NodeList mainTagList5 = doc.getElementsByTagName( "flbweighted" );
                Element mainTagElement5 = (Element) mainTagList5.item( 0 );
                NodeList textMainTagList5 = mainTagElement5.getChildNodes();
                String mainTagValue5 = ((Node) textMainTagList5.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "flbweighted", mainTagValue5 );
                
                NodeList mainTagList6 = doc.getElementsByTagName( "mlbless2500" );
                Element mainTagElement6 = (Element) mainTagList6.item( 0 );
                NodeList textMainTagList6 = mainTagElement6.getChildNodes();
                String mainTagValue6 = ((Node) textMainTagList6.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "mlbless2500", mainTagValue6 );
                
                NodeList mainTagList7 = doc.getElementsByTagName( "flbless2500" );
                Element mainTagElement7 = (Element) mainTagList7.item( 0 );
                NodeList textMainTagList7 = mainTagElement7.getChildNodes();
                String mainTagValue7 = ((Node) textMainTagList7.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "flbless2500", mainTagValue7 );
                
                NodeList mainTagList8 = doc.getElementsByTagName( "mlbless1800" );
                Element mainTagElement8 = (Element) mainTagList8.item( 0 );
                NodeList textMainTagList8 = mainTagElement8.getChildNodes();
                String mainTagValue8 = ((Node) textMainTagList8.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "mlbless1800", mainTagValue8 );
                
                NodeList mainTagList9 = doc.getElementsByTagName( "flbless1800" );
                Element mainTagElement9 = (Element) mainTagList9.item( 0 );
                NodeList textMainTagList9 = mainTagElement9.getChildNodes();
                String mainTagValue9 = ((Node) textMainTagList9.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "flbless1800", mainTagValue9 );
                
                NodeList mainTagList10 = doc.getElementsByTagName( "mlbbf" );
                Element mainTagElement10 = (Element) mainTagList10.item( 0 );
                NodeList textMainTagList10 = mainTagElement10.getChildNodes();
                String mainTagValue10 = ((Node) textMainTagList10.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "mlbbf", mainTagValue10 );
                
                NodeList mainTagList11 = doc.getElementsByTagName( "flbbf" );
                Element mainTagElement11 = (Element) mainTagList11.item( 0 );
                NodeList textMainTagList11 = mainTagElement11.getChildNodes();
                String mainTagValue11 = ((Node) textMainTagList11.item( 0 )).getNodeValue().trim();
                lineListAggDes.put( "flbbf", mainTagValue11 );
                                                                               
                
                lineListSex = mainTagElement1.getAttribute( "sex" );
                lineListWeigh = mainTagElement1.getAttribute( "weigh" );
                lineListbreastFeeding = mainTagElement1.getAttribute( "breastfeeding" );
                
                System.out.println("Sex : "+lineListSex+ " Weigh : "+lineListWeigh);
                
            }// try block end
            catch ( SAXParseException err )
            {
                System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
                System.out.println( " " + err.getMessage() );
            }
            catch ( SAXException e )
            {
                Exception x = e.getException();
                ((x == null) ? e : x).printStackTrace();
            }
            catch ( Throwable t )
            {
                t.printStackTrace();
            }                                    
    }
    
    
    private void saveData(OrganisationUnit organisationUnit, Period period, String deString, String value)
    {                   
        String partsOfdeString[] = deString.split( ":" );
        
        int dataElementId = Integer.parseInt( partsOfdeString[0] );
        int optionComboId = Integer.parseInt( partsOfdeString[1] );                
            
        DataElement dataElement = dataElementService.getDataElement( dataElementId );                
        DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

        if(dataElement == null || optionCombo == null)
        {
            
        }
        else
        {
            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, optionCombo );            
            if ( dataValue == null )
            {                
                if ( value != null )
                {
                    dataValue = new DataValue( dataElement, period, organisationUnit, value, storedBy, new Date(), null, optionCombo );

                    dataValueService.addDataValue( dataValue );
                    llValueMap.put(dataElement.getId(), value);
                    liDEMap.put(dataElement.getId(),optionCombo.getId());
                }
            }
            else
            {
                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );
                dataValue.setStoredBy( storedBy );

                dataValueService.updateDataValue( dataValue );
                llValueMap.put(dataElement.getId(), value);
                liDEMap.put(dataElement.getId(),optionCombo.getId());
            }
        }            

    }
    
    private void updateBreastFeeding(OrganisationUnit organisationUnit, Period period, DataValue[] lineListSexValues, DataValue[] lineListBFValues)
    {
        int count1 = 0;
        int totalMBF = 0;
        int totalFBF = 0;
        
        while(count1 < lineListSexValues.length)
        {
            DataValue dv1 = lineListSexValues[count1];
            if(dv1 == null)
            {
            }
            else
            {
                DataValue dv2 = lineListBFValues[count1];
                if(dv2 == null)
                {
                    
                }
                else
                {
                    if(dv1.getValue().equalsIgnoreCase( "m" ))
                    {
                        if(dv2.getValue().equalsIgnoreCase( "y" ))
                        {
                            totalMBF++;
                        }
                    }
                    else if(dv1.getValue().equalsIgnoreCase( "f" ))
                    {
                        if(dv2.getValue().equalsIgnoreCase( "y" ))
                        {
                            totalFBF++;
                        }
                    }
                }
            }
            count1++;
        }
        
        String deString = lineListAggDes.get( "mlbbf" );
        String value = ""+totalMBF;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "flbbf" );
        value = ""+totalFBF;
        saveData(organisationUnit, period, deString, value);
        
    }
    
    private void updateWeighs(OrganisationUnit organisationUnit, Period period, DataValue[] lineListSexValues, DataValue[] lineListWeighValues)
    {
        int count1 = 0;
        int totalMBW = 0;
        int totalFBW = 0;
        int totalMBW2500 = 0;
        int totalFBW2500 = 0;
        int totalMBW1800 = 0;
        int totalFBW1800 = 0;
        
        while(count1 < lineListSexValues.length)
        {
            DataValue dv1 = lineListSexValues[count1];
            if(dv1 == null)
            {
            }
            else
            {
                DataValue dv2 = lineListWeighValues[count1];
                if(dv2 == null)
                {
                    
                }
                else
                {
                    if(dv1.getValue().equalsIgnoreCase( "m" ))
                    {
                        if(dv2.getValue().equalsIgnoreCase( "nk" ))
                        {
                            
                        }
                        else
                        {
                            try
                            {
                                
                                if(Integer.parseInt( dv2.getValue() ) < 2500)
                                {
                                    totalMBW2500++;
                                }
                                if(Integer.parseInt( dv2.getValue() ) < 1800)
                                {
                                    totalMBW1800++;
                                }
                                totalMBW++;
                            }
                            catch(Exception e)
                            {
                                
                            }
                        }
                    }
                    else if(dv1.getValue().equalsIgnoreCase( "f" ))
                    {
                        if(dv2.getValue().equalsIgnoreCase( "nk" ))
                        {
                            
                        }
                        else
                        {
                            try
                            {
                                if(Integer.parseInt( dv2.getValue() ) < 2500)
                                {
                                    totalFBW2500++;
                                }
                                if(Integer.parseInt( dv2.getValue() ) < 1800)
                                {
                                    totalFBW1800++;
                                }                            
                                totalFBW++;
                            }
                            catch(Exception e)
                            {
                                
                            }
                        }                        
                    }
                }
            }
            count1++;
        }
        
        String deString = lineListAggDes.get( "mlbweighted" );
        String value = ""+totalMBW;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "flbweighted" );
        value = ""+totalFBW;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "mlbless2500" );
        value = ""+totalMBW2500;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "flbless2500" );
        value = ""+totalFBW2500;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "mlbless1800" );
        value = ""+totalMBW1800;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "flbless1800" );
        value = ""+totalFBW1800;
        saveData(organisationUnit, period, deString, value);

    }
    
    private void updateLivebirhts(OrganisationUnit organisationUnit, Period period, DataValue[] lineListSexValues)
    {
        int count1 = 0;
        int totalLiveBirths = 0;
        int totalMaleLiveBirths = 0;
        int totalFemaleLiveBirths = 0;
        
        while(count1 < lineListSexValues.length)
        {
            DataValue dv = lineListSexValues[count1];
            if(dv == null)
            {
            }
            else
            {
                if(dv.getValue().equalsIgnoreCase( "m" ))
                {
                    totalMaleLiveBirths++;
                    totalLiveBirths++;
                }
                else if(dv.getValue().equalsIgnoreCase( "f" ))
                {
                    totalFemaleLiveBirths++;
                    totalLiveBirths++;
                }
            }
            count1++;
        }
        
        String deString = lineListAggDes.get( "livebirth" );
        String value = ""+totalLiveBirths;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "malebirth" );
        value = ""+totalMaleLiveBirths;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "femalebirth" );
        value = ""+totalFemaleLiveBirths;
        saveData(organisationUnit, period, deString, value);
    }

    private void updateDeaths(OrganisationUnit organisationUnit, Period period, DataValue[] lineListDeathSexValues, DataValue[] lineListDeathAgeValues, DataValue[] lineListDeathCauseValues)
    {
        int count1 = 0;
        int totalDeathOver5Y = 0;
        int totalMaleDeathOver5Y = 0;
        int totalFemaleDeathOver5Y = 0;
        int totalMaleDeathOver15Y = 0;
        int totalFemaleDeathOver15Y = 0;
        int totalMaleDeathOver55Y = 0;
        int totalFemaleDeathOver55Y = 0;
        int totalDeathBelow5Y = 0;
        int totalMaleDeathBelow5Y = 0;
        int totalFemaleDeathBelow5Y = 0;
        int totalMaleDeathBelow12M = 0;
        int totalFemaleDeathBelow12M = 0;
        int totalMaleDeathBelow1M = 0;
        int totalFemaleDeathBelow1M = 0;
        int totalMaleDeathBelow1W = 0;
        int totalFemaleDeathBelow1W = 0;
        int totalMaleDeathBelow1D = 0;
        int totalFemaleDeathBelow1D = 0;
        int totalCauseNKDeaths = 0;
        

        while(count1 < lineListDeathSexValues.length)
        {
            DataValue dv1 = lineListDeathAgeValues[count1];
            DataValue dv2 = lineListDeathSexValues[count1];
            DataValue dv3 = lineListDeathCauseValues[count1];
            
            if(dv1 != null && dv2 !=null && dv3 != null)
            {
                String ageValue = dv1.getValue();
                String age =  ageValue.substring( 0,ageValue.length()-2 );
                String ageType = ageValue.substring(ageValue.length()-1);
                try
                { 
                    
                    if(dv3.getValue().equalsIgnoreCase( "nk" ))
                    {
                        totalCauseNKDeaths++;
                    }
                    
                    if( ageType.equalsIgnoreCase( "y" ) )
                    {
                        if((Integer.parseInt( age ) > 5) && (Integer.parseInt( age ) < 15))
                        {
                            if(dv2.getValue().equalsIgnoreCase( "m" ))
                            {
                                totalMaleDeathOver5Y++;
                            }
                            else if(dv2.getValue().equalsIgnoreCase( "f" ))
                            {
                                totalFemaleDeathOver5Y++;
                            }
                            totalDeathOver5Y++;
                        }
                        else if((Integer.parseInt( age ) > 15) && (Integer.parseInt( age ) < 55))
                        {
                            if(dv2.getValue().equalsIgnoreCase( "m" ))
                            {
                                totalMaleDeathOver15Y++;
                            }
                            else if(dv2.getValue().equalsIgnoreCase( "f" ))
                            {
                                totalFemaleDeathOver15Y++;
                            }
                            totalDeathOver5Y++;
                        }
                        else if(Integer.parseInt( age ) > 55)
                        {
                            if(dv2.getValue().equalsIgnoreCase( "m" ))
                            {
                                totalMaleDeathOver55Y++;
                            }
                            else if(dv2.getValue().equalsIgnoreCase( "f" ))
                            {
                                totalFemaleDeathOver55Y++;
                            }
                            totalDeathOver5Y++;
                        }

                        else
                        {
                            if(dv2.getValue().equalsIgnoreCase( "m" ))
                            {
                                totalMaleDeathBelow5Y++;
                            }
                            else if(dv2.getValue().equalsIgnoreCase( "f" ))
                            {
                                totalFemaleDeathBelow5Y++;
                            }
                            totalDeathBelow5Y++; 
                        }
                    }
                    else
                    {
                        totalDeathBelow5Y++;
                        if(dv2.getValue().equalsIgnoreCase( "m" ))
                        {
                            totalMaleDeathBelow5Y++;
                        }
                        else if(dv2.getValue().equalsIgnoreCase( "f" ))
                        {
                            totalFemaleDeathBelow5Y++;
                        }

                        
                        if(ageType.equalsIgnoreCase( "m" ))
                        {
                            if(dv2.getValue().equalsIgnoreCase( "m" ))
                            {
                                totalMaleDeathBelow12M++;
                            }
                            else if(dv2.getValue().equalsIgnoreCase( "f" ))
                            {
                                totalFemaleDeathBelow12M++;
                            }

                        }
                        else
                        {
                            if(dv2.getValue().equalsIgnoreCase( "m" ))
                            {
                                totalMaleDeathBelow12M++;
                                totalMaleDeathBelow1M++;
                            }
                            else if(dv2.getValue().equalsIgnoreCase( "f" ))
                            {
                                totalFemaleDeathBelow12M++;
                                totalFemaleDeathBelow1M++;
                            }

                            if(ageType.equalsIgnoreCase( "w" ))
                            {
                                
                            }
                            else
                            {
                                if(dv2.getValue().equalsIgnoreCase( "m" ))
                                {
                                    totalMaleDeathBelow1W++;
                                }
                                else if(dv2.getValue().equalsIgnoreCase( "f" ))
                                {
                                    totalFemaleDeathBelow1W++;
                                }
                                
                                if(ageType.equalsIgnoreCase( "d" ))
                                {
                                }
                                else
                                {
                                    if(dv2.getValue().equalsIgnoreCase( "m" ))
                                    {
                                        totalMaleDeathBelow1D++;
                                    }
                                    else if(dv2.getValue().equalsIgnoreCase( "f" ))
                                    {
                                        totalFemaleDeathBelow1D++;
                                    }
                                }
                            }
                        }
                    }
                }
                catch(Exception e) { System.out.println(e.getMessage()); }
            }
            count1++;
        }
        
        String deString = lineListAggDes.get( "do5y" );
        String value = ""+totalDeathOver5Y;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "mdo55y" );
        value = ""+totalMaleDeathOver55Y;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "fdo55y" );
        value = ""+totalFemaleDeathOver55Y;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "mdo15y" );
        value = ""+totalMaleDeathOver15Y;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "fdo15y" );
        value = ""+totalFemaleDeathOver15Y;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "mdo5y" );
        value = ""+totalMaleDeathOver5Y;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "fdo5y" );
        value = ""+totalFemaleDeathOver5Y;
        saveData(organisationUnit, period, deString, value);

        deString = lineListAggDes.get( "cdb5y" );
        value = ""+totalDeathBelow5Y;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cmdb5y" );
        value = ""+totalMaleDeathBelow5Y;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cfdb5y" );
        value = ""+totalFemaleDeathBelow5Y;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cmdb12m" );
        value = ""+totalMaleDeathBelow12M;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cfdb12m" );
        value = ""+totalFemaleDeathBelow12M;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cmdb1m" );
        value = ""+totalMaleDeathBelow1M;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cfdb1m" );
        value = ""+totalFemaleDeathBelow1M;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cmdb1w" );
        value = ""+totalMaleDeathBelow1W;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cfdb1w" );
        value = ""+totalFemaleDeathBelow1W;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cmdb1d" );
        value = ""+totalMaleDeathBelow1D;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cfdb1d" );
        value = ""+totalFemaleDeathBelow1D;
        saveData(organisationUnit, period, deString, value);
        
        deString = lineListAggDes.get( "cdnk" );
        value = ""+totalCauseNKDeaths;
        saveData(organisationUnit, period, deString, value);

                
    }

    
}
