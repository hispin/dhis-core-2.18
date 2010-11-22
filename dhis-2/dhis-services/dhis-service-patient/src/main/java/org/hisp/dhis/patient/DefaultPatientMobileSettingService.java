package org.hisp.dhis.patient;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultPatientMobileSettingService implements PatientMobileSettingService
{
    
    private PatientMobileSettingStore patientMobileSettingStore;

    public void setPatientMobileSettingStore( PatientMobileSettingStore patientMobileSettingStore )
    {
        this.patientMobileSettingStore = patientMobileSettingStore;
    }

    @Override
    public int savePatientMobileSetting( PatientMobileSetting setting )
    {
        return patientMobileSettingStore.save(setting);
    }

    @Override
    public void updatePatientMobileSetting( PatientMobileSetting setting )
    {
        patientMobileSettingStore.update(setting);
    }
    
    @Override
    public Collection<PatientMobileSetting> getCurrentSetting(){
        return patientMobileSettingStore.getCurrentSetting();
    }

}
