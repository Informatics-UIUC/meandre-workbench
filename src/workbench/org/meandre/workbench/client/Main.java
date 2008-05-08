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
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;


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
    private VerticalPanel _vpRoot = new VerticalPanel();

    /* Panel that partitions the tree view and canvas views.*/
    private HorizontalSplitPanel _hspMain = new HorizontalSplitPanel();

    /* Graphics panel for drawinf functions (component connections)*/
    private JsGraphicsPanel _jsgPan = new JsGraphicsPanel("g");

    /* Drag panel used for Drag n Drop library.*/
    private AbsolutePanel _absPan = new AbsolutePanel();

    /* Drop panel used for Drag n Drop library.*/
    private AbsolutePanel _boundPan = new AbsolutePanel();

    /* Tab panel used to hold the different tree views.*/
    private TabPanel _tabPan = new TabPanel();

    /*  Controller for the workbench application GUI.*/
    private Controller _controller = null;

    /** Vertical panel used to hold the buttons and labels that
     * appear at the top of the workbench application.
     */
    private HorizontalPanel _buttPan = null;

    private VerticalPanel _vpFlowMain = null;

    private Panel _canvasButtPan = null;

    /** The vertical split panel that partitions the canvas from the
     * lower detail window.
     */
    private VerticalSplitPanel _vspCanvas = new VerticalSplitPanel();

    /* Scroll panel that wraps the lower detail window.*/
    private ScrollPanel _spInfo = new ScrollPanel();

    private ScrollPanel _spTree;

    private ScrollPanel _spCanvas;

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
        VerticalPanel vpComponentsTab = new VerticalPanel();
        HorizontalPanel hpCompHeader = new HorizontalPanel();
        HorizontalPanel hpCompTreeButtons = new HorizontalPanel();
        HorizontalPanel hpCompTreeSort = new HorizontalPanel();

        ListBox lb = new ListBox();
        lb.setVisibleItemCount(1);
        lb.addItem("By Tag");
        lb.addItem("By Name");
        lb.addItem("By Type");
        lb.setSelectedIndex(0);
        lb.addChangeListener(new ChangeListener(){
        	public void onChange(Widget sender){
        		int i = ((ListBox)sender).getSelectedIndex();
        		switch(i){
        		case 0:
            		_controller.buildCompTree(_controller.getCompTreeHandle(), _controller.getCompTreeRoot(), "Available",
            				Controller.s_COMP_TREE_SORT_BY_TAG);
            		break;
        		case 1:
            		_controller.buildCompTree(_controller.getCompTreeHandle(), _controller.getCompTreeRoot(), "Available",
            				Controller.s_COMP_TREE_SORT_ALPHA);
            		break;
        		case 2:
            		_controller.buildCompTree(_controller.getCompTreeHandle(), _controller.getCompTreeRoot(), "Available",
            				Controller.s_COMP_TREE_SORT_TYPE);
            		break;
        		}
        	}
        });

        Label lab = new Label("Sort:");
        lab.addStyleName("gwt-ListBox");

        Button expand = new Button("+");
        Button collapse = new Button("-");
        expand.addStyleName("tree-button");
        collapse.addStyleName("tree-button");
        hpCompTreeButtons.add(expand);
        hpCompTreeButtons.add(collapse);
        hpCompTreeSort.add(lab);
        hpCompTreeSort.add(lb);
        hpCompTreeSort.setCellVerticalAlignment(lab, HorizontalPanel.ALIGN_MIDDLE);
        hpCompTreeSort.setCellVerticalAlignment(lb, HorizontalPanel.ALIGN_MIDDLE);
        hpCompHeader.add(hpCompTreeButtons);
        hpCompHeader.add(hpCompTreeSort);
        hpCompHeader.setCellHorizontalAlignment(hpCompTreeButtons, HorizontalPanel.ALIGN_LEFT);
        hpCompHeader.setCellHorizontalAlignment(hpCompTreeSort, HorizontalPanel.ALIGN_RIGHT);
        hpCompHeader.setWidth("100%");
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


        Tree treeComponents = _controller.buildCompTree(_controller.getCompTreeHandle(),
                                           _controller.getCompTreeRoot(),
                                           "Available",
                                           Controller.s_COMP_TREE_SORT_BY_TAG);
        _spTree = new ScrollPanel(treeComponents);
        _spTree.setHeight("100%");
        vpComponentsTab.add(hpCompHeader);
        vpComponentsTab.add(_spTree);
        vpComponentsTab.setHeight("100%");
        vpComponentsTab.setCellHeight(_spTree, "100%");

        _tabPan.add(vpComponentsTab, "COMPONENTS");

        _tabPan.add(_controller.buildFlowTree(_controller.getFlowTreeHandle(),
                                              _controller.getFlowTreeRoot(),
                                              "Available"), "FLOWS");
//        _tabPan.add(_controller.buildLocationTree(_controller.
//                                                  getLocationTreeHandle(),
//                                                  _controller.
//                                                  getLocationTreeRoot(),
//                                                  "Available"), "LOCATIONS");
//        _tabPan.add(_controller.buildSearchPanel(), "SEARCH");
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

        VerticalPanel vpRepositoryMain = new VerticalPanel();
        vpRepositoryMain.setWidth("100%");
        vpRepositoryMain.setHeight("100%");
        Label lblRepoTitle = new Label("REPOSITORY");
        lblRepoTitle.addStyleName("canvas-label-bar");
        lblRepoTitle.addStyleName("canvas-label-bar-flow-title");
        vpRepositoryMain.add(lblRepoTitle);
        vpRepositoryMain.add(_tabPan);
        vpRepositoryMain.setCellHeight(_tabPan, "100%");

        _tabPan.setHeight("99%");
        _tabPan.getDeckPanel().setHeight("100%");

        _spInfo = new ScrollPanel();

        _boundPan.add(_absPan);
        _absPan.add(_jsgPan);

        _vpFlowMain = new VerticalPanel();
        _vpFlowMain.setWidth("100%");
        _vpFlowMain.setHeight("100%");
        _canvasButtPan = _controller.buildCanvasButtPan();
        _vpFlowMain.add( _canvasButtPan);

        _vpFlowMain.add(_vspCanvas);
        //_vpFlowMain.setCellHeight(_canvasButtPan, "70px");
        _vpFlowMain.setCellHeight(_vspCanvas, "100%");
        //_boundPan.addStyleName("auto-scroll");

        _hspMain.setLeftWidget(vpRepositoryMain);
        //ORIG _hspMain.setRightWidget(_vspMain);
        _hspMain.setRightWidget(_vpFlowMain);
        //_vpFlowMain.addStyleName("no-scroll");

        //_boundPan.addStyleName("debug-green");
        _spCanvas = new ScrollPanel(_boundPan);

        _vspCanvas.setTopWidget(_spCanvas);
        _vspCanvas.setBottomWidget(_spInfo);





        // Hook the window resize event, so that we can adjust the UI.
        //Window.addWindowResizeListener(this);
        Window.addWindowCloseListener(this);
        RootPanel.get().add(_vpRoot);

        //FlowPanel fp = new FlowPanel();
        //MenuBar _mb = _controller.buildMenu();
        _buttPan = _controller.buildButtPan();
        //fp.add(_mb);
        //fp.add(_buttPan);

        _vpRoot.add(_buttPan);
        _vpRoot.add(_hspMain);
        _vpRoot.add(_controller.buildStatusPanel());
        _vpRoot.setSize(Window.getClientWidth() - 20 + "px",
                Window.getClientHeight() - 20 + "px");

        resizeApp();

        _hspMain.setSplitPosition("280px");
        _vspCanvas.setSplitPosition("80%");

        _controller.setStatusMessage("User " +
                                     _controller.getUserName() +
                                     " logged in to " +
                                     _controller.getActiveDomain() + ".");

        // Call the window resized handler to get the initial sizes setup. Doing
        // this in a deferred command causes it to occur after all widgets' sizes
        // have been computed by the browser.
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onWindowResized(Window.getClientWidth(), Window.getClientHeight());
            }
        });

        //onWindowResized(Window.getClientWidth(), Window.getClientHeight());

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
        resizeApp();
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
        return _spInfo;
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

    private void resizeApp() {
       // _vpRoot.setSize("100%", "100%");
        _vpRoot.setCellWidth(_buttPan, "100%");
        _vpRoot.setCellWidth(_hspMain, "100%");
        _vpRoot.setCellHeight(_hspMain, "100%");
        _vspCanvas.setWidth("100%");
        _vspCanvas.setHeight("100%");
        _hspMain.setWidth("100%");
        _hspMain.setHeight("100%");
        _controller.getStatusBar().setWidth("100%");
        _buttPan.setWidth("100%");
        //_tabPan.setHeight("100%");
        _tabPan.setWidth("100%");
        _controller.getCompTreeHandle().setHeight("100%");
        _controller.getCompTreeHandle().setWidth("100%");
        _controller.getFlowTreeHandle().setHeight("100%");
        _controller.getFlowTreeHandle().setWidth("100%");
        _canvasButtPan.setWidth("100%");
        _spInfo.setWidth("100%");
        _spInfo.setHeight("100%");
        _spCanvas.setSize("100%", "100%");
        _boundPan.setSize("100%", "100%");
        _absPan.setSize("100%", "100%");
        _jsgPan.setSize("100%", "100%");
        _spTree.setHeight(_spTree.getOffsetHeight()  + "px");

    }

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
