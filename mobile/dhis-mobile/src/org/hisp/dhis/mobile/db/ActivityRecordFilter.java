package org.hisp.dhis.mobile.db;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.rms.RecordFilter;

/**
 * @author Tran Ng Minh Luan
 *
 */
public class ActivityRecordFilter implements RecordFilter{
        public static final String filterByOrgUnitId = "FILTER_BY_ORGUNIT";
        
        public static final String filterByProgStageInstId = "FILTER_BY_PROGSTAGEINSTID";
        
        public static final String filterByStatusComplete = "FILTER_BY_STATUS_COMPLETE";
        
        public static final String filterByStatusIncomplete = "FILTER_BY_STATUS_INCOMPLETE";
    
        private int orgUnitId;
        
        private int progStageInstId;
        
        private String filter;
        
        public ActivityRecordFilter(String filter) {
                this.filter = filter;
        }

        
        
        public int getProgStageInstId()
        {
            return progStageInstId;
        }
        
        public void setProgStageInstId( int progStageInstId )
        {
            this.progStageInstId = progStageInstId;
        }
        
        public int getOrgUnitId() {
                return orgUnitId;
        }

        public void setOrgUnitId(int orgUnitId) {
                this.orgUnitId = orgUnitId;
        }

        public boolean matches(byte[] candidate){
                if(this.filter.equals( filterByOrgUnitId )){
                    ByteArrayInputStream bis = new ByteArrayInputStream(candidate);
                    DataInputStream dis = new DataInputStream(bis);
                    try{
                    if(dis.readInt() == this.orgUnitId){
                            return true;
                    }else{
                            return false;
                    }
                    }catch(Exception e){
                            System.out.println("Activity Filter get exception");
                            return false;
                    }
                    finally{
                            try {
                                    bis.close();
                                    dis.close();
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                            
                    }
                }else if(this.filter.equals( filterByStatusComplete )){
                    ByteArrayInputStream bis = new ByteArrayInputStream(candidate);
                    DataInputStream dis = new DataInputStream(bis);
                    
                    try{
                        dis.readInt();
                        dis.readUTF();
                        dis.readUTF();
                        dis.readUTF();
                        dis.readLong();
                        dis.readInt();
                        dis.readInt();
                    if(dis.readBoolean() == true){
                            return true;
                    }else{
                            return false;
                    }
                    }catch(Exception e){
                            System.out.println("Activity Filter get exception");
                            return false;
                    }
                    finally{
                            try {
                                    bis.close();
                                    dis.close();
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                            
                    }
                }else if(this.filter.equals( filterByStatusIncomplete )){
                    ByteArrayInputStream bis = new ByteArrayInputStream(candidate);
                    DataInputStream dis = new DataInputStream(bis);
                    try{
                        dis.readInt();
                        dis.readUTF();
                        dis.readUTF();
                        dis.readUTF();
                        dis.readLong();
                        dis.readInt();
                        dis.readInt();
                    if(dis.readBoolean() == false){
                            return true;
                    }else{
                            return false;
                    }
                    }catch(Exception e){
                            System.out.println("Activity Filter get exception");
                            return false;
                    }
                    finally{
                            try {
                                    bis.close();
                                    dis.close();
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                            
                    }
                }else if(this.filter.equals( filterByProgStageInstId )){
                    ByteArrayInputStream bis = new ByteArrayInputStream(candidate);
                    DataInputStream dis = new DataInputStream(bis);
                    try{
                        dis.readInt();
                        dis.readUTF();
                        dis.readUTF();
                        dis.readUTF();
                        dis.readLong();
                    if(dis.readInt() == this.progStageInstId){
                            return true;
                    }else{
                            return false;
                    }
                    }catch(Exception e){
                            System.out.println("Activity Filter get exception");
                            return false;
                    }
                    finally{
                            try {
                                    bis.close();
                                    dis.close();
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                            
                    }
                }
                return false;
                
        }
}
