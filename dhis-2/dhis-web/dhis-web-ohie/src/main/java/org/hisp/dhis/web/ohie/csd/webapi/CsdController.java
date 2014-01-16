package org.hisp.dhis.web.ohie.csd.webapi;

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

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.ohie.csd.domain.Envelope;
import org.hisp.dhis.web.ohie.csd.domain.GetModificationsResponse;
import org.hisp.dhis.web.ohie.csd.domain.csd.CodedType;
import org.hisp.dhis.web.ohie.csd.domain.csd.CommonName;
import org.hisp.dhis.web.ohie.csd.domain.csd.Contact;
import org.hisp.dhis.web.ohie.csd.domain.csd.Csd;
import org.hisp.dhis.web.ohie.csd.domain.csd.Facility;
import org.hisp.dhis.web.ohie.csd.domain.csd.Geocode;
import org.hisp.dhis.web.ohie.csd.domain.csd.Name;
import org.hisp.dhis.web.ohie.csd.domain.csd.Organization;
import org.hisp.dhis.web.ohie.csd.domain.csd.OtherID;
import org.hisp.dhis.web.ohie.csd.domain.csd.Person;
import org.hisp.dhis.web.ohie.csd.domain.csd.Record;
import org.hisp.dhis.web.ohie.csd.domain.csd.Service;
import org.hisp.dhis.web.ohie.csd.domain.wsa.RelatesTo;
import org.hisp.dhis.web.ohie.fred.webapi.v1.utils.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/csd" )
public class CsdController
{
    @Autowired
    private OrganisationUnitService organisationUnitService;

    private static JAXBContext jaxbContext;

    static
    {
        try
        {
            Class[] classes = new Class[]{
                Envelope.class
            };

            // TODO: switch Eclipse MOXy?
            jaxbContext = JAXBContext.newInstance( classes );
        }
        catch ( JAXBException ex )
        {
            ex.printStackTrace();
        }
    }

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE )
    public void careServicesRequest( HttpServletRequest request, HttpServletResponse response ) throws IOException, JAXBException
    {
        Object o = jaxbContext.createUnmarshaller().unmarshal( request.getInputStream() );
        Envelope env = (Envelope) o;

        List<OrganisationUnit> organisationUnits = getOrganisationUnits( env );

        Csd csd = createCsd( organisationUnits );
        Envelope envelope = createResponse( csd, env.getHeader().getMessageID().getValue() );

        response.setContentType( "application/soap+xml" );
        jaxbContext.createMarshaller().marshal( envelope, response.getOutputStream() );
    }

    private List<OrganisationUnit> getOrganisationUnits( Envelope envelope )
    {
        Date lastModified;

        try
        {
            lastModified = envelope.getBody().getGetModificationsRequest().getLastModified();
        }
        catch ( NullPointerException ex )
        {
            throw new HttpClientErrorException( HttpStatus.BAD_REQUEST );
        }

        return new ArrayList<OrganisationUnit>(
            organisationUnitService.getAllOrganisationUnitsByLastUpdated( lastModified ) );
    }

    public Envelope createResponse( Csd csd, String messageID )
    {
        Envelope envelope = new Envelope();

        envelope.getHeader().getAction().setValue( "urn:ihe:iti:csd:2013:GetDirectoryModificationsResponse" );
        envelope.getHeader().setRelatesTo( new RelatesTo( messageID ) );

        GetModificationsResponse response = new GetModificationsResponse( csd );
        envelope.getBody().setGetModificationsResponse( response );

        return envelope;
    }

    private Csd createCsd( Iterable<OrganisationUnit> organisationUnits )
    {
        Csd csd = new Csd();
        csd.getFacilityDirectory().setFacilities( new ArrayList<Facility>() );

        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            Facility facility = new Facility();
            facility.setOid( organisationUnit.getCode() ); // TODO skip if code is null??

            facility.getOtherID().add( new OtherID( organisationUnit.getUid(), "dhis2-uid" ) );

            facility.setPrimaryName( organisationUnit.getDisplayName() );

            if ( organisationUnit.getContactPerson() != null )
            {
                Contact contact = new Contact();
                Person person = new Person();
                Name name = new Name();

                contact.setPerson( person );
                person.setName( name );

                name.getCommonNames().add( new CommonName( organisationUnit.getContactPerson() ) );

                facility.getContacts().add( contact );
            }

            for ( OrganisationUnitGroup organisationUnitGroup : organisationUnit.getGroups() )
            {
                if ( organisationUnitGroup.getCode() == null )
                {
                    continue;
                }

                CodedType codedType = new CodedType();
                codedType.setCode( organisationUnitGroup.getUid() );
                codedType.setCodingSchema( "dhis2-uid" );
                codedType.setValue( organisationUnitGroup.getDisplayName() );

                facility.getCodedTypes().add( codedType );
            }

            Organization organization = new Organization( "1.3.6.1.4.1.21367.200.99.1" );
            facility.getOrganizations().add( organization );

            for ( DataSet dataSet : organisationUnit.getDataSets() )
            {
                if ( dataSet.getCode() == null )
                {
                    continue;
                }

                Service service = new Service();
                service.setOid( dataSet.getCode() );
                service.getNames().add( new Name( new CommonName( dataSet.getDisplayName() ) ) );

                organization.getServices().add( service );
            }

            if ( OrganisationUnit.FEATURETYPE_POINT.equals( organisationUnit.getFeatureType() ) )
            {
                Geocode geocode = new Geocode();

                try
                {
                    GeoUtils.Coordinates coordinates = GeoUtils.parseCoordinates( organisationUnit.getCoordinates() );

                    geocode.setLongitude( coordinates.lng );
                    geocode.setLatitude( coordinates.lat );
                }
                catch ( NumberFormatException ignored )
                {
                }

                facility.setGeocode( geocode );
            }

            Record record = new Record();
            record.setCreated( organisationUnit.getCreated() );
            record.setUpdated( organisationUnit.getLastUpdated() );

            if ( organisationUnit.isActive() )
            {
                record.setStatus( "Active" );
            }
            else
            {
                record.setStatus( "Inactive" );
            }

            facility.setRecord( record );

            csd.getFacilityDirectory().getFacilities().add( facility );
        }

        return csd;
    }
}
