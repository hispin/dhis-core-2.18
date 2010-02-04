package org.hisp.dhis.system.objectmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.amplecode.quick.mapper.RowMapper;
import org.hisp.dhis.dataelement.Operand;

public class OperandMapper
    implements RowMapper<Operand>
{
    @Override
    public Operand mapRow( ResultSet resultSet )
        throws SQLException
    {
        Operand operand = new Operand(
            resultSet.getInt( 1 ),
            resultSet.getInt( 2 ),
            resultSet.getString( 3 ) );
        
        return operand;
    }
}
