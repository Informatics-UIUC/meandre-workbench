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
import com.google.gwt.user.client.ui.Panel;
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
    private Button _butt = null;
    private TextBox _tbox = null;
    private PasswordTextBox _pbox = null;
    private TextBox _ubox = null;
    private TextBox _prtbox = null;
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
        _tbox.setFocus(true);
    }

    private Panel buildPanel() {
        VerticalPanel vp = new VerticalPanel();
        Grid gp = new Grid(4, 2);

        _butt = new Button("Login");
        _butt.setEnabled(false);
        Button cancel = new Button("Cancel");

        HTML tLab = new HTML("<strong>User ID:</strong>");
        HTML pLab = new HTML("<strong>Password:</strong>");
        HTML uLab = new HTML("<strong>Domain:</strong>");
        HTML prtLab = new HTML("<strong>Port:</strong>");
        _tbox = new TextBox();
        _tbox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                checkEnableSubmit();            }
        });
        _pbox = new PasswordTextBox();
        _pbox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                checkEnableSubmit();            }
        });
        _ubox = new TextBox();
        _ubox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                checkEnableSubmit();           }
        });

        _prtbox = new TextBox();
        _prtbox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                ifKeycodeEnterSubmit(keyCode, sender);
                if (!Character.isDigit(keyCode)){
                    ((TextBox) sender).cancelKey();
                    return;
                }
                checkEnableSubmit();            }
        });

        /* Add click listener for cancel button.*/
        cancel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                closeForm();
                _cont.getMain().closeApp();
            }
        });

        /* Add click listener for submit button.*/
        _butt.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {

                if (!_butt.isEnabled()){
                    return;
                }

                _busy.setVisible(true);
                String prt = "";
                if (_prtbox.getText().trim().length() > 0){
                    prt = ":" + _prtbox.getText().trim();
                }
                if (_ubox.getText().toLowerCase().equals("localhost")){
                    _ubox.setText("127.0.0.1");
                }
                _cont.login(_tbox.getText(), _pbox.getText(), "http://"
                            + _ubox.getText() + prt + "/",
                            new AsyncCallback() {
                    public void onSuccess(Object result) {
                        _busy.setVisible(false);
                        WBLoginBean lbean = (WBLoginBean)result;
                        if (lbean.getSuccess()){
                            _cont.loginSuccess(lbean.getUserName(),
                                               lbean.getSessionID(),
                                               lbean.getBaseURL());
                            closeForm();
                        } else {
                            Window.alert(lbean.getFailureMessage());
                            resetForm();                        }
                    }

                    public void onFailure(Throwable caught) {
                        _busy.setVisible(false);
                        // do some UI stuff to show failure
                        Window.alert(
                                "AsyncCallBack Failure -- login():  " +
                                caught.getMessage());
                                    closeForm();
                                    _main.closeApp();
                   }
                });

            }
        });

        gp.setWidget(0, 0, tLab);
        gp.setWidget(0, 1, _tbox);
        gp.setWidget(1, 0, pLab);
        gp.setWidget(1, 1, _pbox);
        gp.setWidget(2, 0, uLab);
        _ubox.setText("127.0.0.1");
        gp.setWidget(2, 1, _ubox);
        gp.setWidget(3, 0, prtLab);
        _prtbox.setText("1714");
        gp.setWidget(3, 1, _prtbox);

        // FOR DEV ONLY
        _tbox.setText("admin");
        _pbox.setText("admin");



        Image logo = new Image("images/meandre-logo.jpg");
        logo.setPixelSize(200, 36);
        _busy = new Image("images/wait-14x14.gif");
        _busy.setVisible(false);
        HorizontalPanel hpan = new HorizontalPanel();
        hpan.add(_busy);
        hpan.add(cancel);
        hpan.add(_butt);
        hpan.setSpacing(20);
        hpan.setCellWidth(_busy, "20px");
        hpan.setHorizontalAlignment(hpan.ALIGN_RIGHT);

        vp.add(logo);
        vp.add(gp);
        vp.add(hpan);

        vp.setCellHorizontalAlignment(hpan,vp.ALIGN_RIGHT);
        vp.setCellHorizontalAlignment(logo,vp.ALIGN_CENTER);

        return vp;
    }

    private void closeForm(){
        clear();
        hide();
    }

    private void resetForm(){
        _tbox.setText("");
        _pbox.setText("");
        _butt.setEnabled(false);
    }

    private void checkEnableSubmit(){
        if ((_tbox.getText().trim().length() > 0)
            && (_tbox.getText().trim().length() > 0)
            && (_ubox.getText().trim().length() > 0)
            && (_prtbox.getText().trim().length() > 0)) {
            _butt.setEnabled(true);
        } else {
            _butt.setEnabled(false);
        }
    }

    private void ifKeycodeEnterSubmit(char keyCode, Widget sender){
        if (keyCode == '\r') {
            if (sender instanceof TextBox){
                ((TextBox) sender).cancelKey();
            } else {
                ((PasswordTextBox) sender).cancelKey();
            }
            _butt.click();
        }
    }
}
