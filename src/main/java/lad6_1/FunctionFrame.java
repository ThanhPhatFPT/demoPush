/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lad6_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author ACER
 */
public class FunctionFrame extends javax.swing.JFrame {

    boolean addNew = false;
    boolean fill = false;
    Vector data = new Vector();
    Vector header = new Vector();
    Vector col = new Vector();
    Connection con = null;

    PreparedStatement pstDetails = null;
    PreparedStatement pstInsert = null;
    PreparedStatement pstDelete = null;
    PreparedStatement pstUpdate = null;
    String sqlInsert = "Insert into Students ([Name],Address,ParentName,Phone,standard) values(?,?,?,?,?)";
    String sqlDelete = "Delete from Students where Name=?";
    String sqlUpdate = "Update Students set Address=?, ParentName=?,Phone=? ,standard=? where Name=?";

    /**
     * Creates new form FunctionFrame
     */
    public FunctionFrame() {
        initComponents();
        setLocationRelativeTo(null);

        try {
            con = DatabaseUtilStudent.getConnection(); // Initialize con with the connection object
            pstInsert = con.prepareStatement(this.sqlInsert);
            pstUpdate = con.prepareStatement(this.sqlUpdate);
            pstDelete = con.prepareStatement(this.sqlDelete);
            pstDetails = con.prepareStatement("select * from Students",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rts = pstDetails.executeQuery();

            JOptionPane.showMessageDialog(this, "Connection Database Successful!");
            this.loadCombobox();
            this.loadData();
            fill = true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        btnUpdate.setEnabled(false);
    }

    private void loadCombobox() {
        String sql1 = "select * from dbo.standars";
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(sql1);
            Vector<String> standards = new Vector<String>();
            Vector<Integer> fees = new Vector<Integer>();
            while (rs.next()) {
                cboStan.addItem(rs.getString(1));
                cboFees.addItem(rs.getString(2));
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void loadData() throws SQLException {
        String sql = "Select [Name], standard from Students";
        try {
            // Khởi tạo PreparedStatement với ResultSet có khả năng cuộn
            pstDetails = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = pstDetails.executeQuery();

            data.removeAllElements();

            // Lấy thông tin cột nếu cần
            ResultSetMetaData rsmd = rs.getMetaData();
            int n = rsmd.getColumnCount();
            for (int i = 1; i <= n; i++) {
                col.add(rsmd.getColumnName(i));
            }

            // Lấy dữ liệu từ ResultSet
            while (rs.next()) {
                Vector v = new Vector();
                v.add(rs.getString(1));
                v.add(rs.getString(2));
                data.add(v);
            }

            // Hiển thị dữ liệu trên bảng
            TableModel tbl = new DefaultTableModel(data, col);
            this.tblStudent.setModel(tbl);

            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    boolean validates() {
        // Kiểm tra tên không được để trống và có thể chứa ký tự chữ, dấu cách và dấu gạch dưới
        if (!txtName.getText().matches("^\\s*[\\p{L}\\d_]+(\\s+[\\p{L}\\d_]+)*\\s*$")) {
            JOptionPane.showMessageDialog(this, "Tên không được để trống và chỉ chứa ký tự chữ, số, dấu cách và dấu gạch dưới", "Chú ý", JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return false;
        }

        String pName = this.txtName.getText().trim();
        // Kiểm tra xem tên đã tồn tại trong dữ liệu hay chưa
        for (Object item : data) {
            Vector v = (Vector) item;
            String name = ((String) v.get(0)).trim();
            if (pName.equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "Tên sinh viên này đã tồn tại!");
                this.txtName.grabFocus();
                return false;
            }
        }

        // Kiểm tra địa chỉ không được để trống và có thể chứa ký tự chữ, số, dấu cách và dấu gạch dưới
        if (!txtAddress.getText().matches("^\\s*[\\p{L}\\d_]+(\\s+[\\p{L}\\d_]+)*\\s*$")) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống và chỉ chứa ký tự chữ, số, dấu cách và dấu gạch dưới", "Chú ý", JOptionPane.WARNING_MESSAGE);
            txtAddress.requestFocus();
            return false;
        }

        // Kiểm tra tên phụ huynh không được để trống và có thể chứa ký tự chữ, dấu cách và dấu gạch dưới
        if (!txtParent.getText().matches("^\\s*[\\p{L}\\d_]+(\\s+[\\p{L}\\d_]+)*\\s*$")) {
            JOptionPane.showMessageDialog(this, "Tên phụ huynh không được để trống và chỉ chứa ký tự chữ, số, dấu cách và dấu gạch dưới", "Chú ý", JOptionPane.WARNING_MESSAGE);
            txtParent.requestFocus();
            return false;
        }

        // Kiểm tra số điện thoại không được để trống và chỉ chứa từ 7 đến 12 ký tự số
        if (!txtContact.getText().matches("^\\d{7,12}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống và chỉ chứa từ 7 đến 12 ký tự số", "Chú ý", JOptionPane.WARNING_MESSAGE);
            txtContact.requestFocus();
            return false;
        }

        // Kiểm tra có một loại học phải được chọn
        if (cboStan.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một loại học");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtName.setText("");
        txtAddress.setText("");
        txtParent.setText("");
        txtContact.setText("");
        cboStan.setSelectedIndex(0);
        cboFees.setSelectedIndex(0);
        txtName.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnNew = new javax.swing.JButton();
        btnInsert = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnPre = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        JS = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        txtParent = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtContact = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboStan = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cboFees = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStudent = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        btnNew.setText("New");
        btnNew.setPreferredSize(new java.awt.Dimension(80, 30));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnNew, gridBagConstraints);

        btnInsert.setText("Insert");
        btnInsert.setPreferredSize(new java.awt.Dimension(80, 30));
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnInsert, gridBagConstraints);

        btnEdit.setText("Edit");
        btnEdit.setPreferredSize(new java.awt.Dimension(80, 30));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnEdit, gridBagConstraints);

        btnUpdate.setText("Update");
        btnUpdate.setPreferredSize(new java.awt.Dimension(80, 30));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnUpdate, gridBagConstraints);

        btnNext.setText("Next");
        btnNext.setPreferredSize(new java.awt.Dimension(80, 30));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnNext, gridBagConstraints);

        btnPre.setText("Pre");
        btnPre.setPreferredSize(new java.awt.Dimension(80, 30));
        btnPre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnPre, gridBagConstraints);

        btnDelete.setText("Delete");
        btnDelete.setPreferredSize(new java.awt.Dimension(80, 30));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnDelete, gridBagConstraints);

        btnExit.setText("Exit");
        btnExit.setPreferredSize(new java.awt.Dimension(80, 30));
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(btnExit, gridBagConstraints);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Name:");
        jPanel4.add(jLabel1, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(txtName, gridBagConstraints);

        jLabel2.setText("Address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel4.add(jLabel2, gridBagConstraints);

        txtAddress.setColumns(20);
        txtAddress.setRows(5);
        JS.setViewportView(txtAddress);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(JS, gridBagConstraints);

        jLabel3.setText("ParentName:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        jPanel4.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(txtParent, gridBagConstraints);

        jLabel4.setText("ContactNo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        jPanel4.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(txtContact, gridBagConstraints);

        jLabel5.setText("Standard");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        jPanel4.add(jLabel5, gridBagConstraints);

        cboStan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "************" }));
        cboStan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboStanActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(cboStan, gridBagConstraints);

        jLabel6.setText("Fees");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        jPanel4.add(jLabel6, gridBagConstraints);

        cboFees.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "************" }));
        cboFees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFeesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(cboFees, gridBagConstraints);

        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.LINE_END);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        tblStudent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStudentMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblStudentMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblStudent);

        jPanel2.add(jScrollPane1);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboStanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboStanActionPerformed
        // TODO add your handling code here:
        cboStan.setSelectedIndex(cboFees.getSelectedIndex());
    }

    private void cboeesActionPerformed(java.awt.event.ActionEvent evt) {
       cboStan.setSelectedIndex(cboFees.getSelectedIndex());
    }//GEN-LAST:event_cboStanActionPerformed

    private void tblStudentMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStudentMouseReleased
        // TODO add your handling code here:
//        if (this.tblStudent.getCellEditor() != null) {
//            this.tblStudent.getCellEditor().cancelCellEditing();
//        }
    }//GEN-LAST:event_tblStudentMouseReleased

    private void tblStudentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStudentMouseClicked
        // TODO add your handling code here:

        try {
            Connection connection = DatabaseUtilStudent.getConnection();
            String query = "SELECT * FROM Students"; // Câu truy vấn SQL để lấy dữ liệu sinh viên
            Statement statement = connection.createStatement();
            ResultSet rts = statement.executeQuery(query);

            int row = tblStudent.getSelectedRow();
            String name = (String) tblStudent.getValueAt(row, 0);
            boolean found = false;

            // Iterate through the ResultSet to find the student by name
            while (rts.next()) {
                String str = rts.getString(2);
                if (str.equalsIgnoreCase(name)) {
                    txtName.setText(rts.getString(2));
                    txtAddress.setText(rts.getString(3));
                    txtContact.setText(String.valueOf(rts.getInt(5)));
                    txtParent.setText(rts.getString(4));
                    cboStan.setSelectedItem(rts.getString(6));
                    cboFees.setSelectedItem(rts.getInt(7));
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên: " + name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi SQL: " + e.getMessage());
            e.printStackTrace(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        }

// Vô hiệu hóa các trường nhập liệu
        txtName.setEnabled(false);
        txtAddress.setEnabled(false);
        txtParent.setEnabled(false);
        txtContact.setEnabled(false);
        cboStan.setEnabled(false);
        cboFees.setEnabled(false);


    }//GEN-LAST:event_tblStudentMouseClicked

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        try {
            int n;
            if (addNew) {
                // Insert new record
                pstInsert.setString(1, this.txtName.getText().trim());
                pstInsert.setString(2, txtAddress.getText().trim());
                pstInsert.setString(3, txtParent.getText().trim());
                pstInsert.setString(4, txtContact.getText().trim());
                pstInsert.setString(5, (String) cboStan.getSelectedItem());
                n = pstInsert.executeUpdate(); // Execute the insert statement
            } else {
                // Update existing record
                pstUpdate.setString(1, this.txtName.getText().trim());
                pstUpdate.setString(2, this.txtAddress.getText().trim());
                pstUpdate.setString(3, this.txtParent.getText().trim());
                pstUpdate.setString(4, this.txtContact.getText().trim());
                pstUpdate.setString(5, (String) cboStan.getSelectedItem());
                n = pstUpdate.executeUpdate(); // Execute the update statement
            }
            // Reload data after insert/update
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }

    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        btnUpdate.setEnabled(true);
        btnEdit.setEnabled(true);
        txtName.setEnabled(true);
        txtAddress.setEnabled(true);
        txtParent.setEnabled(true);
        txtContact.setEnabled(true);
        cboStan.setEnabled(true);
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnPreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreActionPerformed
        // TODO add your handling code here:
        try {
            Connection connection = DatabaseUtilStudent.getConnection();
            String query = "SELECT * FROM Students"; // Câu truy vấn SQL để lấy dữ liệu sinh viên
            Statement statement = connection.createStatement();
            ResultSet rts = statement.executeQuery(query);

            if (!rts.next()) {
                JOptionPane.showMessageDialog(null, "ResultSet is empty!");

            }

            rts.previous();

            btnNext.setEnabled(true);

            if (rts.isBeforeFirst()) {

                btnPre.setEnabled(false);

                JOptionPane.showMessageDialog(null, "You have reached the first record "
                        + "of the ResultSet!!!!");
            } else {

                rts.next();

                txtName.setText(rts.getString(2));
                txtAddress.setText(rts.getString(3));
                txtParent.setText(rts.getString(4));
                txtContact.setText(rts.getString(5));
                cboStan.setSelectedItem(rts.getString(6));
            }
        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage());
        }

    }//GEN-LAST:event_btnPreActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        try {
            Connection connection = DatabaseUtilStudent.getConnection();
            String query = "SELECT * FROM Students"; // Câu truy vấn SQL để lấy dữ liệu sinh viên
            Statement statement = connection.createStatement();
            ResultSet rts = statement.executeQuery(query);
            rts.next();
            btnPre.setEnabled(true);
            if (rts.isAfterLast() || rts.isBeforeFirst()) {
                btnNext.setEnabled(false);

                btnPre.setEnabled(true);
                JOptionPane.showMessageDialog(null, "You have reached the last record"
                        + " of the ResultSet!!!!");
            } else {
                txtName.setText(rts.getString(2));
                txtAddress.setText(rts.getString(3));
                txtParent.setText(rts.getString(4));
                txtContact.setText(rts.getString(5));
                cboStan.setSelectedItem(rts.getString(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        fill = false;
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        try {
            int n = this.tblStudent.getSelectedRow();
            if (n >= 0)//nguoi dung co chon
            {
//this.pnlDetails.setVisible(false);
                Vector v = (Vector) data.get(n);
                int ans = JOptionPane.showConfirmDialog(this, "Ban co thuc su muon xoa Sinh Vien "
                        + ((String) v.get(0)).trim() + " khong?");
                if (ans == JOptionPane.YES_OPTION) {
                    pstDelete.setString(1, (String) v.get(0));
                    pstDelete.executeUpdate();
                    this.loadData();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        // TODO add your handling code here:
        if (!validates()) {
            return;
        }
        String name = txtName.getText();
        String addr = txtAddress.getText();
        String parentName = txtParent.getText();
        String phone = txtContact.getText();
        String standard = (String) cboStan.getSelectedItem();

        try {
            pstInsert.setString(1, name);
            pstInsert.setString(2, addr);
            pstInsert.setString(3, parentName);
            pstInsert.setString(4, phone);
            pstInsert.setString(5, standard);

            int addRows = pstInsert.executeUpdate();
            this.loadData();
            clearForm();
            if (addRows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Students Details Have Been Save", "Successfull", JOptionPane.INFORMATION_MESSAGE);

                // Sau khi thêm mới, cập nhật lại dữ liệu cho combobox
                loadCombobox();
            }
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnNewActionPerformed

    private void cboFeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFeesActionPerformed
        // TODO add your handling code here:
        cboStan.setSelectedIndex(cboFees.getSelectedIndex());
    }//GEN-LAST:event_cboFeesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FunctionFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FunctionFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FunctionFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FunctionFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FunctionFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JS;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPre;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboFees;
    private javax.swing.JComboBox<String> cboStan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStudent;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtContact;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtParent;
    // End of variables declaration//GEN-END:variables
}
