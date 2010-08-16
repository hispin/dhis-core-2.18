/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.cbhis.db;

import javax.microedition.rms.RecordComparator;
import org.hisp.dhis.cbhis.model.AbstractModel;

/**
 *
 * @author abyotag_adm
 */
public class AbstractRecordComparator implements RecordComparator
{

    public final static int SORT_BY_ID = 0;
    public final static int SORT_BY_NAME = 1;

    private int sortOrder = -1;

    public AbstractRecordComparator(int sortOrder)
    {
        switch (sortOrder) {
            case SORT_BY_ID:
            case SORT_BY_NAME:
                this.sortOrder = sortOrder;
                break;
            default:
                this.sortOrder = SORT_BY_ID;
                break;
        }
    }

    public int compare(byte[] rec1, byte[] rec2)
    {
        AbstractModel comp1 = AbstractModel.recordToAbstractModel(rec1);
        AbstractModel comp2 = AbstractModel.recordToAbstractModel(rec2);

        if (sortOrder == SORT_BY_NAME) {

            String name1 = comp1.getName().toLowerCase();
            String name2 = comp2.getName().toLowerCase();

            int result = name1.compareTo(name2);
            if (result == 0)
                return RecordComparator.EQUIVALENT;
            else if (result < 0)
                return RecordComparator.PRECEDES;
            else
                return RecordComparator.FOLLOWS;
        }
        else
        {
            if (comp1.getId() == comp2.getId())
                return RecordComparator.EQUIVALENT;
            else if (comp1.getId() < comp2.getId())
                return RecordComparator.PRECEDES;
            else
                return RecordComparator.FOLLOWS;
        }
    }
}