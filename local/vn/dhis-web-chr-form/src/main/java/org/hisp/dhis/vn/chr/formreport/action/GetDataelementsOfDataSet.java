package org.hisp.dhis.vn.chr.formreport.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class GetDataelementsOfDataSet
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {

        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {

        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private List<Operand> operands = new ArrayList<Operand>();

    public List<Operand> getOperands()
    {
        return operands;
    }

    private ArrayList<DataElement> dataelements;

    public ArrayList<DataElement> getDataelements()
    {
        return dataelements;
    }

    private int dataSetId;

    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        dataelements = new ArrayList<DataElement>( dataSetService.getDataSet( dataSetId ).getDataElements() );

        Collections.sort( dataelements, dataElementComparator );

        displayPropertyHandler.handle( dataelements );

        // ---------------------------------------------------------------------
        // Create Operands
        // ---------------------------------------------------------------------

        operands = new ArrayList<Operand>( dataElementCategoryOptionComboService.getOperands( dataelements ) );

        return SUCCESS;
    }

}
