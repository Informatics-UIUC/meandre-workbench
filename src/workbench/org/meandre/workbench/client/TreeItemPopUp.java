package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * <p>Title: Tree Item Popup</p>
 *
 * <p>Description: A popup panel for tree items.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class TreeItemPopUp extends PopupPanel {

    //==============
    // Constructors
    //==============

    public TreeItemPopUp(String s) {
        // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
        // If this is set, the panel closes itself automatically when the user
        // clicks outside of it.
        super();

        // PopupPanel is a SimplePanel, so you have to set it's widget property to
        // whatever you want its contents to be.

        HTML htm = new HTML(s);
        setWidget(htm);
    }

}
