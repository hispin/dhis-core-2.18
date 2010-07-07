package org.hisp.dhis.system.objectmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hisp.dhis.organisationunit.OrganisationUnitRelationship;
import org.springframework.jdbc.core.RowMapper;

public class OrganisationUnitRelationshipRowMapper
    implements RowMapper<OrganisationUnitRelationship>
{    
    @Override
    public OrganisationUnitRelationship mapRow( ResultSet resultSet, int row )
        throws SQLException
    {
        return new OrganisationUnitRelationship( resultSet.getInt( "parentid" ), resultSet.getInt( "organisationunitid" ) );
    }
}
