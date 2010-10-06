package org.hisp.dhis.mobile.reporting.util;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;



/**
 * @author Tran Ng Minh Luan
 *
 */
public abstract class AlertConfirmListener implements CommandListener
{
    protected Displayable currentScrren;
    
    protected Displayable nextScreen;

    protected MIDlet midlet;
    
    public AlertConfirmListener(  )
    {
        
    }
    
    public void setCurrentScrren( Displayable currentScrren )
    {
        this.currentScrren = currentScrren;
    }



    public void setNextScreen( Displayable nextScreen )
    {
        this.nextScreen = nextScreen;
    }



    public void setMidlet( MIDlet midlet )
    {
        this.midlet = midlet;
    }



    public void commandAction( Command c, Displayable d )
    {
        //Define action when Command == OK
        //Define action when Command == CANCEL
    }

}
