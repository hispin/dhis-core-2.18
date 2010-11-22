package org.hisp.dhis.patient;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;

public interface PatientMobileSettingStore extends GenericStore<PatientMobileSetting>
{

    public Collection<PatientMobileSetting> getCurrentSetting();

}
