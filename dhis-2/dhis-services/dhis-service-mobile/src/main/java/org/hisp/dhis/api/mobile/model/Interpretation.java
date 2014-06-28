package org.hisp.dhis.api.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Interpretation
    implements DataStreamSerializable

{
    private int id;

    private String text;

    private Collection<InterpretationComment> inComments;

    private List<InterpretationComment> interCommentList = new ArrayList<InterpretationComment>();

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public Collection<InterpretationComment> getInComments()
    {
        return inComments;
    }

    public void setInComments( Collection<InterpretationComment> inComments )
    {
        this.inComments = inComments;
    }

    public List<InterpretationComment> getInterCommentList()
    {
        return interCommentList;
    }

    public void setInterCommentList( List<InterpretationComment> interCommentList )
    {
        this.interCommentList = interCommentList;
    }

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( this.getId() );
        dout.writeUTF( this.getText() );

        if ( inComments == null )
        {
            dout.writeInt( 0 );
        }
        else
        {
            dout.writeInt( inComments.size() );
            for ( InterpretationComment interpretation : inComments )
            {
                interpretation.serialize( dout );
            }
        }

    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        id = dataInputStream.readInt();
        text = dataInputStream.readUTF();

        int interCommentSize = dataInputStream.readInt();

        for ( int i = 0; i < interCommentSize; i++ )
        {
            InterpretationComment interComment = new InterpretationComment();
            interComment.deSerialize( dataInputStream );
            interCommentList.add( interComment );
        }

    }

    @Override
    public void serializeVersion2_8( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void serializeVersion2_9( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

}
