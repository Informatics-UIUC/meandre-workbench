package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.Window;

/**
 * <p>Title: Running Indicator</p>
 *
 * <p>Description: This class implements a modal dialog with an animated gif
 * to indicate that the application is running.  This class is a singleton
 * class.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class RunningIndicator extends DialogBox {

    //==============
    // Data Members
    //==============

    /* The single instance of this class.*/
    static private RunningIndicator _busy = null;

    //==============
    // Constructors
    //==============

    /**
     * Build a singleton class.
     */
    private RunningIndicator() {
        super(false, true);
        setWidget(new Image("images/meandering.gif"));
        setText("Running ...");
        show();
        setPopupPosition((Window.getClientWidth() / 2) -
                         (this.getOffsetWidth() / 2),
                         (Window.getClientHeight() / 2) -
                         (this.getOffsetHeight() / 2));
    }

    //================
    // Static Methods
    //================

    /**
     * Show the running dialog widget.
     */
    static void showRunning(){
        if (_busy == null){
            _busy = new RunningIndicator();
        } else {
            _busy.show();
            _busy.setPopupPosition((Window.getClientWidth() / 2) -
                             (_busy.getOffsetWidth() / 2),
                             (Window.getClientHeight() / 2) -
                             (_busy.getOffsetHeight() / 2));

        }
    }

    /**
     * Hide the running dialog widget.
     */
    static void hideRunning(){
        if (_busy != null){
            _busy.hide();
        }
    }

}
