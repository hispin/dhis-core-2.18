package org.hisp.dhis.web.api.service;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.ActivityValue;

public interface ActivityReportingService
{

    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit, String localeString );

    public String saveActivityReport( OrganisationUnit unit, ActivityValue activityValue );

}
