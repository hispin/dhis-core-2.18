package org.hisp.dhis.dataset.action;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: AddDataSetAction.java 6255 2008-11-10 16:01:24Z larshelg $
 */
public class AddDataSetAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }
    
    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }    

    private String frequencySelect;

    public void setFrequencySelect( String frequencySelect )
    {
        this.frequencySelect = frequencySelect;
    }

    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }
    
    private boolean mobile;
    
    public void setMobile(boolean mobile) 
    {
		this.mobile = mobile;
	}

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------    

	public String execute()
        throws Exception
    {
    	// ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        if ( shortName != null && shortName.trim().length() == 0 )
        {
            shortName = null;
        }

        if ( code != null && code.trim().length() == 0 )
        {
            code = null;
        }               
        
        PeriodType periodType = periodService.getPeriodTypeByName( frequencySelect );

        DataSet dataSet = new DataSet( name, shortName, code, periodType );
        
        dataSet.setMobile( mobile );
        

        for ( String id : selectedList )
        {
            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( id ) );

            dataSet.getDataElements().add( dataElement );
        }

        dataSetService.addDataSet( dataSet );

        assignDataSetToUserRole( dataSet );
        
        return SUCCESS;
    }
    
    private void assignDataSetToUserRole( DataSet dataSet )
    {
        User currentUser = currentUserService.getCurrentUser();
        
        if ( !currentUserService.currentUserIsSuper() && currentUser != null )
        {
            UserCredentials userCredentials = userStore.getUserCredentials( currentUser );       
            
            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {                
                userAuthorityGroup.getDataSets().add( dataSet );
                
                userStore.updateUserAuthorityGroup( userAuthorityGroup );
            }
        }
    }
}
