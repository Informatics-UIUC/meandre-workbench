package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * <p>Title: Port Popup Panel</p>
 *
 * <p>Description: A popup panel for port descriptions.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class PortPopUp extends PopupPanel{


      public PortPopUp(String s) {
        // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
        // If this is set, the panel closes itself automatically when the user
        // clicks outside of it.
        super();

        // PopupPanel is a SimplePanel, so you have to set it's widget property to
        // whatever you want its contents to be.
        setWidget(new Label(s));
      }

}
