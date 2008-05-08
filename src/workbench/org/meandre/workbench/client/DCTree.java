package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.Event;
import org.meandre.workbench.client.beans.*;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeImages;

/**
 * <p>Title: Component Tree</p>
 *
 * <p>Description: A tree object used to display components for the
 * meandre workbench application.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Seasrsmith
 * @version 1.0
 */
public class DCTree extends Tree implements TreeListener {

    //==============
    // Data Members
    //==============

    Controller _controller = null;

    //==============
    // Constructors
    //==============

    public DCTree(Controller cont, TreeImages imgs) {
        super(imgs);
        _controller = cont;
        this.sinkEvents(Event.ONDBLCLICK);
        this.addTreeListener(this);
    }

    //================
    // Public Methods
    //================

    public void onBrowserEvent(Event event) {
        if (_controller.isFlowExecuting()) {
            return;
        }
        super.onBrowserEvent(event);
        int type = DOM.eventGetType(event);
        switch (type) {
        case Event.ONDBLCLICK: {
            if (getSelectedItem() != null) {
                WBComponent comp = (WBComponent)this.getSelectedItem().getUserObject();
                if (comp != null) {
                    _controller.addComponentToCanvas(comp);
                }
            }
            break;
        }
        }
    }

    //===========================
    // Interface Implementation: TreeListener
    //===========================

    public void onTreeItemSelected(TreeItem item) {
        if (item.getUserObject() != null) {
            _controller.SetComponentDescriptionInScrollPane((WBComponent) item.
                    getUserObject());
        }
    }

    public void onTreeItemStateChanged(TreeItem item) {

    }


}
