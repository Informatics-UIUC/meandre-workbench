package org.meandre.workbench.client;

//==============
// Java Imports
//==============

import java.util.Date;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextArea;
import org.meandre.workbench.client.beans.WBFlow;
import org.meandre.workbench.client.beans.WBComponentInstance;
import org.meandre.workbench.client.beans.WBTags;
import org.meandre.workbench.client.beans.WBComponentConnection;
import com.google.gwt.user.client.Window;
import org.meandre.workbench.client.beans.WBComponent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;

/**
 * <p>Title: Flow Build Form</p>
 *
 * <p>Description: This is a dialog that appears when a flow is being saved
 * that prompts the user for key information need to save the new flow.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class FlowBuildForm extends DialogBox {

    //==============
    // Data Members
    //==============


    private WBFlow _flow = null;

    private TextBox _name = null;
    private TextBox _creator = null;
    private TextBox _baseURL = null;
    private TextArea _desc = null;
    private TextArea _rights = null;
    private TextBox _tags = null;
    private WBCommand _cmd = null;
    private Button _ok = null;

    private Set _comps = null;
    private Set _conns = null;

    private boolean _saveas = true;


    //==============
    // Constructors
    //==============

    /**
     * Constructor for flow save form.
     *
     * @param flow WBFlow Flow object being saved.
     * @param comps Set Current set of active canvas components.
     * @param conns Set Current set of active canvas connections.
     * @param cmd WBCommand Follow on command or null if none exists.
     */
    public FlowBuildForm(WBFlow flow, Set comps, Set conns, WBCommand cmd) {
        super(false, true);
        _cmd = cmd;
        _flow = flow;
        _comps = comps;
        _conns = conns;
        buildPanel();
        setText("Flow Properties");
        setVisible(false);
        show();
        if (_saveas) {
            _name.setFocus(true);
        } else {
            _desc.setFocus(true);
        }
        setPopupPosition((Window.getClientWidth() / 2) -
                         (this.getOffsetWidth() / 2),
                         (Window.getClientHeight() / 2) -
                         (this.getOffsetHeight() / 2));
        setVisible(true);
    }

    //=================
    // Private Methods
    //=================

    /**
     * Build this dialog panel.
     */
    private void buildPanel() {
        if (_flow.getFlowID().trim().length() > 0) {
            _saveas = false;
        } else {
            _saveas = true;
        }
        VerticalPanel vp = new VerticalPanel();

        //name, description, creator, rights,
        Grid gp = new Grid(6, 2);

        HTML lab = new HTML("<strong>Name:</strong>");
        gp.setWidget(0, 0, lab);
        _name = new TextBox();
        _name.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
            }
        });
        _name.setText("untitled");
        String nm = _flow.getName();
        if (nm.length() > 0) {
            _name.setText(nm);
        }
        gp.setWidget(0, 1, CursorTextBox.wrapTextBox(_name));
        if (!_saveas) {
            _name.setEnabled(false);
        }

        lab = new HTML("<strong>Desc:</strong>");
        gp.setWidget(1, 0, lab);
        _desc = new TextArea();
        _desc.setCharacterWidth(40);
        _desc.setVisibleLines(4);
        String de = _flow.getDescription();
        if (de.length() > 0) {
            _desc.setText(de);
        }
        gp.setWidget(1, 1, CursorTextBox.wrapTextBox(_desc));

        lab = new HTML("<strong>Creator:</strong>");
        gp.setWidget(2, 0, lab);
        _creator = new TextBox();
        _creator.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
            }
        });
        String cr = _flow.getCreator();
        if (cr.length() > 0) {
            _creator.setText(cr);
        } else {
            _creator.setText(Controller.s_controller.getUserName());
        }
        gp.setWidget(2, 1, CursorTextBox.wrapTextBox(_creator));

        lab = new HTML("<strong>Rights:</strong>");
        gp.setWidget(3, 0, lab);
        _rights = new TextArea();
        _rights.setCharacterWidth(40);
        _rights.setVisibleLines(4);
        String ri = _flow.getRights();
        if (ri.length() > 0) {
            _rights.setText(ri);
        }
        gp.setWidget(3, 1, CursorTextBox.wrapTextBox(_rights));

        lab = new HTML("<strong>Base URL:</strong>");
        if (!_saveas) {
            lab = new HTML("<strong>URI:</strong>");
        }

        gp.setWidget(4, 0, lab);
        _baseURL = new TextBox();
        _baseURL.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
            }
        });
        _baseURL.setVisibleLength(50);
        String bu = _flow.getBaseURL();
        if (bu.length() > 0) {
            _baseURL.setText(bu);
        } else {
            _baseURL.setText(Controller.s_baseURL);
        }
        gp.setWidget(4, 1, CursorTextBox.wrapTextBox(_baseURL));
        if (!_saveas) {
            _baseURL.setText(_flow.getFlowID());
            _baseURL.setEnabled(false);
        }

        lab = new HTML("<strong>Tags:</strong>");
        gp.setWidget(5, 0, lab);
        _tags = new TextBox();
        _tags.setVisibleLength(50);
        _tags.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
            }
        });
        String tgs = _flow.getTags().toString();
        if (tgs.length() > 0) {
            _tags.setText(tgs);
        }
        gp.setWidget(5, 1, CursorTextBox.wrapTextBox(_tags));

        _ok = new Button("Done");
        _ok.addStyleName("dialog-button");
        _ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                String burl = "";
                _flow.setCreationDate(new Date());
                _flow.setCreator((_creator.getText() == null) ? "" :
                                 _creator.getText().trim());
                if (_saveas) {
                    String name = _name.getText();
                    if ((name == null) || (name.trim().length() == 0)) {
                        Window.alert("Must enter a valid name!");
                        _name.setText("");
                        return;
                    }
                    _flow.setBaseURL(((_baseURL.getText() == null) ||
                                      (_baseURL.getText().trim().length() == 0)) ?
                                     "" : _baseURL.getText().trim());
                    burl = _flow.getBaseURL();
                    if ((burl.trim().length() > 0) && !burl.endsWith("/")
                        && !burl.endsWith("\\")) {
                        _flow.setBaseURL(burl + "/");
                    }
                    if (Controller.s_controller.flowByNameBaseExists(name,
                            _flow.getBaseURL())) {
                        if (!Window.confirm("Overwrite pre-exisiting flow?")) {
                            return;
                        }
                    }

                    _flow.setName(_name.getText().trim());
                }

                _flow.setDescription((_desc.getText() == null) ? "" :
                                     _desc.getText().trim());
                _flow.setRights((_rights.getText() == null) ? "" :
                                _rights.getText().trim());

                if ((_tags.getText() == null) ||
                    (_tags.getText().trim().length() == 0)) {
                    _flow.getTags().clear();
                } else {
                    if (!_flow.getTags().toString().equals(_tags.getText())) {
                        String txt = _tags.getText();
                        String[] txts = txt.split(" ");
                        Set tagset = new HashSet();
                        for (int i = 0, n = txts.length; i < n; i++) {
                            tagset.add(txts[i].trim());
                        }
                        _flow.getTags().clear();
                        _flow.setTags(new WBTags(tagset));
                    }
                }

                clear();
                hide();

                //add component instances
                for (Iterator itty = _comps.iterator(); itty.hasNext(); ) {
                    WBComponentInstance ci = (WBComponentInstance) ((
                            ComponentPanel)
                            itty.next()).
                                             getComponent();
                    WBComponent comp = ci.getExecutableComponent();
                    ci.setExecutableComponentInstance(comp.getID() + "/"
                            + ci.getName().toLowerCase().replaceAll(" ", "-") +
                            "/"
                            + _flow.getName().toLowerCase().replaceAll(" ", "-"));
                    _flow.addExecutableComponentInstance(ci);
                }

                int connNum = 0;
                if (_saveas) {
                    burl = _flow.getBaseURL();
                    if (burl.trim().length() == 0) {
                        burl = "http://test.org/flow/" +
                               System.currentTimeMillis()
                               +
                               _flow.getName().toLowerCase().replaceAll(" ",
                                "-");
                    }
                }
                for (Iterator itty = _conns.iterator(); itty.hasNext(); ) {
                    PortConn conn = (PortConn) itty.next();
                    WBComponentConnection cc = new WBComponentConnection(
                            burl +
                            _flow.getName().toLowerCase().replaceAll(" ", "-") +
                            "/connector/data/" +
                            connNum,
                            conn.getFrom().getParentComponent().getComponent().
                            getExecutableComponentInstance(),
                            conn.getFrom().getDataportObj().getResourceID(),
                            conn.getTo().getParentComponent().getComponent().
                            getExecutableComponentInstance(),
                            conn.getTo().getDataportObj().getResourceID());
                    _flow.addComponentConnection(cc);
                    connNum++;
                }

                if (_cmd != null) {
                    _cmd.execute(_flow);
                }

            }
        });

        Button cancel = new Button("Cancel");
        cancel.addStyleName("dialog-button");
        cancel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                closeForm();
            }
        });

        vp.add(gp);

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(cancel);
        hp.add(_ok);
        hp.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

        vp.add(hp);
        vp.setCellHorizontalAlignment(hp, vp.ALIGN_RIGHT);
        //vp.setWidth("100%");
        setWidget(vp);
    }

    private void ifKeycodeEnterSubmit(char keyCode, Widget sender) {
        if (keyCode == '\r') {
            ((TextBox) sender).cancelKey();
            _ok.click();
        }
    }

    private void closeForm() {
        clear();
        hide();
    }

}
