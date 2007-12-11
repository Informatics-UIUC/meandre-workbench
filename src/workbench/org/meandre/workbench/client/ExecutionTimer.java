package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.Timer;
import org.meandre.workbench.client.beans.WBExecBean;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextArea;
import java.util.Set;
import java.util.Iterator;
import org.meandre.workbench.client.beans.WBRunningFlow;


/**
 * <p>Title: Execution Timer </p>
 *
 * <p>Description: A timer class for updating synchronous interactively
 * executing flows in an asynchronous manner.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class ExecutionTimer extends Timer {

    //==============
    // Data Members
    //==============

    private String _execID = null;
    private String _sessionID = null;
    private Main _main = null;
    private Controller _cont = null;
    private String _flowID = null;
    private TextArea _ta = null;
    private boolean _webuiFinishedQuerying = false;

    //==============
    // Constructors
    //==============

    public ExecutionTimer(String sid, String execid, String flowid,
                          Controller cont, Main main) {
        _execID = execid;
        _sessionID = sid;
        _cont = cont;
        _main = main;
        _flowID = flowid;
        initializeMain();
        startRun();
    }

    //================
    // Public Methods
    //================

    public void run() {
        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                WBExecBean cbo = (WBExecBean) result;
                if (cbo.getSuccess()) {
                    if ((cbo.getConsoleCont() != null) &&
                        (cbo.getConsoleCont().length() > 0)) {
                        updateMain(cbo.getConsoleCont());
                    }
                    if (!cbo.getCompleted()) {
                        ExecutionTimer.this.schedule(1000);
                    } else {
                        _webuiFinishedQuerying = true;
                        _cont.setStatusMessage("Flow executed successfully.");
                        _cont.hideStatusBusy();
                    }
                } else {
                    _webuiFinishedQuerying = true;
                    _cont.setStatusMessage("Flow execution failure.");
                    _cont.hideStatusBusy();
                    String msg =
                            "Flow interactive execution was NOT successful: " +
                            cbo.getFailureMessage();
                    Window.alert(msg);
                    updateMain(msg);
                }
            }

            public void onFailure(Throwable caught) {
                _webuiFinishedQuerying = true;
                _cont.setStatusMessage("Flow execution failure.");
                _cont.hideStatusBusy();
                Window.alert("AsyncCallBack Failure -- saveFlow:  " +
                             caught.toString());
            }
        };

        _cont.updateInteractiveExecution(_sessionID,
                                         _execID,
                                         callback);
    }

    //=================
    // Private Methods
    //=================

    private void startRun() {

        _cont.setStatusMessage("Running flow: " + this._flowID + ".");
        _cont.showStatusBusy();

        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                WBExecBean cbo = (WBExecBean) result;
                if (cbo.getSuccess()) {
                    if ((cbo.getConsoleCont() != null) &&
                        (cbo.getConsoleCont().length() > 0)) {
                        updateMain(cbo.getConsoleCont());
                    }
                    if (!cbo.getCompleted()) {
                        ExecutionTimer.this.schedule(1000);
                    } else {
                        _webuiFinishedQuerying = true;
                        _cont.setStatusMessage("Flow executed successfully.");
                        _cont.hideStatusBusy();
                    }
                } else {
                    _webuiFinishedQuerying = true;
                    _cont.hideRunningIndicator();
                    String msg =
                            "Flow interactive execution was NOT successful: " +
                            cbo.getFailureMessage();
                    Window.alert(msg);
                    updateMain(msg);
                }
            }

            public void onFailure(Throwable caught) {
                _webuiFinishedQuerying = true;
                _cont.setStatusMessage("Flow execution failure.");
                _cont.hideStatusBusy();
                Window.alert("AsyncCallBack Failure -- saveFlow:  " +
                             caught.toString());
            }
        };

        _cont.startInteractiveExecution(_sessionID,
                                        _execID,
                                        _flowID,
                                        callback);

        AsyncCallback webuiCallback = new AsyncCallback() {
            public void onSuccess(Object result) {
                Set rbeans = (Set) result;
                if (!_webuiFinishedQuerying) {
                    if (!rbeans.isEmpty()) {
                        //find our flow else query again ...
                        for (Iterator itty = rbeans.iterator(); itty.hasNext(); ) {
                            WBRunningFlow rbean = (WBRunningFlow) itty.next();
                            //Window.alert(rbean.getID() + "    " + _flowID);
                            if (rbean.getID().trim().startsWith(_flowID.trim())) {
                                _cont.showWebUI(rbean.getWebUIURL(), _flowID);
                                _webuiFinishedQuerying = true;
                                return;
                            }
                        }
                    }
                    //query again ...
                    _cont.listRunningFlows(this);

                }
            }

            public void onFailure(Throwable caught) {
                _webuiFinishedQuerying = true;
                Window.alert("AsyncCallBack Failure -- getRunningFlows:  " +
                             caught.toString());
            }
        };

        _cont.listRunningFlows(webuiCallback);

    }

    private void updateMain(String msg) {
        String txt = _ta.getText();
        //int pos = txt.length() + msg.length();
        _ta.setText(txt + msg);
        //_ta.setCursorPos(pos);
        _main.getCompDescScrollPanel().setScrollPosition(_main.
                getCompDescScrollPanel().getOffsetHeight());
    }

    private void initializeMain() {
        _main.getCompDescScrollPanel().clear();
        _ta = new TextArea();
        _ta.setCharacterWidth(120);
        _ta.setText("");
        _main.getCompDescScrollPanel().add(_ta);
        _ta.setHeight("100%");
        _ta.setWidth("100%");
    }
}
