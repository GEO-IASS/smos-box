package org.esa.beam.smos.visat;

import com.bc.ceres.binio.CompoundMember;
import com.bc.ceres.binio.CompoundType;
import com.jidesoft.swing.CheckBoxList;
import org.esa.beam.dataio.smos.L1cSmosFile;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.product.ProductSceneView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class GridPointBtDataTableToolView extends GridPointBtDataToolView {

    public static final String ID = GridPointBtDataTableToolView.class.getName();

    private JTable table;
    private DefaultTableModel nullModel;
    private JButton columnsButton;
    private JButton exportButton;

    public GridPointBtDataTableToolView() {
        nullModel = new DefaultTableModel();
    }

    @Override
    protected void updateClientComponent(ProductSceneView smosView) {
        boolean enabled = smosView != null && getSelectedSmosFile() instanceof L1cSmosFile;
        table.setEnabled(enabled);
        columnsButton.setEnabled(enabled);
        exportButton.setEnabled(enabled);
    }


    @Override
    protected JComponent createGridPointComponent() {
        table = new JTable();
        return new JScrollPane(table);
    }

    @Override
    protected JComponent createGridPointComponentOptionsComponent() {
        columnsButton = new JButton("Columns...");
        columnsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final CompoundType btDataType = ((L1cSmosFile) getSelectedSmosFile()).getBtDataType();
                final CompoundMember[] members = btDataType.getMembers();
                String[] names = new String[members.length];
                for (int i = 0; i < members.length; i++) {
                    CompoundMember member = members[i];
                    names[i] = member.getName();
                }
                CheckBoxList checkBoxList = new CheckBoxList(names);

                final ModalDialog dialog = new ModalDialog(SwingUtilities.windowForComponent(columnsButton),
                                                           "Select Columns",
                                                           new JScrollPane(checkBoxList),
                                                           ModalDialog.ID_OK_CANCEL, null);
                final int i = dialog.show();
                if (i == ModalDialog.ID_OK) {
                    // todo - filter columns (nf,20081208)
                }

            }
        });

        exportButton = new JButton("Export...");
        exportButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final TableModelExportRunner modelExportRunner = new TableModelExportRunner(
                        getPaneWindow(), getTitle(), table.getModel());
                modelExportRunner.run();
            }
        });

        final JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        optionsPanel.add(columnsButton);
        optionsPanel.add(exportButton);
        return optionsPanel;
    }


    @Override
    protected void updateGridPointBtDataComponent(GridPointBtDataset ds) {
        table.setModel(new GridPointBtDataTableModel(ds));
    }

    @Override
    protected void updateGridPointBtDataComponent(IOException e) {
        table.setModel(nullModel);
    }

    @Override
    protected void clearGridPointBtDataComponent() {
        table.setModel(nullModel);
    }

}