package org.hisp.dhis.mobile.app.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import com.opensymphony.xwork2.Action;
import java.io.File;

public class CreateMobileAppAction implements Action {

    private String antPath;
    private String antStatus;

    public String getAntPath() {
        //long startTime = System.currentTimeMillis();
        String PATH = System.getenv("PATH");
        String[] locations;
        if (getOSName().equals("win")) {
            locations = PATH.split(";");
        } else {
            locations = PATH.split(":");
        }
        for (String location : locations) {
            File folder = new File(location);
            String filePath = scanPath(getOSName(), folder);
            if (!filePath.equals("")) {
                antPath = filePath;
                break;
            }
        }
        //long endTime = System.currentTimeMillis();
        //System.out.println("OS = " + getOSName() + "; Total exec time=" + Long.toString(endTime - startTime) + "ms to find" + antPath);
        return antPath;
    }

    public void setAntPath(String path) {
        File antFolder = new File(path);
        String filePath = scanPath(getOSName(), antFolder);
        if ( !filePath.equals("")) {
            this.antPath = filePath;
        }else{
            antStatus = "Could not find ant.bat at the location you entered";
        }
    }

    public String getAntStatus(){
        return antStatus;
    }

    public void setAntStatus(String status){
        this.antStatus = status;
    }

    private String getOSName() {
        String osName;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            osName = "win";
        } else {
            osName = "nix";
        }
        return osName;
    }

    private String scanPath(String osName, File folder) {
        String filePath = new String();
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    if (osName.equals("win")) {
                        if (file.getName().equals("ant.bat")) {
                            filePath = file.getAbsolutePath();
                            break;
                        }
                    } else if (osName.equals("nix")) {
                        if (file.getName().equals("ant")) {
                            filePath = file.getAbsolutePath();
                            break;
                        }
                    }
                }
            }
        }
        return filePath;
    }

    @Override
    public String execute()
            throws Exception {

        return SUCCESS;
    }
}
