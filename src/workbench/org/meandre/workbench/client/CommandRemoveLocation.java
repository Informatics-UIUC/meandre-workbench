package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import org.meandre.workbench.client.beans.WBLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import org.meandre.workbench.client.beans.WBCallbackObject;

/**
 * <p>Title: Command Remove Location </p>
 *
 * <p>Description: This command class performs the remove location operations.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class CommandRemoveLocation implements WBCommand {

    //==============
    // Data Members
    //==============

    private Main _main = null;
    private WBRepositoryQueryAsync _repquery = null;
    private WBCommand _cmd = null;
    private WBLocation _location = null;
    private Controller _cont = null;

    //==============
    // Constructors
    //==============

    CommandRemoveLocation(Controller cont, WBCommand cmd) {
        _main = cont.getMain();
        _cmd = cmd;
        _cont = cont;
    }

    //=====================================
    // Interface Implementation: WBCommand
    //=====================================

    public void execute(Object obj) {
        _location = (WBLocation) obj;

        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                // do some UI stuff to show success
                WBCallbackObject cbo = (WBCallbackObject) result;
                if (cbo.getSuccess()) {
                    _cont.regenerateTabbedPanel(false);
                    //selectlocation tab
                    _cont.getMain().getTabPanel().selectTab(2);

                    _cont.hideStatusBusy();
                    _cont.setStatusMessage("Location removed successfully.  Regenerating location list ...");

                    if (_cmd != null) {
                        _cmd.execute(_location);
                    }

                } else {
                    _cont.hideStatusBusy();
                    _cont.setStatusMessage("Location removal failed.");
                    Window.alert("Location removal operation was NOT successful: " +
                                 cbo.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                _cont.hideStatusBusy();
                _cont.setStatusMessage("Location removal failed.");
                Window.alert("AsyncCallBack Failure -- Delete Flow:  " +
                             caught.toString());
            }
        };

        _cont.showStatusBusy();
        _cont.setStatusMessage("Removing location " + _location.getLocation()+ ".");
        _cont.removeLocation(_location.getLocation(), callback);
    }

}
