package GUI;

import db.Init;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Predicate;

public class DisplayTable {
    private final Class<?> entity;
    private final HashMap<Field, Method> fields;
    private DefaultTableModel table;
    private JTable jTable;
    private final StringBuilder headerLine;
    private final String[] header;
    private final ArrayList<Object> objects;
    private TableWrapper guiImpl;
    private final Predicate<Object> pred;
    private boolean editable;

    public DisplayTable(Class<?> entity, Predicate<Object> pred) {
        this.entity = entity;
        this.fields = new HashMap<>();
        this.objects = new ArrayList<>();
        this.pred = pred;

        headerLine = new StringBuilder();

        Arrays.stream(entity.getDeclaredFields())
                .forEach(f -> {
                    fields.put(f, null);
                    headerLine.append(f.getName()).append(" ");
                });
        header = headerLine.toString().split("\\s+");

        Arrays.stream(entity.getDeclaredMethods())
                .forEach(m -> {
                    fields.keySet().stream().filter(f -> m.getName().toLowerCase().equals("set" + f.getName().toLowerCase()))
                            .forEach(f -> fields.put(f, m));
                });

        update();

        table.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                String value = (String) table.getDataVector().get(e.getLastRow()).get(e.getColumn());
                fields.keySet().stream()
                        .filter(f -> f.getName().equals(header[e.getColumn()]))
                        .forEach(f -> {
                            try {
                                Object val = null;

                                if (f.getType() != String.class && f.getType() != Date.class && !f.getType().isAnnotationPresent(Entity.class)) {
                                    Optional<Method> res =
                                            Arrays.stream(f.getType().getDeclaredMethods())
                                                    .filter(m -> m.getParameterCount() == 1)
                                                    .filter(m -> m.getParameterTypes()[0] == String.class)
                                                    .filter(m -> m.getName().contains("value"))
                                                    .findFirst();
                                    if (res.isPresent()) {
                                        val = f.getType().cast(res.get().invoke(null, value));
                                    }
                                } else if (f.getType().isAnnotationPresent(Entity.class)) {
                                    val = Init.getEntityManager().createQuery("SELECT u FROM " + f.getType().getName().substring(f.getType().getName().indexOf('.') + 1) + " u WHERE u.id = :id", f.getType())
                                            .setParameter("id", Integer.parseInt(value))
                                            .getSingleResult();
                                } else if (f.getType() == Date.class) { //that type is ruining my beautiful code. like why do you need DateFormat.getDateInstance().parse()
                                    val = DateFormat.getDateInstance(DateFormat.SHORT).parse(value.replaceAll("-", "."));
                                } else {
                                    val = value;
                                }

                                fields.get(f).invoke(objects.get(e.getLastRow()), val);
                            } catch (Exception ex) {
                                update();
                                if (guiImpl != null) {
                                    guiImpl.changeTable(this);
                                }
                                new Error(ex);
                            }
                        });
            }
        });
    }

    public DisplayTable(Class<?> entity) {
        this(entity, o -> true);
    }

    public void setGuiImpl(TableWrapper guiImpl) {
        this.guiImpl = guiImpl;
    }

    public JTable getTable() {
        return jTable;
    }

    public Object getValue(int row, int column) {
        return table.getValueAt(row, column);
    }

    public String getHeader() {
        StringBuilder ret = new StringBuilder();
        Arrays.stream(header)
                .filter(s -> !s.equalsIgnoreCase("id"))
                .forEach(s -> ret.append("'").append(s).append("' "));
        return ret.toString();
    }

    public void update() {
        ArrayList<StringBuilder> tsv = new ArrayList<>();
        tsv.add(headerLine);
        try {
            Init.getEntityManager().createQuery("SELECT o FROM " + entity.getName() + " o", entity)
                    .getResultStream()
                    .filter(pred)
                    .forEach(o -> {
                        StringBuilder rowLine = new StringBuilder();
                        objects.add(o);
                        for (String s : header) {
                            fields.keySet().stream()
                                    .filter(f -> f.getName().equals(s))
                                    .forEach(f -> {
                                        f.setAccessible(true);
                                        try {
                                            if (f.getType().isAnnotationPresent(Entity.class)) {
                                                Optional<Field> field =
                                                        Arrays.stream(f.get(o).getClass().getDeclaredFields())
                                                                .filter(ff -> ff.getName().equals("id"))
                                                                .findFirst();
                                                if (field.isPresent()) {
                                                    field.get().setAccessible(true);
                                                    rowLine.append(field.get().get(f.get(o))).append(" ");
                                                } else {
                                                    throw new RuntimeException("No id field in Entity class");
                                                }
                                            } else if (f.getType() == Date.class) {
                                                rowLine.append(f.get(o).toString().split("\\s+")[0]).append(" ");
                                            } else {
                                                rowLine.append(f.get(o)).append(" ");
                                            }
                                        } catch (Exception e) {
                                            new Error(e);
                                        }
                                    });
                        }
                        tsv.add(rowLine);
                    });

        } catch (Exception _) {
        }

        String[][] data = new String[tsv.size() - 1][fields.size()];

        for (int i = 1; i < tsv.size(); i++) {
            String[] val = tsv.get(i).toString().trim().split("\\s+");
            if (val.length > data[i - 1].length) { //handling for spaces
                String[] cVal = val;
                val = new String[fields.size()];
                System.arraycopy(cVal, 0, val, 0, fields.size());
                StringBuilder addr = new StringBuilder();
                for (int l = fields.size() - 1; l < cVal.length; l++) {
                    addr.append(cVal[l]).append(" ");
                }
                val[fields.size()-1] = addr.toString();
            }
            for (int f = 0; f < val.length; f++) {
                data[i - 1][f] = val[f];
            }
        }

        table = new DefaultTableModel(data, tsv.getFirst().toString().split("\\s+")) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (editable) {
                    Object val = Init.getEntityManager().createQuery("SELECT u FROM " + entity.getName().substring(entity.getName().indexOf('.') + 1) + " u WHERE u.id = :id", entity)
                            .setParameter("id", Integer.parseInt((String) table.getDataVector().get(row).getFirst()))
                            .getSingleResult();

                    return Arrays.stream(val.getClass().getDeclaredFields())
                            .filter(f -> f.getName().equals(header[column]))
                            .anyMatch(f -> Arrays.stream(f.getDeclaredAnnotations())
                                    .filter(a -> a.annotationType() == Column.class)
                                    .anyMatch(a -> ((Column) a).updatable()));
                } else {
                    return false;
                }
            }
        };
        jTable = new JTable(table);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public JScrollPane get() {
        return new JScrollPane(jTable);
    }

    public String getClassName() {
        return entity.getName();
    }
}
