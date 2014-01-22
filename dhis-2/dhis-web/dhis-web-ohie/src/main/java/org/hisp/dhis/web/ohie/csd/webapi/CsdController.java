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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.ohie.common.domain.soap.Envelope;
import org.hisp.dhis.web.ohie.common.domain.soap.Fault;
import org.hisp.dhis.web.ohie.common.domain.wsa.RelatesTo;
import org.hisp.dhis.web.ohie.common.exception.SoapException;
import org.hisp.dhis.web.ohie.csd.domain.Address;
import org.hisp.dhis.web.ohie.csd.domain.AddressLine;
import org.hisp.dhis.web.ohie.csd.domain.CodedType;
import org.hisp.dhis.web.ohie.csd.domain.CommonName;
import org.hisp.dhis.web.ohie.csd.domain.Contact;
import org.hisp.dhis.web.ohie.csd.domain.Csd;
import org.hisp.dhis.web.ohie.csd.domain.Facility;
import org.hisp.dhis.web.ohie.csd.domain.Geocode;
import org.hisp.dhis.web.ohie.csd.domain.GetModificationsResponse;
import org.hisp.dhis.web.ohie.csd.domain.Name;
import org.hisp.dhis.web.ohie.csd.domain.Organization;
import org.hisp.dhis.web.ohie.csd.domain.OtherID;
import org.hisp.dhis.web.ohie.csd.domain.Person;
import org.hisp.dhis.web.ohie.csd.domain.Record;
import org.hisp.dhis.web.ohie.csd.domain.Service;
import org.hisp.dhis.web.ohie.csd.exception.MissingGetDirectoryModificationsRequestException;
import org.hisp.dhis.web.ohie.csd.exception.MissingGetModificationsRequestException;
import org.hisp.dhis.web.ohie.csd.exception.MissingLastModifiedException;
import org.hisp.dhis.web.ohie.fred.webapi.v1.utils.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/csd" )
public class CsdController
{
    private static String SOAP_CONTENT_TYPE = "application/soap+xml";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private static Marshaller marshaller;

    private static Unmarshaller unmarshaller;

    static
    {
        try
        {
            Class[] classes = new Class[]{
                Envelope.class
            };

            // TODO: switch Eclipse MOXy?
            JAXBContext jaxbContext = JAXBContext.newInstance( classes );

            marshaller = jaxbContext.createMarshaller();
            unmarshaller = jaxbContext.createUnmarshaller();
        }
        catch ( JAXBException ex )
        {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE )
    public void careServicesRequest( HttpServletRequest request, HttpServletResponse response ) throws IOException, JAXBException
    {
        Object o = unmarshaller.unmarshal( request.getInputStream() );
        Envelope env = (Envelope) o;

        validateRequest( env );

        List<OrganisationUnit> organisationUnits = getOrganisationUnits( env );

        Csd csd = createCsd( organisationUnits );
        Envelope envelope = createResponse( csd, env.getHeader().getMessageID().getValue() );

        response.setContentType( SOAP_CONTENT_TYPE );
        marshaller.marshal( envelope, response.getOutputStream() );
    }

    @ExceptionHandler
    public void soapError( SoapException ex, HttpServletResponse response ) throws JAXBException, IOException
    {
        Envelope envelope = new Envelope();
        envelope.setHeader( null );
        envelope.getBody().setFault( new Fault() );
        envelope.getBody().getFault().getCode().getValue().setValue( ex.getFaultCode() );
        envelope.getBody().getFault().getReason().getText().setValue( ex.getMessage() );

        response.setContentType( SOAP_CONTENT_TYPE );
        marshaller.marshal( envelope, response.getOutputStream() );
    }


    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void validateRequest( Envelope envelope )
    {
        try
        {
            if ( !"urn:ihe:iti:csd:2013:GetDirectoryModificationsRequest".equals(
                envelope.getHeader().getAction().getValue() ) )
            {
                throw new MissingGetDirectoryModificationsRequestException();
            }
        }
        catch ( NullPointerException ex )
        {
            throw new SoapException();
        }

        try
        {
            if ( envelope.getBody().getGetModificationsRequest() == null )
            {
                throw new MissingGetModificationsRequestException();
            }
        }
        catch ( NullPointerException ex )
        {
            throw new SoapException();
        }

        try
        {
            if ( envelope.getBody().getGetModificationsRequest().getLastModified() == null )
            {
                throw new MissingLastModifiedException();
            }
        }
        catch ( NullPointerException ex )
        {
            throw new SoapException();
        }
    }

    private List<OrganisationUnit> getOrganisationUnits( Envelope envelope ) throws MissingGetModificationsRequestException
    {
        Date lastModified = envelope.getBody().getGetModificationsRequest().getLastModified();

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
            facility.setOid( "No oid, please provide facility_oid attribute value" );

            for ( AttributeValue attributeValue : organisationUnit.getAttributeValues() )
            {
                if ( attributeValue.getAttribute().getName().equals( "facility_oid" ) )
                {
                    facility.setOid( attributeValue.getValue() );
                    break;
                }
            }

            facility.getOtherID().add( new OtherID( organisationUnit.getUid(), "dhis2-uid" ) );
            
            if (organisationUnit.getCode() != null)
            {
                facility.getOtherID().add( new OtherID( organisationUnit.getCode(), "dhis2-code" ) );                
            }
            
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
                codedType.setCode( organisationUnitGroup.getCode() );
                
                codedType.setCodingSchema("Unknown" );                
                for ( AttributeValue attributeValue : organisationUnit.getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getName().equals( "code_system" ) )
                    {
                        codedType.setCodingSchema( attributeValue.getValue() );
                        break;
                    }
                }

                codedType.setValue( organisationUnitGroup.getDisplayName() );

                facility.getCodedTypes().add( codedType );
            }

            Organization organization = new Organization( "No oid, please provide organisation_oid attribute value." );
            facility.getOrganizations().add( organization );

            for ( DataSet dataSet : organisationUnit.getDataSets() )
            {
                if ( dataSet.getCode() == null )
                {
                    continue;
                }

                Service service = new Service();
                service.setOid( "No oid, please provide service_oid attribute value." );

                for ( AttributeValue attributeValue : dataSet.getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getName().equals( "service_oid" ) )
                    {
                        service.setOid( attributeValue.getValue() );
                        break;
                    }
                }

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

            Map<String, List<AddressLine>> addressLines = Maps.newHashMap();

            for ( AttributeValue attributeValue : organisationUnit.getAttributeValues() )
            {
                if ( attributeValue.getAttribute().getName().startsWith( "Address_" ) )
                {
                    String[] attributeSplit = attributeValue.getAttribute().getName().split( "_" );

                    if ( attributeSplit.length > 3 )
                    {
                        continue;
                    }

                    if ( addressLines.get( attributeSplit[1] ) == null )
                    {
                        addressLines.put( attributeSplit[1], Lists.<AddressLine>newArrayList() );
                    }

                    AddressLine addressLine = new AddressLine();
                    addressLine.setComponent( attributeSplit[2] );
                    addressLine.setValue( attributeValue.getValue() );

                    addressLines.get( attributeSplit[1] ).add( addressLine );
                }
                else if ( attributeValue.getAttribute().getName().equals( "organisation_oid" ) )
                {
                    organization.setOid( attributeValue.getValue() );
                }
            }

            for ( String key : addressLines.keySet() )
            {
                Address address = new Address( key );
                address.setAddressLines( addressLines.get( key ) );

                facility.getAddresses().add( address );
            }

            csd.getFacilityDirectory().getFacilities().add( facility );
        }

        return csd;
    }

}
