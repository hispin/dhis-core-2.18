package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2012, University of Oslo
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


/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ImportOptions
{
    private boolean dryRun = false;

    private IdScheme idScheme = IdScheme.getDefaultIdScheme();

    private ImportStrategy importStrategy;

    private static ImportOptions defaultImportOptions = new ImportOptions( IdScheme.getDefaultIdScheme(),
        ImportStrategy.getDefaultImportStrategy() );

    public static ImportOptions getDefaultImportOptions()
    {
        return defaultImportOptions;
    }

    public ImportOptions()
    {
        this.idScheme = IdScheme.getDefaultIdScheme();
        this.importStrategy = ImportStrategy.getDefaultImportStrategy();
    }

    public ImportOptions( IdScheme idScheme, ImportStrategy importStrategy )
    {
        this.idScheme = idScheme;
        this.importStrategy = importStrategy;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public void setDryRun( boolean dryRun )
    {
        this.dryRun = dryRun;
    }

    public IdScheme getIdScheme()
    {
        return idScheme;
    }

    public void setIdScheme( IdScheme idScheme )
    {
        this.idScheme = idScheme;
    }

    public ImportStrategy getImportStrategy()
    {
        return importStrategy;
    }

    public void setImportStrategy( ImportStrategy importStrategy )
    {
        this.importStrategy = importStrategy;
    }
}
