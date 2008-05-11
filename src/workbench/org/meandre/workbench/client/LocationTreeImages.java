package org.meandre.workbench.client;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * <p>Title: Location Tree Image Bundle</p>
 *
 * <p>Description: An image bundle class for the location tree.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Sesarsmith
 * @version 1.0
 */
/**
   * Allows us to override Tree default images. If we don't override one of the
   * methods, the default will be used.
   */
  interface LocationTreeImages extends TreeImages {

    /**
     * @gwt.resource comp-tree-folder-open.gif
     */
    AbstractImagePrototype treeOpen();

    /**
     * @gwt.resource comp-tree-folder-closed.gif
     */
    AbstractImagePrototype treeClosed();

    /**
     * @gwt.resource loc-tree-leaf.png
     */
    AbstractImagePrototype treeLeaf();

  }
