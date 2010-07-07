package org.hisp.dhis.gis;

import java.util.List;
import java.util.Set;

import org.hisp.dhis.system.deletion.DeletionHandler;

public class LegendDeleteHandler extends DeletionHandler
{
    
    private LegendService legendService;
    
    public void setLegendService( LegendService legendService )
    {
        this.legendService = legendService;
    }

    @Override
    protected String getClassName()
    {
        return Legend.class.getSimpleName();
    }
    
    public boolean allowDeleteLegend( Legend legend )
    {
        Set<LegendSet> legendSets = legendService.getAllLegendSet();
        if( legendSets != null && legendSets.size() > 0 )
        {
            for( LegendSet legendSet : legendSets )
            {
                List<Legend> legends = legendSet.getLegends();
              
                if( legends != null && legends.size() > 0 )
                {
                    for( Legend l : legends )
                    {
                        if( l.getId()  == legend.getId() ) 
                        {
                            return false;
                        }
                    }
                }
              
            }
        }
        return true;
    }
    
     
    
    
    
}
