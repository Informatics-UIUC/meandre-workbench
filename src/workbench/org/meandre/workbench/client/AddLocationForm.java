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
import org.meandre.workbench.client.beans.WBCallbackObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>Title: Add Location Form</p>
 *
 * <p>Description: This is a dialog that appears when a location is being added
 * that prompts the user for key information need to add the new location.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class AddLocationForm extends DialogBox {

    //==============
    // Data Members
    //==============


    private String _location = null;
    private String _desc = null;

    private WBFlow _flow = null;

    private TextBox _locTB = null;
    private TextArea _descTA = null;
    private Button _ok = null;

    private Controller _cont = null;


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
    public AddLocationForm(Controller cont) {
        super(false, true);
        _cont = cont;
        buildPanel();
        setText("Add a Repository");
        setVisible(false);
        show();
        _locTB.setFocus(true);
        setPopupPosition((Window.getClientWidth() / 2) -
                         (this.getOffsetWidth() / 2),
                         (Window.getClientHeight() / 2) -
                         (this.getOffsetHeight() / 2));
        _descTA.setWidth(""+_locTB.getOffsetWidth()+"px");
        setVisible(true);
    }

    //=================
    // Private Methods
    //=================

    /**
     * Build this dialog panel.
     */
    private void buildPanel() {
        VerticalPanel vp = new VerticalPanel();

        //name, description, creator, rights,
        Grid gp = new Grid(2, 2);

        HTML lab = new HTML("<strong>Location:</strong>");
        gp.setWidget(0, 0, lab);
        _locTB = new TextBox();
        _locTB.setVisibleLength(60);
        _locTB.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
            }
        });
        gp.setWidget(0, 1, CursorTextBox.wrapTextBox(_locTB));

        lab = new HTML("<strong>Desc:</strong>");
        gp.setWidget(1, 0, lab);
        _descTA = new TextArea();
        _descTA.setCharacterWidth(40);
        _descTA.setVisibleLines(4);
        gp.setWidget(1, 1, CursorTextBox.wrapTextBox(_descTA));


        _ok = new Button("Done");
        _ok.addStyleName("dialog-button");
        _ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {

                _location = _locTB.getText();
                if((_location == null) || (_location.trim().length() == 0)){
                    Window.alert("You must enter a valid location string first!");
                    return;
                }
                _desc = _descTA.getText();
                if((_desc == null) || (_desc.trim().length() == 0)){
                    _desc = "";
                }

                AsyncCallback callback = new AsyncCallback() {
                    public void onSuccess(Object result) {
                        WBCallbackObject cbo = (WBCallbackObject) result;
                        if (cbo.getSuccess()) {
                            _cont.regenerateTabbedPanel(false);
                            //selectlocation tab
                            _cont.getMain().getTabPanel().selectTab(2);
                            _cont.hideStatusBusy();
                            _cont.setStatusMessage(
                                    "Location added successfully.");
                        } else {
                            _cont.hideStatusBusy();
                            _cont.setStatusMessage("");
                            Window.alert(
                                    "Location add operation was NOT successful: " +
                                    cbo.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        _cont.hideStatusBusy();
                        _cont.setStatusMessage("");
                        Window.alert(
                                "AsyncCallBack Failure -- addLocation:  " +
                                caught.getMessage());
                    }
                };
                _cont.showStatusBusy();
                _cont.setStatusMessage("Adding location ...");
                _cont.addLocation(_location, _desc, callback);


                closeForm();

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
