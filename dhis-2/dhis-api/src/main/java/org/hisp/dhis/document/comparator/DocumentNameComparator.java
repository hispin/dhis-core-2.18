package org.hisp.dhis.document.comparator;

import java.util.Comparator;

import org.hisp.dhis.document.Document;

public class DocumentNameComparator
    implements Comparator<Document>
{
    public int compare( Document object1, Document object2 )
    {
        return object1.getName().compareToIgnoreCase( object2.getName() );
    }
}
