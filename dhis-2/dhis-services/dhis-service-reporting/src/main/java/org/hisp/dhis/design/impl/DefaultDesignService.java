package org.hisp.dhis.design.impl;

import java.util.Collection;

import org.hisp.dhis.design.Design;
import org.hisp.dhis.design.DesignStore;

public class DefaultDesignService
{
    private DesignStore designStore;

    public void setDesignStore( DesignStore designStore )
    {
        this.designStore = designStore;
    }

    public int saveDesign( Design design )
    {
        return designStore.saveDesign( design );
    }
    
    public void updateDesign( Design reportTemplate )
    {
        designStore.updateDesign( reportTemplate );
    }
    
    public Design getDesign( int id )
    {
        return designStore.getDesign( id );
    }
    
    public void deleteReportTemplate( Design design )
    {
        designStore.deleteDesign( design );
    }
    
    public Collection<Design> getAllDesigns()
    {
        return designStore.getAllDesigns();
    }
}
