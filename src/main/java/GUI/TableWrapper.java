package GUI;

import javax.swing.*;
import java.awt.*;

public class TableWrapper extends JFrame {
    private JScrollPane table;
    private boolean editable = true;

    public TableWrapper(DisplayTable table) {
        setTitle(table.getClassName());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Dimension size = new Dimension(1280, 720);
        setSize(size);
        setPreferredSize(size);
        this.table = table.get();
        table.setEditable(editable);
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

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void changeTable(DisplayTable table) {
        setVisible(false);
        table.setGuiImpl(this);
        if (this.table != null) {
            remove(this.table);
        }
        this.table = table.get();
        table.setEditable(editable);
        add(this.table);
        pack();
        setVisible(true);
    }
}
