package org.hisp.dhis.de.screen;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface DataEntryScreenManager 
{	    
    String getScreenType( DataSet dataSet );    
    
    boolean hasMultiDimensionalDataElement( DataSet dataSet );
    
    boolean hasSection( DataSet dataSet );
    
    Collection<Integer> getAllCalculatedDataElements( DataSet dataSet );
    
    Map<CalculatedDataElement, Map<DataElement, Integer>> getNonSavedCalculatedDataElements( DataSet dataSet );
    
    Map<CalculatedDataElement, Integer> populateValuesForCalculatedDataElements( OrganisationUnit organisationUnit, DataSet dataSet, Period period );
    
    String populateCustomDataEntryScreen( String dataEntryFormCode, Collection<DataValue> dataValues, Map<CalculatedDataElement,Integer> calculatedValueMap, Map<Integer, MinMaxDataElement> minMaxMap, String disabled, I18n i18n );
    
    String populateCustomDataEntryScreenForMultiDimensional( String dataEntryFormCode, Collection<DataValue> dataValues, Map<CalculatedDataElement,Integer> calculatedValueMap, Map<Integer, MinMaxDataElement> minMaxMap, String disabled, Boolean saveMode, I18n i18n );

}
