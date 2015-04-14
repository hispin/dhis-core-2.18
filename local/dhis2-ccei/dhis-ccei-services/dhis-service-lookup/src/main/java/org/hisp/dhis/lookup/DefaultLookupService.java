package org.hisp.dhis.lookup;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultLookupService implements LookupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private LookupStore lookupStore;

    public void setLookupStore( LookupStore lookupStore )
    {
        this.lookupStore = lookupStore;
    }

    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    @Override
    public int addLookup( Lookup lookup )
    {
        return lookupStore.save( lookup );
    }

    @Override
    public void updateLookup( Lookup lookup )
    {
        lookupStore.update( lookup );
    }

    @Override
    public void deleteLookup( Lookup lookup )
    {
        lookupStore.delete( lookup );
    }

    @Override
    public Lookup getLookup( int id )
    {
        return lookupStore.get( id );
    }

    @Override
    public Lookup getLookupByName( String name )
    {
        return lookupStore.getByName( name );
    }

    @Override
    public Collection<Lookup> getAllLookupsByType( String type )
    {
        return lookupStore.getAllLookupsByType( type );
    }

    @Override
    public Collection<Lookup> getAllLookups()
    {
        return lookupStore.getAll( );
    }
    

    @Override
    public Collection<Lookup> getAllLookupsOrderByName()
    {
        return lookupStore.getAllLookupsOrderByName();
    }
    
    
    // Search lookup by name
    public void searchLookupByName( List<Lookup> lookups, String key )
    {
        Iterator<Lookup> iterator = lookups.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                iterator.remove();
            }
        }
    }
    
}
