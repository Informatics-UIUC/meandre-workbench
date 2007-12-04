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
 * <p>Title: Command Publish Flows</p>
 *
 * <p>Description: This command class publishes flows to the public
 * repository.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class CommandPublishFlow implements WBCommand {

    //==============
    // Data Members
    //==============

    private WBCommand _cmd = null;
    private Controller _cont = null;

    //==============
    // Constructors
    //==============

    CommandPublishFlow(Controller cont, WBCommand cmd) {
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
                _cont.setStatusMessage("Publish flow operation successful!");

                if (_cmd != null) {
                    _cmd.execute(null);
                }

            }

            public void onFailure(Throwable caught) {
                // do some UI stuff to show failure
                _cont.hideStatusBusy();
                _cont.setStatusMessage("Publish flow operation failed!");
                Window.alert(
                        "AsyncCallBack Failure -- publish:  " +
                        caught.getMessage());
            }
        };
        _cont.publish(uri, callback);

    }

}
