package org.hisp.dhis.dxf2.metadata;

import org.hisp.dhis.common.IdentifiableObject;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ImportUtils
{
    /**
     * @param object Object to get display name for
     * @return A usable display name
     */
    public static String getDisplayName( Object object )
    {
        if ( object == null )
        {
            return "[ object is null ]";
        }
        else if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;

            if ( identifiableObject.getName() != null && identifiableObject.getName().length() > 0 )
            {
                return identifiableObject.getName();
            }
            else if ( identifiableObject.getUid() != null && identifiableObject.getName().length() > 0 )
            {
                return identifiableObject.getUid();
            }
            else if ( identifiableObject.getCode() != null && identifiableObject.getName().length() > 0 )
            {
                return identifiableObject.getCode();
            }

        }

        return object.getClass().getName();
    }
}
