package org.hisp.dhis.patient.action.mobilesetting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientMobileSettingService;

import com.opensymphony.xwork2.Action;

public class UpdateMobileSettingAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private PatientAttributeService patientAttributeService;

    public PatientAttributeService getPatientAttributeService()
    {
        return patientAttributeService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private PatientMobileSettingService patientMobileSettingService;

    public PatientMobileSettingService getPatientMobileSettingService()
    {
        return patientMobileSettingService;
    }

    public void setPatientMobileSettingService( PatientMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    @Override
    public String execute()
        throws Exception
    {
        if(selectedList.size() > 0){
            PatientMobileSetting setting;
            if(patientMobileSettingService.getCurrentSetting().size()>0){
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                Set<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.updatePatientMobileSetting( setting );
            }else{
                setting = new PatientMobileSetting();
                Set<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.savePatientMobileSetting( setting );
            }
        }else{
            PatientMobileSetting setting;
            if(patientMobileSettingService.getCurrentSetting().size()>0){
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                Set<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.updatePatientMobileSetting( setting );
            }else{
                setting = new PatientMobileSetting();
                Set<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.savePatientMobileSetting( setting );
            }
        }
        return SUCCESS;
    }
    
    private void fillValues(Set<PatientAttribute> attributes){
        for(String id : selectedList){
            attributes.add( patientAttributeService.getPatientAttribute( Integer.parseInt( id )) );
        }
    }

}
