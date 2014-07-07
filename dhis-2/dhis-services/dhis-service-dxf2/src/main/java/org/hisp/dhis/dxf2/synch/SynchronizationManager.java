package org.hisp.dhis.dxf2.synch;

public interface SynchronizationManager
{
    AvailabilityStatus isRemoteServerAvailable();
    
    boolean executeDataSynch();
    
    void enableDataSynch();
    
    void disableDataSynch();
}
