package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import org.meandre.workbench.client.beans.WBFlow;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import org.meandre.workbench.client.beans.WBCallbackObject;

/**
 * <p>Title: Command Save Flow </p>
 *
 * <p>Description: This command class performs the save flow operations.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class CommandSaveFlow implements WBCommand {

        //==============
        // Data Members
        //==============

    private Main _main = null;
    private WBRepositoryQueryAsync _repquery = null;
    private WBCommand _cmd = null;
    private WBFlow _flow = null;
    private Controller _cont = null;

    //==============
    // Constructors
    //==============

    CommandSaveFlow(WBRepositoryQueryAsync query,
                           Controller cont, WBCommand cmd) {
        _main = cont.getMain();
        _repquery = query;
        _cmd = cmd;
        _cont = cont;
    }

    //=====================================
    // Interface Implementation: WBCommand
    //=====================================

    public void execute(Object obj) {
        _flow = (WBFlow) obj;

        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                // do some UI stuff to show success
                WBCallbackObject cbo = (WBCallbackObject) result;
                if (cbo.getSuccess()) {
                    Window.alert("Flow saved successfully.");
                    _cont.buildFlowTree(_cont.getFlowTreeHandle(),
                                        _cont.getFlowTreeRoot(),
                                        "Available");
                    _main.getTabPanel().selectTab(1);

                    _flow.setFlowID(cbo.getMessage());

                    if (_cmd != null) {
                        _cmd.execute(_flow);
                    }

                } else {
                    Window.alert("Flow save operation was NOT successful: " +
                                 cbo.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                // do some UI stuff to show failure
                Window.alert("AsyncCallBack Failure -- saveFlow:  " +
                             caught.toString());
            }
        };
        _repquery.saveFlow(_flow, _cont.getSessionID(), callback);

    }

}
