/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlSchema( xmlns = {
    @XmlNs( prefix = "soap", namespaceURI = "http://www.w3.org/2003/05/soap-envelope" ),
    @XmlNs( prefix = "wsa", namespaceURI = "http://www.w3.org/2005/08/addressing" ),
    @XmlNs( prefix = "csd", namespaceURI = "urn:ihe:iti:csd:2013" )
} )
package org.hisp.dhis.web.ohie.csd.domain.wsa;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
