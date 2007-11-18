package org.meandre.workbench.client;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Command;
import org.gwtwidgets.client.wrap.JsGraphicsPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * <p>Title: Main Meandre Workbench Application Class</p>
 *
 * <p>Description: This is the entry point class for the Meandre Workbench
 * GUI</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class Main implements EntryPoint, WindowResizeListener,
        WindowCloseListener {

    //==============
    // Data Members
    //==============

    /* The highest level panel in the work bench application */
    private VerticalPanel _dockPan = new VerticalPanel();

    /* Panel that partitions the tree view and canvas views.*/
    private HorizontalSplitPanel _hsp = new HorizontalSplitPanel();

    /* Graphics panel for drawinf functions (component connections)*/
    private JsGraphicsPanel _jsgPan = new JsGraphicsPanel("g");

    /* Drag panel used for Drag n Drop library.*/
    private AbsolutePanel _absPan = new AbsolutePanel();

    /* Drop panel used for Drag n Drop library.*/
    private AbsolutePanel _boundPan = new AbsolutePanel();

    /* Root menu bar for work bench application */
    private MenuBar _mb = null;

    /* Tab panel used to hold the different tree views.*/
    private TabPanel _tabPan = new TabPanel();

    /*  Controller for the workbench application GUI.*/
    private Controller _controller = null;

    /** Vertical panel used to hold the buttons and labels that
     * appear at the top of the workbench application.
     */
    private HorizontalPanel _buttPan = null;

    /** The vertical split panel that partitions the canvas from the
     * lower detail window.
     */
    private VerticalSplitPanel _vsp = new VerticalSplitPanel();

    /* Scroll panel that wraps the lower detail window.*/
    private ScrollPanel _vscroll = new ScrollPanel();

    //================
    // Public Methods
    //================

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // First, create controller
        // Sets up all remote interfaces
        DOM.setStyleAttribute(RootPanel.get().getBodyElement(), "background",
                              "black");
        _controller = new Controller(this, _jsgPan);
        _controller.login();
    }

    void onModuleLoadContinued() {

        DOM.setStyleAttribute(RootPanel.get().getBodyElement(), "background",
                              "white");

        // Make sure we catch unexpected exceptions in web mode, especially in other browsers
        GWT.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());

        //Get the component Tree
        FlowPanel ctvp = new FlowPanel();
        HorizontalPanel cthp = new HorizontalPanel();
        Button expand = new Button("+");
        Button collapse = new Button("-");
        expand.addStyleName("tree-button");
        collapse.addStyleName("tree-button");
        cthp.add(expand);
        cthp.add(collapse);
        expand.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                _controller.expandAllTreeItems(_controller.getCompTreeHandle());
            }
        });
        collapse.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                _controller.collapseAllTreeItems(_controller.getCompTreeHandle());
            }
        });
        ctvp.add(cthp);
        ctvp.add(_controller.buildCompTree(_controller.getCompTreeHandle(),
                                           _controller.getCompTreeRoot(),
                                           "Available",
                                           Controller.s_COMP_TREE_SORT_TYPE));
        _tabPan.add(ctvp, "COMPONENTS");

        _tabPan.add(_controller.buildFlowTree(_controller.getFlowTreeHandle(),
                                              _controller.getFlowTreeRoot(),
                                              "Available"), "FLOWS");
        _tabPan.add(_controller.buildSearchPanel(), "SEARCH");
        _tabPan.selectTab(0);

        _tabPan.addTabListener(new TabListener() {
            public boolean onBeforeTabSelected(SourcesTabEvents sender,
                                               int tabIndex) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
                if (tabIndex == 2) {
                    _controller.searchPanelTBSetFocus();
                }
            }

        });

        _hsp.setLeftWidget(_tabPan);

        _vscroll = new ScrollPanel();
        _boundPan.add(_absPan);
        _absPan.add(_jsgPan);
        _vsp.setTopWidget(_boundPan);
        _vsp.setBottomWidget(_vscroll);

        _hsp.setRightWidget(_vsp);

        // Hook the window resize event, so that we can adjust the UI.
        Window.addWindowResizeListener(this);
        Window.addWindowCloseListener(this);
        RootPanel.get().add(_dockPan);

        FlowPanel fp = new FlowPanel();
        MenuBar _mb = _controller.buildMenu();
        _buttPan = _controller.buildButtPan();
        fp.add(_mb);
        fp.add(_buttPan);

        _dockPan.add(fp);
        _dockPan.add(_hsp);

        _dockPan.setSize("100%", "100%");
        _hsp.setWidth("99%");
        _hsp.setHeight("99%");
        _buttPan.setWidth("100%");
        _tabPan.setHeight("99%");
        _tabPan.setWidth("99%");
        _hsp.setSplitPosition("220px");
        _vsp.setWidth("100%");
        _vsp.setHeight("100%");
        _vsp.setSplitPosition("80%");
        _controller.getCompTreeHandle().setHeight("100%");
        _controller.getCompTreeHandle().setWidth("100%");
        _controller.getFlowTreeHandle().setHeight("100%");
        _controller.getFlowTreeHandle().setWidth("100%");
        _boundPan.setWidth("100%");
        _boundPan.setHeight("100%");
        _vscroll.setWidth("100%");
        _vscroll.setHeight("100%");
        _absPan.setSize("100%", "100%");
        _jsgPan.setSize("100%", "100%");

        // Call the window resized handler to get the initial sizes setup. Doing
        // this in a deferred command causes it to occur after all widgets' sizes
        // have been computed by the browser.
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onWindowResized(Window.getClientWidth(), Window.getClientHeight());
            }
        });

        onWindowResized(Window.getClientWidth(), Window.getClientHeight());

    }

    //===========================================
    // Implement Interface: WindowResizeListener
    //===========================================

    /**
     * This methjos is called when the work bench application window is
     * ewsized.
     *
     * @param width int The width of the work bench application window.
     * @param height int The height of the work bench application window.
     */
    public void onWindowResized(int width, int height) {

        _dockPan.setSize("100%", "100%");
        _dockPan.setCellWidth(_buttPan, "100%");
        _dockPan.setCellWidth(_hsp, "100%");
        _dockPan.setCellHeight(_hsp, "100%");
        _hsp.setWidth("99%");
        _hsp.setHeight("99%");
        _buttPan.setWidth("100%");
        _tabPan.setHeight("99%");
        _tabPan.setWidth("99%");
        _vsp.setWidth("100%");
        _vsp.setHeight("100%");
        _controller.getCompTreeHandle().setHeight("100%");
        _controller.getCompTreeHandle().setWidth("100%");
        _boundPan.setWidth("100%");
        _boundPan.setHeight("100%");
        _vscroll.setWidth("100%");
        _vscroll.setHeight("100%");
        _absPan.setSize("100%", "100%");
        _jsgPan.setSize("100%", "100%");
        _controller.getFlowTreeHandle().setHeight("100%");
        _controller.getFlowTreeHandle().setWidth("100%");
    }

    //===============================================
    // Interface Implementation: WindowCloseListener
    //===============================================

    /**
     * Method that is called when user clicks to close the work bench
     * application window.
     *
     * @return String A string that displays in a confirm dialog box in
     * response to the user's clicking of the window close icon.
     */
    public String onWindowClosing() {
        if (this._controller.canvasHasComps() && this._controller.isDirty()) {
            return "Your itinerary will not be saved.";
        }
        return null;
    }

    /**
     *  Method that is called after the work bench application window has been
     *  closed by the user.
     */
    public void onWindowClosed() {
        cleanUp();
    }

    //=================
    // Package Methods
    //=================

    /**
     * Get the drop panel that is used for drag and drop operations.
     * @return AbsolutePanel The drop panel that is used for drag and
     * drop operations.
     */
    AbsolutePanel getDropPanel() {
        return _absPan;
    }

    /**
     * Get the boundary panel that is used for drag and drop operations.
     * @return AbsolutePanel The boundary panel that is used for drag and
     * drop operations.
     */
    AbsolutePanel getBoundaryPanel() {
        return _boundPan;
    }

    /**
     * Get the scroll panel that contains detail information.
     *
     * @return ScrollPanel The scroll panel that contains detail information.
     */
    ScrollPanel getCompDescScrollPanel() {
        return _vscroll;
    }

    /**
     * Get the stack panel that contains the tree views.
     *
     * @return StackPanel The stack panel that contains the tree views.
     */
    TabPanel getTabPanel() {
        return this._tabPan;
    }

    /**
     * Call to close the application.
     */
    void closeApp() {
        cleanUp();
        closeAppJS();
    }

    //=================
    // Private Methods
    //=================

    /**
     * Called on application shutdown to clean up any dangling resources.
     */
    private void cleanUp() {
        _controller.clearCanvas();
    }

    /**
     * Javascript call to close the application window.
     */
    private void closeAppJS() {
        RootPanel.get().clear();
        DOM.setStyleAttribute(RootPanel.get().getBodyElement(), "background",
                              "black");
    }

}
