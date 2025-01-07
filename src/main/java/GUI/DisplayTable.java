package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class DisplayTable {
    private final Class<?> entity;
    private final ArrayList<Field> fields;
    private DefaultTableModel table;

    public DisplayTable(Class<?> entity) {
        this.entity = entity;
        this.fields = new ArrayList<>();

        ArrayList<StringBuilder> tsv = new ArrayList<>();
        StringBuilder headerLine = new StringBuilder();

        Arrays.stream(entity.getDeclaredFields())
                .forEach(f -> {
                    fields.add(f);
                    headerLine.append(f.getName()).append(" ");
                });
        tsv.add(headerLine);

        try {
            db.Init.getEntityManager().createQuery("SELECT o FROM " + entity.getName() + " o", entity)
                    .getResultStream()
                    .forEach(o -> {
                        StringBuilder rowLine = new StringBuilder();
                        for (Field f : fields) {
                            f.setAccessible(true);
                            try {
                                rowLine.append(f.get(o)).append(" ");
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                        tsv.add(rowLine);
                    });

        } catch (Exception _) {
        }

        String[][] data = new String[tsv.size() - 1][fields.size()];

        for (int i = 1; i < tsv.size(); i++) {
            String[] val = tsv.get(i).toString().trim().split("\\s+");
            for (int f = 0; f < val.length; f++) {
                data[i - 1][f] = val[f];
            }
        }

        table = new DefaultTableModel(data, tsv.getFirst().toString().split("\\s+"));
    }

    public JScrollPane get() {
        JTable tableView = new JTable(table);
        return new JScrollPane(tableView);
    }
}
