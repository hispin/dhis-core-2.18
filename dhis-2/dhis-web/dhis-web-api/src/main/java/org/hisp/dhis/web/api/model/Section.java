package org.hisp.dhis.web.api.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Tran Ng Minh Luan
 * 
 */
public class Section
    extends AbstractModel
{

    private List<DataElement> dataElements;

    public Section()
    {
    }

    public List<DataElement> getDes()
    {
        return dataElements;
    }

    public void setDes( List<DataElement> des )
    {
        this.dataElements = des;
    }

    public void serialize( OutputStream out )
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeInt( this.getId() );
        dout.writeUTF( getName() );

        if ( dataElements == null )
        {
            dout.writeInt( 0 );
        }
        else
        {
            dout.writeInt( dataElements.size() );
            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement de = (DataElement) dataElements.get( i );
                dout.writeInt( de.getId() );
                dout.writeUTF( de.getName() );
                dout.writeUTF( de.getType() );
                List<AbstractModel> cateOptCombos = de.getCategoryOptionCombos().getAbstractModels();
                if ( cateOptCombos == null || cateOptCombos.size() <= 0 )
                {
                    dout.writeInt( 0 );
                }
                else
                {
                    dout.writeInt( cateOptCombos.size() );
                    for ( AbstractModel each : cateOptCombos )
                    {
                        dout.writeInt( each.getId() );
                        dout.writeUTF( each.getName() );
                    }
                }

            }
        }
        bout.flush();
        bout.writeTo( out );
    }

}
