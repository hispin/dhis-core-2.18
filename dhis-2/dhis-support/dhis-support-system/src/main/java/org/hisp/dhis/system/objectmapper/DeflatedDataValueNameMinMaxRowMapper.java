package org.hisp.dhis.system.objectmapper;

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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.amplecode.quick.mapper.RowMapper;
import org.hisp.dhis.datavalue.DeflatedDataValue;

/**
 * RowMapper which expects a result set with the following columns:
 * 
 * <ul>
 * <li>1: dataelementid</li>
 * <li>2: periodid</li>
 * <li>3: sourceid</li>
 * <li>4: categoryoptioncomboid</li>
 * <li>5: value</li>
 * <li>6: storedby</li>
 * <li>7: lastupdated</li>
 * <li>8: comment</li>
 * <li>9: followup</li>
 * <li>10: minvalue</li>
 * <li>11: maxvalue</li>
 * <li>12: dataelementname</li>
 * <li>13: periodtypename</li>
 * <li>14: startdate</li>
 * <li>15: enddate</li>
 * <li>16: sourcename</li>
 * <li>17: categoryoptioncomboname</li>
 * </ul>
 * 
 * @author Lars Helge Overland
 */
public class DeflatedDataValueNameMinMaxRowMapper
    implements RowMapper<DeflatedDataValue>
{
    public DeflatedDataValue mapRow( ResultSet resultSet )
        throws SQLException
    {
        final DeflatedDataValue value = new DeflatedDataValue();
        
        value.setDataElementId( resultSet.getInt( "dataelementid" ) );
        value.setPeriodId( resultSet.getInt( "periodid" ) );
        value.setSourceId( resultSet.getInt( "sourceid" ) );
        value.setCategoryOptionComboId( resultSet.getInt( "categoryoptioncomboid" ) );
        value.setValue( resultSet.getString( "value" ) );
        value.setStoredBy( resultSet.getString( "storedby" ) );
        value.setTimestamp( resultSet.getDate( "lastupdated" ) );
        value.setComment( resultSet.getString( "comment" ) );
        value.setFollowup( resultSet.getBoolean( "followup" ) );
        value.setMin( resultSet.getInt( "minvalue" ) );
        value.setMax( resultSet.getInt( "maxvalue" ) );
        value.setDataElementName( resultSet.getString( "dataelementname" ) );
        value.setPeriod( 
            resultSet.getString( "periodtypename" ), 
            resultSet.getString( "startdate" ),
            resultSet.getString( "enddate" ) );
        value.setSourceName( resultSet.getString( "sourcename" ) );
        value.setCategoryOptionComboName( resultSet.getString( "categoryoptioncomboname" ) );
        
        return value;
    }
}
