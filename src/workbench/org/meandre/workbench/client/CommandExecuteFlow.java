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

        if (_cmd != null) {
            _cmd.execute(_flow);
        }

    }

}
