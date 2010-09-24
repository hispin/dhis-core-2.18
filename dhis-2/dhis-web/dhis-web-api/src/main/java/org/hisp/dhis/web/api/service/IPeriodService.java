/**
 * 
 */
package org.hisp.dhis.web.api.service;

import org.hisp.dhis.period.Period;

/**
 * @author abyotag_adm
 *
 */
public interface IPeriodService {
	
	Period getPeriod(String periodName, org.hisp.dhis.dataset.DataSet dataSet);

}
