package org.hisp.dhis.dataapproval;

/**
 * Created by jim on 9/24/14.
 */
public class DataApprovalAndPermissions
    extends DataApproval
{
    private boolean mayApprove;

    private boolean mayUnapprove;

    private boolean mayAccept;

    private boolean mayUnaccept;

    private boolean mayReadData;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataApprovalAndPermissions()
    {
    }

    public DataApprovalAndPermissions( DataApproval d )
    {
        super( d );
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public boolean isMayApprove()
    {
        return mayApprove;
    }

    public void setMayApprove( boolean mayApprove )
    {
        this.mayApprove = mayApprove;
    }

    public boolean isMayUnapprove()
    {
        return mayUnapprove;
    }

    public void setMayUnapprove( boolean mayUnapprove )
    {
        this.mayUnapprove = mayUnapprove;
    }

    public boolean isMayAccept()
    {
        return mayAccept;
    }

    public void setMayAccept( boolean mayAccept )
    {
        this.mayAccept = mayAccept;
    }

    public boolean isMayUnaccept()
    {
        return mayUnaccept;
    }

    public void setMayUnaccept( boolean mayUnaccept )
    {
        this.mayUnaccept = mayUnaccept;
    }

    public boolean isMayReadData()
    {
        return mayReadData;
    }

    public void setMayReadData( boolean mayReadData )
    {
        this.mayReadData = mayReadData;
    }
}
