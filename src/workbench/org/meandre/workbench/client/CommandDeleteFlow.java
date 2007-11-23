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
class CommandDeleteFlow implements WBCommand {

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

    CommandDeleteFlow(WBRepositoryQueryAsync query,
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
                    _cont.buildFlowTree(_cont.getFlowTreeHandle(),
                                        _cont.getFlowTreeRoot(),
                                        "Available");
                    _main.getTabPanel().selectTab(1);

                    _cont.hideStatusBusy();
                    _cont.setStatusMessage("Flow deleted successfully.  Regenerating flow list ...");

                    if (_cmd != null) {
                        _cmd.execute(_flow);
                    }

                } else {
                    _cont.hideStatusBusy();
                    _cont.setStatusMessage("Flow deletion failed.");
                    Window.alert("Flow delete operation was NOT successful: " +
                                 cbo.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                _cont.hideStatusBusy();
                _cont.setStatusMessage("Flow deletion failed.");
                Window.alert("AsyncCallBack Failure -- Delete Flow:  " +
                             caught.toString());
            }
        };

        _cont.showStatusBusy();
        _cont.setStatusMessage("Deleting flow " + _flow.getFlowID() + ".");
        _repquery.deleteFlowFromRepository(_cont.getSessionID(),
                                           _flow.getFlowID(),
                                           callback);
    }

}
