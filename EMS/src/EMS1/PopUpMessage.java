package EMS1;

import javax.swing.JButton;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 562915
 */
public class PopUpMessage extends javax.swing.JPanel {
    
    /**
     * Creates new form ErrorMessage
     */
    
    public PopUpMessage() {
        initComponents();
    }
    
    public void setMessage(String m) {
        Message.setText(m);
    }
    
    public JButton getYes() {
        return YesButton;
    }
    
    public JButton getNo() {
        return NoButton;
    }
    
    public void setYes(String m) {
        this.YesButton.setText(m);
    }
    
    public void setNo(String m) {
        this.NoButton.setText(m);
    }
    
    public void numButtons(int n) {
        if (n == 1) {
            NoButton.setVisible(false);
        } else if (n == 2) {
            NoButton.setVisible(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Message = new javax.swing.JLabel();
        YesButton = new javax.swing.JButton();
        NoButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(247, 247, 247));

        Message.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        Message.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Message.setText("Message");

        YesButton.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        YesButton.setText("Yes");

        NoButton.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        NoButton.setText("No");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(YesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NoButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Message, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(Message, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(YesButton, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Message;
    private javax.swing.JButton NoButton;
    private javax.swing.JButton YesButton;
    // End of variables declaration//GEN-END:variables
}