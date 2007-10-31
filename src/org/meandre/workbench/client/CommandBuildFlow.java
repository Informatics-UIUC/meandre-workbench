package org.seasr.client;

//==============
// Java Imports
//==============

import java.util.Set;

//===============
// Other Imports
//===============

import org.seasr.client.beans.WBFlow;

/**
 * <p>Title: Command Build Flow</p>
 *
 * <p>Description: This command class initiates the flow building process.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class CommandBuildFlow implements WBCommand {

    //==============
    // Data Members
    //==============

    private Set _comps = null;
    private Set _conns = null;
    private WBCommand _cmd = null;

    //==============
    // Constructors
    //==============

    CommandBuildFlow(Set comps, Set conns, WBCommand cmd) {
        _comps = comps;
        _conns = conns;
        _cmd = cmd;
    }

    //=====================================
    // Interface Implementation: WBCommand
    //=====================================

    public void execute(Object obj) {
        WBFlow flow = (WBFlow) obj;
        new FlowBuildForm(flow, _comps, _conns, _cmd);
    }

}
