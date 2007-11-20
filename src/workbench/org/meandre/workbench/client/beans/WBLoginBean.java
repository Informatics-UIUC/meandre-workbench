package org.meandre.workbench.client.beans;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <p>Title: Workbench Login Bean</p>
 *
 * <p>Description: Bean that contains returned login information.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class WBLoginBean implements IsSerializable{

    //==============
    // Data Members
    //==============

    /* User Name */
    private String _user = null;

    /* Session ID */
    private String _sid = null;

    /* Session ID */
    private String _baseURL = null;

    /* Indicates success of login attempt */
    private boolean _success = false;

    /* Message if login fails */
    private String _failureMsg = null;

    //==============
    // Constructors
    //==============

    public WBLoginBean() {
    }

    public WBLoginBean(String uname, String sid, String base){
        _sid = sid;
        _user = uname;
        _baseURL = base;
        _success = true;
    }

    public WBLoginBean(String failMsg){
        _failureMsg = failMsg;
    }

    //================
    // Public Methods
    //================

    public void setUserName(String uName){
        _user = uName;
    }

    public String getUserName(){
        return _user;
    }

    public void setSessionID(String sid){
        _sid = sid;
    }

    public String getSessionID(){
        return _sid;
    }

    public String getBaseURL(){
        return this._baseURL;
    }

    public void setSuccess(boolean b){
        _success = b;
    }

    public boolean getSuccess(){
        return _success;
    }

    public void setFailureMessage(String msg){
        _failureMsg = msg;
    }

    public String getFailureMessage(){
        return _failureMsg;
    }
}
