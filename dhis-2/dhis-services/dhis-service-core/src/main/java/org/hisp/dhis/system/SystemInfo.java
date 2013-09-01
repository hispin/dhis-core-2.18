package org.hisp.dhis.system;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.Date;

import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.system.database.DatabaseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement( localName = "systemInfo", namespace = DxfNamespaces.DXF_2_0 )
public class SystemInfo
{
    private String contextPath;

    private String userAgent;
    
    private String version;
    
    private String revision;
    
    private Date buildTime;
    
    private Date serverDate;
    
    private String environmentVariable;

    private String javaVersion;
    
    private String javaVendor;
    
    private String osName;
    
    private String osArchitecture;
    
    private String osVersion;
    
    private String javaIoTmpDir;

    private String externalDirectory;

    private DatabaseInfo databaseInfo;

    private String javaOpts;

    private String memoryInfo;

    private int cpuCores;

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath( String contextPath )
    {
        this.contextPath = contextPath;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent( String userAgent )
    {
        this.userAgent = userAgent;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getRevision()
    {
        return revision;
    }

    public void setRevision( String revision )
    {
        this.revision = revision;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getBuildTime()
    {
        return buildTime;
    }

    public void setBuildTime( Date buildTime )
    {
        this.buildTime = buildTime;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getServerDate()
    {
        return serverDate;
    }

    public void setServerDate( Date serverDate )
    {
        this.serverDate = serverDate;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getEnvironmentVariable()
    {
        return environmentVariable;
    }

    public void setEnvironmentVariable( String environmentVariable )
    {
        this.environmentVariable = environmentVariable;
    }

    public String getJavaVersion()
    {
        return javaVersion;
    }

    public void setJavaVersion( String javaVersion )
    {
        this.javaVersion = javaVersion;
    }

    public String getJavaVendor()
    {
        return javaVendor;
    }

    public void setJavaVendor( String javaVendor )
    {
        this.javaVendor = javaVendor;
    }

    public String getOsName()
    {
        return osName;
    }

    public void setOsName( String osName )
    {
        this.osName = osName;
    }

    public String getOsArchitecture()
    {
        return osArchitecture;
    }

    public void setOsArchitecture( String osArchitecture )
    {
        this.osArchitecture = osArchitecture;
    }

    public String getOsVersion()
    {
        return osVersion;
    }

    public void setOsVersion( String osVersion )
    {
        this.osVersion = osVersion;
    }

    public String getJavaIoTmpDir()
    {
        return javaIoTmpDir;
    }

    public void setJavaIoTmpDir( String javaIoTmpDir )
    {
        this.javaIoTmpDir = javaIoTmpDir;
    }

    public String getExternalDirectory()
    {
        return externalDirectory;
    }

    public void setExternalDirectory( String externalDirectory )
    {
        this.externalDirectory = externalDirectory;
    }

    public DatabaseInfo getDatabaseInfo()
    {
        return databaseInfo;
    }

    public void setDatabaseInfo( DatabaseInfo databaseInfo )
    {
        this.databaseInfo = databaseInfo;
    }

    public String getJavaOpts()
    {
        return javaOpts;
    }

    public void setJavaOpts( String javaOpts )
    {
        this.javaOpts = javaOpts;
    }

    public String getMemoryInfo()
    {
        return memoryInfo;
    }

    public void setMemoryInfo( String memoryInfo )
    {
        this.memoryInfo = memoryInfo;
    }

    public int getCpuCores()
    {
        return cpuCores;
    }

    public void setCpuCores( int cpuCores )
    {
        this.cpuCores = cpuCores;
    }
}
