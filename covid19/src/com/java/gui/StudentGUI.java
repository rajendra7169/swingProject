package com.java.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class StudentGUI extends JFrame {
    JMenuBar menuBar;
    JPanel dataPanel;
    JPanel tablePanel;
    DefaultTableModel model;
    JTable table;
    JTextField txtName, txtid, txtcourse;
    JRadioButton rdMale, rdFemale;
    ButtonGroup bgGroup;
    JCheckBox chkPositive;
    JButton btnSave, btnUpdate, btnDelete, btnClear;
    Connection con;

    StudentGUI() {
        try {
            con = DBUtility.getDbConnection();
            System.out.println("Connection Successful");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        setTitle("Database for Tracking Student's Covid19 Status");
        setVisible(true);
        // setMinimumSize(new Dimension(300, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(getMenu());

        setLayout(new GridLayout(1, 1));
        JPanel subPanel = new JPanel(new GridLayout(1, 1));
        subPanel.add(dataUI());
        subPanel.add(tableUI());
        add(subPanel);
        pack();
        setLocationRelativeTo(null);

    }

    private JMenuBar getMenu() {
        menuBar = new JMenuBar();

        // define top level menu
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");

        // creating sub menu
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem exitItem = new JMenuItem("Exit");

        // sub menu added inside file menu
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // top level menu added inside menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);

        return menuBar;
    }

    private JPanel dataUI() {
        dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Data Entry"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        dataPanel.add(new JLabel("Name"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;

        dataPanel.add(new JLabel("Address"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;

        dataPanel.add(new JLabel("Gender"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;

        btnSave = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        dataPanel.add(btnSave, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;

        dataPanel.add(btnUpdate, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;

        dataPanel.add(btnDelete, gbc);
        gbc.gridx = 2;
        gbc.gridy = 4;

        dataPanel.add(btnClear, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtName = new JTextField(20);
        dataPanel.add(txtName, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtid = new JTextField(20);
        dataPanel.add(txtid, gbc);

        rdMale = new JRadioButton("Male");
        rdMale.setSelected(true);
        rdMale.setSize(80, 20);
        rdMale.setLocation(270, 300);
        dataPanel.add(rdMale);

        rdFemale = new JRadioButton("Female");
        rdFemale.setSelected(false);
        rdFemale.setSize(80, 20);
        rdFemale.setLocation(150, 500);
        dataPanel.add(rdFemale);

        bgGroup = new ButtonGroup();
        bgGroup.add(rdMale);
        bgGroup.add(rdFemale);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = txtName.getText().trim();
                String id = txtid.getText().trim();
                String course = txtcourse.getText().trim();

                if (name.isEmpty() || id.isEmpty()) {
                    JOptionPane.showMessageDialog(dataPanel, "Some of the fields are empty", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // model.addRow(new String[]{name, address, gender, positive});

                    String query = "INSERT INTO students (name, student_id, course) values (?,?,?)";
                    try {
                        PreparedStatement statement = con.prepareStatement(query);
                        statement.setString(1, name);
                        statement.setString(2, id);
                        statement.setString(3, course);

                        statement.executeUpdate();
                        refreshTable();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    btnClear.doClick();
                    JOptionPane.showMessageDialog(dataPanel, "New record is added successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String row = JOptionPane.showInputDialog(dataPanel, "Please enter ID number to delete?", "Queries",
                        JOptionPane.QUESTION_MESSAGE);
                int confirm = JOptionPane.showConfirmDialog(dataPanel, "Are you sure want to delete row?", "Warning",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int rowDelete = Integer.parseInt(row);
                        String query = "DELETE FROM students WHERE id = ?";

                        try {
                            PreparedStatement statement = con.prepareStatement(query);
                            statement.setInt(1, rowDelete);
                            statement.execute();
                            refreshTable();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        // model.removeRow(rowDelete - 1);
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(dataPanel, "You must enter valid number.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (ArrayIndexOutOfBoundsException exception) {
                        JOptionPane.showMessageDialog(dataPanel,
                                "Provided row doesn't exist. Please enter valid row number.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtName.setText("");
                txtid.setText("");
                txtcourse.setText("");

            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                String name = txtName.getText().trim();
                String s_id = txtid.getText().trim();
                String course = txtcourse.getText().trim();

                if (name.isEmpty() || s_id.isEmpty()) {
                    JOptionPane.showMessageDialog(dataPanel, "Some of the fields are empty", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    /*
                     * model.setValueAt(name, selectedRow, 0);
                     * model.setValueAt(address, selectedRow, 1);
                     * model.setValueAt(gender, selectedRow, 2);
                     * model.setValueAt(positive, selectedRow, 3);
                     */
                    int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    String query = "UPDATE students SET name = ?, student_id = ? , course = ? WHERE id = ?";
                    try {
                        PreparedStatement statement = con.prepareStatement(query);
                        statement.setString(1, name);
                        statement.setString(2, s_id);
                        statement.setString(3, course);
                        statement.setInt(4, id);

                        statement.executeUpdate();
                        refreshTable();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    btnClear.doClick();
                    JOptionPane.showMessageDialog(dataPanel,
                            "Record at row " + selectedRow + " is updated successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        return dataPanel;
    }

    private JPanel tableUI() {
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("List of data"));
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] { "ID", "Name", "StudentID", "Course" });
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane);

        // added event listener for row selection inside JTable
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // returns row number of selected row in JTable
                int selectedRow = table.getSelectedRow();
                // model.getValueAt(selectedRow, 0) returns a value for the cell at the given
                // row and column
                // .toString() returns string representation of the object
                txtName.setText(model.getValueAt(selectedRow, 1).toString());
                txtid.setText(model.getValueAt(selectedRow, 2).toString());
                txtcourse.setText(model.getValueAt(selectedRow, 3).toString());

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        refreshTable();
        return tablePanel;
    }

    private void refreshTable() {
        model.setRowCount(0);
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM students");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                model.addRow(new Object[] {
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("student_id"),
                        resultSet.getString("course"),
                });
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new StudentGUI();
    }
}