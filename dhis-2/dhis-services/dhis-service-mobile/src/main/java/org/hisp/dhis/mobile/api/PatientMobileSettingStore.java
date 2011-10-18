package org.hisp.dhis.mobile.api;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.patient.PatientMobileSetting;

public interface PatientMobileSettingStore extends GenericStore<PatientMobileSetting>
{

    public Collection<PatientMobileSetting> getCurrentSetting();

}
