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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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

    private Controller _controller = null;
    private Main _main = null;
    private Button _btnLogin = null;
    private TextBox _tbUserId = null;
    private PasswordTextBox _ptbPassword = null;
    private TextBox _tbServer = null;
    private TextBox _tbPort = null;
    private Image _imgBusy = null;


    public WBLoginDialog(Main main, Controller cont) {
        super(false, true);
        _main = main;
        _controller = cont;
        setWidget(createLoginPanel());
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

    private Panel createLoginPanel() {
        VerticalPanel vpLoginMain = new VerticalPanel();
        Grid gpLoginDetails = new Grid(4, 2);

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
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (!(Character.isDigit(keyCode)
                        || (int)keyCode == 8        /* BACKSPACE */
                        || (int)keyCode == 46       /* DELETE    */
                        || (int)keyCode == 35       /* HOME      */
                        || (int)keyCode == 36       /* END       */
                        || (int)keyCode == 37       /* LEFT      */
                        || (int)keyCode == 39)) {   /* RIGHT     */
                    ((TextBox)sender).cancelKey();
                    return;
                }
            }
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                checkEnableSubmit();
            }
        });

        /* Add click listener for cancel button. */
        btnCancel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                closeForm();
                _controller.getMain().closeApp();
            }
        });

        /* Add click listener for submit button. */
        _btnLogin.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {

                if (!_btnLogin.isEnabled()) {
                    return;
                }

                _imgBusy.setVisible(true);
                String sPort = "";
                if (_tbPort.getText().trim().length() > 0) {
                    sPort = ":" + _tbPort.getText().trim();
                }
                if (_tbServer.getText().toLowerCase().equals("localhost")) {
                    _tbServer.setText("127.0.0.1");
                }
                _controller.login(_tbUserId.getText(), _ptbPassword.getText(),
                        "http://" + _tbServer.getText() + sPort + "/",
                        new AsyncCallback() {
                            public void onSuccess(Object result) {
                                _imgBusy.setVisible(false);
                                WBLoginBean loginBean = (WBLoginBean) result;
                                if (loginBean.getSuccess()) {
                                    _controller.loginSuccess(loginBean.getUserName(),
                                            loginBean.getSessionID(), loginBean.getBaseURL());
                                    closeForm();
                                } else {
                                    Window.alert(loginBean.getFailureMessage());
                                    resetForm();
                                }
                            }

                            public void onFailure(Throwable caught) {
                                _imgBusy.setVisible(false);
                                // do some UI stuff to show failure
                                Window
                                        .alert("AsyncCallBack Failure -- login():  "
                                                + caught.getMessage());
                                //closeForm();
                                //_main.closeApp();
                            }
                        });

            }
        });

        gpLoginDetails.setWidget(0, 0, lblUserId);
        gpLoginDetails.setWidget(0, 1, _tbUserId);
        gpLoginDetails.setWidget(1, 0, lblPassword);
        gpLoginDetails.setWidget(1, 1, _ptbPassword);
        gpLoginDetails.setWidget(2, 0, lblServer);
        _tbServer.setText("127.0.0.1");
        gpLoginDetails.setWidget(2, 1, _tbServer);
        gpLoginDetails.setWidget(3, 0, lblPort);
        _tbPort.setText("1714");
        gpLoginDetails.setWidget(3, 1, _tbPort);

        // FOR DEV ONLY
        _tbUserId.setText("admin");
        // _pbox.setText("admin");

        Image logo = new Image("images/meandre-logo.jpg");
        logo.setPixelSize(200, 36);
        _imgBusy = new Image("images/wait-14x14.gif");
        _imgBusy.setVisible(false);

        HorizontalPanel hpButtons = new HorizontalPanel();
        hpButtons.add(_imgBusy);
        hpButtons.add(btnCancel);
        hpButtons.add(_btnLogin);
        hpButtons.setSpacing(20);
        hpButtons.setCellWidth(_imgBusy, "20px");
        hpButtons.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

        vpLoginMain.add(logo);
        vpLoginMain.add(gpLoginDetails);
        vpLoginMain.add(hpButtons);

        vpLoginMain.setCellHorizontalAlignment(hpButtons, VerticalPanel.ALIGN_RIGHT);
        vpLoginMain.setCellHorizontalAlignment(logo, VerticalPanel.ALIGN_CENTER);

        return vpLoginMain;
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
