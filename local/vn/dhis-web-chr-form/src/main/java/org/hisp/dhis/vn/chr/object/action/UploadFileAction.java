package org.hisp.dhis.vn.chr.object.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.opensymphony.xwork.Action;

public class UploadFileAction implements Action {
	
	private final String dir = "C:/";
	
	private List files = new ArrayList();	
	
	public List getFiles() {
		return files;
	}

	public void setFiles(List files) {
		this.files = files;
	}
	
	
	private File file;
    private String contentType;
    private String filename;

    public void setUpload(File file) {
       this.file = file;
    }

    public void setUploadContentType(String contentType) {
       this.contentType = contentType;
    }

    public void setUploadFileName(String filename) {
       this.filename = filename;
    }

    public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String execute()
	{
			
			FileInputStream fin = null;
			FileOutputStream fout = null;
			try {

				if (file != null)
				{
					System.out.println(file.getPath());//(doc.getPath());
					fin = new FileInputStream(file.getPath());//(doc.getPath());
					byte[] data = new byte[8192];
					int byteReads = fin.read(data);
	
					fout = new FileOutputStream(dir + filename);
	
					while (byteReads != -1) {
						fout.write(data, 0, byteReads);
						fout.flush();
						byteReads = fin.read(data);
					}
	
					fin.close();
					fout.close();
				}
				
				File myDir = new File(dir);
				if( myDir.exists() && myDir.isDirectory())
				{
					File[] listfiles = myDir.listFiles();
					for(int i=0; i < listfiles.length; i++){
						if (!listfiles[i].isDirectory() && !listfiles[i].isHidden()) 
							files.add(listfiles[i]);
					}
				}
				
				return SUCCESS;

			} catch (Exception ex) {
				ex.printStackTrace();
				return ERROR;
			}
						
						
		}

	
}
