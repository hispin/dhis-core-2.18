package org.hisp.dhis.gis;

import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.deletion.DeletionHandler;

public class FeatureDeleteHandler
    extends DeletionHandler
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FeatureService featureService;

    public void setFeatureService( FeatureService featureService )
    {
        this.featureService = featureService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    protected String getClassName()
    {
        return Feature.class.getSimpleName();
    }

    @Override
    public void deleteSource( Source source )
    {
        featureService.deleteFeatureByOrganisationUnit( source.getId() );
        featureService.deleteMapFileByOrganisationUnit( source.getId() );
    }

}
