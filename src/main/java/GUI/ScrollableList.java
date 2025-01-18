package GUI;

import javax.swing.*;

/**
 * Simple implementation of JList embedded in a JScrollPane in one class
 */
public class ScrollableList<T> extends JScrollPane {
    private final JList<T> list;

    ScrollableList(JList<T> list) {
        super(list);
        this.list = list;
    }

    /**
     * Function to get internal JList to get its values
     */
    public JList<T> getList() {
        return list;
    }
}
