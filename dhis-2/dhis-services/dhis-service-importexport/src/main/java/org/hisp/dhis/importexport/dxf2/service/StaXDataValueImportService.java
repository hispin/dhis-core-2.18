package org.hisp.dhis.importexport.dxf2.service;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.xml.namespace.QName;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.importexport.ImportException;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.dxf2.model.DataValueSet;
import org.hisp.dhis.importexport.importer.DataValueImporter;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

/**
 * Really basic DXF2 class for reading data
 *
 * @author bobj
 */
public class StaXDataValueImportService
    implements DataValueImportService
{
    private static final Log log = LogFactory.getLog( StaXDataValueImportService.class );

    // -------------------------------------------------------------------------
    // Status/Log messages
    // TODO: internationalise these
    // -------------------------------------------------------------------------
    
    public static final String NO_DATAVALUESETS = "There are no datasets in this message";

    public static final String IMPORTING_DATAVALUES = "Importing data values";

    public static final String INVALID_PERIOD = "Invalid period : %s";

    public static final String NO_SUCH_ORGUNIT = "No such orgunit : %s = %s";

    public static final String NO_SUCH_DATAELEMENT = "No such dataElement : %s = %s";

    public static final String NO_ROOT = "Couldn't find dxf root element";

    public static final String UNKNOWN_ID_STRATEGY = "Unknown id strategy = %s";

    public static final String SUCCESS = "DataValue import complete";

    public static final String COUNTER = "%s DataValues imported";

    public static final int DISPLAYCOUNT = 1000;

    public static final String NAMESPACE_20 = "http://dhis2.org/schema/dxf/2.0";

    public static final String DXFROOT = "dxf";

    public static final String ATTRIBUTE_MINOR_VERSION = "minorVersion";

    public static final String ATTRIBUTE_EXPORTED = "exported";

    public static final String DATAVALUESETS = "dataValueSets";

    public static final String DATAVALUESET = "dataValueSet";

    public static final String MINOR_VERSION_10 = "1.0";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
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

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    public void read( XMLReader reader, ImportParams params, ProcessState state )
    {
        String user = currentUserService.getCurrentUsername();

        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler(
            DataValueBatchHandler.class ).init();

        DataValueImporter importer = 
            new DataValueImporter(batchHandler, aggregatedDataValueService, params);

        int cumulativeDataValueCounter = 0;

        try
        {
            if ( !reader.moveToStartElement( DXFROOT, DXFROOT ) )
            {
                throw new ImportException( NO_ROOT );
            }
            
            QName rootName = reader.getElementQName();

            params.setNamespace( defaultIfEmpty( rootName.getNamespaceURI(), NAMESPACE_20 ) );
            String version = reader.getAttributeValue( ATTRIBUTE_MINOR_VERSION );
            params.setMinorVersion( version != null ? version : MINOR_VERSION_10 );

            log.debug( String.format( "Importing %s minor version  %s", rootName.getNamespaceURI(), version ) );

            // move straight to the DataValue sets, we are not looking for metadata
            reader.moveToStartElement( DATAVALUESETS );

            Date timestamp = new Date();

            if ( !reader.isStartElement( DATAVALUESETS ) )
            {
                throw new ImportException( NO_DATAVALUESETS );
            }

            // Outer loop, process datavaluesets until no more datavaluesets
            int countDataValueSets = 0;
            
            do
            {
                // look for a  DataValue set
                if ( !reader.isStartElement( DATAVALUESET ) )
                {
                    try
                    {
                       reader.moveToStartElement( DATAVALUESET );
                    } 
                    catch ( java.util.NoSuchElementException ex )
                    {
                        // we have to reach here eventuallyperiodId
                        break;
                    }
                }

                // Pick off the attributes
                String idSchemeStr = reader.getAttributeValue( DataValueSet.ATTR_IDSCHEME );
                String dataSet = reader.getAttributeValue( DataValueSet.ATTR_DATASET );
                String period = reader.getAttributeValue( DataValueSet.ATTR_PERIOD );
                String outerOrgunit = reader.getAttributeValue( DataValueSet.ATTR_ORGUNIT );
                String comment = reader.getAttributeValue( DataValueSet.ATTR_COMMENT );

                log.debug( String.format(
                    "Importing datavalueset (%s): period %s : orgunit %s : idscheme : %s",
                    comment, period, outerOrgunit, idSchemeStr ) );

                // Determine identifier scheme to use

                DataValueSet.IdentificationStrategy idScheme = DataValueSet.DEFAULT_STRATEGY;

                if ( idSchemeStr != null )
                {
                    try
                    {
                        idScheme = DataValueSet.IdentificationStrategy.valueOf( idSchemeStr );
                    } 
                    catch ( IllegalArgumentException ex )
                    {
                        throw new ImportException( String.format( UNKNOWN_ID_STRATEGY, idSchemeStr ) );
                    }
                }

                Period outerPeriod = getPeriodObj( period );

                // maps for translating identifiers
                Map<String, Integer> dataelementMap = null;
                Map<String, Integer> orgunitMap = null;

                // get map for translating dataelement identifiers
                dataelementMap = getDataElementMap( dataSet, idScheme );

                Integer outerOrgunitId = null;
                // if orgunit defined at datavalueset level, use it
                if ( outerOrgunit != null )
                {
                    outerOrgunitId = getOrgUnitByIdentifier( outerOrgunit, idScheme ).getId();
                } else
                {
                    // get map for translating orgunit identifiers
                    orgunitMap = getOrgUnitMap( dataSet, idScheme );
                }

                // only supporting default optioncombo at present
                DataElementCategoryOptionCombo optioncombo =
                    categoryService.getDefaultDataElementCategoryOptionCombo();

                int countDataValues = 0;
                // process datavalues - loop until no more datavalues
                
                do
                {
                    // look for a  DataValue
                    reader.moveToStartElement();
                    
                    if ( !reader.isStartElement( DataValueSet.DATAVALUE ) )
                    {
                        // we have to reach here eventually
                        break;
                    }

                    log.debug( "Reading Datavalue" );

                    String dataElementId = reader.getAttributeValue(
                        org.hisp.dhis.importexport.dxf2.model.DataValue.ATTR_DATAELEMENT );
                    String innerOrgUnitId = reader.getAttributeValue(
                        org.hisp.dhis.importexport.dxf2.model.DataValue.ATTR_ORGUNIT );
                    String value = reader.getAttributeValue(
                        org.hisp.dhis.importexport.dxf2.model.DataValue.ATTR_VALUE );

                    DataValue dv = new DataValue();
                    dv.setPeriod( outerPeriod );
                    dv.setValue( value );
                    // populate with placeholders
                    dv.setDataElement( new DataElement() );
                    dv.setSource( new OrganisationUnit() );
                    dv.setOptionCombo( optioncombo );
                    dv.setComment( comment );
                    dv.setStoredBy( user );
                    dv.setTimestamp( timestamp );

                    // if no outer orgunit defined, use the map
                    if ( outerOrgunit == null )
                    {
                        Integer id = orgunitMap.get( innerOrgUnitId );
                        if ( id == null )
                        {
                            log.info( "Unknown orgunit: " + innerOrgUnitId + " Rejecting value");
                            continue;
                        }
                        dv.getSource().setId( orgunitMap.get( innerOrgUnitId ) );

                    } 
                    else
                    {
                        dv.getSource().setId( outerOrgunitId );
                    }

                    dv.getDataElement().setId( dataelementMap.get( dataElementId ) );

                    importer.importObject(dv,params);

                    ++countDataValues;
                    ++cumulativeDataValueCounter;

                    if (countDataValues % DISPLAYCOUNT == 0) {
                        state.setMessage( String.format(COUNTER,cumulativeDataValueCounter));
                    }

                    log.debug( cumulativeDataValueCounter + " DataValues read" );

                } while ( true ); // DataValues loop

                ++countDataValueSets;
                log.debug( countDataValueSets + " DataValueSets read" );

            } while ( true ); // DataValueSets loop

            log.info( String.format(COUNTER,cumulativeDataValueCounter));
            state.setMessage( String.format(COUNTER,cumulativeDataValueCounter));

        } 
        catch ( ImportException ex )
        {
            log.warn( ex.toString() );
            state.setMessage( ex.toString() );
        } 
        finally
        {
            batchHandler.flush();
        }
    }

    private Period getPeriodObj( String period )
        throws ImportException
    {
        Period periodObj;

        PeriodType pt = PeriodType.getPeriodTypeFromIsoString( period );

        if ( pt == null )
        {
            throw new ImportException( String.format( INVALID_PERIOD, period ) );
        }

        try
        {
            periodObj = pt.createPeriod( period );

        } catch ( Exception e )
        {
            throw new ImportException( String.format( INVALID_PERIOD, period ) );
        }

        Period storedPeriod = periodService.getPeriod( periodObj.getStartDate(), periodObj.getEndDate(), pt );

        if ( storedPeriod == null )
        {
            int periodId = periodService.addPeriod( periodObj );

            periodObj.setId( periodId );

        } else
        {
            periodObj = storedPeriod;
        }

        return periodObj;
    }

    private Map<String, Integer> getDataElementMap( String dataSet, DataValueSet.IdentificationStrategy idScheme )
    {
        Collection<DataElement> dataelements;
        Map<String, Integer> result = null;

        if ( dataSet != null )
        {
            DataSet ds = getDataSet( dataSet, idScheme );
            dataelements = ds.getDataElements();
        } else
        {
            dataelements = dataElementService.getAllDataElements();
        }
        switch ( idScheme )
        {
            case CODE:
                result = DataElement.getCodeMap( dataelements );
                break;
            case INTERNAL:
                break;
            default:
                throw new IllegalArgumentException( "Can't map with :" + idScheme );
        }
        return result;
    }

    private Map<String, Integer> getOrgUnitMap( String dataSet, DataValueSet.IdentificationStrategy idScheme )
    {
        Collection<OrganisationUnit> orgunits;
        Map<String, Integer> result = null;

        if ( dataSet != null )
        {
            DataSet ds = getDataSet( dataSet, idScheme );
            orgunits = ds.getSources();
        } else
        {
            orgunits = organisationUnitService.getAllOrganisationUnits();
        }

        switch ( idScheme )
        {
            case CODE:
                result = OrganisationUnit.getCodeMap( orgunits );
                break;
            case INTERNAL:
                break;
            default:
                throw new IllegalArgumentException( "Can't map with :" + idScheme );
        }
        log.debug( result.size() + " orgunits in map" );
        return result;
    }

    /**
     * For a given orgunit identifier and id scheme, returns the orgunit object reference
     * @param orgunit
     * @param idScheme
     * @return
     * @throws ImportException thrown if no orgunit matches
     */
    private OrganisationUnit getOrgUnitByIdentifier( String orgunit, DataValueSet.IdentificationStrategy idScheme )
        throws ImportException
    {
        OrganisationUnit ou;
        switch ( idScheme )
        {
            case UID:
                ou = organisationUnitService.getOrganisationUnit( orgunit );
                break;
            case CODE:
                ou = organisationUnitService.getOrganisationUnitByCode( orgunit );
                break;
            case INTERNAL:
                ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgunit ) );
                break;
            default:
                throw new IllegalArgumentException( "Can't map with :" + idScheme );
        }

        if ( ou == null )
        {
            throw new ImportException( String.format( NO_SUCH_ORGUNIT, idScheme, orgunit ) );
        }
        return ou;
    }

    private DataSet getDataSet( String dataSet, DataValueSet.IdentificationStrategy idScheme )
    {
        DataSet result = null;
        switch ( idScheme )
        {
            case INTERNAL:
                result = dataSetService.getDataSet( Integer.parseInt( dataSet ) );
                break;
            case CODE:
                result = dataSetService.getDataSetByCode( dataSet );
                break;
            default:
                result = dataSetService.getDataSet( dataSet );
        }
        return result;
    }
}
