package org.hisp.dhis.patient.action.mobilesetting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientMobileSettingService;

import com.opensymphony.xwork2.Action;

public class ShowMobileSettingFormAction
    implements Action
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

    private Set<PatientAttribute> patientAtts;

    public Set<PatientAttribute> getPatientAtts()
    {
        return patientAtts;
    }

    public void setPatientAtts( Set<PatientAttribute> patientAtts )
    {
        this.patientAtts = patientAtts;
    }
    
    private Collection<PatientAttribute> attributes;

    public Collection<PatientAttribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( Collection<PatientAttribute> attributes )
    {
        this.attributes = attributes;
    }

    @Override
    public String execute()
        throws Exception
    {
        //List of all attribute
        attributes = patientAttributeService.getAllPatientAttributes();
        
        Collection<PatientMobileSetting> paSettings = new HashSet<PatientMobileSetting>(
            patientMobileSettingService.getCurrentSetting() );
        if(paSettings != null){
            Iterator<PatientMobileSetting> settingsIt = paSettings.iterator();
            
            if ( settingsIt.hasNext() )
            {
                PatientMobileSetting setting = settingsIt.next(); 
                
                //Selected List
                patientAtts = setting.getPatientAttributes();
                
                //Delete object which is in "selected list"
                for(PatientAttribute attribute : patientAtts){
                    if(attributes.contains( attribute )){
                        attributes.remove( attribute );
                    }
                }
            }
            else
            {
                System.out.println( "Setting is null" );
                patientAtts = new HashSet<PatientAttribute>();
            }
        }
//        System.out.println( "List " + patientAtts + " has size: " + patientAtts.size() );
        return SUCCESS;
    }

}
