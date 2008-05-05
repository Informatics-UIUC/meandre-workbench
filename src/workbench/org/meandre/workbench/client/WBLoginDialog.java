package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.meandre.workbench.client.beans.WBLoginBean;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * <p>Title: Workbench Login Dialog</p>
 *
 * <p>Description: Dialog used for acquiring login information for the
 * meandre workbench. </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class WBLoginDialog extends DialogBox {

    //==============
    // Data Members
    //==============

    private Controller _cont = null;
    private Main _main = null;
    private Button _btnLogin = null;
    private TextBox _tbUserId = null;
    private PasswordTextBox _ptbPassword = null;
    private TextBox _tbServer = null;
    private TextBox _tbPort = null;
    private Image _busy = null;


    public WBLoginDialog(Main main, Controller cont) {
        super(false, true);
        _main = main;
        _cont = cont;
        setWidget(buildPanel());
        setText("Meandre Workbench Login");
        this.setVisible(false);
        show();
        setPopupPosition((Window.getClientWidth() / 2) -
                         (this.getOffsetWidth() / 2),
                         (Window.getClientHeight() / 2) -
                         (this.getOffsetHeight() / 2));
        this.setVisible(true);
        checkEnableSubmit();
        _tbUserId.setFocus(true);
    }

    private Panel buildPanel() {
        VerticalPanel vp = new VerticalPanel();
        Grid gp = new Grid(4, 2);

        _btnLogin = new Button("Login");
        _btnLogin.setEnabled(false);
        Button btnCancel = new Button("Cancel");

        HTML lblUserId = new HTML("<strong>User ID:</strong>");
        HTML lblPassword = new HTML("<strong>Password:</strong>");
        HTML lblServer = new HTML("<strong>Server:</strong>");
        HTML lblPort = new HTML("<strong>Port:</strong>");

        _tbUserId = new TextBox();
        _tbUserId.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                checkEnableSubmit();
            }
        });

        _ptbPassword = new PasswordTextBox();
        _ptbPassword.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                checkEnableSubmit();
            }
        });

        _tbServer = new TextBox();
        _tbServer.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                checkEnableSubmit();
            }
        });

        _tbPort = new TextBox();
        _tbPort.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                if (!Character.isDigit(keyCode)) {
                    ((TextBox) sender).cancelKey();
                    return;
                }
                checkEnableSubmit();
            }
        });

        /* Add click listener for cancel button. */
        btnCancel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                closeForm();
                _cont.getMain().closeApp();
            }
        });

        /* Add click listener for submit button. */
        _btnLogin.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {

                if (!_btnLogin.isEnabled()) {
                    return;
                }

                _busy.setVisible(true);
                String prt = "";
                if (_tbPort.getText().trim().length() > 0) {
                    prt = ":" + _tbPort.getText().trim();
                }
                if (_tbServer.getText().toLowerCase().equals("localhost")) {
                    _tbServer.setText("127.0.0.1");
                }
                _cont.login(_tbUserId.getText(), _ptbPassword.getText(),
                        "http://" + _tbServer.getText() + prt + "/",
                        new AsyncCallback() {
                            public void onSuccess(Object result) {
                                _busy.setVisible(false);
                                WBLoginBean lbean = (WBLoginBean) result;
                                if (lbean.getSuccess()) {
                                    _cont.loginSuccess(lbean.getUserName(),
                                            lbean.getSessionID(), lbean
                                                    .getBaseURL());
                                    closeForm();
                                } else {
                                    Window.alert(lbean.getFailureMessage());
                                    resetForm();
                                }
                            }

                            public void onFailure(Throwable caught) {
                                _busy.setVisible(false);
                                // do some UI stuff to show failure
                                Window
                                        .alert("AsyncCallBack Failure -- login():  "
                                                + caught.getMessage());
                                closeForm();
                                _main.closeApp();
                            }
                        });

            }
        });

        gp.setWidget(0, 0, lblUserId);
        gp.setWidget(0, 1, _tbUserId);
        gp.setWidget(1, 0, lblPassword);
        gp.setWidget(1, 1, _ptbPassword);
        gp.setWidget(2, 0, lblServer);
        _tbServer.setText("127.0.0.1");
        gp.setWidget(2, 1, _tbServer);
        gp.setWidget(3, 0, lblPort);
        _tbPort.setText("1714");
        gp.setWidget(3, 1, _tbPort);

        // FOR DEV ONLY
        _tbUserId.setText("admin");
        // _pbox.setText("admin");

        Image logo = new Image("images/meandre-logo.jpg");
        logo.setPixelSize(200, 36);
        _busy = new Image("images/wait-14x14.gif");
        _busy.setVisible(false);
        HorizontalPanel hpan = new HorizontalPanel();
        hpan.add(_busy);
        hpan.add(btnCancel);
        hpan.add(_btnLogin);
        hpan.setSpacing(20);
        hpan.setCellWidth(_busy, "20px");
        hpan.setHorizontalAlignment(hpan.ALIGN_RIGHT);

        vp.add(logo);
        vp.add(gp);
        vp.add(hpan);

        vp.setCellHorizontalAlignment(hpan, vp.ALIGN_RIGHT);
        vp.setCellHorizontalAlignment(logo, vp.ALIGN_CENTER);

        return vp;
    }

    private void closeForm(){
        clear();
        hide();
    }

    private void resetForm(){
        _tbUserId.setText("");
        _ptbPassword.setText("");
        _btnLogin.setEnabled(false);
    }

    private void checkEnableSubmit(){
        if ((_tbUserId.getText().trim().length() > 0)
            && (_tbServer.getText().trim().length() > 0)
            && (_tbPort.getText().trim().length() > 0)) {
            _btnLogin.setEnabled(true);
        } else {
            _btnLogin.setEnabled(false);
        }
    }

    private void ifKeycodeEnterSubmit(char keyCode, Widget sender){
        if (keyCode == '\r') {
            ((TextBoxBase)sender).cancelKey();
            _btnLogin.click();
        }
    }
}
