package org.hisp.dhis.pbf.payment.action;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.LookupService;
import org.hisp.dhis.pbf.api.QualityMaxValueService;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadPaymentAdjustmentAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataValueService dataValueService;

    @Autowired
    private TariffDataValueService tariffDataValueService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private QualityMaxValueService qualityMaxValueService;

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitId;

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String periodIso;

    public void setPeriodIso( String periodIso )
    {
        this.periodIso = periodIso;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit selOrgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        DataSet selDataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        
        Period period = PeriodType.getPeriodFromIsoString( periodIso );
        
        
        //----------------------------------------------
        // Quantity Calculation
        //----------------------------------------------
        
        Map<DataElement, Double> pbfQtyMap = new HashMap<DataElement, Double>();
        
        //-------------------------------------------------
        // Quantity Tariff Calculation
        //-------------------------------------------------
        Map<DataElement, Double> pbfTariffMap = new HashMap<DataElement, Double>();
        
        //-------------------------------------------------
        // 
        //-------------------------------------------------
        
        return SUCCESS;
    }
}
