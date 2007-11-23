package org.meandre.workbench.client;

//==============
// Java Imports
//==============

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.core.client.GWT;
import com.allen_sauer.gwt.dragdrop.client.DragController;
import com.allen_sauer.gwt.dragdrop.client.PickupDragController;
import com.allen_sauer.gwt.dragdrop.client.drop.DropController;
import com.allen_sauer.gwt.dragdrop.client.drop.BoundaryDropController;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import org.meandre.workbench.client.beans.WBComponent;
import org.meandre.workbench.client.beans.WBFlow;
import org.meandre.workbench.client.beans.WBComponentInstance;
import org.meandre.workbench.client.beans.WBProperties;
import org.meandre.workbench.client.beans.WBComponentConnection;
import org.gwtwidgets.client.wrap.JsGraphicsPanel;
import com.allen_sauer.gwt.dragdrop.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dragdrop.client.DragEndEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Image;
import com.gwt.components.client.Effects;
import com.gwt.components.client.Effects.EffectListenerAdapter;
import com.gwt.components.client.Effects.Effect;
import org.meandre.workbench.client.beans.WBPropertiesDefinition;
import org.meandre.workbench.client.beans.WBDataport;
import com.google.gwt.user.client.Cookies;
import org.meandre.workbench.client.beans.WBLoginBean;
import java.util.Date;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TabPanel;
import org.meandre.workbench.client.beans.WBLocation;
import org.meandre.workbench.client.beans.WBCallbackObject;

/**
 * <p>Title: Controller</p>
 *
 * <p>Description: This is the controller for the Meandre Workbench GUI</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class Controller {

    //==============
    // Data Members
    //==============

    /* Component placement properties.*/
    static private String s_TopKey = "wb_top_pix_pos";
    static private String s_LeftKey = "wb_left_pix_pos";
    static private int s_TopVal = 20;
    static private int s_LeftVal = 20;

    static Controller s_controller = null;
    public static final String s_baseURL = "http://test.org/";

    /* Sorts for the component trees*/
    public static final int s_COMP_TREE_SORT_ALPHA = 1;
    public static final int s_COMP_TREE_SORT_TYPE = 2;

    private boolean _dirty = false;

    private WBFlow _workingFlow = null;

    /* GUI status bar*/
    private Label _statusBar = null;

    /* Repository remote interface */
    private WBRepositoryQueryAsync _repquery = null;

    /* Handle to Main instance */
    private Main _main = null;

    /* Component Drag Controller */
    private DragController _compDragController = null;

    /* Set of components on the canvas. */
    private Set _canvasComps = null;

    /* Set of connections between components. */
    private Set _connections = null;

    /* Graphics panel */
    private JsGraphicsPanel _drawPan = null;

    /**
     * A boolean value that indicates if a connection is currently
     * being being created.
     */
    private boolean _drawingConn = false;

    /**
     * The "from" port of the connection under construction.
     */
    private PortComp _fromPort = null;

    /* Drag handler listener adapter. */
    private DragHandlerAdapter _mdh = null;

    /* The selected component on the canvas. */
    private ComponentPanel _selectedComp = null;

    /* Horizontal component spacing used for auto format */
    public final int _compHorizSpacing = 150;

    /* Vertical component spacing used for auto format */
    public final int _compVertSpacing = 140;

    /* Counter value used to create unique resource ids*/
    public int _compCount = 0;

    /* Component tree */
    private Tree _compTree = null;

    /* Root tree item for component tree.*/
    private TreeItem _compTreeRoot = null;

    /* Root tree item for flow tree.*/
    private TreeItem _flowTreeRoot = null;

    /* Flow tree.*/
    private Tree _flowTree = null;

    /* TreeItem holder for tree roots so the callback can access them*/
    private TreeItem _flowRootTemp = null;

    /* Root tree item for location tree.*/
    private TreeItem _locTreeRoot = null;

    /* Location tree.*/
    private Tree _locTree = null;

    /* TreeItem holder for tree roots so the callback can access them*/
    private TreeItem _locRootTemp = null;

    /* TreeItem holder for tree roots so the callback can access them*/
    private TreeItem _compRootTemp = null;

    /* Tree sort holder for comp trees so the callback can access them*/
    private int _ctSort = 0;

    /* A text box for the search panel.*/
    private TextBox _searchBox = null;

    /* A submit button for the search panel.*/
    private Button _searchButt = null;

    /* Component tree for the search panel. */
    private Tree _compSearchResults = null;

    /* Component tree root for the search panel.*/
    private TreeItem _compSearchResultsRoot = null;

    /* Flow tree for the search panel.*/
    private Tree _flowSearchResults = null;

    /* Flow tree root for the search panel.*/
    private TreeItem _flowSearchResultsRoot = null;

    /* Component Name Map */
    private Set _componentNameSet = null;

    /* Tells is a flow is currently executing. */
    private boolean _flowExecuting = false;

    /* session ID */
    private String _sessionID = null;

    /* user name */
    private String _userName = "Default";

    /* cookie property key for session id */
    static public final String _sidKey = "SEASR.WB.SESSION.KEY";

    /* hash of flows by name */
    private HashMap _flowsByName = new HashMap();

    /* Active Domain */
    private String _activeDomain = "";

    /* the status bar image */
    private Image _statusIcon = null;

    //================
    // Constructor(s)
    //================

    public Controller(Main main, JsGraphicsPanel dp) {
        s_controller = this;
        _repquery = setupRepQuery();
        _main = main;
        _drawPan = dp;
        _drawPan.setStrokeWidth(1);
        _drawPan.setColor(org.gwtwidgets.client.style.Color.BLACK);
        setupComponentDragNDrop();
        _canvasComps = new HashSet();
        _connections = new HashSet();

        /* set up tree instances for the tree views*/
        _compTree = new DCTree(this,
                               (TreeImages) GWT.create(ComponentTreeImages.class));
        _compTreeRoot = new WBTreeItem("Available", null);
        _compTree.addItem(_compTreeRoot);
        _flowTree = new FlowTree(this, (TreeImages) GWT.create(FlowTreeImages.class));
        _flowTreeRoot = new WBTreeItem("Available", null);
        _flowTree.addItem(_flowTreeRoot);
        _locTree = new LocationTree(this,
                                    (TreeImages) GWT.create(LocationTreeImages.class));
        _locTreeRoot = new WBTreeItem("Available", null);
        _locTree.addItem(_locTreeRoot);
        _componentNameSet = new HashSet();

        newCanvas();
    }

    //=================
    // Package Methods
    //=================

    void login() {
        //Do we have a cookie already?
        String sessionID = Cookies.getCookie(_sidKey);
        if (sessionID != null) {
            checkSessionID(sessionID, new AsyncCallback() {
                public void onSuccess(Object result) {
                    WBLoginBean lbean = (WBLoginBean) result;
                    if (lbean.getSuccess()) {
                        loginSuccess(lbean.getUserName(), lbean.getSessionID()
                                     , lbean.getBaseURL());
                    } else {
                        new WBLoginDialog(_main, Controller.this);
                    }
                }

                public void onFailure(Throwable caught) {
                    // do some UI stuff to show failure
                    _userName = null;
                    Window.alert("AsyncCallBack Failure -- getUser():  " +
                                 caught.getMessage());
                    _main.closeApp();
                }
            });
        } else {
            new WBLoginDialog(_main, this);
        }
    }

    public String getActiveDomain() {
        return _activeDomain;
    }

    void loginSuccess(String uname, String sid, String dom) {
        setUserName(uname);
        _sessionID = sid;
        _activeDomain = dom;

        final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
        Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie(this._sidKey, sid, expires, null, "/", false);
        _main.onModuleLoadContinued();
    }

    void logout() {
        Cookies.removeCookie(this._sidKey);
    }

    String getSessionID() {
        return _sessionID;
    }

    /**
     * Get the user name.
     *
     * @return String User name for current session.
     */
    String getUserName() {
        return this._userName;
    }

    /**
     * Set the user name.
     * @param name String the user name.
     */
    void setUserName(String name) {
        _userName = name;
    }

    /**
     * Get a handle to the Maion class.
     * @return Main
     */
    Main getMain() {
        return _main;
    }

    /**
     * Show the busy indicator for flow execution.
     */
    void showRunningIndicator() {
        RunningIndicator.showRunning();
        _flowExecuting = true;
    }

    /**
     * Hide the busy inidicator for flow execution.
     */
    void hideRunningIndicator() {
        RunningIndicator.hideRunning();
        _flowExecuting = false;
    }

    /**
     * Returns a boolean indicating whether a flow is executing or not.
     *
     * @return boolean A true/false value that indicates whether a flow
     * is executing or not.
     */
    boolean isFlowExecuting() {
        return _flowExecuting;
    }

    //redirect the browser to the given url
    public static native void redirectOrClose(String url) /*-{
                                       if ($wnd.opener && !$wnd.opener.closed){
                $wnd.close();
                                                                       } else {
                          $wnd.location = url;
                                                                       }
                                                                     }-*/
            ;

    //==================================================
    // Interface Implementation: WBRepositoryQueryAsync
    //==================================================

    /**
     * Fetches the locations for the current user repository.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void getRegeneratRepository(AsyncCallback cb) {
        _repquery.regenerateRepository(getSessionID(), cb);
    }

    /**
     * Fetches the locations for the current user repository.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void getLocations(AsyncCallback cb) {
        _repquery.getLocations(getSessionID(), cb);
    }

    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param flowid String flow uri.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void deleteFlowFromRepository(String sid,
                                         String flowid,
                                         AsyncCallback cb) {
        _repquery.deleteFlowFromRepository(sid, flowid, cb);
    }

    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @param flowid String flow uri.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void startInteractiveExecution(String sid,
                                          String execid,
                                          String flowid,
                                          AsyncCallback cb) {
        _repquery.startInteractiveExecution(sid, execid, flowid, cb);
    }

    /**
     * Updates status of execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void updateInteractiveExecution(String sid,
                                           String execid,
                                           AsyncCallback cb) {
        _repquery.updateInteractiveExecution(sid, execid, cb);
    }

    /**
     * Log the user into the application.
     * @param sid String session id
     * @param cb AsyncCallback Callback object returned from the server.
     */
    void checkSessionID(String sessionID, AsyncCallback cb) {
        _repquery.checkSessionID(sessionID, cb);
    }

    /**
     * Log the user into the application.
     * @param userid String user's id
     * @param password String user's password
     * @param url String URL of server to connect with.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    void login(String user, String pass, String url, AsyncCallback cb) {
        _repquery.login(user, pass, url, cb);
    }

    /**
     * Get the active components in the current user's repository.
     *
     * @param cb AsyncCallback Callback object returned from the server.
     */
    void getActiveComponents(AsyncCallback cb) {
        _repquery.getActiveComponents(getSessionID(), cb);
    }

    /**
     * Get the active flows in the current user's repository.
     *
     * @param cb AsyncCallback Callback object returned from the server.
     */
    void getActiveFlows(AsyncCallback cb) {
        _repquery.getActiveFlows(getSessionID(), cb);
    }

    /**
     * Get the active components in the current user's repository that match
     * the search criteria.
     *
     * @param search String The search string for this query.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    void getActiveComponents(String search, AsyncCallback cb) {
        _repquery.getActiveComponents(search, getSessionID(), cb);
    }

    //===================================================
    //===================================================
    //===================================================


    //==============
    // GUI Building
    //==============

    /**
     * Builds the button panel that appears at the top of the work bench
     * application.
     *
     * @return VerticalPanel The vertical panel containing the buttons.
     */
    HorizontalPanel buildButtPan() {
        HorizontalPanel buttPan = new HorizontalPanel();
        /**
         * Add logo to header.
         */
        Image logo = new Image("images/meandre-logo.jpg");
        logo.setPixelSize(200, 36);
        Grid gPan = new Grid(2, 1);
        buttPan.add(gPan);
        buttPan.add(logo);
        /**
         * Add user name to header.
         */
        HTML userlab = new HTML("<bold><font size=\"-1\">Welcome, "
                                + getUserName() + "</font></bold>"
                                , true);
        buttPan.add(userlab);
        //buttPan.addStyleName("menu-panel");
        buttPan.setCellVerticalAlignment(logo, HorizontalPanel.ALIGN_MIDDLE);
        buttPan.setCellVerticalAlignment(userlab, HorizontalPanel.ALIGN_TOP);
        buttPan.setCellHorizontalAlignment(userlab, HorizontalPanel.ALIGN_RIGHT);
        buttPan.setCellHorizontalAlignment(logo, HorizontalPanel.ALIGN_RIGHT);

        Button button = new Button("<IMG SRC='images/gnome-quit-32.png'>",
                                   new ClickListener() {
            public void onClick(Widget sender) {
                if (canvasHasComps() && isDirty()) {
                    if (Window.confirm(
                            "Are you sure you want to exit? Your flow will not be saved.")) {
                        _main.closeApp();
                    }
                } else {
                    _main.closeApp();
                }
            }
        });
        button.setHeight("40px");
        button.setWidth("40px");
        button.addStyleName("top-menu-button");
        gPan.setWidget(0, 0, button);
        Label lab = new Label("Exit");
        lab.addStyleName("top-menu-button-text");
        gPan.setWidget(1, 0, lab);

        return buttPan;
    }

    Panel buildStatusPanel() {
        HorizontalPanel pan = new HorizontalPanel();
        _statusIcon = new Image("./images/timy-meandre-logo.png");
        _statusBar = new Label();
        _statusIcon.setPixelSize(16, 16);
        pan.setSpacing(2);
        pan.add(_statusIcon);
        pan.add(_statusBar);
        pan.setWidth("100%");
        pan.setStyleName("status-bar");
        return pan;
    }

    void showStatusBusy() {
        _statusIcon.setUrl("images/wait-14x14.gif");
    }

    void hideStatusBusy() {
        _statusIcon.setUrl("./images/timy-meandre-logo.png");
    }

    Label getStatusBar() {
        return _statusBar;
    }

    void setStatusMessage(String s) {
        _statusBar.setText(s);
    }

    void clearStatusMessage() {
        _statusBar.setText("");
    }

    /**
     * Builds the button panel that appears at the top of the work bench
     * application.
     *
     * @return VerticalPanel The vertical panel containing the buttons.
     */
    HorizontalPanel buildCanvasButtPan() {
        HorizontalPanel buttPan = new HorizontalPanel();

        Grid gPan = new Grid(2, 6);
        gPan.getColumnFormatter().setWidth(0, "52px");
        gPan.getColumnFormatter().setWidth(1, "52px");
        gPan.getColumnFormatter().setWidth(2, "52px");
        gPan.getColumnFormatter().setWidth(3, "52px");
        gPan.getColumnFormatter().setWidth(4, "52px");
        gPan.getColumnFormatter().setWidth(5, "52px");

        gPan.getCellFormatter().setHorizontalAlignment(0, 0,
                HasHorizontalAlignment.ALIGN_CENTER);
        gPan.getCellFormatter().setHorizontalAlignment(0, 1,
                HasHorizontalAlignment.ALIGN_CENTER);
        gPan.getCellFormatter().setHorizontalAlignment(0, 2,
                HasHorizontalAlignment.ALIGN_CENTER);
        gPan.getCellFormatter().setHorizontalAlignment(0, 3,
                HasHorizontalAlignment.ALIGN_CENTER);
        gPan.getCellFormatter().setHorizontalAlignment(0, 4,
                HasHorizontalAlignment.ALIGN_CENTER);
        gPan.getCellFormatter().setHorizontalAlignment(0, 5,
                HasHorizontalAlignment.ALIGN_CENTER);

        buttPan.add(gPan);
        buttPan.addStyleName("menu-panel");

        Button button = new Button("<IMG SRC='images/gnome-save-22.png'>",
                                   new ClickListener() {
            public void onClick(Widget sender) {
                saveFlow(false);
            }
        });
        button.setHeight("32px");
        button.setWidth("32px");
        button.addStyleName("menu-button");
        gPan.setWidget(0, 0, button);
        Label lab = new Label("Save");
        lab.addStyleName("menu-button-text");
        gPan.setWidget(1, 0, lab);

        button = new Button("<IMG SRC='images/gnome-save-22.png'>",
                            new ClickListener() {
            public void onClick(Widget sender) {
                saveFlow(true);
            }
        });
        button.setHeight("32px");
        button.setWidth("32px");
        button.addStyleName("menu-button");
        gPan.setWidget(0, 1, button);
        lab = new Label("Save As");
        lab.addStyleName("menu-button-text");
        gPan.setWidget(1, 1, lab);

        button = new Button("<IMG SRC='images/gnome-quit-22.png'>",
                            new ClickListener() {
            public void onClick(Widget sender) {
                removeSelectedComponent();
            }
        });
        button.setHeight("32px");
        button.setWidth("32px");
        button.addStyleName("menu-button");
        gPan.setWidget(0, 2, button);
        lab = new Label("Remove");
        lab.addStyleName("menu-button-text");
        gPan.setWidget(1, 2, lab);

        button = new Button("<IMG SRC='images/gnome-clear-22.png'>",
                            new ClickListener() {
            public void onClick(Widget sender) {
                clearCanvas();
            }
        });
        button.setHeight("32px");
        button.setWidth("32px");
        button.addStyleName("menu-button");
        gPan.setWidget(0, 3, button);
        lab = new Label("Clear");
        lab.addStyleName("menu-button-text");
        gPan.setWidget(1, 3, lab);

        button = new Button("<IMG SRC='images/gnome-reboot-22.png'>",
                            new ClickListener() {
            public void onClick(Widget sender) {
                saveFlowAndExecute();
            }
        });
        button.setHeight("32px");
        button.setWidth("32px");
        button.addStyleName("menu-button");
        gPan.setWidget(0, 4, button);
        lab = new Label("Run");
        lab.addStyleName("menu-button-text");
        gPan.setWidget(1, 4, lab);

        button = new Button("<IMG SRC='images/gnome-format-22.png'>",
                            new ClickListener() {
            public void onClick(Widget sender) {
                formatFlow();
            }
        });
        button.setHeight("32px");
        button.setWidth("32px");
        button.addStyleName("menu-button");
        gPan.setWidget(0, 5, button);
        lab = new Label("Layout");
        lab.addStyleName("menu-button-text");
        gPan.setWidget(1, 5, lab);

        button = new Button("<IMG SRC='images/gnome-properties-22.png'>",
                            new ClickListener() {
            public void onClick(Widget sender) {
                Window.alert("Not implermented yet.");
            }
        });
        button.setHeight("32px");
        button.setWidth("32px");
        button.addStyleName("menu-button");
        //gPan.setWidget(0, 6, button);
        lab = new Label("Props");
        lab.addStyleName("menu-button-text");
        //gPan.setWidget(1, 6, lab);



        return buttPan;
    }

    /**
     * Builds the menu for the work bench application.
     *
     * @return MenuBar The top level menu bar object.
     */
    MenuBar buildMenu() {
        Command cmd = new Command() {
            public void execute() {
                Window.alert("You selected a menu item!");
            }
        };

        Command saveCmd = new Command() {
            public void execute() {
                saveFlow(false);
            }
        };
        Command saveAsCmd = new Command() {
            public void execute() {
                saveFlow(true);
            }
        };
        Command newFlowCmd = new Command() {
            public void execute() {
                clearCanvas();
            }
        };
        Command removeCompCmd = new Command() {
            public void execute() {
                removeSelectedComponent();
            }
        };
        Command layoutFlowCmd = new Command() {
            public void execute() {
                Controller.this.formatFlow();
            }
        };
        Command exitAppCmd = new Command() {
            public void execute() {
                Controller.this.getMain().closeApp();
            }
        };
        Command exitAndLogoutAppCmd = new Command() {
            public void execute() {
                Controller.this.logout();
                Controller.this.getMain().closeApp();
            }
        };
        Command executeInteractiveCmd = new Command() {
            public void execute() {
                Controller.this.saveFlowAndExecute();
            }
        };
        Command deleteFlowCmd = new Command() {
            public void execute() {
                Controller.this.deleteFlow();
            }
        };
        Command regenerateRepositoryCmd = new Command() {
            public void execute() {
                Controller.this.regenerateRepository();
            }
        };

        // Make some sub-menus that we will cascade from the top menu.


        //app commands
        MenuBar appMenu = new MenuBar(true);
        appMenu.addItem("Exit (Logout)", exitAndLogoutAppCmd);
        appMenu.addItem("Exit", exitAppCmd);

        //flow commands
        MenuBar flowMenu = new MenuBar(true);
        flowMenu.addItem("Publish", cmd);
        flowMenu.addItem("Unpublish", cmd);
        flowMenu.addItem("Delete", deleteFlowCmd);

        //canvas commands
        MenuBar canvasMenu = new MenuBar(true);
        canvasMenu.addItem("Save", saveCmd);
        canvasMenu.addItem("Save As ...", saveAsCmd);
        canvasMenu.addItem("New", newFlowCmd);
        canvasMenu.addItem("Layout", layoutFlowCmd);
        canvasMenu.addItem("Remove Component", removeCompCmd);

        //component commands
        MenuBar compMenu = new MenuBar(true);
        compMenu.addItem("Create", cmd);
        compMenu.addItem("Publish", cmd);
        compMenu.addItem("Unpublish", cmd);
        compMenu.addItem("Properties", cmd);
        compMenu.addItem("Delete", cmd);

        //exec commands
        MenuBar execMenu = new MenuBar(true);
        execMenu.addItem("Run Interactive", executeInteractiveCmd);

        //Repository
        MenuBar repoMenu = new MenuBar(true);
        repoMenu.addItem("Regenerate", regenerateRepositoryCmd);
        repoMenu.addItem("Upload", cmd);
        repoMenu.addItem("Add Location", cmd);
        repoMenu.addItem("Remove Location", cmd);

        //help
        MenuBar helpMenu = new MenuBar(true);
        helpMenu.addItem("About", cmd);

        // Make a new menu bar, adding a few cascading menus to it.
        MenuBar menu = new MenuBar();
        menu.addItem("Application", appMenu);
        menu.addItem("Canvas", canvasMenu);
        menu.addItem("Flow", flowMenu);
        menu.addItem("Component", compMenu);
        menu.addItem("Execute", execMenu);
        menu.addItem("Repository", repoMenu);
        menu.addItem("Help", helpMenu);

        menu.setStyleName("gwt-MenuBar");
        appMenu.setStyleName("gwt-MenuBar");
        helpMenu.setStyleName("gwt-MenuBar");
        flowMenu.setStyleName("gwt-MenuBar");
        repoMenu.setStyleName("gwt-MenuBar");
        compMenu.setStyleName("gwt-MenuBar");
        execMenu.setStyleName("gwt-MenuBar");
        canvasMenu.setStyleName("gwt-MenuBar");
        return menu;
    }

    //==========================================================
    //==========================================================
    //==========================================================

    // Component Drag N Drop

    /**
     * Signals the DnD library that this component is designated
     * to be draggable.
     *
     * @param comp Widget Component to be designated as draggable.
     */
    void makeComponentDraggable(Widget comp) {
        _compDragController.makeDraggable(comp);
    }

    /**
     * Signals the DnD library that this component is designated
     * to be draggable.  The handle is the object that
     * is used to activate dragging.
     *
     * @param comp Widget Component to be designated as draggable.
     * @param comp Widget Component to be used as the object that
     * is used to activate dragging.
     */
    void makeComponentDraggable(Widget comp, Widget handle) {
        _compDragController.makeDraggable(comp, handle);
    }


    /**
     * Returns the "from" port of the connection under construction.
     * @return PortComp
     */
    PortComp getDrawingFromPort() {
        return this._fromPort;
    }

    /**
     * Set the current connection flag.  If the flag is true then set the
     * from port of the current connection to <code>pc</code>.
     * @param b boolean boolean value that indicates if a connection is
     * currently being being created.
     * @param pc PortComp The "from" port of the connection under construction.
     */
    void setDrawingConnection(boolean b, PortComp pc) {
        this._drawingConn = b;
        if (!b) {
            this._fromPort = null;
        } else {
            this._fromPort = pc;
        }
    }

    /**
     * Get the boolean value that indicates if a connection is currently being
     * created.
     *
     * @return boolean Boolean value that indicates if a connection is being
     * created but not yet completed.
     */
    boolean getDrawingConn() {
        return this._drawingConn;
    }

    /**
     * Clear the current canvas and prepare to build a new flow.
     */
    void newCanvas() {
        _canvasComps.clear();
        _connections.clear();
        _componentNameSet.clear();
        _drawingConn = false;
        _fromPort = null;
        _selectedComp = null;
        _compCount = 0;
        _workingFlow = null;
        _dirty = false;
        _flowExecuting = false;
        clearConnections();
    }

    /**
     * Build an HTML flow description for input component.
     *
     * @param flow WBComponent Component to build description for.
     * @param incRights Flag indicating whether to include "rights" information
     * in the description.
     * @return String The component description.
     */
    String buildComponentDescription(WBComponent ecd, boolean incRights) {
        String s = "";
        s += "<div id=\"componentshow\">" +
                "<h3>" + ecd.getName() + "</h3>" +
                "<p>" +
                "<table>" +
                "<tr><td><em>Resource:</td><td></em>" +
                ecd.getID() + "</td></tr>" +
                "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                "<tr><td><em>Tags:</td><td></em>" + ecd.getTags().toString() +
                "</td></tr>" +
                "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                "<tr><td><em>Name:</em></td><td>" + ecd.getName() +
                "</td></tr>" +
                "<tr><td><em>Description:</em></td><td>" + ecd.getDescription() +
                "</td></tr>";
        if (incRights) {
            s += "<tr><td><em>Rights:</em></td><td>" + ecd.getRights() +
                    "</td></tr>";
        }
        s += "<tr><td><em>Creator:</em></td><td>" + ecd.getCreator() +
                "</td></tr>" +
                "<tr><td><em>Date:</em></td><td>" + ecd.getCreationDate() +
                "</td></tr>" +
                "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                "<tr><td valign=\"top\">Properties:</td><td>";
        WBPropertiesDefinition pdd = ecd.getProperties();
        if (pdd.getKeys().size() > 0) {
            for (Iterator itty = pdd.getKeys().iterator(); itty.hasNext(); ) {
                String sKey = (String) itty.next();
                s += sKey + " = " + pdd.getValue(sKey) + " (" +
                        pdd.getDescription(sKey).trim() + ")" + "<br/>";
            }
        } else {
            s += "<em>none</em>";
        }
        s += "</td></tr>" +
                "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                "<tr><td>Runnable:</td><td>" + ecd.getRunnable() + "</td></tr>" +
                "<tr><td>Format:</td><td>" + ecd.getFormat() + "</td></tr>" +
                "<tr><td>Location:</td><td>" + ecd.getLocation() + "</td></tr>" +
                "<tr><td valign=\"top\">Context:</td><td>";
        for (Iterator itty = ecd.getContext().iterator(); itty.hasNext(); ) {
            String res = (String) itty.next();
            s += res + "<br/>";
        }
        s += "</td></tr>" +
                "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                "<tr><td valign=\"top\">Inputs:</td><td valign=\"top\"><table>";
        if (ecd.getInputs().size() > 0) {
            s += "<tr><td>Firing policy:</td><td><em>" + ecd.getFiringPolicy() +
                    "</em></td></tr>" +
                    "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
            for (Iterator itty = ecd.getInputs().iterator(); itty.hasNext(); ) {
                WBDataport dpd = (WBDataport) itty.next();
                s += "<tr><td>Resource:</td><td>" + dpd.getResourceID() +
                        "</td></tr>";
                s += "<tr><td>Identifier:</td><td>" + dpd.getIdentifier() +
                        "</td></tr>";
                s += "<tr><td>Nane:</td><td>" + dpd.getName() + "</td></tr>";
                s += "<tr><td>Description:</td><td>" + dpd.getDescription() +
                        "</td></tr>";
                s += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
            }
        } else {
            s += "<tr><td><em>none</em></td></tr>";
        }
        s += "</table></td></tr>" +
                "<tr><td valign=\"top\">Outputs:</td><td valign=\"top\"><table>";
        if (ecd.getOutputs().size() > 0) {
            for (Iterator itty = ecd.getOutputs().iterator(); itty.hasNext(); ) {
                WBDataport dpd = (WBDataport) itty.next();
                s += "<tr><td>Resource:</td><td>" + dpd.getResourceID() +
                        "</td></tr>";
                s += "<tr><td>Identifier:</td><td>" + dpd.getIdentifier() +
                        "</td></tr>";
                s += "<tr><td>Nane:</td><td>" + dpd.getName() + "</td></tr>";
                s += "<tr><td>Description:</td><td>" + dpd.getDescription() +
                        "</td></tr>";
                s += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
            }
        } else {
            s += "<tr><td><em>none</em></td></tr>";
        }
        s += "</table></td></tr>" +
                "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                "</table>" +
                "</p>" +
                "</div>";

        return s;
    }

    /**
     * For the input component, fetch and display its description in the lower
     * display window.
     *
     * @param flow WBComponent A component descriptor for the flow whose
     * information should be displayed in the lower display window.
     */
    void SetComponentDescriptionInScrollPane(WBComponent comp) {
        _main.getCompDescScrollPanel().clear();
        HTML h = new HTML(buildComponentDescription(comp, true));
        _main.getCompDescScrollPanel().add(h);
    }

    /**
     * Build an HTML flow description for input flow.
     *
     * @param flow WBFlow Flow to build description for.
     * @param incRights Flag indicating whether to include "rights" information
     * in the description.
     * @return String The flow description.
     */
    String buildFlowDescription(WBFlow flow, boolean incRights) {

        String s = "<div id=\"componentshow\">" +
                   "<h3>" + flow.getName() + "</h3>" +
                   "<p>" +
                   "<table>" +
                   "<tr><td><em>Resource:</td><td></em>" + flow.getFlowID() +
                   "</td></tr>" +
                   "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                   "<tr><td><em>Tags:</td><td></em>" + flow.getTags().toString() +
                   "</td></tr>" +
                   "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                   "<tr><td><em>Name:</em></td><td>" + flow.getName() +
                   "</td></tr>" +
                   "<tr><td><em>Description:</em></td><td>" +
                   flow.getDescription() + "</td></tr>";
        if (incRights) {
            s += "<tr><td><em>Rights:</em></td><td>" + flow.getRights() +
                    "</td></tr>";
        }
        s += "<tr><td><em>Creator:</em></td><td>" + flow.getCreator() +
                "</td></tr>" +
                "<tr><td><em>Date:</em></td><td>" + flow.getCreationDate() +
                "</td></tr>" +
                "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" +
                "<tr><td valign=\"top\">Instances:</td><td valign=\"top\"><table>";

        Set secid = flow.getExecutableComponentInstances();
        if (secid.size() > 0) {
            for (Iterator itty = secid.iterator(); itty.hasNext(); ) {
                WBComponentInstance ecid = (WBComponentInstance) itty.next();
                s += "<tr><td>Instance:</td><td>" +
                        ecid.getExecutableComponentInstance() + "</td></tr>";
                s += "<tr><td>Component:</td><td>" +
                        ecid.getExecutableComponent().toString() + "</td></tr>";
                s += "<tr><td>Name:</td><td>" + ecid.getName() + "</td></tr>";
                s += "<tr><td>Description:</td><td>" + ecid.getDescription() +
                        "</td></tr>";
                WBProperties pd = ecid.getProperties();
                if (pd.getKeys().size() > 0) {
                    s +=
                            "<tr><td valign=\"top\">Properties:</td><td valign=\"top\"><table>";
                    for (Iterator itty2 = pd.getKeys().iterator();
                                          itty2.hasNext(); ) {
                        String sKey = (String) itty2.next();
                        s += sKey + " = " + pd.getValue(sKey) + "<br />";
                    }
                    s += "</table></td></tr>";
                } else {
                    s += "<tr><td>Properties:</td><td><em>none</em></td></tr>";
                }
                s += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
            }
        } else {
            s += "<em>none</em>";
        }
        s += "</table></td></tr>" +
                "<tr><td valign=\"top\">Connector:</td><td valign=\"top\"><table>";
        Set scd = flow.getConnectorDescriptions();
        if (scd.size() > 0) {
            for (Iterator itty = scd.iterator(); itty.hasNext(); ) {
                WBComponentConnection cd = (WBComponentConnection) itty.next();
                s += "<tr><td>Connector:</td><td>" + cd.getConnector() +
                        "</td></tr>";
                s += "<tr><td>Source:</td><td>" + cd.getSourceInstance() +
                        "</td></tr>";
                s += "<tr><td>Source data port:</td><td>" +
                        cd.getSourceIntanceDataPort() + "</td></tr>";
                s += "<tr><td>Target:</td><td>" + cd.getTargetInstance() +
                        "</td></tr>";
                s += "<tr><td>Target data port:</td><td>" +
                        cd.getTargetIntanceDataPort() + "</td></tr>";
                s += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
            }
        }
        s += "</table></td></tr>" +
                "</table>" +
                "</p>" +
                "</div>";

        return s;
    }

    /**
     * For the input, flow fetch and display its description in the lower
     * display window.
     *
     * @param flow WBFlow A flow descriptor for the flow whose information
     * should be displayed in the lower display window.
     */
    void SetFlowDescriptionInScrollPane(WBFlow flow) {
        _main.getCompDescScrollPanel().clear();
        HTML h = new HTML(buildFlowDescription(flow, true));
        _main.getCompDescScrollPanel().add(h);
    }

    // Canvas Actions ======================================================

    /**
     * Return boolean indicating if there are currently any components on the
     * canvas.
     *
     * @return boolean  A boolean value indicating whether or not there are any
     * components on the canvas.
     */
    boolean canvasHasComps() {
        return (this._canvasComps.size() > 0);
    }

    /**
     * Clear the canvas of all components and connections and reset all state
     * in the controller.
     */
    void clearCanvas() {
        /**
         * @todo insert code here to query for saving working flow.
         */
        Set samp = new HashSet(_canvasComps);
        for (Iterator itty = samp.iterator(); itty.hasNext(); ) {
            ComponentPanel conn = (ComponentPanel) itty.next();
            removeComponent(conn);
        }
        newCanvas();
    }

    void regenerateRepository() {
        if (Window.confirm(
                "All unpublished components and flows will be deleted.  Are you certain that you want to regenerate the repository?")) {
            if (_dirty) {
                Window.alert("Save or clear the working flow first.");
            } else {
                AsyncCallback callback = new AsyncCallback() {
                    public void onSuccess(Object result) {
                        WBCallbackObject cbo = (WBCallbackObject) result;
                        if (cbo.getSuccess()) {
                            Controller.this.clearCanvas();
                            _main.getCompDescScrollPanel().clear();
                            regenerateTabbedPanel();
                            Controller.this.hideStatusBusy();
                            Controller.this.setStatusMessage(
                                    "Repository regenerated successfully.");
                        } else {
                            Controller.this.hideStatusBusy();
                            Controller.this.setStatusMessage("");
                            Window.alert(
                                    "Repository regeneration operation was NOT successful: " +
                                    cbo.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        Controller.this.hideStatusBusy();
                        Controller.this.setStatusMessage("");
                        Window.alert(
                                "AsyncCallBack Failure -- regeneratRepository:  " +
                                caught.getMessage());
                    }
                };
                Controller.this.showStatusBusy();
                Controller.this.setStatusMessage("Regenerating repository ...");
                this.getRegeneratRepository(callback);
            }
        }
    }

    void regenerateTabbedPanel() {
        buildCompTree(getCompTreeHandle(),
                      getCompTreeRoot(),
                      "Available",
                      Controller.s_COMP_TREE_SORT_TYPE);
        buildFlowTree(getFlowTreeHandle(),
                      getFlowTreeRoot(),
                      "Available");
        buildLocationTree(getLocationTreeHandle(),
                          getLocationTreeRoot(),
                          "Available");
        ((Panel) _main.getTabPanel().getWidget(3)).clear();
        _main.getTabPanel().remove(3);
        _main.getTabPanel().add(buildSearchPanel(), "SEARCH");
        _main.getTabPanel().selectTab(0);
    }

    /**
     * Delete a selected flow from the flow tree.
     */
    void deleteFlow() {
        TabPanel tp = _main.getTabPanel();
        if (tp.getTabBar().getSelectedTab() != 1) {
            Window.alert("Please select a flow first!");
        } else {
            Tree tree = this.getFlowTreeHandle();
            TreeItem ti = tree.getSelectedItem();
            if (ti == null) {
                Window.alert("Please select a flow first!");
            } else {
                WBFlow flow = (WBFlow) ti.getUserObject();
                if (Window.confirm(
                        "Are you certain that you want to delete this flow from the repository?")) {
                    if ((this._workingFlow != null) &&
                        (this._workingFlow.
                         getFlowID().trim().equals(flow.getFlowID().trim()))) {
                        this.clearCanvas();
                        _main.getCompDescScrollPanel().clear();
                    }
                    new CommandDeleteFlow(this._repquery, this,
                                          null).execute(flow);
                }
            }
        }
    }

    /**
     * If a component is currently marked as selected then remove it.
     */
    void removeSelectedComponent() {
        if (_selectedComp == null) {
            Window.alert("Please select a component to remove first.");
            return;
        }
        if (Window.confirm(
                "Are you sure you want to remove this component?")) {
            ComponentPanel cp = _selectedComp;
            setSelectedComp(null);
            removeComponent(cp);
        }
    }

    /**
     * Remove a component from the canvas.
     *
     * @param cp ComponentPanel The component to remove from the canvas.
     */
    private ComponentPanel _tempremoveComponentCP = null;
    void removeComponent(ComponentPanel cp) {
        _tempremoveComponentCP = cp;

        Set samp = new HashSet(_connections);
        for (Iterator iter = cp.getInputs().iterator();
                             iter.hasNext(); ) {
            PortComp pc = (PortComp) iter.next();
            for (Iterator itty = samp.iterator(); itty.hasNext(); ) {
                PortConn conn = (PortConn) itty.next();
                if (pc == conn.getTo()) {
                    removeConnection(conn);
                }
            }
        }
        samp.clear();
        samp.addAll(_connections);
        for (Iterator iter = cp.getOutputs().iterator();
                             iter.hasNext(); ) {
            PortComp pc = (PortComp) iter.next();
            for (Iterator itty = samp.iterator(); itty.hasNext(); ) {
                PortConn conn = (PortConn) itty.next();
                if (pc == conn.getFrom()) {
                    removeConnection(conn);
                }
            }
        }

        this._canvasComps.remove(cp);
        this.removeComponentNameFromLookup(cp.getComponent().getName());

        Effects.Effect("Fade", cp,
                       "{ duration: 1.0 }").addEffectListener(new
                EffectListenerAdapter() {
            public void onAfterFinish(Effect sender) {
                _main.getBoundaryPanel().remove(_tempremoveComponentCP);
                _tempremoveComponentCP.free();
            }
        });
    }

    /**
     * Set the component on the canvas that has been selected.  At this point
     * in time only one component may be selected at once.
     *
     * @param cp ComponentPanel The component to mark as selected.
     */
    void setSelectedComp(ComponentPanel cp) {
        if (cp == null) {
            if (this._selectedComp != null) {
                this._selectedComp.setSelected(false);
            }
            this._selectedComp = null;
            return;
        }
        if (cp != this._selectedComp) {
            if (this._selectedComp != null) {
                this._selectedComp.setSelected(false);
            }
            this._selectedComp = cp;
            cp.setSelected(true);
        }
    }

    /**
     * Save the current flow to the repository.
     */
    void saveFlow(boolean saveas) {
        WBFlow flow = new WBFlow(); ;
        if (saveas || !hasActiveFlow()) {
            if (this.hasActiveFlow()) {
                flow.setDescription(this._workingFlow.getDescription());
                flow.setCreator(this._workingFlow.getCreator());
                flow.setRights(this._workingFlow.getRights());
                flow.setBaseURL(this._workingFlow.getBaseURL());
                flow.setTags(this._workingFlow.getTags());
                flow.setName(this._workingFlow.getName());
            }
        } else {
            flow.setDescription(this._workingFlow.getDescription());
            flow.setCreator(this._workingFlow.getCreator());
            flow.setRights(this._workingFlow.getRights());
            flow.setBaseURL(this._workingFlow.getBaseURL());
            flow.setTags(this._workingFlow.getTags());
            flow.setName(this._workingFlow.getName());
            flow.setFlowID(this._workingFlow.getFlowID());
        }
        _workingFlow = flow;
        new CommandBuildFlow(_canvasComps, _connections,
                             new CommandSaveFlow(_repquery, this, null)).
                execute(flow);
        _dirty = false;
    }

    /**
     * Set the working flow in this controller.
     *
     * @param flow WBFlow The flow to set into this controller.
     */
    void setWorkingFlow(WBFlow flow) {
        _workingFlow = flow;
    }

    /**
     * Get the working flow in thisa controller.
     *
     * @return WBFlow The working flow in this controller.
     */
    WBFlow getWorkingFlow() {
        return _workingFlow;
    }

    /**
     * Save a flow to a newly generated name and then execute the flow.
     *
     * @todo Need to break this functionality apart.
     */
    void saveFlowAndExecute() {
        if (!this.canvasHasComps()) {
            Window.alert("Can't execute an empty canvas.");
            return;
        }
        if (!_dirty) {
            new CommandExecuteFlow(this, null).execute(getWorkingFlow());
        } else {
            WBFlow flow = new WBFlow();
            if (this.hasActiveFlow()) {
                flow.setDescription(this._workingFlow.getDescription());
                flow.setCreator(this._workingFlow.getCreator());
                flow.setRights(this._workingFlow.getRights());
                flow.setBaseURL(this._workingFlow.getBaseURL());
                flow.setTags(this._workingFlow.getTags());
                flow.setName(this._workingFlow.getName());
                flow.setFlowID(this._workingFlow.getFlowID());
            }
            _workingFlow = flow;
            new CommandBuildFlow(_canvasComps, _connections,
                                 new
                                 CommandSaveFlow(_repquery, this,
                                                 new CommandExecuteFlow(this, null))).
                    execute(flow);
            _dirty = false;
        }
    }

    /**
     * Format flows programmatically for display on the canvas.
     */
    void formatFlow() {

        if (this._canvasComps.size() == 0) {
            return;
        }

        int row = 0;
        int col = 0;
        HashSet comps = new HashSet(_canvasComps);
        HashSet track = new HashSet(_canvasComps);
        HashSet heads = new HashSet();

        //find head module(s)
        for (Iterator itty = comps.iterator(); itty.hasNext(); ) {
            ComponentPanel cp = (ComponentPanel) itty.next();
            if (cp.getInputs().size() == 0) {
                heads.add(cp);
            } else {
                boolean anyInConn = false;
                boolean anyOutConn = false;
                for (Iterator iter = cp.getInputs().iterator();
                                     iter.hasNext(); ) {
                    PortComp pc = (PortComp) iter.next();
                    if (pc.isConnected()) {
                        anyInConn = true;
                    }
                }
                for (Iterator iter = cp.getOutputs().iterator();
                                     iter.hasNext(); ) {
                    PortComp pc = (PortComp) iter.next();
                    if (pc.isConnected()) {
                        anyOutConn = true;
                    }
                }
                if ((!anyInConn) && anyOutConn) {
                    heads.add(cp);
                }
            }
        } while (!heads.isEmpty()) {
            ComponentPanel cp = (ComponentPanel) heads.iterator().next();
            col = 0;
            row = plotProgenyComps(cp, track, row, col);
            heads.remove(cp);
        } while (!track.isEmpty()) {
            ComponentPanel cp = (ComponentPanel) track.iterator().next();
            col = 0;
            row = plotProgenyComps(cp, track, row, col);
        }
        DeferredCommand.addPause();
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                drawConnections();
            }
        });
    }

    /**
     * Move a component on the campus.
     *
     * @param cp ComponentPanel Component to move.
     * @param x int New component top position.
     * @param y int New component left position
     * @param redraw boolean Canvas redraw switch.
     */
    void moveComponent(ComponentPanel cp, int left, int top,
                       boolean redraw) {
        _main.getBoundaryPanel().remove(cp);
        setComponent(cp, left, top);
        if (redraw) {
            drawConnections();
        }
    }

    /**
     * Clear the canvas and add the flow to the canvas.
     *
     * @param flow WBFlow Flow to add to the canvas.
     */
    void addFlowToCanvas(WBFlow flow) {
        //attemp to fix drawing canvas initialization bug ===
        boolean formatFirstTime = false;
        if (this._workingFlow == null) {
            formatFirstTime = true;
        }
        //===================================================

        this.clearCanvas();

        boolean defFormat = true;

        WBFlow flownew = new WBFlow();

        for (Iterator iter = flow.getExecutableComponentInstances().
                             iterator();
                             iter.hasNext(); ) {
            WBComponentInstance ci = (WBComponentInstance) iter.next();

            WBComponent comp = ci.getExecutableComponent();
            WBComponentInstance cinew = new WBComponentInstance(ci.
                    getExecutableComponentInstance(),
                    comp, ci.getName(), ci.getDescription(),
                    new WBProperties(new java.util.HashMap(ci.getProperties().
                    getValuesMap())));
            checkAssignNewName(cinew);

            ComponentPanel cp = new ComponentPanel(this, cinew);
            cp.setVisible(false);

            String x = (String) cinew.getProperties().getValue(this.
                    s_LeftKey);
            String y = (String) cinew.getProperties().getValue(this.
                    s_TopKey);
            if (x == null) {
                //set with default positions
                setComponent(cp);
            } else {
                defFormat = false;
                //set with saved x, y positions
                setComponent(cp, Integer.parseInt(x), Integer.parseInt(y));
            }
            Effects.Effect("Appear", cp, "{duration: 1.0}");

            // Make individual widgets draggable
            makeComponentDraggable(cp, cp.getImage());
            _canvasComps.add(cp);
            flownew.addExecutableComponentInstance(cinew);
        }
        for (Iterator iter = flow.getConnectorDescriptions().iterator();
                             iter.hasNext(); ) {
            WBComponentConnection conn = (WBComponentConnection) iter.next();
            PortComp pcompFrom = findPortCompForIDComponentID(conn.
                    getSourceIntanceDataPort(), conn.getSourceInstance());
            PortComp pcompTo = findPortCompForIDComponentID(conn.
                    getTargetIntanceDataPort(), conn.getTargetInstance());
            makeConnection(pcompFrom, pcompTo);
            flownew.addComponentConnection(new WBComponentConnection(conn.
                    getConnector(), conn.getSourceInstance(),
                    conn.getSourceIntanceDataPort(),
                    conn.getTargetInstance(),
                    conn.getTargetIntanceDataPort()));
        }
        if (defFormat) {
            formatFlow();
        } else {
            drawConnections();
        }
        flownew.setCreator(flow.getCreator());
        flownew.setDescription(flow.getDescription());
        flownew.setRights(flow.getRights());
        flownew.setTags(flow.getTags());
        flownew.setFlowID(flow.getFlowID());
        flownew.setBaseURL(flow.getBaseURL());
        flownew.setName(flow.getName());
        _workingFlow = flownew;
        _dirty = false;

        //attemp to fix drawing canvas initialization bug ===
//        if (formatFirstTime) {
        DeferredCommand.addPause();
        if (defFormat) {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    formatFlow();
                }
            });
        } else {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    drawConnections();
                }
            });
        }
//        }
        //====================================================
    }

    /**
     * Indicates whether there have been edits to the current flow that have
     * ot been saved.
     *
     * @return boolean Indictaes if there are editsa to the current flow that
     * have not benn saved.
     */
    boolean isDirty() {
        return _dirty;
    }

    void setDirty(boolean b) {
        _dirty = b;
    }

    /**
     * Return a boolean that indicates whether there is an active flow being
     * edited.
     *
     * @return boolean Indicates whether an active flow object exists in the
     * controller.
     */
    boolean hasActiveFlow() {
        return _workingFlow != null;
    }

    /**
     * Add the component to the canvas.
     *
     * @param comp WBComponent Component to add to the canvas.
     */
    void addComponentToCanvas(WBComponent comp) {
        //change param to component obj

        WBComponentInstance ci = new WBComponentInstance(comp.getID() + "/" +
                comp.getName().toLowerCase().trim().replaceAll(" ", "-"),
                comp, comp.getName(), comp.getDescription(),
                new WBProperties(new java.util.HashMap(comp.getProperties().
                getValuesMap())));

        checkAssignNewName(ci);

        ComponentPanel cp = new ComponentPanel(this, ci);
        _canvasComps.add(cp);
        _dirty = true;
        cp.setVisible(false);
        setComponent(cp);
        Effects.Effect("Appear", cp, "{ duration: 1.0 }");
        // Make individual widgets draggable
        makeComponentDraggable(cp, cp.getImage());

    }

    /**
     * Change the component name of a pre-existing component.  NOTE: if the
     * input name already exists the new name will be appended with a number
     * to make it unique.
     *
     * @param cp ComponentPanel Component panel of component whose name will be
     * changed.
     * @param name String New name.
     */
    void changeComponentName(ComponentPanel cp, String name) {
        /* remove old name from lookup set */
        removeComponentNameFromLookup(cp.getComponent().getName());
        /* Set new name in component */
        cp.getComponent().setName(name);
        /* Make sure new name isn't already used, else make it unique*/
        checkAssignNewName(cp.getComponent());
        /* Set the text on the component label */
        cp.getComponentLabel().setText(cp.getComponent().getName());
        _dirty = true;
    }

    /**
     * Remove this string from the set of component names of active canvas
     * components.
     *
     * @param s String Name to remove from the set of component names.
     */
    void removeComponentNameFromLookup(String s) {
        this._componentNameSet.remove(s);
    }

    /**
     * Checks to see if the input component's name is already in use.  If so,
     * an integer is appended to the name to make it unique.
     *
     * @param ci WBComponentInstance Component the name of which will be
     * checked.
     */
    void checkAssignNewName(WBComponentInstance ci) {
        String cname = ci.getName();
        if (this._componentNameSet.contains(cname)) {
            int cnt = 1;
            String cname2 = cname + "_" + cnt;
            while (this._componentNameSet.contains(cname2)) {
                cnt++;
                cname2 = cname + "_" + cnt;
            }
            this._componentNameSet.add(cname2);
            ci.setName(cname2);
            _dirty = true;
        } else {
            this._componentNameSet.add(cname);
        }
    }

    // Connections =========================================================

    /**
     * Makes a connection between the output port of one compovevt and the
     * input port of another component.
     *
     * @param from PortComp Component connecting from.
     * @param to PortComp Component connecting to.
     */
    PortConn makeConnection(PortComp from, PortComp to) {
        from.setConnected(true, to);
        to.setConnected(true, from);
        PortConn pc = new PortConn(from, to);
        _connections.add(pc);
        drawConnections();
        _dirty = true;
        return pc;
    }

    /**
     * Removes the connection from the output port.
     * @param out PortComp Output port to remove component from.
     */
    void removeConnection(PortConn conn) {
        conn.getFrom().setConnected(false, null);
        conn.getTo().setConnected(false, null);
        _connections.remove(conn);
        drawConnections();
    }

    /**
     * Removes the connection from the output port.
     * @param out PortComp Output port to remove component from.
     */
    void removeConnection(PortComp out) {
        PortConn targ = null;
        for (Iterator itty = _connections.iterator(); itty.hasNext(); ) {
            PortConn conn = (PortConn) itty.next();
            if (out == conn.getFrom()) {
                conn.getFrom().setConnected(false, null);
                conn.getTo().setConnected(false, null);
                targ = conn;
                break;
            }
        }
        if (targ != null) {
            _connections.remove(targ);
            drawConnections();
            _dirty = true;
        }
    }

    /**
     * Clears the canvas and redraws all the connections between
     * components.
     */
    void drawConnections() {
        clearConnections();
        for (Iterator itty = _connections.iterator(); itty.hasNext(); ) {
            PortConn conn = (PortConn) itty.next();
            PortComp from = conn.getFrom();
            PortComp to = conn.getTo();
            _drawPan.drawLine(from.getAbsoluteLeft() + 10 -
                              _drawPan.getAbsoluteLeft(),
                              from.getAbsoluteTop() + 2 -
                              _drawPan.getAbsoluteTop(),
                              to.getAbsoluteLeft() -
                              _drawPan.getAbsoluteLeft(),
                              to.getAbsoluteTop() + 2 -
                              _drawPan.getAbsoluteTop());
        }
        _drawPan.paint();
    }

    /**
     * Return the first input port that contains the point (x,y) or else null.
     *
     * @param x int X screen positon
     * @param y int Y screen position.
     * @return PortComp First port containing point (X,Y).
     */
    PortComp findInputPortOver(int x, int y) {
        for (Iterator itty = _canvasComps.iterator(); itty.hasNext(); ) {
            Set ins = (Set) ((ComponentPanel) itty.next()).getInputs();
            for (Iterator itty1 = ins.iterator(); itty1.hasNext(); ) {
                PortComp pc = (PortComp) itty1.next();
                if (pc.getPortOrientation() == PortComp.s_INPUT_PORT_TYPE) {
                    int pcx1 = pc.getAbsoluteLeft();
                    int pcy1 = pc.getAbsoluteTop();
                    int pcx2 = pcx1 + pc.getOffsetWidth();
                    int pcy2 = pcy1 + pc.getOffsetHeight();
                    if ((x >= pcx1) && (x <= pcx2) && (y >= pcy1) &&
                        (y <= pcy2)) {
                        return pc;
                    }
                }
            }
        }
        return null;
    }

    /**
     * For the input COmponentPanel show a popup dialogbox with the components
     * editable properties.
     *
     * @param cp ComponentPanel Component for which to show properties.
     */
    void showPropertiesDialog(ComponentPanel cp) {
        new PropertiesDialog(cp, this);
    }

    /**
     * Get the root tree item for the location tree.
     *
     * @return TreeItem Flow tree's root tree item.
     */
    TreeItem getLocationTreeRoot() {
        return this._locTreeRoot;
    }

    /**
     * Get a handle to the location tree.
     * @return Tree The flow tree.
     */
    Tree getLocationTreeHandle() {
        return this._locTree;
    }

    /**
     * Get the root tree item for the flow tree.
     *
     * @return TreeItem Flow tree's root tree item.
     */
    TreeItem getFlowTreeRoot() {
        return this._flowTreeRoot;
    }

    /**
     * Get a handle to the flow tree.
     * @return Tree The flow tree.
     */
    Tree getFlowTreeHandle() {
        return this._flowTree;
    }

    /**
     * Get the root tree item for the component tree.
     *
     * @return TreeItem Component tree's root tree item.
     */
    TreeItem getCompTreeRoot() {
        return this._compTreeRoot;
    }

    /**
     * Get a handle to the component tree.
     * @return Tree The flow tree.
     */
    Tree getCompTreeHandle() {
        return this._compTree;
    }

    /**
     * Build the search panel.
     * @return Panel The constructed search panel.
     */
    Panel buildSearchPanel() {
        FlowPanel fp = new FlowPanel();

        HorizontalPanel hp = new HorizontalPanel();
        _searchBox = new TextBox();
        _searchBox.setVisibleLength(20);
        _searchBox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if (keyCode == '\r') {
                    ((TextBox) sender).cancelKey();
                    _searchButt.click();
                }
            }
        });
        hp.add(CursorTextBox.wrapTextBox(_searchBox));
        _searchButt = new Button("Search", new ClickListener() {
            public void onClick(Widget sender) {
                if (isFlowExecuting()) {
                    return;
                }
                String txt = _searchBox.getText();
                if ((txt != null) && (txt.trim().length() > 0)) {
                    AsyncCallback callback = new AsyncCallback() {
                        public void onSuccess(Object result) {
                            // do some UI stuff to show success

                            if (result == null) {
                                Window.alert("Session ID no longer valid.");
                                return;
                            }

                            _compSearchResultsRoot.removeItems();
                            _flowSearchResultsRoot.removeItems();
                            Set items = (Set) result;
                            if (items.size() == 0) {
                                _compSearchResultsRoot.addItem("No Results");
                                _flowSearchResultsRoot.addItem("No Results");
                            } else {
                                WBTreeNode flowroot = new WBTreeNode();
                                WBTreeNode comproot = new WBTreeNode();
                                for (Iterator itty = items.iterator();
                                        itty.hasNext(); ) {
                                    Object obj = itty.next();
                                    if (obj instanceof WBComponent) {
                                        WBComponent ecd = (WBComponent) obj;
                                        String putxt =
                                                getCompTreeItemPopUpText(
                                                ecd);
                                        WBTreeItem ti = new WBTreeItem(ecd.
                                                getName(), putxt);
                                        ti.setUserObject(ecd);
                                        comproot.addChild(new WBTreeNode(ti));
                                    } else if (obj instanceof WBFlow) {
                                        WBFlow f = (WBFlow) obj;
                                        String fname = (f.getName() != null) ?
                                                f.getName() : "";
                                        String flowid = (f.getFlowID() != null) ?
                                                f.getFlowID() : "";
                                        String flowdesc = (f.getDescription() != null) ?
                                                f.getDescription() : "";
                                        String flowrts = (f.getRights() != null) ?
                                                f.getRights() : "";
                                        String putxt = "Name:&nbsp;" +
                                                fname + "<br>" +
                                                "<font color=\"#0000ff\">" +
                                                "&nbsp;Base&nbsp;URL:&nbsp;" +
                                                flowid +
                                                "<br>" +
                                                "&nbsp;Description:&nbsp;" +
                                                flowdesc + "<br>" +
                                                "&nbsp;Creator:&nbsp;" +
                                                flowrts +
                                                "<br>" +
                                                "</font>";
                                        WBTreeItem ti = new WBTreeItem(f.
                                                getName(), putxt);
                                        ti.setUserObject(f);
                                        flowroot.addChild(new WBTreeNode(ti));
                                    }
                                }
                                Iterator itty = comproot.getChildren().
                                                iterator();
                                if (!itty.hasNext()) {
                                    _compSearchResultsRoot.addItem(
                                            "No Results");
                                } while (itty.hasNext()) {
                                    _compSearchResultsRoot.addItem(((
                                            WBTreeNode)
                                            itty.next()).getNodeItem());
                                }
                                itty = flowroot.getChildren().iterator();
                                if (!itty.hasNext()) {
                                    _flowSearchResultsRoot.addItem(
                                            "No Results");
                                } while (itty.hasNext()) {
                                    _flowSearchResultsRoot.addItem(((
                                            WBTreeNode)
                                            itty.next()).getNodeItem());
                                }
                            }
                            _flowSearchResultsRoot.setState(true);
                            _compSearchResultsRoot.setState(true);
                        }

                        public void onFailure(Throwable caught) {
                            // do some UI stuff to show failure
                            _compSearchResultsRoot.setText(
                                    "Failure Retrieving Components");
                            _flowSearchResultsRoot.setText(
                                    "Failure Retrieving Components");
                            Window.alert(
                                    "AsyncCallBack Failure -- getActiveComponents:  " +
                                    caught.getMessage());
                        }
                    };
                    getActiveComponents(txt, callback);
                }
            }
        });
        hp.add(_searchButt);
        _compSearchResults = new DCTree(this,
                                        (TreeImages) GWT.create(
                                                ComponentTreeImages.class));
        _compSearchResultsRoot = new WBTreeItem("Components", null);
        _compSearchResults.addItem(_compSearchResultsRoot);
        _flowSearchResults = new FlowTree(this,
                                          (TreeImages) GWT.create(
                                                  FlowTreeImages.class));
        _flowSearchResultsRoot = new WBTreeItem("Flows", null);
        _flowSearchResults.addItem(_flowSearchResultsRoot);

        fp.add(hp);
        fp.add(_compSearchResults);
        fp.add(_flowSearchResults);

        return fp;
    }

    void searchPanelTBSetFocus() {
        _searchBox.setFocus(true);
    }

    /**
     * Expand all the tree items in the imput tree.
     *
     * @param t Tree Tree for which all items will be expanded.
     */
    void expandAllTreeItems(Tree t) {
        for (Iterator itty = t.treeItemIterator(); itty.hasNext(); ) {
            ((TreeItem) itty.next()).setState(true);
        }
    }

    /**
     * Collapse all items for the input tree.
     *
     * @param t Tree Tree for which all items will be collapsed.
     */
    void collapseAllTreeItems(Tree t) {
        for (Iterator itty = t.treeItemIterator(); itty.hasNext(); ) {
            ((TreeItem) itty.next()).setState(false);
        }
    }

    /**
     * Build a component tree.
     * @param compTree Tree Tree to use for building.
     * @param compTreeRoot TreeItem Tree root to use for building.
     * @param rootTxt String Root node text for this tree.
     * @param compTreeSort int Sort option for this component tree.
     * @return Tree The constructed component tree.
     */
    Tree buildCompTree(Tree compTree, TreeItem compTreeRoot, String rootTxt,
                       int compTreeSort) {
        if (compTree != null) {
            compTree.clear();
        } else {
            compTree = new DCTree(this,
                                  (TreeImages) GWT.create(
                                          ComponentTreeImages.class));
        }
        if (compTreeRoot != null) {
            compTreeRoot.removeItems();
            compTreeRoot.setText(rootTxt);
        } else {
            compTreeRoot = new WBTreeItem(rootTxt, null);
        }
        compTree.addItem(compTreeRoot);

        _compRootTemp = compTreeRoot;
        _ctSort = compTreeSort;
        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                // do some UI stuff to show success
                Set items = (Set) result;
                if (_ctSort == s_COMP_TREE_SORT_ALPHA) {
                    buildCompTreeAsFlatListAlpha(items, _compRootTemp);
                } else if (_ctSort == s_COMP_TREE_SORT_TYPE) {
                    buildCompTreeAsTypeTree(items, _compRootTemp);
                } else {
                    buildCompTreeAsFlatListAlpha(items, _compRootTemp);
                }
                _compRootTemp.setState(true);
            }

            public void onFailure(Throwable caught) {
                // do some UI stuff to show failure
                _compRootTemp.addItem("Failure Retrieving Components");
                _compRootTemp.setState(true);
                Window.alert(
                        "AsyncCallBack Failure -- getActiveComponents:  " +
                        caught.getMessage());
            }
        };
        getActiveComponents(callback);

        return compTree;
    }

    /**
     * Add component nodes to this tree root in node name alpha order.
     * @param items Set Set of components to sort.
     * @param compTreeRoot TreeItem Root node with items added.
     */
    private void buildCompTreeAsFlatListAlpha(Set items,
                                              TreeItem compTreeRoot) {
        WBTreeNode root = new WBTreeNode();

        Iterator itty = items.iterator();
        while (itty.hasNext()) {
            WBComponent ecd = (WBComponent) itty.next();
            String putxt = getCompTreeItemPopUpText(ecd);
            WBTreeItem ti = new WBTreeItem(ecd.getName(), putxt);
            ti.setUserObject(ecd);
            root.addChild(new WBTreeNode(ti));
        }
        itty = root.getChildren().iterator();
        while (itty.hasNext()) {
            _compTreeRoot.addItem(((WBTreeNode) itty.next()).getNodeItem());
        }
    }

    /**
     * Add component nodes to this tree root in node Java component type order.
     * @param items Set Set of components to sort.
     * @param compTreeRoot TreeItem Root node with items added.
     */
    private void buildCompTreeAsTypeTree(Set items, TreeItem compTreeRoot) {
        WBTreeNode rootNode = new WBTreeNode();
        HashMap rootMap = new HashMap();
        //Assume we have only Java type trees to worry about

        Iterator itty = items.iterator();
        while (itty.hasNext()) {
            WBComponent ecd = (WBComponent) itty.next();
            String runtype = ecd.getRunnable();
            if ((runtype == null) || (runtype.trim().length() == 0)) {
                runtype = "UNKNOWN";
            }
            ArrayList ndsarr = new ArrayList();
            ndsarr.add(runtype);
            if (runtype.equalsIgnoreCase(WBComponent.s_JAVA_RUNNABLE)) {
                String work = ecd.getLocation();
                if ((work == null) || (work.trim().length() == 0)) {
                    work = "UNKNOWN";
                }
                int posB = -1;
                while ((posB = work.indexOf(".")) != -1) {
                    ndsarr.add(work.substring(0, posB));
                    work = work.substring(posB + 1);
                    posB = -1;
                }
                if (work.trim().length() != 0) {
                    ndsarr.add(work);
                }
            }
            buildTypeTreeOfMaps(ndsarr, rootMap, ecd);
        }
        buildTypeTreeOfNodes(rootMap, rootNode);
        buildTypeTreeOfTreeItems(rootNode, compTreeRoot);
    }

    /**
     * Recurse the node tree and build the actual display tree.
     *
     * @param rootNode WBTreeNode The root of the node tree at this iteration.
     * @param compTreeRoot TreeItem The roo of the display tree at this
     * iteration.
     */
    private void buildTypeTreeOfTreeItems(WBTreeNode rootNode,
                                          TreeItem compTreeRoot) {
        if (rootNode.hasChildren()) {
            for (Iterator itty = rootNode.getChildren().iterator();
                                 itty.hasNext(); ) {
                WBTreeNode val = (WBTreeNode) itty.next();
                compTreeRoot.addItem(val.getNodeItem());
                buildTypeTreeOfTreeItems(val, val.getNodeItem());
            }
        }
    }

    /**
     * Recurse the map tree and build a node tree that automatically sorts
     * leaves.
     *
     * @param rootMap Map The root of the map tree at this iteration.
     * @param rootNode WBTreeNode The root of the node tree at this iteration.
     */
    private void buildTypeTreeOfNodes(Map rootMap, WBTreeNode rootNode) {
        for (Iterator itty = rootMap.keySet().iterator(); itty.hasNext(); ) {
            String key = (String) itty.next();
            Object val = rootMap.get(key);
            WBTreeNode newND = new WBTreeNode();
            if (val instanceof org.meandre.workbench.client.beans.WBComponent) {
                WBTreeItem newItem = new WBTreeItem(key,
                        getCompTreeItemPopUpText((WBComponent) val));
                newItem.setUserObject(val);
                newND.setNodeItem(newItem);
                rootNode.addChild(newND);
            } else {
                WBTreeItem newItem = new WBTreeItem(key, null);
                newND.setNodeItem(newItem);
                rootNode.addChild(newND);
                buildTypeTreeOfNodes((Map) val, newND);
            }
        }
    }

    /**
     * Recurse the type string adding a map node at each directory
     * until we get to the class name when we add the component object
     * itself.
     *
     * @param nds ArrayList List of type directory segments.
     * @param nd Map Parent's child map.
     * @param ecd WBComponent This types component object.
     */
    private void buildTypeTreeOfMaps(ArrayList nds, Map nd, WBComponent ecd) {
        String name = (String) nds.remove(0);
        if (nds.isEmpty()) {
            nd.put(name, ecd);
            return;
        }
        Map children = (Map) nd.get(name);
        if (children == null) {
            children = new HashMap();
        }
        nd.put(name, children);
        buildTypeTreeOfMaps(nds, children, ecd);
    }

    /**
     * Generate the pop up text for this compnent.
     *
     * @param ecd WBComponent Target component.
     * @return String Pop up text (HTML) to use for this compnent.
     */
    private String getCompTreeItemPopUpText(WBComponent ecd) {
        String putxt = "Name:&nbsp;" + ecd.getName() + "<br>" +
                       "<font color=\"#0000ff\">" +
                       "&nbsp;ID:&nbsp;" + ecd.getID() +
                       "<br>" +
                       "&nbsp;Description:&nbsp;" +
                       ecd.getDescription() + "<br>" +
                       "&nbsp;Creator:&nbsp;" + ecd.getCreator() +
                       "<br>" + /*
                       "&nbsp;Rights:&nbsp;" + ecd.getRights() +
                                          "<br>" + */
                       "</font>";
        return putxt;
    }

    /**
     * Build a flow tree.
     * @param flowTree Tree Tree object to use for building flow tree.
     * @param flowTreeRoot TreeItem Root node to use for flow tree.
     * @param rootTxt String Text for the root node for this flow tree.
     * @return Tree The constructed flow tree.
     */
    Tree buildFlowTree(Tree flowTree, TreeItem flowTreeRoot, String rootTxt) {

        if (flowTree != null) {
            flowTree.clear();
        } else {
            flowTree = new FlowTree(this,
                                    (TreeImages) GWT.create(FlowTreeImages.class));
        }
        if (flowTreeRoot != null) {
            flowTreeRoot.removeItems();
            flowTreeRoot.setText(rootTxt);
        } else {
            flowTreeRoot = new WBTreeItem(rootTxt, null);
        }
        flowTree.addItem(flowTreeRoot);

        _flowRootTemp = flowTreeRoot;

        _flowsByName.clear();

        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                // do some UI stuff to show success
                WBTreeNode root = new WBTreeNode();
                Set items = (Set) result;
                Iterator itty = items.iterator();
                while (itty.hasNext()) {
                    WBFlow f = (WBFlow) itty.next();
                    String putxt = "Name:&nbsp;" + f.getName() + "<br>" +
                                   "<font color=\"#0000ff\">" +
                                   "&nbsp;ID:&nbsp;" +
                                   f.getFlowID() +
                                   "<br>" +
                                   "&nbsp;Description:&nbsp;" +
                                   f.getDescription() + "<br>" +
                                   "&nbsp;Creator:&nbsp;" + f.getCreator() +
                                   "<br>" +
                                   "</font>";
                    WBTreeItem ti = new WBTreeItem(f.getName()
                            + "<br><font color=\"#0000ff\" size=\"-4\">[" +
                            f.getFlowID() + "]</font>", putxt);
                    ti.setUserObject(f);
                    //add to flows by name
                    Object obj = _flowsByName.get(f.getName());
                    if (obj == null) {
                        obj = new ArrayList();
                    }
                    ((ArrayList) obj).add(f);
                    _flowsByName.put(f.getName(), obj);

                    root.addChild(new WBTreeNode(ti));
                }
                itty = root.getChildren().iterator();
                while (itty.hasNext()) {
                    _flowRootTemp.addItem(((WBTreeNode) itty.next()).
                                          getNodeItem());
                }
                _flowRootTemp.setState(true);
            }

            public void onFailure(Throwable caught) {
                _flowRootTemp.addItem("Failure Retrieving Components");
                _flowRootTemp.setState(true);
                Window.alert(
                        "AsyncCallBack Failure -- getActiveComponents:  " +
                        caught.getMessage());
            }
        };
        getActiveFlows(callback);

        return flowTree;
    }

    /**
     * Build a Location tree.
     * @param locTree Tree Tree object to use for building flow tree.
     * @param locTreeRoot TreeItem Root node to use for flow tree.
     * @param rootTxt String Text for the root node for this flow tree.
     * @return Tree The constructed flow tree.
     */
    Tree buildLocationTree(Tree locTree, TreeItem locTreeRoot, String rootTxt) {

        if (locTree != null) {
            locTree.clear();
        } else {
            locTree = new LocationTree(this,
                                       (TreeImages) GWT.create(
                                               LocationTreeImages.class));
        }
        if (locTreeRoot != null) {
            locTreeRoot.removeItems();
            locTreeRoot.setText(rootTxt);
        } else {
            locTreeRoot = new WBTreeItem(rootTxt, null);
        }
        locTree.addItem(locTreeRoot);

        _locRootTemp = locTreeRoot;

        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                // do some UI stuff to show success
                WBTreeNode root = new WBTreeNode();
                Set items = (Set) result;
                Iterator itty = items.iterator();
                while (itty.hasNext()) {
                    WBLocation l = (WBLocation) itty.next();
                    String putxt = "Location:&nbsp;" + l.getLocation() + "<br>" +
                                   "<font color=\"#0000ff\">" +
                                   "&nbsp;Description:&nbsp;" +
                                   l.getDescription() + "<br>" +
                                   "</font>";
                    WBTreeItem ti = new WBTreeItem(l.getLocation(), putxt);
                    ti.setUserObject(l);

                    root.addChild(new WBTreeNode(ti));
                }
                itty = root.getChildren().iterator();
                while (itty.hasNext()) {
                    _locRootTemp.addItem(((WBTreeNode) itty.next()).
                                         getNodeItem());
                }
                _locRootTemp.setState(true);
            }

            public void onFailure(Throwable caught) {
                // do some UI stuff to show failure
                _locRootTemp.addItem("Failure Retrieving Locations");
                _locRootTemp.setState(true);
                Window.alert(
                        "AsyncCallBack Failure -- getLocations:  " +
                        caught.getMessage());
            }
        };
        getLocations(callback);

        return locTree;
    }

    boolean flowByNameBaseExists(String name, String base) {
        Object obj = _flowsByName.get(name);
        if (obj != null) {
            ArrayList al = (ArrayList) obj;
            for (int i = 0, n = al.size(); i < n; i++) {
                WBFlow comp = (WBFlow) al.get(i);
                if (comp.getBaseURL().equals(base)) {
                    return true;
                }
            }
        }
        return false;
    }

    //=================
    // Private Methods
    //=================

    /**
     * Move component on panel to row and col position.  Remove the component
     * from the track set.  Recursively call this method on components that are
     * connected to this components outputs.
     *
     * @param cp ComponentPanel Component to move on canvas.
     * @param track Set Set of compnents being placed.
     * @param row int Row to move component to.
     * @param col int Column to move component to.
     * @return int The last row where a component was placed.
     */
    private int plotProgenyComps(ComponentPanel cp, Set track, int row,
                                 int col) {
        int rowNum = row;
        moveComponent(cp, (col++ * this._compHorizSpacing) + 20,
                      (rowNum * this._compVertSpacing) + 20, false);
        track.remove(cp);
        for (Iterator itty = cp.getOutputs().iterator(); itty.hasNext(); ) {
            PortComp pc = (PortComp) itty.next();
            if (pc.isConnected()) {
                if (track.contains(pc.getConnectedTo().getParentComponent())) {
                    ComponentPanel ch = pc.getConnectedTo().
                                        getParentComponent();
                    if (col >= 5) {
                        col = 0;
                        rowNum++;
                    }
                    rowNum = plotProgenyComps(ch, track, rowNum++, col);
                }
            }
        }
        if (rowNum == row) {
            rowNum++;
        }
        return rowNum;
    }

    /**
     * Clear all connection lines from the graphics panel.
     */
    private void clearConnections() {
        _drawPan.clear();
    }

    /**
     * Find the PortComp with a given port id attached to a component with a
     * given id.
     *
     * @param id String Port id to compare to.
     * @param compID String Component id to compare to.
     * @return PortComp Matching PortComp or null.
     */
    private PortComp findPortCompForIDComponentID(String id, String compID) {
        for (Iterator itty = _canvasComps.iterator(); itty.hasNext(); ) {
            ComponentPanel cp = (ComponentPanel) itty.next();
            Set ports = new HashSet();
            ports.addAll(cp.getInputs());
            ports.addAll(cp.getOutputs());
            for (Iterator itty1 = ports.iterator(); itty1.hasNext(); ) {
                PortComp pc = (PortComp) itty1.next();
                if ((pc.getDataportObj().getResourceID().equals(id)) &&
                    (pc.getParentComponent().getComponent().
                     getExecutableComponentInstance().equals(compID))) {
                    return pc;
                }
            }
        }
        return null;
    }

    /**
     * Setup Repository Query remote interface
     * @return WBRepositoryQueryAsync interface Impl
     */
    private WBRepositoryQueryAsync setupRepQuery() {
        WBRepositoryQueryAsync ret = (WBRepositoryQueryAsync) GWT.create(
                WBRepositoryQuery.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) ret;
        String moduleRelativeURL = GWT.getModuleBaseURL() +
                                   "WBRepositoryQuery";
        endpoint.setServiceEntryPoint(moduleRelativeURL);
        return ret;
    }

    private void setupComponentDragNDrop() {
        // Create a DragController for each logical area where a set of draggable
        // widgets and drop targets will be allowed to interact with one another.
        _compDragController = new PickupDragController(_main.
                getBoundaryPanel(), true) {
//            public Widget maybeNewDraggableProxy(){
//                Image img = new Image(".\\images\\CfG-Crystal-SVG-1.2.0\\48x48\\apps\\gnome-reboot.png");
//                makeDraggable(img);
//                return img;
//            }
        };
//        ((PickupDragController)_compDragController).setDragProxyEnabled(true);

        // Create a DropController for each drop target on which draggable widgets can be dropped
        DropController dropController = new BoundaryDropController(_main.
                getDropPanel(), true);
        // Don't forget to register each DropController with a DragController
        _compDragController.registerDropController(dropController);

        _mdh = new DragHandlerAdapter() {
            public void onDragEnd(DragEndEvent w) {
                drawConnections();
                resetComponentCoords();
            };
        };

        this._compDragController.addDragHandler(_mdh);

    }

    private void resetComponentCoords() {
        for (Iterator itty = _canvasComps.iterator(); itty.hasNext(); ) {
            ComponentPanel cp = (ComponentPanel) itty.next();

            cp.getComponent().getProperties().add(this.s_TopKey,
                                                  "" +
                                                  (cp.getAbsoluteTop() -
                    _drawPan.getAbsoluteTop()));
            cp.getComponent().getProperties().add(this.s_LeftKey,
                                                  "" +
                                                  (cp.getAbsoluteLeft() -
                    _drawPan.getAbsoluteLeft()));
        }
    }

    private void setComponent(ComponentPanel cp) {
        WBComponentInstance ci = cp.getComponent();
        ci.getProperties().add(this.s_TopKey, "" + this.s_TopVal);
        ci.getProperties().add(this.s_LeftKey, "" + this.s_LeftVal);
        _main.getBoundaryPanel().add(cp, this.s_LeftVal, this.s_TopVal);
    }

    private void setComponent(ComponentPanel cp, int x, int y) {
        WBComponentInstance ci = cp.getComponent();
        ci.getProperties().add(this.s_TopKey, "" + y);
        ci.getProperties().add(this.s_LeftKey, "" + x);
        _main.getBoundaryPanel().add(cp, x, y);
    }
}
