package de.enough.polish.app.view;

import java.util.Date;

import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.DateField;
import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Graphics;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.Locale;

public class TheScreen
    extends FramedForm
{
    public TheScreen( String title )
    {

        // #style formScreen
        super( title );

        // TODO Auto-generated constructor stub
 
    }

    public TextField addtextField( String myStr )
    {
        TextField myTextField = new TextField( myStr, null, 10, TextField.NUMERIC );
        //#style TextField
        append( myTextField );
        return myTextField;
    }

    public ChoiceGroup addChoiceGroup( String label, String[] stringElements, Image[] imageElements )
    {
        //#style TextField
        ChoiceGroup myChoiceGroup = new ChoiceGroup( label, ChoiceGroup.POPUP, stringElements, imageElements );
        append( myChoiceGroup );
        return myChoiceGroup;
    }

    public ChoiceGroup addChoiceGroupSingle( String label, String[] stringElements, Image[] imageElements )
    {
        // #style TextField
        ChoiceGroup myChoiceGroup = new ChoiceGroup( label, ChoiceGroup.EXCLUSIVE, stringElements, imageElements );
        append( myChoiceGroup );
        return myChoiceGroup;
    }

    public ChoiceGroup addChoiceGroupMultiple( String label, String[] stringElements, Image[] imageElements )
    {
        // #style TextField
        ChoiceGroup myChoiceGroup = new ChoiceGroup( label, ChoiceGroup.MULTIPLE, stringElements, imageElements );
        append( myChoiceGroup );
        return myChoiceGroup;
    }
    
    public DateField addDateField ()
    {
        //#style TextField
        DateField myDateField = new DateField( Locale.get( "RegisterDate" ), DateField.DATE );
        myDateField.setDateFormatPattern( "dd/MM/yyyy" );
        
        Date myDate = new Date();
        
        myDateField.setDate( myDate );
        append (myDateField);
        return myDateField;
    }

    public void addObject( Object myObject )
    {
        if ( myObject instanceof Item )
            append( (Item) myObject );
        else if ( myObject instanceof Image )
            append( (Image) myObject );
        else if ( myObject instanceof String )
            append( (String) myObject );
        else if ( myObject instanceof StringItem )
            append( (StringItem) myObject );
        else if ( myObject instanceof ChoiceGroup )
            append( (ChoiceGroup) myObject );
        else
        {
        }
    }

    public void addTextField( TextField myTextField )
    {
        append(myTextField );
    }

    public void addLastTextField( TextField myTextField )
    {

        append( Graphics.BOTTOM, myTextField);
    }
    
    
}
