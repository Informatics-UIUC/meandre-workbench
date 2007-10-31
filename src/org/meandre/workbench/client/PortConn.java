package org.seasr.client;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PortConn {

    //==============
    // Data Members
    //==============

    private PortComp _from = null;
    private PortComp _to = null;

    //==============
    // Constructors
    //==============

    public PortConn(PortComp from, PortComp to) {
        _from = from;
        _to = to;
    }

    //================
    // Public Methods
    //================

    public PortComp getFrom() {
        return _from;
    }

    public PortComp getTo() {
        return _to;
    }


}
