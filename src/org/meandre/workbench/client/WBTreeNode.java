package org.seasr.client;

//==============
// Java Imports
//==============

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//===============
// Other Imports
//===============

/**
 * <p>Title: Workbench Tree Node</p>
 *
 * <p>Description: This class serves as a temporary container in the
 * construction of tree views for the Meandre Workbench Application.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
class WBTreeNode {

    //==============
    // Data Members
    //==============

    /* Child nodes of this node.*/
    private ArrayList _children = null;

    /* Tree item to be used to sort this node.*/
    private WBTreeItem _treeItem = null;

    //==============
    // Constructors
    //==============

    /**
     * Create a WBTreeNode with empty sort name and no
     * child nodes.
     */
    public WBTreeNode() {
        _children = new ArrayList();
    }

    /**
     * Create a WBTreeNode with input sort name and no
     * child nodes.
     *
     * @param sname String The sort name for this node.
     */
    public WBTreeNode(WBTreeItem item) {
        _children = new ArrayList();
        _treeItem = item;
    }

    /**
     * Create a new WBTreeNode with the input sort name and one
     * child node.
     *
     * @param sname String The sort name for this node.
     * @param nd WBTreeNode A child node for this node.
     */
    public WBTreeNode(WBTreeItem item, WBTreeNode nd) {
        _children = new ArrayList();
        _treeItem = item;
        _children.add(nd);
    }

    /**
     * Create a new WBTreeNode with the input name and
     * child nodes.
     *
     * @param sname String The sort name for this node.
     * @param nds List Child nodes for this node.
     */
    public WBTreeNode(WBTreeItem item, List nds) {
        _children = new ArrayList();
        _treeItem = item;
        _children.add(nds);
    }

    //=================
    // Package Methods
    //=================

    /**
     * Get the child nodes for this node.
     *
     * @return List Child nodes of this node.
     */
    List getChildren(){
        Collections.sort(_children, new Alpha_Comparator());
        return _children;
    }

    boolean hasChildren(){
        return !_children.isEmpty();
    }

    /**
     * Set child nodes for this node.
     *
     * @param s List The List to set as child nodes for this node.
     */
    void setChildren(List s){
        if (s == null){
            return;
        }
        if (_children != null){
            _children.clear();
        }
        _children = new ArrayList();
        _children.add(s);
    }

    /**
     * Add nodes in List to children of this node.
     *
     * @param s List  List of nodes to add to children of this node.
     */
    void addChildren(List s){
        _children.addAll(s);
    }

    /**
     * Add a child node to the children of this node.
     *
     * @param nd WBTreeNode Node to add to the children of this node.
     */
    void addChild(WBTreeNode nd){
        _children.add(nd);
    }

    /**
     * Get sort name for this node.
     *
     * @return String Sort name for this node.
     */
    WBTreeItem getNodeItem(){
        return _treeItem;
    }

    /**
     * Set the sort name for this node.
     *
     * @param s String String to use as the sort name for this node.
     */
    void setNodeItem(WBTreeItem s){
        _treeItem = s;
    }

    //===============
    // Inner Classes
    //===============

    private class Alpha_Comparator
        implements java.util.Comparator {

      /**
       * put your documentation comment here
       */
      public Alpha_Comparator() {
      }

      //======================
      //Interface: Comparator
      //======================
      public int compare(Object o1, Object o2) {
       String s1 = ((WBTreeNode)o1).getNodeItem().getText().toLowerCase();
       String s2 = ((WBTreeNode)o2).getNodeItem().getText().toLowerCase();
       return s1.compareTo(s2);
      }
    }

}
