package org.meandre.workbench.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContextPopup extends PopupPanel {
    private TreeItem _object;

    public ContextPopup(TreeItem treeItem) {
        super(true);

        _object = treeItem;

        VerticalPanel vpMain = new VerticalPanel();
        vpMain.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
        vpMain.setSpacing(1);

        vpMain.add(new ContextPopupMenuItem("Publish") {
            public void onClick(Widget sender) {
                hide();
                Controller.s_controller.publish();
            }
        });

        vpMain.add(new ContextPopupMenuItem("UnPublish") {
            public void onClick(Widget sender) {
                hide();
                Controller.s_controller.unpublish();
            }
        });

        vpMain.add(new ContextPopupMenuItem("Delete") {
            public void onClick(Widget sender) {
                hide();
                Controller.s_controller.delete();
            }
        });

        setWidget(vpMain);
        setStyleName("context-popup");
    }

    public TreeItem getTargetTreeItem() {
        return _object;
    }

    public void setTargetTreeItem(TreeItem object) {
        _object = object;
    }
}
