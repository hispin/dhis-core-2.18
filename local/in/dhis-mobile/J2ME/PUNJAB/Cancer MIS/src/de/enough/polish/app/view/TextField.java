package de.enough.polish.app.view;

import de.enough.polish.ui.Canvas;

public class TextField
    extends de.enough.polish.ui.TextField
{
    private boolean enableGameKey = true;

    public boolean isEnableGameKey()
    {
        return enableGameKey;
    }

    public void setEnableGameKey( boolean enableGameKey )
    {
        this.enableGameKey = enableGameKey;
    }

    public TextField( String label, String text, int maxSize, int constraints )
    {
        super( label, text, maxSize, constraints );
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
     */
    protected boolean handleKeyPressed( int keyCode, int gameAction )
    {
        if(enableGameKey)
            return super.handleKeyPressed( keyCode, gameAction );
        else{
            if ((gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)){
                return true;
            }
            return super.handleKeyPressed( keyCode, gameAction );
        }
    }

}
