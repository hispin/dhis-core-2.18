package org.hisp.dhis.patient;

import java.util.Collection;

public interface PatientMobileSettingService
{
    public int savePatientMobileSetting(PatientMobileSetting setting);
    public void updatePatientMobileSetting(PatientMobileSetting setting);
    public Collection<PatientMobileSetting> getCurrentSetting();    
}
