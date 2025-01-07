package GUI;

import javax.swing.*;
import java.awt.*;

public class TableWrapper extends JFrame {
    private JScrollPane table;

    public TableWrapper(DisplayTable table) {
        setTitle(table.getClassName());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Dimension size = new Dimension(1280, 720);
        setSize(size);
        setPreferredSize(size);
        this.table = table.get();
        add(this.table);
        pack();
        setVisible(true);
    }

    public TableWrapper() {
        setTitle("TableWrapper");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Dimension size = new Dimension(1280, 720);
        setSize(size);
        setPreferredSize(size);
        pack();
        setVisible(true);
    }

    public void changeTable(DisplayTable table) {
        setVisible(false);
        if (this.table != null) {
            remove(this.table);
        }
        this.table = table.get();
        add(this.table);
        pack();
        setVisible(true);
    }
}
