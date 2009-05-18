package org.hisp.dhis.design;

import java.util.Collection;

public interface DesignService
{
    final String ID = DesignService.class.getName();
    
    int saveDesign( Design design );
    
    void updateDesign( Design design );
    
    Design getDesign( int id );
    
    void deleteDesign( Design design );
    
    Collection<Design> getAllDesigns();
}
