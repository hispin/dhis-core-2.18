package org.hisp.dhis.mobile;

import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.table.TableLayout;
import javax.microedition.midlet.*;

public class DHISMobile extends MIDlet {

    String fieldArr[] = {"Antenatal Care", "Postnatal Care", "Routine Immunization", "Multi National Company which has center in Australia", "Software", "Sunny", "Hardware", "Manpower", "Powerful"};

    public DHISMobile() {
    }

    public void startApp() {

        try {
            Display.init(this);
            showSplashScreen(true);
            showDatasetForm(true);
            //showPeriodForm(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showPeriodForm(boolean show) {
        if (show == true) {
            Form f = new Form();
            f.setLayout(null);
        }
    }

    private void showDatasetForm(boolean show) {
        if (show == true) {
            Form f = new Form();
            f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            for (int i = 0; i < fieldArr.length; i++) {
                Container con = new Container();
                TableLayout layout = new TableLayout(1, 2);
                con.setLayout(layout);
                TableLayout.Constraint constraint = layout.createConstraint();
                constraint.setWidthPercentage(80);
                Label lbl = new Label(fieldArr[i]);
                con.addComponent(constraint, lbl);
                TextField tf = new TextField(3);
                con.addComponent(tf);
                f.addComponent(con);
            }
            f.show();
        }
    }

    private void showSplashScreen(boolean show) {
        if (show == true) {
            Form f = new Form();
            f.setLayout(new BorderLayout());
            try {
                Image img = Image.createImage("/org/hisp/dhis/mobile/images/nrhm-logo.png");
                Label imgLabel = new Label(img);
                imgLabel.setAlignment(Label.CENTER);
                f.addComponent(BorderLayout.CENTER, imgLabel);
                f.show();
                Thread.sleep(2000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
