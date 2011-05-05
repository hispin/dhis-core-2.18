package org.hisp.dhis.oum.action.organisationunit;

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

import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: UpdateOrganisationUnitAction.java 1898 2006-09-22 12:06:56Z torgeilo $
 */
public class UpdateOrganisationUnitAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

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

    private Boolean active;

    public void setActive( Boolean active )
    {
        this.active = active;
    }

    private String openingDate;

    public void setOpeningDate( String openingDate )
    {
        this.openingDate = openingDate;
    }

    private String closedDate;

    public void setClosedDate( String closedDate )
    {
        this.closedDate = closedDate;
    }

    private String comment;

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    private String coordinates;

    public void setCoordinates( String coordinates )
    {
        this.coordinates = coordinates;
    }
    
    private String featureType;

    public void setFeatureType( String featureType )
    {
        this.featureType = featureType;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }
    
    private String contactPerson;

    public void setContactPerson( String contactPerson )
    {
        this.contactPerson = contactPerson;
    }

    private String address;

    public void setAddress( String address )
    {
        this.address = address;
    }

    private String email;

    public void setEmail( String email )
    {
        this.email = email;
    }

    private String phoneNumber;

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    private Collection<String> dataSets = new HashSet<String>();

    public void setDataSets( Collection<String> dataSets )
    {
        this.dataSets = dataSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        code = nullIfEmpty( code );
        comment = nullIfEmpty( comment );
        coordinates = nullIfEmpty( coordinates );
        featureType = nullIfEmpty( featureType );
        url = nullIfEmpty( url );
        
        contactPerson = nullIfEmpty( contactPerson );
        address = nullIfEmpty( address );
        email = nullIfEmpty( email );
        phoneNumber = nullIfEmpty( phoneNumber );

        Date oDate = format.parseDate( openingDate );

        Date cDate = null;

        if ( closedDate != null && closedDate.trim().length() != 0 )
        {
            cDate = format.parseDate( closedDate );
        }

        // ---------------------------------------------------------------------
        // Update organisation unit
        // ---------------------------------------------------------------------

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( id.intValue() );

        organisationUnit.setName( name );
        organisationUnit.setShortName( shortName );
        organisationUnit.setCode( code );
        organisationUnit.setActive( active.booleanValue() );
        organisationUnit.setOpeningDate( oDate );
        organisationUnit.setClosedDate( cDate );
        organisationUnit.setComment( comment );
        organisationUnit.setCoordinates( coordinates );
        organisationUnit.setUrl( url );
        organisationUnit.setFeatureType( featureType );
        organisationUnit.setContactPerson( contactPerson );
        organisationUnit.setAddress( address );
        organisationUnit.setEmail( email );
        organisationUnit.setPhoneNumber( phoneNumber );

        for ( DataSet dataSet : organisationUnit.getDataSets() ) // Remove current
        {
            dataSet.getSources().remove( organisationUnit );
        }
        
        organisationUnit.getDataSets().clear();
        
        for ( String id : dataSets ) // Add selected
        {
            DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( id ) );
            dataSet.getSources().add( organisationUnit );
            dataSetService.updateDataSet( dataSet );
        }
        
        organisationUnitService.updateOrganisationUnit( organisationUnit );

        return SUCCESS;
    }
}
