package org.meandre.workbench.client.beans;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <p>Title: Workbench Execution Bean</p>
 *
 * <p>Description: Bean for updating interactive flow executions.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public class WBExecBean implements IsSerializable{

    //==============
    // Data Members
    //==============

    /* Indicates success of login attempt */
    private boolean _success = false;

    /* Message if login fails */
    private String _failureMsg = null;

    /* Console content*/
    private String _consoleCont = null;

    private String _execID = null;

    private boolean _completed = false;

    //==============
    // Constructors
    //==============

    public WBExecBean(){}

    public WBExecBean(String content, String execID, boolean comp) {
        _completed = comp;
        _consoleCont = content;
        _execID = execID;
        _success = true;
    }

    public WBExecBean(String failMsg){
        _failureMsg = failMsg;
    }

    //================
    // Public Methods
    //================

    public boolean getCompleted(){
        return _completed;
    }

    public String getExecID(){
        return _execID;
    }

    public String getConsoleCont(){
        return _consoleCont;
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
