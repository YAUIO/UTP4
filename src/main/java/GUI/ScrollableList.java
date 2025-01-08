package GUI;

import javax.swing.*;

public class ScrollableList<T> extends JScrollPane {
    private final JList<T> list;

    ScrollableList(JList<T> list) {
        super(list);
        this.list = list;
    }

    public JList<T> getList() {
        return list;
    }
}
