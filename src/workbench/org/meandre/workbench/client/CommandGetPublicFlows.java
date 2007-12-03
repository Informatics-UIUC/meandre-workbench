package org.meandre.workbench.client;


//==============
// Java Imports
//==============

import java.util.Set;
import java.util.Map;
import java.util.Iterator;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import org.meandre.workbench.client.beans.WBFlow;

/**
 * <p>Title: Command Get Public Components</p>
 *
 * <p>Description: This command class retrieves flows from the public
 * repository.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class CommandGetPublicFlows implements WBCommand {

    //==============
    // Data Members
    //==============

    private WBCommand _cmd = null;
    private Controller _cont = null;

    //==============
    // Constructors
    //==============

    CommandGetPublicFlows(Controller cont, WBCommand cmd) {
        _cont = cont;
        _cmd = cmd;
    }

    //=====================================
    // Interface Implementation: WBCommand
    //=====================================

    public void execute(Object obj) {

        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                // do some UI stuff to show success
                Set items = (Set) result;

                Map pcs = _cont.getPublicFlowsMap();
                pcs.clear();
                if ((items != null) && (!items.isEmpty())){
                    for (Iterator itty = items.iterator(); itty.hasNext();){
                        WBFlow flow = (WBFlow)itty.next();
                        pcs.put(flow.getFlowID(), flow);
                    }
                }

                if (_cmd != null) {
                    _cmd.execute(null);
                }

            }

            public void onFailure(Throwable caught) {
                // do some UI stuff to show failure
                Window.alert(
                        "AsyncCallBack Failure -- getPublicRepositoryFlows:  " +
                        caught.getMessage());
            }
        };
        _cont.getPublicRepositoryFlows(callback);

    }

}
