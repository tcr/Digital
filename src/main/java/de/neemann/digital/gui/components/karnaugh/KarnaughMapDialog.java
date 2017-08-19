package de.neemann.digital.gui.components.karnaugh;

import de.neemann.digital.analyse.TruthTable;
import de.neemann.digital.analyse.quinemc.BoolTable;
import de.neemann.digital.gui.components.table.ExpressionListenerStore;
import de.neemann.digital.lang.Lang;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dialog to show a KV map
 */
public class KarnaughMapDialog extends JDialog {

    private final KarnaughMapComponent kvComponent;
    private final JComboBox<ExpressionListenerStore.Result> combo;
    private TruthTable table;
    private List<ExpressionListenerStore.Result> results;

    /**
     * Creates a new instance
     *
     * @param parent the parent dialog
     */
    public KarnaughMapDialog(Dialog parent) {
        super(parent, Lang.get("menu_karnaughMap"), false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        kvComponent = new KarnaughMapComponent();
        getContentPane().add(kvComponent);

        combo = new JComboBox<>();
        getContentPane().add(combo, BorderLayout.NORTH);
        combo.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int i = combo.getSelectedIndex();
                if (i >= 0) {
                    BoolTable boolTable = getResultTable(results.get(i).getName());
                    if (boolTable != null)
                        kvComponent.setResult(table.getVars(), boolTable, results.get(i).getExpression());
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private BoolTable getResultTable(String name) {
        for (int i = 0; i < table.getResultCount(); i++)
            if (table.getResultName(i).endsWith(name))
                return table.getResult(i);
        return null;
    }

    /**
     * Sets the available results
     *
     * @param table   the table
     * @param results the result list
     */
    public void setResult(TruthTable table, List<ExpressionListenerStore.Result> results) {
        this.table = table;
        this.results = results;
        combo.setModel(new MyComboBoxModel(results));
        combo.setSelectedIndex(0);
    }

    private class MyComboBoxModel implements ComboBoxModel<ExpressionListenerStore.Result> {
        private List<ExpressionListenerStore.Result> results;
        private ExpressionListenerStore.Result selected;

        private MyComboBoxModel(List<ExpressionListenerStore.Result> results) {
            this.results = results;
        }

        public void setSelected(ExpressionListenerStore.Result selected) {
            this.selected = selected;
        }

        @Override
        public void setSelectedItem(Object o) {
            selected = (ExpressionListenerStore.Result) o;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public int getSize() {
            return results == null ? 0 : results.size();
        }

        @Override
        public ExpressionListenerStore.Result getElementAt(int i) {
            return results.get(i);
        }

        @Override
        public void addListDataListener(ListDataListener listDataListener) {
        }

        @Override
        public void removeListDataListener(ListDataListener listDataListener) {
        }
    }
}