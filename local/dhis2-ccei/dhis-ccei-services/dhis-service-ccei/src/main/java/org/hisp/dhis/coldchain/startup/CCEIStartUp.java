package org.hisp.dhis.coldchain.startup;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Mithilesh Kumar Thakur
 */
public class CCEIStartUp extends AbstractStartupRoutine
{
    //private static final Log log = LogFactory.getLog( StartUp.class );
    //public static final String NAME_TYPE = "Type";
    //public static final String NAME_OWNERSHIP = "Ownership";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    
    @Override
    public void execute() throws Exception
    {
        // Add OrganisationUnitGroupSets names type, Ownership, Facility Type
        
        List<OrganisationUnitGroupSet> types = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( Model.NAME_TYPE_GROUP_SET ) );
        OrganisationUnitGroupSet type = types.isEmpty() ? null : types.get( 0 );

        if ( type == null )
        {
            type = new OrganisationUnitGroupSet();
            type.setName( "Type" );
            type.setDescription( "Type of organisation unit, examples are PHU, chiefdom and district" );
            type.setCompulsory( false );

            organisationUnitGroupService.addOrganisationUnitGroupSet( type );
        }

        List<OrganisationUnitGroupSet> ownerships = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( Model.NAME_OWNERSHIP_GROUP_SET ) );
        OrganisationUnitGroupSet ownership = ownerships.isEmpty() ? null : ownerships.get( 0 );

        if ( ownership == null )
        {
            ownership = new OrganisationUnitGroupSet();
            ownership.setName( "Ownership" );
            ownership.setDescription( "Ownership of organisation unit, examples are private and public" );
            ownership.setCompulsory( true );

            organisationUnitGroupService.addOrganisationUnitGroupSet( ownership );
        }
        
        List<OrganisationUnitGroupSet> facilityTypes = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( Model.NAME_FACILITY_TYPE ) );
        OrganisationUnitGroupSet facilityType = facilityTypes.isEmpty() ? null : facilityTypes.get( 0 );

        if ( facilityType == null )
        {
            facilityType = new OrganisationUnitGroupSet();
            facilityType.setName( Model.NAME_FACILITY_TYPE );
            facilityType.setDescription( "Facility Type of organisation unit, examples are hospital, divisions, dispensary, health centre etc" );
            facilityType.setCompulsory( true );

            organisationUnitGroupService.addOrganisationUnitGroupSet( facilityType );
        }        
        
        // Add OrganisationUnitGroup names Health Facility
        List<OrganisationUnitGroup> groups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) );
        OrganisationUnitGroup group = groups.isEmpty() ? null : groups.get( 0 );
        
        if ( group == null )
        {
            group = new OrganisationUnitGroup();
            
            group.setName( EquipmentAttributeValue.HEALTHFACILITY  );
            group.setShortName( EquipmentAttributeValue.HEALTHFACILITY );
            
            organisationUnitGroupService.addOrganisationUnitGroup( group );
        }
        
        
        // add dataSet Name Facility Management Data Set and Short Name FMD
        List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getDataSetByShortName( EquipmentAttributeValue.FACILITY_MANAGEMENT_DATA_SET_SHORT_NAME ) );
        DataSet dataSet = dataSets.isEmpty() ? null : dataSets.get( 0 );
        
        PeriodType periodType = PeriodType.getPeriodTypeByName( "Yearly" );
        
        if ( dataSet == null )
        {
            dataSet = new DataSet();
            
            dataSet.setName( EquipmentAttributeValue.FACILITY_MANAGEMENT_DATA_SET_NAME  );
            dataSet.setShortName( EquipmentAttributeValue.FACILITY_MANAGEMENT_DATA_SET_SHORT_NAME );
            dataSet.setPeriodType( periodType );
            dataSet.setMobile( false );
            
            dataSetService.addDataSet( dataSet );
            
        }
        
        // add Lookup Name STORAGE CAPACITY and Type CCEI_AGG_TYPE
        Lookup storageCapacity = lookupService.getLookupByName( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY );
        
        if ( storageCapacity == null )
        {
            storageCapacity = new Lookup();
            
            storageCapacity.setName( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY );
            storageCapacity.setDescription( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY );
            storageCapacity.setCode( Lookup.CCEI_AGG_TYPE );
            storageCapacity.setType( Lookup.CCEI_AGG_TYPE );
            storageCapacity.setValue( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY );

            lookupService.addLookup( storageCapacity );
        }
        
        // add Lookup Name REF WORKING STATUS BY MODEL and Type CCEI_AGG_TYPE
        Lookup refWorkingStatusByModel = lookupService.getLookupByName( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL );
        
        if ( refWorkingStatusByModel == null )
        {
            refWorkingStatusByModel = new Lookup();
            
            refWorkingStatusByModel.setName( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL );
            refWorkingStatusByModel.setDescription( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL );
            refWorkingStatusByModel.setCode( Lookup.CCEI_AGG_TYPE );
            refWorkingStatusByModel.setType( Lookup.CCEI_AGG_TYPE );
            refWorkingStatusByModel.setValue( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL );

            lookupService.addLookup( refWorkingStatusByModel );
        }
        
        // add Lookup Name REF UTILIZATION and Type CCEI_AGG_TYPE
        Lookup refUtilization = lookupService.getLookupByName( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION );
        
        if ( refUtilization == null )
        {
            refUtilization = new Lookup();
            
            refUtilization.setName( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION );
            refUtilization.setDescription( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION );
            refUtilization.setCode( Lookup.CCEI_AGG_TYPE );
            refUtilization.setType( Lookup.CCEI_AGG_TYPE );
            refUtilization.setValue( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION );

            lookupService.addLookup( refUtilization );
        }           
        
        // add Lookup Name REF WORKING STATUS BY TYPE and Type CCEI_AGG_TYPE
        Lookup refWorkingStatusByType = lookupService.getLookupByName( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE );
        
        if ( refWorkingStatusByType == null )
        {
            refWorkingStatusByType = new Lookup();
            
            refWorkingStatusByType.setName( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE );
            refWorkingStatusByType.setDescription( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE );
            refWorkingStatusByType.setCode( Lookup.CCEI_AGG_TYPE );
            refWorkingStatusByType.setType( Lookup.CCEI_AGG_TYPE );
            refWorkingStatusByType.setValue( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE );

            lookupService.addLookup( refWorkingStatusByType );
        }       
        
        // add Lookup Name REF TEMP ALARMS and Type CCEI_AGG_TYPE
        Lookup refTempAlarm = lookupService.getLookupByName( Lookup.CCEI_AGG_TYPE_REF_TEMP_ALARMS );
        
        if ( refTempAlarm == null )
        {
            refTempAlarm = new Lookup();
            
            refTempAlarm.setName( Lookup.CCEI_AGG_TYPE_REF_TEMP_ALARMS );
            refTempAlarm.setDescription( Lookup.CCEI_AGG_TYPE_REF_TEMP_ALARMS );
            refTempAlarm.setCode( Lookup.CCEI_AGG_TYPE );
            refTempAlarm.setType( Lookup.CCEI_AGG_TYPE );
            refTempAlarm.setValue( Lookup.CCEI_AGG_TYPE_REF_TEMP_ALARMS );

            lookupService.addLookup( refTempAlarm );
        }
        
        // add Lookup Name MODEL_MODELTYPEATTRIBUTE
        Lookup modelTypeAttribute = lookupService.getLookupByName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
        
        if ( modelTypeAttribute == null )
        {
            modelTypeAttribute = new Lookup();
            
            modelTypeAttribute.setName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
            modelTypeAttribute.setDescription( "REF WORKING STATUS BY MODEL" );
            modelTypeAttribute.setCode( "REF WORKING STATUS BY MODEL" );
            modelTypeAttribute.setType( "REF WORKING STATUS" );

            lookupService.addLookup( modelTypeAttribute );
        }
        
        // add Lookup Name UTILIZATION_OPTIONSET
        Lookup utilizationOptionSet = lookupService.getLookupByName( Lookup.CCEI_UTILIZATION_OPTIONSET );
        
        if ( utilizationOptionSet == null )
        {
            utilizationOptionSet = new Lookup();
            
            utilizationOptionSet.setName( Lookup.CCEI_UTILIZATION_OPTIONSET );
            utilizationOptionSet.setDescription( "REF UTILIZATION OPTION SET" );
            utilizationOptionSet.setCode( "REF UTILIZATION" );
            utilizationOptionSet.setType( "REF UTILIZATION" );

            lookupService.addLookup( utilizationOptionSet );
        } 
        
        // add Lookup Name WORKING_STATUS_OPTIONSET
        Lookup workingStatusOptionSet = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
        
        if ( workingStatusOptionSet == null )
        {
            workingStatusOptionSet = new Lookup();
            
            workingStatusOptionSet.setName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
            workingStatusOptionSet.setDescription( "REF WORKING STATUS BY MODEL" );
            workingStatusOptionSet.setCode( "REF WORKING STATUS BY MODEL" );
            workingStatusOptionSet.setType( "REF WORKING STATUS" );

            lookupService.addLookup( workingStatusOptionSet );
        }               
        
        // add Lookup Name EQUIPMENTTYPE_MODELTYPEATTRIBUTE
        
        Lookup equipmentTypeModelTypeAttribute = lookupService.getLookupByName( Lookup.CCEI_EQUIPMENTTYPE_MODELTYPEATTRIBUTE );
        
        if ( equipmentTypeModelTypeAttribute == null )
        {
            equipmentTypeModelTypeAttribute = new Lookup();
            
            equipmentTypeModelTypeAttribute.setName( Lookup.CCEI_EQUIPMENTTYPE_MODELTYPEATTRIBUTE );
            equipmentTypeModelTypeAttribute.setDescription( Lookup.CCEI_EQUIPMENTTYPE_MODELTYPEATTRIBUTE );
            equipmentTypeModelTypeAttribute.setCode( "REF WORKING STATUS BY TYPE" );
            equipmentTypeModelTypeAttribute.setType( "REF WORKING STATUS BY TYPE" );

            lookupService.addLookup( equipmentTypeModelTypeAttribute );
        }            
    }
    
}
