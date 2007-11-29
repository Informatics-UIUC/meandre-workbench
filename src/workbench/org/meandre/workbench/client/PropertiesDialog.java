package org.meandre.workbench.client;

//==============
// Java Imports
//==============

import java.util.Map;
import java.util.Iterator;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * <p>Title: Component Properties Dialog </p>
 *
 * <p>Description: This class implements a popup panel that is a dialog box
 * used for editing properties of meandre components.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class PropertiesDialog extends DialogBox {

    //==============
    // Data Members
    //==============

    /* Component whose properties are being edited.*/
    private ComponentPanel _cp = null;
    /* Map of property descriptions.*/
    private Map _descMap = null;
    /* Map of property values of component instances.*/
    private Map _valuesMap = null;
    /* Map of property values of base components.*/
    private Map _baseValuesMap = null;
    /* Controller class for this GUI.*/
    private Controller _cont = null;
    /* Text area used to display component description.*/
    private HTML _ta = new HTML();
    /* Array of text boxes for each component property.*/
    private TextBox[] _tboxes = null;
    /* Array of property key values for this component.*/
    private String[] _keys = null;
    /* Popup panel used for the display of the component property descriptions.*/
    private TreeItemPopUp _ppp = null;
    /* Map used to find property description for each label.*/
    private Map _descs = new java.util.HashMap();
    /* Name textbox used to set focus.*/
    private TextBox _name = null;

    //==============
    // Constructors
    //==============

    public PropertiesDialog(ComponentPanel cp, Controller cont) {
        super(false, true);
        _cont = cont;
        _cp = cp;
        _descMap = cp.getComponent().getExecutableComponent().getProperties().
                   getDescriptionsMap();
        _valuesMap = cp.getComponent().getProperties().
                     getValuesMap();
        _baseValuesMap = cp.getComponent().getExecutableComponent().
                         getProperties().
                         getValuesMap();
        _descs = new java.util.HashMap();
        setText("Properties: " + _cp.getComponent().getName());
        buildPanel();
        this.setVisible(false);
        show();
        setPopupPosition(200, 200);
        this.setVisible(true);
        _name.setFocus(true);
//        setPopupPosition((Window.getClientWidth() / 2) -
//                         (this.getOffsetWidth() / 2),
//                         (Window.getClientHeight() / 2) -
//                         (this.getOffsetHeight() / 2));
    }

    //=================
    // Private Methods
    //=================

    /**
     * Build this dialog panel.
     */
    private void buildPanel() {
        TabPanel tabPan = new TabPanel();
        VerticalPanel vp = new VerticalPanel();

        Grid gp = new Grid(_baseValuesMap.size() + 1, 2);
        _tboxes = new TextBox[_baseValuesMap.size() + 1];
        _keys = new String[_baseValuesMap.size() + 1];
        int x = 0;
        for (Iterator itty = _baseValuesMap.keySet().iterator();
                             itty.hasNext() || (x < _tboxes.length); x++) {

            String key = null;
            String desc = null;
            if (x == 0) {
                key = "Name";
                desc = "Display name for this component.";
            } else {
                key = (String) itty.next();
                desc = (String) _descMap.get(key);
            }
            if (desc == null) {
                desc = "";
            }

            HTML lab = new HTML("<bold><right>" + key + ":</right></bold>") {
                public void onBrowserEvent(Event event) {
                    switch (DOM.eventGetType(event)) {
                    case Event.ONMOUSEOVER:
                        _ppp = new TreeItemPopUp((String) _descs.get(getElement()));
                        _ppp.addStyleName("port-popup");
                        _ppp.setPopupPosition(this.getAbsoluteLeft() + 20,
                                              this.getAbsoluteTop() - 20);
                        _ppp.show();
                        break;
                    case Event.ONMOUSEOUT:
                        _ppp.hide();
                        break;
                    }
                }
            };
            _descs.put(lab.getElement(), desc);
            gp.setWidget(x, 0, lab);

            TextBox tb = new TextBox();
            String val = null;
            String ival = null;
            if (x == 0) {
                val = (String) _cp.getComponent().getExecutableComponent().
                      getName();
                ival = (String) _cp.getComponent().getName();
            } else {
                val = (String) _baseValuesMap.get(key);
                ival = (String) _valuesMap.get(key);
            }
            if (ival != null) {
                val = ival;
            }
            tb.setText(val);
            gp.setWidget(x, 1, CursorTextBox.wrapTextBox(tb));
            if (x == 0){
                _name = tb;
            }
            _tboxes[x] = tb;
            _keys[x] = key;
        }

        _ta.setHTML(_cont.buildComponentDescription(_cp.getComponent().getExecutableComponent(), false));
        tabPan.add(gp, "Properties");
        ScrollPanel sp = new ScrollPanel(_ta);
        sp.setAlwaysShowScrollBars(true);
        sp.setHeight("100%");
        _ta.setPixelSize(500, 300);
        tabPan.add(sp, "Description");
        Button ok = new Button("OK");
        ok.addStyleName("dialog-button");
        ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                boolean dirty = false;
                for (int i = 0, n = _tboxes.length; i < n; i++) {
                    String val = _tboxes[i].getText();
                    int pos1 = -1;
                    while ((pos1 = val.indexOf("<")) != -1) {
                        int pos2 = val.indexOf(">");
                        val = val.substring(0, pos1) +
                              val.substring(pos2 + 1, val.length());
                    }
                    if (i == 0) {
                        if (!_cp.getComponent().getName().equals(val)) {
                            _cont.changeComponentName(_cp, val);
                        }
                    } else {
                        if (!_baseValuesMap.get(_keys[i]).equals(val)) {
                            _cp.getComponent().getProperties().
                                    getValuesMap().put(_keys[i], val);
                            dirty = true;
                        }
                    }
                }
                if (dirty) {
                    _cont.setDirty(true);
                }
                clear();
                hide();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.addStyleName("dialog-button");
        cancel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                clear();
                hide();
            }
        });

        vp.add(tabPan);
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(cancel);
        hp.add(ok);
        hp.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        vp.add(hp);
        vp.setCellHorizontalAlignment(hp, vp.ALIGN_RIGHT);
        vp.setWidth("100%");
        tabPan.setWidth("100%");
        tabPan.selectTab(0);
        this.setWidget(vp);
    }

}
