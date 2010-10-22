package org.hisp.dhis.aggregation.jdbc;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.amplecode.quick.mapper.ObjectMapper;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueStore;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.AggregatedMapValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.dimension.DimensionType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.objectmapper.AggregatedDataMapValueRowMapper;
import org.hisp.dhis.system.objectmapper.AggregatedDataValueRowMapper;
import org.hisp.dhis.system.objectmapper.AggregatedIndicatorMapValueRowMapper;
import org.hisp.dhis.system.objectmapper.AggregatedIndicatorValueRowMapper;
import org.hisp.dhis.system.objectmapper.DataValueRowMapper;
import org.hisp.dhis.system.objectmapper.DeflatedDataValueRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class JdbcAggregatedDataValueStore
    implements AggregatedDataValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private StatementManager statementManager;

    // -------------------------------------------------------------------------
    // AggregatedDataValue
    // -------------------------------------------------------------------------
    
    public Double getAggregatedDataValue( int dataElement, int period, int organisationUnit )
    {
        final String sql = 
            "SELECT SUM(value) " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid = " + dataElement + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit;
        
        return statementManager.getHolder().queryForDouble( sql );
    }

    public Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, int period, int organisationUnit )
    {
        final String sql =
            "SELECT value " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid = " + dataElement + " " +
            "AND categoryoptioncomboid = " + categoryOptionCombo + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit;
        
        return statementManager.getHolder().queryForDouble( sql );
    }

    public Double getAggregatedDataValue( DataElement dataElement, DimensionOption dimensionOption, Period period, OrganisationUnit organisationUnit )
    {
        if ( dimensionOption.getDimensionType().equals( DimensionType.CATEGORY ) )
        {
            String ids = getCommaDelimitedString( getIdentifiers( DataElementCategoryOptionCombo.class, dimensionOption.getDimensionOptionElements() ) );
            
            final String sql =
                "SELECT SUM(value) " +
                "FROM aggregateddatavalue " +
                "WHERE dataelementid = " + dataElement.getId() + " " +
                "AND categoryoptioncomboid IN (" + ids + ") " +
                "AND periodid = " + period.getId() + " " +
                "AND organisationunitid = " + organisationUnit.getId();

            return statementManager.getHolder().queryForDouble( sql );
        }
        
        throw new IllegalArgumentException();
    }
    
    public Collection<AggregatedDataValue> getAggregatedDataValues( int dataElementId, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<AggregatedDataValue> mapper = new ObjectMapper<AggregatedDataValue>();
        
        try
        {
            final String sql = 
                "SELECT * " +
                "FROM aggregateddatavalue " +
                "WHERE dataelementid = " + dataElementId + " " +
                "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new AggregatedDataValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated data value", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public int deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregateddatavalue " +
            "WHERE dataelementid IN ( " + getCommaDelimitedString( dataElementIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        return statementManager.getHolder().executeUpdate( sql );        
    }

    public int deleteAggregatedDataValues()
    {
        return statementManager.getHolder().executeUpdate( "DELETE FROM aggregateddatavalue" ); 
    }

    // -------------------------------------------------------------------------
    // AggregatedDataMapValue
    // -------------------------------------------------------------------------
    
    public Collection<AggregatedMapValue> getAggregatedDataMapValues( int dataElementId, int periodId, int level )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<AggregatedMapValue> mapper = new ObjectMapper<AggregatedMapValue>();
        
        try
        {
            final String sql = 
                "SELECT o.organisationunitid, o.name, a.value, a.periodid " +
                "FROM aggregateddatavalue AS a, organisationunit AS o " +
                "WHERE a.dataelementid  = " + dataElementId + " " +
                "AND a.periodid = " + periodId + " " + 
                "AND a.level = " + level + " " +
                "AND a.organisationunitid = o.organisationunitid";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new AggregatedDataMapValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated data map values", ex );
        }
        finally
        {
            holder.close();
        }
    }

    // -------------------------------------------------------------------------
    // AggregatedIndicatorValue
    // -------------------------------------------------------------------------

    public Double getAggregatedIndicatorValue( int indicator, int period, int organisationUnit )
    {
        final String sql =
            "SELECT value " +
            "FROM aggregatedindicatorvalue " +
            "WHERE indicatorid = " + indicator + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit;
        
        return statementManager.getHolder().queryForDouble( sql );
    }

    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<AggregatedIndicatorValue> mapper = new ObjectMapper<AggregatedIndicatorValue>();
        
        try
        {
            final String sql =
                "SELECT * " +
                "FROM aggregatedindicatorvalue " +
                "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new AggregatedIndicatorValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated indicator value", ex );
        }
        finally
        {
            holder.close();
        }
    }

    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> indicatorIds, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<AggregatedIndicatorValue> mapper = new ObjectMapper<AggregatedIndicatorValue>();
        
        try
        {
            final String sql =
                "SELECT * " +
                "FROM aggregatedindicatorvalue " +
                "WHERE indicatorid IN ( " + getCommaDelimitedString( indicatorIds ) + " ) " +
                "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new AggregatedIndicatorValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated indicator value", ex );
        }
        finally
        {
            holder.close();
        }
    }

    public int deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregatedindicatorvalue " +
            "WHERE indicatorid IN ( " + getCommaDelimitedString( indicatorIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        return statementManager.getHolder().executeUpdate( sql );        
    }
    
    public int deleteAggregatedIndicatorValues()
    {
        return statementManager.getHolder().executeUpdate( "DELETE FROM aggregatedindicatorvalue" );
    }

    // -------------------------------------------------------------------------
    // AggregatedIndicatorMapValue
    // -------------------------------------------------------------------------

    public Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, int level )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<AggregatedMapValue> mapper = new ObjectMapper<AggregatedMapValue>();
        
        try
        {
            final String sql = 
                "SELECT o.organisationunitid, o.name, a.value, a.periodid, a.factor, a.numeratorvalue, a.denominatorvalue " +
                "FROM aggregatedindicatorvalue AS a, organisationunit AS o " +
                "WHERE a.indicatorid  = " + indicatorId + " " +
                "AND a.periodid = " + periodId + " " + 
                "AND a.level = " + level + " " +
                "AND a.organisationunitid = o.organisationunitid";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new AggregatedIndicatorMapValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated indicator map values", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, int level, int organisationUnitId )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<AggregatedMapValue> mapper = new ObjectMapper<AggregatedMapValue>();
        
        try
        {
            final String sql = 
                "SELECT o.organisationunitid, o.name, a.value, a.periodid, a.factor, a.numeratorvalue, a.denominatorvalue " +
                "FROM aggregatedindicatorvalue AS a, organisationunit AS o " +
                "WHERE a.indicatorid = " + indicatorId + " " +
                "AND a.periodid = " + periodId + " " +
                "AND a.level = " + level + " " +
                "AND a.organisationunitid = o.organisationunitid" + 
                "AND o.organisationunitid = " + organisationUnitId;
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new AggregatedIndicatorMapValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated indicator map values", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, Collection<Integer> periodIds, int level, int organisationUnitId )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<AggregatedMapValue> mapper = new ObjectMapper<AggregatedMapValue>();
        
        try
        {
            final String sql = 
                "SELECT o.organisationunitid, o.name, a.value, a.periodid, a.factor, a.numeratorvalue, a.denominatorvalue " +
                "FROM aggregatedindicatorvalue AS a, organisationunit AS o " +
                "WHERE a.indicatorid = " + indicatorId + " " +
                "AND a.periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                "AND a.level = " + level + " " +
                "AND a.organisationunitid = o.organisationunitid " + 
                "AND o.organisationunitid = " + organisationUnitId;
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new AggregatedIndicatorMapValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated indicator map values", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    // -------------------------------------------------------------------------
    // DataValue
    // -------------------------------------------------------------------------

    public Collection<DeflatedDataValue> getDeflatedDataValues( int dataElementId, int periodId, Collection<Integer> sourceIds )
    {
        final StatementHolder holder = statementManager.getHolder();
            
        final ObjectMapper<DeflatedDataValue> mapper = new ObjectMapper<DeflatedDataValue>();
        
        try
        {
            final String sql =
                "SELECT * FROM datavalue " +
                "WHERE dataelementid = " + dataElementId + " " +
                "AND periodid = " + periodId + " " +
                "AND sourceid IN ( " + getCommaDelimitedString( sourceIds ) + " )";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new DeflatedDataValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get deflated data values", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public DataValue getDataValue( int dataElementId, int categoryOptionComboId, int periodId, int sourceId )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<DataValue> mapper = new ObjectMapper<DataValue>();
        
        try
        {
            final String sql =
                "SELECT * FROM datavalue " +
                "WHERE dataelementid = " + dataElementId + " " +
                "AND categoryoptioncomboid = " + categoryOptionComboId + " " +
                "AND periodid = " + periodId + " " +
                "AND sourceid = " + sourceId;
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getObject( resultSet, new DataValueRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get deflated data values", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public Map<DataElementOperand, String> getDataValueMap( int periodId, int sourceId )
    {
        final StatementHolder holder = statementManager.getHolder();
            
        try
        {
            final String sql =
                "SELECT dataelementid, categoryoptioncomboid, value " +
                "FROM datavalue " +
                "WHERE periodid = " + periodId + " " +
                "AND sourceid = " + sourceId;
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            final Map<DataElementOperand, String> map = new HashMap<DataElementOperand, String>();
            
            while ( resultSet.next() )
            {
                final DataElementOperand operand = new DataElementOperand( resultSet.getInt( 1 ), resultSet.getInt( 2 ), null );
                
                map.put( operand, resultSet.getString( 3 ) );
            }
            
            return map;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get DataValues", ex );
        }
        finally
        {
            holder.close();
        }
    }
}
