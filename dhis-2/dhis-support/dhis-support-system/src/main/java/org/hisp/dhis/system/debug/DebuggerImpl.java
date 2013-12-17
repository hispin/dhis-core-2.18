package org.hisp.dhis.system.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class DebuggerImpl
    implements Debugger
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Transactional
    public void markDbLog( String key )
    {
        jdbcTemplate.queryForObject( "select count(*) from attribute where uid = '" + key + "'", Integer.class );
    }
}
