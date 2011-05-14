package org.hisp.dhis.scheduling;

import java.util.ArrayList;
import java.util.List;

public class Runnables
    implements Runnable
{
    private List<Runnable> runnables = new ArrayList<Runnable>();
    
    public void addRunnable( Runnable runnable )
    {
        this.runnables.add( runnable );
    }
    
    @Override
    public void run()
    {
        for ( Runnable runnable : runnables )
        {
            runnable.run();
        }
    }    
}
