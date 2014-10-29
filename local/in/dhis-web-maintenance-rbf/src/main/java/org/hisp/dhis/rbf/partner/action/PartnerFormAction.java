package org.hisp.dhis.rbf.partner.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class PartnerFormAction implements Action
{
    private final String OPTION_SET_PARTNER = "Partner";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private OptionService optionService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private LookupService lookupService;
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
 
    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private List<Option> options = new ArrayList<Option>();
    
    public List<Option> getOptions()
    {
        return options;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        Lookup partnerOptionSetLookup = lookupService.getLookupByName( Lookup.OPTION_SET_PARTNER );
        
        OptionSet activitesOptionSet = optionService.getOptionSet( Integer.parseInt( partnerOptionSetLookup.getValue() ) );
        
        if( activitesOptionSet != null )
        {
            options = new ArrayList<Option>( activitesOptionSet.getOptions() );
        }
        
        dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        
        /*
        for ( Option option : options )
        {       
            option.getId();
            
            System.out.println( " Option Id -- " + option.getId() + " Option name -- " + option.getName() );
        }
        */
        
        return SUCCESS;
    }

}
