package org.hisp.dhis.web.api.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSetList
    extends Model implements DataStreamSerializable
{
    private List<DataSet> addedDataSets;

    private List<DataSet> deletedDataSets;

    private List<DataSet> modifiedDataSets;
    
    private List<DataSet> currentDataSets;

    public DataSetList()
    {
    }

    public List<DataSet> getAddedDataSets()
    {
        return addedDataSets;
    }

    public void setAddedDataSets( List<DataSet> addedDataSets )
    {
        this.addedDataSets = addedDataSets;
    }

    public List<DataSet> getDeletedDataSets()
    {
        return deletedDataSets;
    }

    public void setDeletedDataSets( List<DataSet> deletedDataSets )
    {
        this.deletedDataSets = deletedDataSets;
    }

    public List<DataSet> getModifiedDataSets()
    {
        return modifiedDataSets;
    }

    public void setModifiedDataSets( List<DataSet> modifiedDataSets )
    {
        this.modifiedDataSets = modifiedDataSets;
    }

    public List<DataSet> getCurrentDataSets()
    {
        return currentDataSets;
    }

    public void setCurrentDataSets( List<DataSet> currentDataSets )
    {
        this.currentDataSets = currentDataSets;
    }

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        if ( addedDataSets != null )
        {
            dout.writeInt( addedDataSets.size() );
            for ( DataSet dataSet : addedDataSets )
            {
                dataSet.serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( deletedDataSets != null )
        {
            dout.writeInt( deletedDataSets.size() );
            for ( DataSet dataSet : deletedDataSets )
            {
                dataSet.serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( modifiedDataSets != null )
        {
            dout.writeInt( modifiedDataSets.size() );
            for ( DataSet dataSet : modifiedDataSets )
            {
                dataSet.serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( currentDataSets != null )
        {
            dout.writeInt( currentDataSets.size() );
            for ( DataSet dataSet : currentDataSets )
            {
                dataSet.serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
    }
    
    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        int temp = 0;
        temp = dataInputStream.readInt();
        if(temp > 0){
            addedDataSets = new ArrayList<DataSet>();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                addedDataSets.add( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            deletedDataSets = new ArrayList<DataSet>();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                deletedDataSets.add( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            modifiedDataSets = new ArrayList<DataSet>();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                modifiedDataSets.add( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            currentDataSets = new ArrayList<DataSet>();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                currentDataSets.add( dataSet );
            }
        }
    }

}
