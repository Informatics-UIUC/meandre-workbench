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

/**
 * <p>Title: Workbench Login Dialog</p>
 *
 * <p>Description: Dialog used for acquiring login information for the meandre workbench. </p>
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
    private Image _prog = null;


    public WBLoginDialog(Main main, Controller cont) {
        super(false, true);
        _main = main;
        _cont = cont;
        setWidget(buildPanel());
        setText("Meandre Workbench Login");
        show();
        setPopupPosition((Window.getClientWidth() / 2) -
                         (this.getOffsetWidth() / 2),
                         (Window.getClientHeight() / 2) -
                         (this.getOffsetHeight() / 2));
    }

    private Panel buildPanel() {
        VerticalPanel vp = new VerticalPanel();
        Grid gp = new Grid(4, 2);
        _butt = new Button("Login");
        _butt.setEnabled(false);

        HTML tLab = new HTML("<bold><right>User ID:</right></bold>");
        HTML pLab = new HTML("<bold><right>Password:</right></bold>");
        HTML uLab = new HTML("<bold><right>Location:</right></bold>");
        _tbox = new TextBox();
        _tbox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (keyCode == '\r') {
                    ((TextBox) sender).cancelKey();
                    _butt.click();
                }
                if ((_tbox.getText().trim().length() > 0)
                    && (_tbox.getText().trim().length() > 0)
                    && (_ubox.getText().trim().length() > 0)) {
                    _butt.setEnabled(true);
                } else {
                    _butt.setEnabled(false);
                }
            }
        });
        _pbox = new PasswordTextBox();
        _pbox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (keyCode == '\r') {
                    ((TextBox) sender).cancelKey();
                    _butt.click();
                }
                if ((_tbox.getText().trim().length() > 0)
                    && (_tbox.getText().trim().length() > 0)
                    && (_ubox.getText().trim().length() > 0)) {
                    _butt.setEnabled(true);
                } else {
                    _butt.setEnabled(false);
                }
            }
        });
        _ubox = new TextBox();
        _ubox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (keyCode == '\r') {
                    ((TextBox) sender).cancelKey();
                    _butt.click();
                }
                if ((_tbox.getText().trim().length() > 0)
                    && (_tbox.getText().trim().length() > 0)
                    && (_ubox.getText().trim().length() > 0)) {
                    _butt.setEnabled(true);
                } else {
                    _butt.setEnabled(false);
                }
            }
        });

        gp.setWidget(0, 0, tLab);
        gp.setWidget(0, 1, _tbox);
        gp.setWidget(1, 0, pLab);
        gp.setWidget(1, 1, _pbox);
        gp.setWidget(2, 0, uLab);
        gp.setWidget(2, 1, _ubox);

        gp.setWidget(3, 1, _butt);

        Image logo = new Image("images/meandre-logo.jpg");
        _prog = new Image("images/wait-14x14.gif");
        _prog.setVisible(false);
        gp.setWidget(3, 0, _prog);

        vp.add(logo);
        vp.add(gp);

        _butt.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                _prog.setVisible(true);
                _cont.login(_tbox.getText(), _pbox.getText(), _ubox.getText(),
                            new AsyncCallback() {
                    public void onSuccess(Object result) {
                        _prog.setVisible(false);
                        WBLoginBean lbean = (WBLoginBean)result;
                        if (lbean.getSuccess()){
                            _cont.loginSuccess(lbean.getUserName(), lbean.getSessionID());
                            clear();
                            hide();
                        } else {
                            Window.alert(lbean.getFailureMessage());
                            _tbox.setText("");
                            _pbox.setText("");
                            _ubox.setText("");
                            _butt.setEnabled(false);
                        }
                    }

                    public void onFailure(Throwable caught) {
                        _prog.setVisible(false);
                        // do some UI stuff to show failure
                        Window.alert(
                                "AsyncCallBack Failure -- login():  " +
                                caught.getMessage());
                                    _main.closeApp();
                    }
                });

            }
        });

        return vp;
    }


}
