package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeImages;

/**
 * <p>Title: Location Tree</p>
 *
 * <p>Description: Location tree object for the meandre workbench
 * application.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class LocationTree extends Tree implements TreeListener {

    //==============
    // Data Members
    //==============

    Controller _controller = null;

    //==============
    // Constructors
    //==============

    public LocationTree(Controller cont, TreeImages imgs) {
        super(imgs);
        _controller = cont;
        this.sinkEvents(Event.ONDBLCLICK);
        this.addTreeListener(this);
    }

    //================
    // Public Methods
    //================

//    public void onBrowserEvent(Event event) {
//        if (_controller.isFlowExecuting()){
//            return;
//        }
//        super.onBrowserEvent(event);
//        int type = DOM.eventGetType(event);
//        switch (type) {
//        case Event.ONDBLCLICK: {
//            if (getSelectedItem() != null){
//                WBFlow flow = (WBFlow)this.getSelectedItem().getUserObject();
//                if (flow != null) {
//                    _controller.addFlowToCanvas(flow);
//                }
//            }
//            break;
//        }
//        }
//    }

    //===========================
    // Interface Implementation: TreeListener
    //===========================

    public void onTreeItemSelected(TreeItem item) {
//        if (item.getUserObject() != null) {
//            _controller.SetFlowDescriptionInScrollPane((WBFlow) item.
//                    getUserObject());
//        }
    }

    public void onTreeItemStateChanged(TreeItem item) {

    }
}
