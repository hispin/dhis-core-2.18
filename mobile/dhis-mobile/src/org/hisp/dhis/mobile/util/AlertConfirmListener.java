package org.hisp.dhis.mobile.util;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.ui.DHISMIDlet;

public class AlertConfirmListener implements CommandListener
{
    private Displayable currentScrren;
    
    private Displayable nextScreen;

    private MIDlet midlet;
    
    public AlertConfirmListener( MIDlet midlet, Displayable currentScrren, Displayable nextScreen )
    {
        this.midlet = midlet;
        this.nextScreen = nextScreen;
        this.currentScrren = currentScrren;
    }

    public void commandAction( Command c, Displayable d )
    {
        if(c.getCommandType() == Command.OK){
            //Do other actions
            ((DHISMIDlet)this.midlet).switchDisplayable(null,nextScreen);
        }else if(c.getCommandType() == Command.CANCEL){
            ((DHISMIDlet)this.midlet).switchDisplayable(null,currentScrren);
        }
    }

}
