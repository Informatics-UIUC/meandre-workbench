package org.meandre.workbench.client;

//==============
// Java Imports
//==============

import java.util.Map;
import java.util.HashMap;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.TextArea;

/**
 * <p>Title: Workbench Repository Upload Form</p>
 *
 * <p>Description: A form for uploading components, flows, and their supporting
 * jars (contexts) to a user's repository.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class WBRepositoryUploadForm extends DialogBox {

    //==============
    // Data Members
    //==============

    private Controller _cont = null;



    static final String s_repoFileName = Controller.s_PROXY_POST_REPO_FIELDS_KEY;
    static final String s_ctxFileName = Controller.s_PROXY_POST_JAR_FIELDS_KEY;

    private FileUpload _currentFU = null;
    private ListBox _lbRepos = null;
    private HorizontalPanel _hpanRepo = null;
    private Map _repos = new HashMap();

    private FileUpload _jarFU = null;
    private ListBox _lbJars = null;
    private HorizontalPanel _hpanJar = null;
    private Map _jars = new HashMap();
    private FormPanel _form = null;

    //==============
    // Constructors
    //==============

    public WBRepositoryUploadForm(Controller cont) {
        super(false, true);
        _cont = cont;
        buildForm();
        setText("Repository Upload Form");
        this.setVisible(false);
        show();
        setPopupPosition((Window.getClientWidth() / 2) -
                         (this.getOffsetWidth() / 2),
                         (Window.getClientHeight() / 2) -
                         (this.getOffsetHeight() / 2));
        this.setVisible(true);
    }

    void buildForm() {
        // Create a FormPanel and point it at a service.
        _form = new FormPanel();
        this.setWidget(_form);
        _form.setAction(Controller.s_PROXY_SERVLET_PATH);


        //===================================
        //===================================
        //===================================

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multipart MIME encoding.
        _form.setEncoding(FormPanel.ENCODING_MULTIPART);
        _form.setMethod(FormPanel.METHOD_POST);


        // Create a panel to hold all of the form widgets.
        VerticalPanel panel = new VerticalPanel();

        _form.setWidget(panel);

        //====================
        // Add repo controls
        //====================
        Label lab1 = new Label("Repository Description Files:");
        lab1.addStyleName("fu-label");
        panel.add(lab1);
        _hpanRepo = new HorizontalPanel();
        //create an add to listbox button


        Button butt = new Button("Add Repository", new ClickListener() {
            public void onClick(Widget sender) {
                String val = _currentFU.getFilename();
                if ((val != null) && (val.trim().length() > 0)) {
                    _repos.put(val, _currentFU);
                    _lbRepos.addItem(val.trim());
                    _currentFU.setVisible(false);
                    _currentFU = new FileUpload();
                    _currentFU.setName(s_repoFileName);
                    _currentFU.setTitle("Find a repository descriptor file ...");
                    _currentFU.addStyleName("top-menu-button");
                    _hpanRepo.add(_currentFU);
                }
            }
        });
        butt.addStyleName("top-menu-button");
        butt.addStyleName("top-menu-button-text");
        _hpanRepo.add(butt);
        // Create a FileUpload widget.
        _currentFU = new FileUpload();
        _currentFU.setName(this.s_repoFileName);
        _currentFU.setTitle("Find a repository descriptor file ...");
        _currentFU.addStyleName("top-menu-button");
        _hpanRepo.add(_currentFU);
        _hpanRepo.setSpacing(5);
        panel.add(_hpanRepo);
        // Create a ListBox for repository files
        _lbRepos = new ListBox();
        _lbRepos.setMultipleSelect(false);
        _lbRepos.setVisibleItemCount(7);
        _lbRepos.setWidth("99%");
        //ScrollPanel sp = new ScrollPanel(_lbRepos);
        //sp.setAlwaysShowScrollBars(true);
        _lbRepos.addStyleName("fu-listbox");
        panel.add(_lbRepos);
        butt = new Button("Remove", new ClickListener() {
            public void onClick(Widget sender) {
                int sel = _lbRepos.getSelectedIndex();
                if ((sel >= 0) && (sel < _lbRepos.getItemCount())){
                    String val = _lbRepos.getItemText(_lbRepos.getSelectedIndex());

                    if ((val != null) && (val.trim().length() > 0)) {
                        Object obj = _repos.get(val);
                        _repos.remove(val);
                        _lbRepos.removeItem(_lbRepos.getSelectedIndex());
                        _hpanRepo.remove((Widget) obj);
                    }
                }
            }
        });
        butt.addStyleName("top-menu-button");
        butt.addStyleName("top-menu-button-text");
        panel.add(butt);
        //====================
        //====================
        //====================

        panel.add(new HTML("<br>"));
        //===================================
        // Add session id to form as hidden
        //===================================
        Hidden sid = new Hidden(Controller.s_GET_PARAM_SID_KEY, _cont.getSessionID());
        sid.setID(Controller.s_GET_PARAM_SID_KEY);
        panel.add(sid);

        Hidden url = new Hidden(Controller.s_PROXY_TARGET_KEY, Controller.s_CORE_ADD_PATH);
        sid.setID(Controller.s_PROXY_TARGET_KEY);
        panel.add(url);

        Hidden meth = new Hidden(Controller.s_PROXY_POST_METHOD_KEY, Controller.s_PROXY_POST_METHOD_REPO_UPLOAD);
        sid.setID(Controller.s_PROXY_POST_METHOD_KEY);
        panel.add(meth);
        //====================
        // Add ctx controls
        //====================

        lab1 = new Label("Context Resource Files:");
        lab1.addStyleName("fu-label");
        panel.add(lab1);
        _hpanJar = new HorizontalPanel();
        //create an add to listbox button


        butt = new Button("Add Context", new ClickListener() {
            public void onClick(Widget sender) {
                String val = _jarFU.getFilename();
                if ((val != null) && (val.trim().length() > 0)) {
                    _jars.put(val, _jarFU);
                    _lbJars.addItem(val.trim());
                    _jarFU.setVisible(false);
                    _jarFU = new FileUpload();
                    _jarFU.setName(s_ctxFileName);
                    _jarFU.setTitle("Find a context resource ...");
                    _jarFU.addStyleName("top-menu-button");
                    _hpanJar.add(_jarFU);
                }
            }
        });
        butt.addStyleName("top-menu-button");
        butt.addStyleName("top-menu-button-text");
        _hpanJar.add(butt);
        // Create a FileUpload widget.
        _jarFU = new FileUpload();
        _jarFU.setName(this.s_ctxFileName);
        _jarFU.setTitle("Find a context resource ...");
        _jarFU.addStyleName("top-menu-button");
        _hpanJar.add(_jarFU);
        _hpanJar.setSpacing(5);
        panel.add(_hpanJar);
        // Create a ListBox for context files
        _lbJars = new ListBox();
        _lbJars.setMultipleSelect(false);
        _lbJars.setVisibleItemCount(10);
        _lbJars.setWidth("99%");
        _lbJars.addStyleName("fu-listbox");

        //sp = new ScrollPanel(_lbJars);
        //sp.setAlwaysShowScrollBars(true);
        panel.add(_lbJars);
        butt = new Button("Remove", new ClickListener() {
            public void onClick(Widget sender) {
                int sel = _lbJars.getSelectedIndex();
                if ((sel >= 0) && (sel < _lbJars.getItemCount())){
                    String val = _lbJars.getItemText(_lbJars.getSelectedIndex());
                    if ((val != null) && (val.trim().length() > 0)) {
                        Object obj = _jars.get(val);
                        _jars.remove(val);
                        _lbJars.removeItem(_lbJars.getSelectedIndex());
                        _hpanJar.remove((Widget) obj);
                    }
                }
            }
        });
        butt.addStyleName("top-menu-button");
        butt.addStyleName("top-menu-button-text");
        panel.add(butt);
        //====================
        //====================
        //====================

        panel.add(new HTML("<br>"));

        //==============================================
        // Cancel and Submit Buttons
        //==============================================

        HorizontalPanel hpan01 = new HorizontalPanel();
        hpan01.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        hpan01.setSpacing(20);

        // Add a 'cancel' button.
        butt = new Button("Cancel", new ClickListener() {
            public void onClick(Widget sender) {
                _form.clear();
                clear();
                hide();
            }
        });
        butt.addStyleName("top-menu-button");
        butt.addStyleName("top-menu-button-text");
        hpan01.add(butt);

        // Add a 'submit' button.
        butt = new Button("Submit", new ClickListener() {
            public void onClick(Widget sender) {
                _cont.showStatusBusy();
                _cont.setStatusMessage("Uploading file to repository ...");
                _form.submit();
            }
        });
        butt.addStyleName("top-menu-button");
        butt.addStyleName("top-menu-button-text");
        hpan01.add(butt);

        panel.add(hpan01);
        panel.setCellHorizontalAlignment(hpan01, panel.ALIGN_RIGHT);

        //==============================================
        //==============================================
        //==============================================

        panel.setWidth("99%");
        panel.addStyleName("fu-panel");

        // Add an event handler to the form.
        _form.addFormHandler(new FormHandler() {
            public void onSubmit(FormSubmitEvent event) {
                // This event is fired just before the form is submitted. We can take
                // this opportunity to perform validation.

                if ((_lbJars.getItemCount() == 0) && (_lbRepos.getItemCount() == 0)){
                    Window.alert("Need to add at least one file.");
                    event.setCancelled(true);
                    return;
                }
                if (_lbRepos.getItemCount() > 0){
                    int ttlCnt = 0, ntCnt = 0, rdfCnt = 0, otherCnt = 0;
                    int cntSz = _lbRepos.getItemCount();
                    for (int i = 0, n = cntSz; i < n; i++){
                        String s = _lbRepos.getItemText(i);
                        if (s.toLowerCase().endsWith(".ttl")){
                            ttlCnt++;
                        } else if (s.toLowerCase().endsWith(".nt")){
                            ntCnt++;
                        } else if (s.toLowerCase().endsWith(".rdf")){
                            rdfCnt++;
                        } else {
                            otherCnt++;
                        }
                    }
                    if (otherCnt > 0){
                        Window.alert("Repository files must all have extensions 'rdf'.");
                        event.setCancelled(true);
                        return;
                    }
                    // For now, all repo files must end with rdf ...
                    if (/*(ttlCnt != cntSz) && (ntCnt != cntSz) && */(rdfCnt != cntSz)){
                        Window.alert("Repository files must all have extensions 'rdf'.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (_lbJars.getItemCount() > 0){
                    int jarCnt = 0, zipCnt = 0, otherCnt = 0;
                    int cntSz = _lbJars.getItemCount();
                    for (int i = 0, n = cntSz; i < n; i++){
                        String s = _lbJars.getItemText(i);
                        if (s.toLowerCase().endsWith(".jar")){
                            jarCnt++;
                        } else if (s.toLowerCase().endsWith(".zip")){
                            zipCnt++;
                        } else {
                            otherCnt++;
                        }
                    }
                    if (otherCnt > 0){
                        Window.alert("Context files must have extensions either 'jar' or 'zip'.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                // When the form submission is successfully completed, this event is
                // fired. Assuming the service returned a response of type text/html,
                // we can get the result text here (see the FormPanel documentation for
                // further explanation).
                if (event.getResults().indexOf("success") == -1){
                    _cont.hideStatusBusy();
                    _cont.setStatusMessage("Error in file upload.");
                    _cont.getMain().getCompDescScrollPanel().clear();
                    TextArea ta = new TextArea();
                    ta.setCharacterWidth(120);
                    ta.setText(event.getResults());
                    _cont.getMain().getCompDescScrollPanel().add(ta);
                    ta.setHeight("100%");

                    _form.clear();
                    clear();
                    hide();

                } else {

                    _form.clear();
                    clear();
                    hide();

                    _cont.setStatusMessage("Upload successful. Regenerating tree views ...");

                    _cont.regenerateTabbedPanel(false);

                    _cont.hideStatusBusy();

                }
            }
        });



    }
}
