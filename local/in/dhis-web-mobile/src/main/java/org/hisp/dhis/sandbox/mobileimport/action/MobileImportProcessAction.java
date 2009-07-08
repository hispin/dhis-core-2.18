package org.hisp.dhis.sandbox.mobileimport.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.sandbox.mobileimport.api.MobileImportParameters;
import org.hisp.dhis.sandbox.mobileimport.api.MobileImportService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork.Action;

public class MobileImportProcessAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MobileImportService mobileImportService;

    public void setMobileImportService( MobileImportService mobileImportService )
    {
        this.mobileImportService = mobileImportService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private String importStatus;

    public String getImportStatus()
    {
        return importStatus;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        importStatus = "";

        List<String> fileNames = new ArrayList<String>( mobileImportService.getImportFiles() );

        System.out.println( fileNames.size() );

        for ( String importFile : fileNames )
        {

            MobileImportParameters mobImportParameters = mobileImportService.getParametersFromXML( importFile );

            if ( mobImportParameters == null )
            {
                System.out.println( importFile + " Import File is not Propely Formated First" );

                importStatus += new Date() + ": \t " + importFile + " Import File is not Propely Formated \n";

                mobileImportService.moveFailedFile( importFile );

                continue;
            }

            User curUser = mobileImportService.getUserInfo( mobImportParameters.getMobileNumber() );

            UserCredentials userCredentials = userStore.getUserCredentials( curUser );

            String storedBy = userCredentials.getUsername();

            List<Source> sources = new ArrayList<Source>( curUser.getOrganisationUnits() );

            if ( sources == null || sources.size() <= 0 )
            {
                System.out.println( "Source is NULL" );
                return INPUT;
            }
            Source source = sources.get( 0 );

            Period period = mobileImportService.getPeriodInfo( mobImportParameters.getStartDate() );

            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-mm-dd" );

            Date timeStamp = dateFormat.parse( mobImportParameters.getSmsTime() );

            Map<String, Integer> dataValueMap = new HashMap<String, Integer>( mobImportParameters.getDataValues() );

            if ( dataValueMap == null || dataValueMap.size() <= 0 )
            {
                System.out.println( "dataValue map is null" );
            }
            else if ( source == null )
            {
                System.out.println( "source is null" );
            }
            else if ( period == null )
            {
                System.out.println( "period is null" );
            }
            else if ( timeStamp == null )
            {
                System.out.println( "timeStamp is null" );
            }

            if ( source == null || period == null || timeStamp == null || dataValueMap == null
                || dataValueMap.size() <= 0 )
            {
                System.out.println( importFile + " Import File is not Propely Formated" );

                importStatus += new Date() + ": \t " + importFile + " Import File is not Propely Formated \n";

                mobileImportService.moveFailedFile( importFile );

                continue;
            }

            Set<String> keys = dataValueMap.keySet();

            for ( String key : keys )
            {
                // DataElement dataElement = dataElementService.getDataElement(
                // key );

                String parts[] = key.split( "\\." );
                
                System.out.println(key + "\t : \t" + parts.length);

                String deStr = parts[0];

                String optStr = parts[1];

                String value = String.valueOf( dataValueMap.get( key ) );

                System.out.println( "OPTION ID IS : \t" + optStr );

                DataElement dataElement = dataElementService.getDataElement( Integer.valueOf( deStr ) );

                DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

                optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer
                    .valueOf( optStr ) );

                DataValue dataValue = dataValueService.getDataValue( source, dataElement, period, optionCombo );

                if ( dataValue == null )
                {
                    if ( value != null )
                    {
                        dataValue = new DataValue( dataElement, period, source, value, storedBy, timeStamp, null,
                            optionCombo );

                        dataValueService.addDataValue( dataValue );
                    }
                }
                else
                {
                    dataValue.setValue( value );

                    dataValue.setTimestamp( timeStamp );

                    dataValue.setStoredBy( storedBy );

                    dataValueService.updateDataValue( dataValue );
                }

            }

            System.out.println( importFile + " is Imported Successfully" );

            importStatus += new Date() + ": \t " + importFile + " is Imported Successfully \n";

            mobileImportService.moveImportedFile( importFile );
        }

        return SUCCESS;
    }

}
