package org.hisp.dhis.system.util;

public class AuditLogUtil
{
    public static final String ACTION_ADD = "added";
    
    public static final String ACTION_EDIT = "edited";
    
    public static final String ACTION_DELETE = "deleted";
    
    /**
     * Generate audit trail logging message
     * @param userName : Current user name
     * @param action : user's action ( add, edidt, delete )
     * @param objectType : The name of the object that user is working on
     * @param objectName : The value of the name attribute of the object that user is working on
     * @return : the audit trail logging message
     */
    
    public static String logMessage(String userName, String action, String objectType, String objectName)
    {
        String message = "";
        
        message  = "User "+ " \"" + userName + "\" " + action + " " + objectType + " \"" + objectName + "\"";
        
        return message ;
    }
}
