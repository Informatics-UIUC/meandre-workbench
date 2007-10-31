package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import org.meandre.workbench.client.beans.WBDataport;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventPreview;

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
public class PortComp extends Image implements EventPreview {

//==============
// Data Members
//==============

    private String _mouseOverImgURL = "images/comp-port-over.png";
    private String _mouseOutImgURL = "images/comp-port-out.png";
    private WBDataport _dp = null;
    private PortPopUp _ppp = null;
    static final int s_INPUT_PORT_TYPE = 0;
    static final int s_OUTPUT_PORT_TYPE = 1;
    private int _port_type = -1;
    private Controller _cont = null;

    private boolean _drawing = false;
    private boolean _connected = false;

    private ComponentPanel _parentComp = null;

    private PortComp _connectedTo = null;

//==============
// Constructors
//==============

    public PortComp(Controller cont, WBDataport dp, int pt, ComponentPanel parent) {
        super();
        this.setUrl(_mouseOutImgURL);
        this.addStyleName("comp-ports");
        _parentComp = parent;
        _dp = dp;
        _ppp = new PortPopUp(_dp.getName());
        _ppp.addStyleName("port-popup");
        _port_type = pt;
        _cont = cont;
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    public ComponentPanel getParentComponent(){
        return _parentComp;
    }

    public WBDataport getDataportObj(){
        return _dp;
    }

    public int getPortOrientation(){
        return _port_type;
    }

    public void setConnected(boolean b, PortComp conn){
        _connected = b;
        if (b){
            _connectedTo = conn;
            this.setUrl(_mouseOverImgURL);
        } else {
            this.setUrl(_mouseOutImgURL);
            _connectedTo = null;
        }
    }

    public PortComp getConnectedTo(){
        return this._connectedTo;
    }

    public boolean isConnected(){
        return _connected;
    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        switch (DOM.eventGetType(event)) {
        case Event.ONCLICK:
            if ((_port_type == s_OUTPUT_PORT_TYPE) && (!_drawing) && (!_cont.getDrawingConn())) {
                _drawing = true;
                _cont.setDrawingConnection(true, this);
                DOM.addEventPreview(this);
            } else if ((_port_type == s_INPUT_PORT_TYPE) && (_cont.getDrawingConn())) {
                _cont.getDrawingFromPort().onEventPreview(event);
            }
            break;
        case Event.ONMOUSEOVER:
            if ((!_drawing) && (!_connected)) {
                this.setUrl(_mouseOverImgURL);
            }
            int left = -1;
            int top = -1;
            top = this.getAbsoluteTop() - 15;
            if (_port_type == this.s_OUTPUT_PORT_TYPE) {
                left = this.getAbsoluteLeft() + 15;
            } else {
                left = this.getAbsoluteLeft() + 15 /*-
                       (_dp.getName().length() * 7)*/;
            }
            _ppp.setPopupPosition(left, top);
            _ppp.show();

            break;
        case Event.ONMOUSEOUT:
            if ((!_drawing) && (!_connected)) {
                this.setUrl(_mouseOutImgURL);
            }
            _ppp.hide();
            break;
        }
    }

    public boolean onEventPreview(Event event) {

      Element target = DOM.eventGetTarget(event);
      // Is the click over this element?
      boolean eventTargetsPopup = DOM.isOrHasChild(getElement(), target);

      int type = DOM.eventGetType(event);
      switch (type) {
        case Event.ONCLICK: {
                // If click not over this element ...
                //Window.alert("" + eventTargetsPopup);
          // Don't eat events if event capture is enabled, as this can interfere
          // with dialog dragging, for example.
          if (DOM.getCaptureElement() != null) {
            return true;
          }
          if (!eventTargetsPopup) {
              DOM.removeEventPreview(this);
              _drawing = false;
              _cont.setDrawingConnection(false, null);
              int x = DOM.eventGetClientX(event);
              int y = DOM.eventGetClientY(event);
              PortComp pc = _cont.findInputPortOver(x, y);
              //Window.alert("" + pc.getPortOrientation());
              // If connected remove connections regardless.
              if (_connected){
                  _cont.removeConnection(this);
              }
              // If click over an input port make a connection
              if (pc != null){
                  _cont.makeConnection(this, pc);
              } else {
                  // Else, set image back to port out.
                  this.setUrl(_mouseOutImgURL);
              }
          }

          return true;
        }
      }
      return true;
    }


}
