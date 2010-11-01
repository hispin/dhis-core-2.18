package org.hisp.dhis.web.api.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlType(propOrder= {"id", "name", "periodType", "dataElements"})
public class DataSet extends AbstractModel {
	
	private String periodType;
	
//	@XmlElementWrapper( name = "des" )
//	@XmlElement(name = "de")
	private List<Section> sections;
//	private List<DataElement> dataElements;	
	
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	
	public String getPeriodType() {
		return periodType;
	}	
	
	
//	public List<DataElement> getDataElements() {
//		return dataElements;
//	}
//
//	public void setDataElements(List<DataElement> dataElements) {
//		this.dataElements = dataElements;
//	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

//	public byte[] serialize() throws IOException
//    {
//        ByteArrayOutputStream bout = new ByteArrayOutputStream();
//        DataOutputStream dout = new DataOutputStream(bout);       
//
//        dout.writeInt(this.getId());
//        dout.writeUTF(this.getName());
//        dout.writeUTF(this.getPeriodType());
//        dout.writeInt(dataElements.size());
//
//        for(int i=0; i<dataElements.size(); i++)
//        {
//            DataElement de = (DataElement)dataElements.get(i);
//            dout.writeInt( de.getId() );
//            dout.writeUTF( de.getName() );
//            dout.writeUTF( de.getType() );
//        }
//
//        return bout.toByteArray();
//    }

//    public void deSerialize(byte[] data) throws IOException
//    {
//        ByteArrayInputStream bin = new ByteArrayInputStream(data);
//        DataInputStream din = new DataInputStream(bin);
//
//        this.setId( din.readInt() ) ;
//        this.setName( din.readUTF() );
//        this.setPeriodType( din.readUTF() ) ;
//
//        int size = din.readInt();
//
//        for(int i=0; i<size; i++)
//        {
//            DataElement de = new DataElement();
//            de.setId( din.readInt() );
//            de.setName( din.readUTF() );
//            de.setType( din.readUTF() );
//            this.dataElements.add(de);
//        }
//    }
    
    public void serialize( OutputStream out ) throws IOException
    {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);       

        dout.writeInt(this.getId());
        dout.writeUTF(this.getName());
        dout.writeUTF(this.getPeriodType());
        
        if(this.sections == null){
        	dout.writeInt(0);
        }else{
        	dout.writeInt(this.sections.size());
        	for(Section section : this.sections){
            	section.serialize(dout);
            }
        }
        bout.flush();
        bout.writeTo(out);
        
        
//        dout.writeInt(dataElements.size());

//        for(int i=0; i<dataElements.size(); i++)
//        {
//            DataElement de = (DataElement)dataElements.get(i);
//            dout.writeInt( de.getId() );
//            dout.writeUTF( de.getName() );
//            dout.writeUTF( de.getType() );
//        }       
        
        
    	
    }    
}


