package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import org.meandre.workbench.client.beans.WBFlow;

/**
 * <p>Title: Command Execute Flow</p>
 *
 * <p>Description: This command class executes a flow object.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class CommandExecuteFlow implements WBCommand {

    //==============
    // Data Members
    //==============

    private Main _main = null;
    private WBCommand _cmd = null;
    private WBFlow _flow = null;
    private Controller _cont = null;

    //==============
    // Constructors
    //==============

    CommandExecuteFlow(Controller cont, WBCommand cmd) {
        _main = cont.getMain();
        _cont = cont;
        _cmd = cmd;
    }

    //=====================================
    // Interface Implementation: WBCommand
    //=====================================

    public void execute(Object obj) {
        _flow = (WBFlow) obj;

        new ExecutionTimer(_cont.getSessionID(),
                           _flow.getFlowID() + "_" + System.currentTimeMillis(),
                           _flow.getFlowID(),
                           _cont,
                           _main);

//        HTTPRequest req = new HTTPRequest();
//        String s = "../action-run-flow.jsp?component=" +
//                   URL.encodeComponent(_flow.getFlowID())
//                   + "&sid="
//                   + URL.encodeComponent(Controller.s_controller.getSessionID());
//        _cont.showRunningIndicator();
//        req.asyncGet(s, new ResponseTextHandler() {
//            public void onCompletion(String text) {
//                /**
//                 * Remove the link to get the
//                 */
//
//                _main.getCompDescScrollPanel().clear();
//                TextArea ta = new TextArea();
//                ta.setCharacterWidth(120);
//                ta.setText(text);
//                _main.getCompDescScrollPanel().add(ta);
//                ta.setHeight("100%");
//                _cont.hideRunningIndicator();
//            }
//        });
//        req = new HTTPRequest();
//        s = "../content-execution-list.jsp?delay=true";
//        req.asyncGet(s, new ResponseTextHandler() {
//            public void onCompletion(String text) {
//                if (text.toLowerCase().indexOf("<a ") != -1){
//                    String work = text;
//                    int posb = work.toLowerCase().indexOf("<a href=\"");
//                    work = work.substring(posb + 9);
//                    int pose = work.indexOf("\"");
//                    work = work.substring(0, pose);
//
//                    HTTPRequest req = new HTTPRequest();
//                    Window.open(work, "UI Window", null);
//                }
//            }
//        });

        if (_cmd != null) {
            _cmd.execute(_flow);
        }

    }

}
