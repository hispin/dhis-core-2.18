package org.hisp.dhis.web.api.service;

import org.hisp.dhis.web.api.model.ActivityPlan;

public interface IActivityPlanService {

	ActivityPlan getCurrentActivityPlan( String localeString );
	
}
