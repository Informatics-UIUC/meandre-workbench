package org.meandre.workbench.client;


//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;

/**
 * <p>Title: Command Unpublish Flows</p>
 *
 * <p>Description: This command class unpublishes flows from the public
 * repository.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class CommandUnpublishFlow implements WBCommand {

    //==============
    // Data Members
    //==============

    private WBCommand _cmd = null;
    private Controller _cont = null;

    //==============
    // Constructors
    //==============

    CommandUnpublishFlow(Controller cont, WBCommand cmd) {
        _cont = cont;
        _cmd = cmd;
    }

    //=====================================
    // Interface Implementation: WBCommand
    //=====================================

    public void execute(Object obj) {
        String uri = (String)obj;

        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                // do some UI stuff to show success
                _cont.hideStatusBusy();
                _cont.setStatusMessage("Unpublish flow operation successful!");

                if (_cmd != null) {
                    _cmd.execute(null);
                }

            }

            public void onFailure(Throwable caught) {
                // do some UI stuff to show failure
                _cont.hideStatusBusy();
                _cont.setStatusMessage("Unpublish flow operation failed!");
                Window.alert(
                        "AsyncCallBack Failure -- unpublish:  " +
                        caught.getMessage());
            }
        };
        _cont.unpublish(uri, callback);

    }

}
