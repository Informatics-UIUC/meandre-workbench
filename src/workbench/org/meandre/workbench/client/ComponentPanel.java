package org.meandre.workbench.client;

//==============
// Java Imports
//==============

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.meandre.workbench.client.beans.*;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>Title: Component Panel</p>
 *
 * <p>Description: This is a panel that holds the component image, ports, and
 * label.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class ComponentPanel extends DockPanel implements SourcesClickEvents,
        SourcesMouseEvents {

    //==============
    // Data Members
    //==============

    private ClickListenerCollection clickListeners;
    private MouseListenerCollection mouseListeners;
    private Image _cg = null;
    private WBComponentInstance _comp = null;
    private Controller _cont = null;
    private Set _inputs = new HashSet();
    private Set _outputs = new HashSet();
    private Label _compLab = null;

    //==============
    // Constructors
    //==============

    /**
     * Null Constructor
     */
    public ComponentPanel() {}

    /**
     * Construct a component panel with the controller and component instance
     * object.
     *
     * @param cont Controller GUI controller instance.
     * @param comp WBComponentInstance Component instance object.
     */
    public ComponentPanel(Controller cont, WBComponentInstance comp) {
        super();
        _cont = cont;
        _comp = comp;
        VerticalPanel leftVP = new VerticalPanel();
        leftVP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        VerticalPanel rightVP = new VerticalPanel();
        rightVP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        for (Iterator itty = comp.getExecutableComponent().getInputs().iterator();
                             itty.hasNext(); ) {
            PortComp pc = new PortComp(cont, (WBDataport) itty.next(),
                                       PortComp.s_INPUT_PORT_TYPE, this);
            leftVP.add(pc);
            _inputs.add(pc);
        }

        for (Iterator itty = comp.getExecutableComponent().getOutputs().
                             iterator(); itty.hasNext(); ) {
            PortComp pc = new PortComp(cont, (WBDataport) itty.next(),
                                       PortComp.s_OUTPUT_PORT_TYPE, this);
            rightVP.add(pc);
            _outputs.add(pc);
        }

        if (_inputs.isEmpty()) {
            leftVP.setWidth("10");
        }
        if (_outputs.isEmpty()) {
            rightVP.setWidth("10");
        }

        HorizontalPanel hpan = new HorizontalPanel();
        hpan.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hpan.add(leftVP);
        if (comp.getExecutableComponent().getProperties().getKeys().isEmpty()) {
            _cg = new Image("images/gnome-reboot-48.png");
        } else {
            _cg = new Image("images/gnome-reboot-48-props-chad.png");
        }
        _cg.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                _cont.setSelectedComp(ComponentPanel.this);
            }
        });
        _cg.setStyleName("comp-panel-image");
        hpan.add(_cg);
        hpan.add(rightVP);

        this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        this.add(hpan, this.CENTER);

        _compLab = new Label(comp.getName());
        _compLab.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                _cont.setSelectedComp(null);
            }
        });

        _compLab.addStyleName("comp-label");
        this.add(_compLab, this.SOUTH);

        this.sinkEvents(Event.ONDBLCLICK);
    }

    //================
    // Public Methods
    //================

    /**
     * Release resources held by this object.
     */
    void free() {
        if (clickListeners != null) {
            clickListeners.clear();
        }
        if (mouseListeners != null) {
            mouseListeners.clear();
        }
        Image _cg = null;
        _comp = null;
        Controller _cont = null;
        _inputs.clear();
        _outputs.clear();
        this.clear();
    }

    /**
     * Set this component's selected flag to the boolean value.
     * @param b boolean Value to set this component's selected flag to.
     */
    void setSelected(boolean b) {
        if (b) {
            _cg.removeStyleName("comp-panel-image");
            _cg.addStyleName("comp-panel-image-selected");
        } else {
            _cg.removeStyleName("comp-panel-image-selected");
            _cg.addStyleName("comp-panel-image");
        }
    }

    /**
     * Get input ports for this component.
     * @return Set Input ports of this component.
     */
    Set getInputs() {
        return new HashSet(_inputs);
    }

    /**
     * Get output ports for this component.
     * @return Set Output ports of this component.
     */
    Set getOutputs() {
        return new HashSet(_outputs);
    }

    /**
     * Get image object for this component.
     * @return Image IMage object of this component.
     */
    Image getImage() {
        return _cg;
    }

    /**
     * Get label object for this component.
     * @return Label Label object for this component.
     */
    Label getComponentLabel() {
        return _compLab;
    }

    /**
     * Get component instance for this canvas component.
     * @return WBComponentInstance Component instance for this canvas component.
     */
    WBComponentInstance getComponent() {
        return this._comp;
    }

    public void addClickListener(ClickListener listener) {
        if (clickListeners == null) {
            clickListeners = new ClickListenerCollection();
        }
        clickListeners.add(listener);
    }

    public void addMouseListener(MouseListener listener) {
        if (mouseListeners == null) {
            mouseListeners = new MouseListenerCollection();
        }
        mouseListeners.add(listener);
    }

    public void removeClickListener(ClickListener listener) {
        if (clickListeners != null) {
            clickListeners.remove(listener);
        }
    }

    public void removeMouseListener(MouseListener listener) {
        if (mouseListeners != null) {
            mouseListeners.remove(listener);
        }
    }

    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONDBLCLICK: {
            _cont.showPropertiesDialog(this);
            break;
        }
        case Event.ONCLICK:
            if (clickListeners != null) {
                clickListeners.fireClick(this);
            }
            break;

        case Event.ONMOUSEDOWN:
            if (mouseListeners != null) {
                mouseListeners.fireMouseEvent(this, event);
            }
            break;
        case Event.ONMOUSEUP:
            if (mouseListeners != null) {
                mouseListeners.fireMouseEvent(this, event);
            }
            break;
        case Event.ONMOUSEMOVE:
            if (mouseListeners != null) {
                mouseListeners.fireMouseEvent(this, event);
            }
            break;
        case Event.ONMOUSEOVER:
            if (mouseListeners != null) {
                mouseListeners.fireMouseEvent(this, event);
            }
            break;
        case Event.ONMOUSEOUT:
            if (mouseListeners != null) {
                mouseListeners.fireMouseEvent(this, event);
            }
            break;

        }
    }


}
