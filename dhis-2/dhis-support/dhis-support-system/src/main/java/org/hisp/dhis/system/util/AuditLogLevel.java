package org.hisp.dhis.system.util;

import org.apache.log4j.Level;


public class AuditLogLevel extends Level
{
    /**
     * 
     */
    private static final long serialVersionUID = -4475981504189586316L;

    /**
     * This value is greater than {@link org.apache.log4j.Priority#INFO_INT}
     * 
     */
    public static final int AUDIT_TRAIL_INT = INFO_INT + 10;
   
    /**
     * {@link Level} representing my log level
     */
    public static final Level AUDIT_TRAIL = new AuditLogLevel(AUDIT_TRAIL_INT, "AUDIT_TRAIL",7);

    /**
     * Constructor
     *
     * @param arg0
     * @param arg1
     * @param arg2
     */
    protected AuditLogLevel(int arg0, String arg1, int arg2) {
        super(arg0, arg1, arg2);

    }
   
    /**
     * Checks whether <code>sArg</code> is "AUDIT_TRAIL" level. If yes then returns {@link AuditLogLevel#AUDIT_TRAIL},
     * else calls {@link AuditLogLevel#toLevel(String, Level)} passing it {@link Level#DEBUG} as the defaultLevel
     *
     * @see Level#toLevel(java.lang.String)
     * @see Level#toLevel(java.lang.String, org.apache.log4j.Level)
     *
     */
    public static Level toLevel(String sArg) {
        if (sArg != null && sArg.toUpperCase().equals("AUDIT_TRAIL")) {
            return AUDIT_TRAIL;
        }
        return (Level) toLevel(sArg, Level.DEBUG);
    }

    /**
     * Checks whether <code>val</code> is {@link AuditLogLevel#AUDIT_TRAIL_INT}. If yes then returns {@link AuditLogLevel#AUDIT_TRAIL},
     * else calls {@link AuditLogLevel#toLevel(int, Level)} passing it {@link Level#DEBUG} as the defaultLevel
     *
     * @see Level#toLevel(int)
     * @see Level#toLevel(int, org.apache.log4j.Level)
     *
     */
    public static Level toLevel(int val) {
        if (val == AUDIT_TRAIL_INT) {
            return AUDIT_TRAIL;
        }
        return (Level) toLevel(val, Level.DEBUG);
    }

    /**
     * Checks whether <code>val</code> is {@link AuditLogLevel#AUDIT_TRAIL_INT}. If yes then returns {@link AuditLogLevel#AUDIT_TRAIL},
     * else calls {@link Level#toLevel(int, org.apache.log4j.Level)}
     *
     * @see Level#toLevel(int, org.apache.log4j.Level)
     */
    public static Level toLevel(int val, Level defaultLevel) {
        if (val == AUDIT_TRAIL_INT) {
            return AUDIT_TRAIL;
        }
        return Level.toLevel(val,defaultLevel);
    }

    /**
     * Checks whether <code>sArg</code> is "AUDIT_TRAIL" level. If yes then returns {@link AuditLogLevel#AUDIT_TRAIL},
     * else calls {@link Level#toLevel(java.lang.String, org.apache.log4j.Level)}
     *
     * @see Level#toLevel(java.lang.String, org.apache.log4j.Level)
     */
    public static Level toLevel(String sArg, Level defaultLevel) {                 
    if(sArg != null && sArg.toUpperCase().equals("AUDIT_TRAIL")) {
        return AUDIT_TRAIL;
    }
    return Level.toLevel(sArg,defaultLevel);
 }
  
}
