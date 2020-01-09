package EMS1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Howard
 */
public class MainJFrame extends javax.swing.JFrame {

    //HashTable
    MyHashTable theHashTable;
    EmployeeInfo selectedEmployee;

    //Form Layout
    CardLayout c;

    //PopUpFrame
    JFrame popUpFrame = new JFrame();
    PopUpMessage popUpPanel = new PopUpMessage();
    String command = "Open";

    //PopUpFrame Save and Load
    JFrame SLONFrame = new JFrame();
    SaveLoadOpenNewPanel SLONPanel = new SaveLoadOpenNewPanel();

    //Add Employee Booleans
    boolean FTERaised = false;
    boolean PTERaised = true;
    boolean autoSave = false;

    //Reading and writing to file
    BufferedReader reader;
    BufferedWriter writer;
    String loadFileName;
    String saveFileName;
    String separator = "&";

    public MainJFrame() {
        theHashTable = new MyHashTable(10);

        initComponents();
        initCard();
        initPopUp();
        initSLPopup();

    }

    // Custom Initializers
    private void initPopUp() {
        popUpFrame.add(popUpPanel);
        popUpFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        popUpFrame.setResizable(false);
        popUpFrame.setType(Type.POPUP);
        popUpFrame.setTitle("Pop Up Window");
        popUpFrame.setVisible(false);
        popUpFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                popUpFrameClosing(evt);
            }

            private void popUpFrameClosing(WindowEvent evt) {
//                System.out.println("POPUPCLOSING");
//                if (SLFrame.isEnabled()) {
//                    setEnabled(true);
//                    setFocusableWindowState(true);
//                } else {
//                    SLFrame.setEnabled(true);
//                    SLFrame.setFocusableWindowState(true);
//                }
            }
        });
        popUpPanel.getYes().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                YesButtonMouseReleased(evt);
            }

            private void YesButtonMouseReleased(MouseEvent evt) {
                if (popUpPanel.getNo().isVisible()) { //Two buttons
                    switch (command) {
                        case "Load":
                            popUpLoadEmployees();
                            break;
                        case "Open":
                            popUpOpenEmployees();
                            break;

                    }
                } else { // One Button
                    switch (command) {
                        case "Load":
                            popUpLoadEmployees();
                            break;
                        case "Open":
                            popUpOpenEmployees();
                            break;
                        case "Employee Does Not Exist":
                        case "Load Success":
                        case "Save Success":
                        case "Open Success":
                        case "Add Employee Error":
                        case "Add Employee Success":
                        case "Edit Employee Error":
                        case "Edit Employee Success":
                        case "Input Error":
                        case "No Selection":
                            setEnabled(true);
                            setFocusableWindowState(true);
                    }

                }

                popUpFrame.setVisible(false);
            }
        }
        );
        popUpPanel.getNo().addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                NoButtonMouseReleased(evt);
            }

            private void NoButtonMouseReleased(MouseEvent evt) {
                setEnabled(true);
                setFocusableWindowState(true);

                popUpFrame.setVisible(false);
            }
        });
    }

    private void initSLPopup() {
        SLONFrame.add(SLONPanel);
        SLONFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        SLONFrame.setTitle("Employee Mangement System");
        SLONFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                SLFrameClosing(evt);

            }

            private void SLFrameClosing(WindowEvent evt) {
                setEnabled(true);
                setFocusableWindowState(true);
            }
        });
        SLONPanel.getButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SLButtonActionPerformed(evt);
            }

            private void SLButtonActionPerformed(ActionEvent evt) {
                String m = SLONPanel.getMode();
                if (m == "load") {
                    //Load Employees
                    loadEmployees();
                } else if (m == "save") {
                    //Save Employees
                    saveFileName = SLONPanel.getFileName();
                    saveEmployees();
                } else if (m == "open") {
                    openEmployees();
                }

                SLONFrame.setVisible(false);

            }
        });
        SLONPanel.getFileNameField().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                FileNameFieldKeyTyped(evt);
            }

            private void FileNameFieldKeyTyped(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String m = SLONPanel.getMode();
                    if (m == "load") {
                        //Load Employees
                        loadEmployees();
                    } else if (m == "save") {
                        //Save Employees
                        saveFileName = SLONPanel.getFileName();
                        saveEmployees();
                    } else if (m == "open") {
                        openEmployees();
                    }

                    SLONFrame.setVisible(false);
                }
            }
        });

    }

    private void initCard() {
        c = (CardLayout) getContentPane().getLayout();
        c.show(getContentPane(), "WelcomeCard");
//        getContentPane().setPreferredSize(WelcomePanel.getPreferredSize());
//        pack();
//        setLocationRelativeTo(null);
        MenuBar.setVisible(false);

    }

    // Action Methods
    private void loadEmployees() {
        loadFileName = SLONPanel.getFileName();
        File loadFile = new File("src/" + loadFileName + ".txt");
        if (loadFile.length() == 0) {
            command = "Load";
            popUpMessageOne("File Does Not Exist");
        } else {
            try {
                reader = new BufferedReader(new FileReader(new File("src/" + loadFileName + ".txt")));
                String line;
                while (!"END".equals(line = reader.readLine())) {
                    String[] attr = line.split("\\&");
                    if (attr.length == 9) {
                        this.theHashTable.addEmployee(new PartTimeEmployee(parseInt(attr[0]),
                                attr[1],
                                attr[2],
                                attr[3],
                                parseDouble(attr[4]),
                                attr[5],
                                parseDouble(attr[6]),
                                parseDouble(attr[7]),
                                parseDouble(attr[8])));
                    } else if (attr.length == 7) {
                        this.theHashTable.addEmployee(new FullTimeEmployee(parseInt(attr[0]),
                                attr[1],
                                attr[2],
                                attr[3],
                                parseDouble(attr[4]),
                                attr[5],
                                parseDouble(attr[6])));
                    }

                }
                reader.close();
                System.out.println("Finished Loading File");

            } catch (FileNotFoundException ex) {
                System.out.println("File not found?");
            } catch (IOException ex) {
                System.out.println("Error reading");
            }
            command = "Load Success";
            popUpMessageOne("Successfully Loaded");
            showDisplayEmployees();
//            setEnabled(true);
//            setFocusableWindowState(true);
        }

    }

    private void saveEmployees() {
        if ("".equals(saveFileName)) {
            popUpMessageOne("Please Enter A Real File Name");
        } else if (saveFileName == null) {
            popUpSaveEmployees();
        } else {
            try {
                File saveFile = new File("src/" + saveFileName + ".txt");
                writer = new BufferedWriter(new FileWriter(saveFile));

                for (ArrayList<EmployeeInfo> bucket : theHashTable.buckets) {
                    for (EmployeeInfo emp : bucket) {
                        if (emp instanceof PartTimeEmployee) {
                            writer.write(emp.getEmployeeNumber() + separator
                                    + emp.getFirstName() + separator
                                    + emp.getLastName() + separator
                                    + emp.getGender() + separator
                                    + emp.getDeductionRate() + separator
                                    + emp.getLocation() + separator
                                    + ((PartTimeEmployee) emp).getHourlyWage() + separator
                                    + ((PartTimeEmployee) emp).getHoursPerWeek() + separator
                                    + ((PartTimeEmployee) emp).getWeeksPerYear() + "\r\n");
                        } else if (emp instanceof FullTimeEmployee) {
                            writer.write(emp.getEmployeeNumber() + separator
                                    + emp.getFirstName() + separator
                                    + emp.getLastName() + separator
                                    + emp.getGender() + separator
                                    + emp.getDeductionRate() + separator
                                    + emp.getLocation() + separator
                                    + ((FullTimeEmployee) emp).getYearlySalary() + "\r\n");
                        }

                    }
                }

                writer.write("END");
                System.out.println("Finished Writing To File");

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    System.out.println("Error Closing Writer");
                }
            }
            command = "Save Success";
            popUpMessageOne("Successfully Saved");
//            setEnabled(true);
//            setFocusableWindowState(true);

        }

    }

    private void openEmployees() {
        File loadFile = new File("src/" + SLONPanel.getFileName() + ".txt");
        if (loadFile.length() == 0) {
            command = "Open";
            popUpMessageOne("File Does Not Exist");
        } else {
            loadFileName = SLONPanel.getFileName();
            saveFileName = SLONPanel.getFileName();
            this.theHashTable = new MyHashTable(10);
            try {
                reader = new BufferedReader(new FileReader(new File("src/" + loadFileName + ".txt")));
                String line;
                while (!"END".equals(line = reader.readLine())) {
                    String[] attr = line.split("\\&");
                    if (attr.length == 9) {
                        this.theHashTable.addEmployee(new PartTimeEmployee(parseInt(attr[0]),
                                attr[1],
                                attr[2],
                                attr[3],
                                parseDouble(attr[4]),
                                attr[5],
                                parseDouble(attr[6]),
                                parseDouble(attr[7]),
                                parseDouble(attr[8])));
                    } else if (attr.length == 7) {
                        this.theHashTable.addEmployee(new FullTimeEmployee(parseInt(attr[0]),
                                attr[1],
                                attr[2],
                                attr[3],
                                parseDouble(attr[4]),
                                attr[5],
                                parseDouble(attr[6])));
                    }

                }
                reader.close();
                System.out.println("Finished Opening File");

            } catch (FileNotFoundException ex) {
                System.out.println("File not found?");
            } catch (IOException ex) {
                System.out.println("Error reading");
            }
            command = "Open Success";
            popUpMessageOne("Successfully Opened");
//            setEnabled(true);
//            setFocusableWindowState(true);
            showDisplayEmployees();
        }
    }

    // Displaying Menus
    private void showMenu() {
//        this.setVisible(false);
        c.show(getContentPane(), "MainMenuCard");
//        getContentPane().setPreferredSize(MainMenu.getPreferredSize());
//        pack();
//        setLocationRelativeTo(null);
//        this.setVisible(true);
    }

    private void showEmployeeProfile() {
//        this.setVisible(false);
        c.show(getContentPane(), "EmployeeProfileCard");

        if (selectedEmployee instanceof PartTimeEmployee) {
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getEmployeeNumber(), 0, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getFirstName(), 1, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getLastName(), 2, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getGender(), 3, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getDeductionRate(), 4, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getLocation(), 5, 1);
            EmployeeProfileTable.getModel().setValueAt(((PartTimeEmployee) selectedEmployee).calcAnnualGrossIncome(), 6, 1);
            EmployeeProfileTable.getModel().setValueAt(((PartTimeEmployee) selectedEmployee).getHourlyWage(), 7, 1);
            EmployeeProfileTable.getModel().setValueAt(((PartTimeEmployee) selectedEmployee).getHoursPerWeek(), 8, 1);
            EmployeeProfileTable.getModel().setValueAt(((PartTimeEmployee) selectedEmployee).getWeeksPerYear(), 9, 1);
            EmployeeProfileTable.getModel().setValueAt("Hourly Wage", 7, 0);
            EmployeeProfileTable.getModel().setValueAt("Hours Per Week", 8, 0);
            EmployeeProfileTable.getModel().setValueAt("Weeks Per Year", 9, 0);
            EmployeeProfileTable.setPreferredSize(new Dimension(420, 250));
            EmployeeChangeButton.setText("Change to Full Time");

        } else if (selectedEmployee instanceof FullTimeEmployee) {
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getEmployeeNumber(), 0, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getFirstName(), 1, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getLastName(), 2, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getGender(), 3, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getDeductionRate(), 4, 1);
            EmployeeProfileTable.getModel().setValueAt(selectedEmployee.getLocation(), 5, 1);
            EmployeeProfileTable.getModel().setValueAt(((FullTimeEmployee) selectedEmployee).calcAnnualGrossIncome(), 6, 1);
            EmployeeProfileTable.getModel().setValueAt(((FullTimeEmployee) selectedEmployee).getYearlySalary(), 7, 1);
            EmployeeProfileTable.getModel().setValueAt(null, 8, 1);
            EmployeeProfileTable.getModel().setValueAt(null, 9, 1);
            EmployeeProfileTable.getModel().setValueAt("Yearly Salary", 7, 0);
            EmployeeProfileTable.getModel().setValueAt(null, 8, 0);
            EmployeeProfileTable.getModel().setValueAt(null, 9, 0);
            EmployeeProfileTable.setPreferredSize(new Dimension(420, 200));
            EmployeeChangeButton.setText("Change to Part Time");
        }

//        getContentPane().setPreferredSize(EmployeeProfilePanel.getPreferredSize());
//        pack();
//        setLocationRelativeTo(null);
//        this.setVisible(true);
    }

    private void showSearchEmployees() {
//        this.setVisible(false);
        c.show(getContentPane(), "SearchEmployeeCard");
//        getContentPane().setPreferredSize(SearchEmployeePanel.getPreferredSize());
//        pack();
//        setLocationRelativeTo(null);
//        this.setVisible(true);
        SearchTextField.requestFocus();

    }

    private void showAddEmployees() {
//        this.setVisible(false);
        c.show(getContentPane(), "AddEmployeeCard");

        this.HPWLabel.setVisible(true);
        this.HPWField.setVisible(true);
        this.WPYField.setVisible(true);
        this.WPYLabel.setVisible(true);

//        getContentPane().setPreferredSize(AddEmployeePanel.getPreferredSize());
        FTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        PTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        FTERaised = false;
        PTERaised = true;

        this.SWLabel.setText("Salary");
        this.HPWLabel.setVisible(false);
        this.HPWField.setVisible(false);
        this.WPYField.setVisible(false);
        this.WPYLabel.setVisible(false);

//        pack();
//
//        setLocationRelativeTo(null);
//        this.setVisible(true);
    }

    private void showDisplayEmployees() {
//        this.setVisible(false);
        c.show(getContentPane(), "DisplayEmployeeCard");
        ArrayList<EmployeeInfo> displayList = theHashTable.displayEmployees();

        Object[][] dtm = new Object[displayList.size()][];

        for (int r = 0; r < displayList.size(); r++) {

            EmployeeInfo emp = displayList.get(r);
            int i = 0;
            if (emp instanceof FullTimeEmployee) {
                i = (int) ((FullTimeEmployee) emp).calcAnnualGrossIncome();
            } else if (emp instanceof PartTimeEmployee) {
                i = (int) ((PartTimeEmployee) emp).calcAnnualGrossIncome();
            }
            dtm[r] = new Object[]{
                emp.getEmployeeNumber(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getGender(),
                emp.getDeductionRate(),
                emp.getLocation(),
                i};
        }

        DisplayEmployeeTable.setModel(new javax.swing.table.DefaultTableModel(
                dtm,
                new String[]{
                    "Employee Number", "First Name", "Last Name", "Gender", "Deduction Rate", "Location", "Gross Annual Income"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        DisplayEmployeeTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        DisplayEmployeeTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        DisplayEmployeeTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        DisplayEmployeeTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        DisplayEmployeeTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        DisplayEmployeeTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        DisplayEmployeeTable.getColumnModel().getColumn(6).setPreferredWidth(130);

        DisplayEmployeeScroll.getViewport().setBackground(Color.white);

        DisplayEmployeeSearchField.setText("");

//        getContentPane().setPreferredSize(DisplayEmployeePanel.getPreferredSize());
//        pack();
//        setLocationRelativeTo(null);
//        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AddEmployeePanel = new javax.swing.JPanel();
        FTEButton = new javax.swing.JLabel();
        PTEButton = new javax.swing.JLabel();
        AddEmployeeLabel = new javax.swing.JLabel();
        EmployeeNumLabel = new javax.swing.JLabel();
        FNLabel = new javax.swing.JLabel();
        LNLabel = new javax.swing.JLabel();
        GLabel = new javax.swing.JLabel();
        DRLabel = new javax.swing.JLabel();
        ENField = new javax.swing.JTextField();
        FNField = new javax.swing.JTextField();
        LNField = new javax.swing.JTextField();
        DRField = new javax.swing.JTextField();
        GMaleRadio = new javax.swing.JRadioButton();
        GFemaleRadio = new javax.swing.JRadioButton();
        GOtherField = new javax.swing.JTextField();
        SWLabel = new javax.swing.JLabel();
        HPWLabel = new javax.swing.JLabel();
        WPYLabel = new javax.swing.JLabel();
        SWField = new javax.swing.JTextField();
        HPWField = new javax.swing.JTextField();
        WPYField = new javax.swing.JTextField();
        AddEmpReturn = new javax.swing.JLabel();
        AddEmpClear = new javax.swing.JLabel();
        AddEmployeeButton = new javax.swing.JLabel();
        GOtherRadio = new javax.swing.JRadioButton();
        LLabel = new javax.swing.JLabel();
        LField = new javax.swing.JTextField();
        WelcomePanel = new javax.swing.JPanel();
        ProceedButton = new javax.swing.JLabel();
        TitleTextArea = new javax.swing.JTextArea();
        MainMenu = new javax.swing.JPanel();
        MainMenuLabel = new javax.swing.JLabel();
        AddEmpButton = new javax.swing.JLabel();
        DisplayEmpButton = new javax.swing.JLabel();
        SearchEmpButton = new javax.swing.JLabel();
        LoadEmpButton = new javax.swing.JLabel();
        SearchEmployeePanel = new javax.swing.JPanel();
        SearchEmployeeLabel = new javax.swing.JLabel();
        SearchBasedOnLabel = new javax.swing.JLabel();
        SearchTextField = new javax.swing.JTextField();
        SearchButtonLabel = new javax.swing.JLabel();
        SearchReturnButton = new javax.swing.JLabel();
        DisplayEmployeePanel = new javax.swing.JPanel();
        DisplayEmployeeScroll = new javax.swing.JScrollPane();
        DisplayEmployeeTable = new javax.swing.JTable();
        DisplayReturnButton = new javax.swing.JLabel();
        DisplayEditButton = new javax.swing.JLabel();
        DisplayRemoveButton = new javax.swing.JLabel();
        AddEmployeeLabel1 = new javax.swing.JLabel();
        DisplayEmployeeSearchField = new javax.swing.JTextField();
        DisplaySearchButton = new javax.swing.JLabel();
        DisplayUserManual = new javax.swing.JLabel();
        EmployeeProfilePanel = new javax.swing.JPanel();
        EmployeeProfileLabel = new javax.swing.JLabel();
        EmployeeProfileTable = new javax.swing.JTable();
        EmployeeProfileReturnButton = new javax.swing.JLabel();
        EmployeeInfoUpdateButton = new javax.swing.JLabel();
        EmployeeRemoveButton = new javax.swing.JLabel();
        EmployeeChangeButton = new javax.swing.JLabel();
        MenuBar = new javax.swing.JMenuBar();
        BarFile = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ItemOpenData = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        CheckBoxAutoSave = new javax.swing.JCheckBoxMenuItem();
        ItemSave = new javax.swing.JMenuItem();
        ItemSaveToNew = new javax.swing.JMenuItem();
        BarMenu = new javax.swing.JMenu();
        ItemAddEmp = new javax.swing.JMenuItem();
        ItemDispEmp = new javax.swing.JMenuItem();
        ItemLoadEmp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Employee Management System");
        setBackground(new java.awt.Color(247, 247, 247));
        setResizable(false);
        getContentPane().setLayout(new java.awt.CardLayout());

        AddEmployeePanel.setBackground(new java.awt.Color(247, 247, 247));
        AddEmployeePanel.setPreferredSize(new java.awt.Dimension(600, 550));

        FTEButton.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        FTEButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        FTEButton.setText("Full Time Employee");
        FTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        FTEButton.setPreferredSize(new java.awt.Dimension(250, 60));
        FTEButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                FTEButtonMouseReleased(evt);
            }
        });

        PTEButton.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        PTEButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PTEButton.setText("Part Time Employee");
        PTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PTEButton.setPreferredSize(new java.awt.Dimension(250, 60));
        PTEButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                PTEButtonMouseReleased(evt);
            }
        });

        AddEmployeeLabel.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        AddEmployeeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddEmployeeLabel.setText("Add Employee");
        AddEmployeeLabel.setPreferredSize(new java.awt.Dimension(510, 30));

        EmployeeNumLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        EmployeeNumLabel.setText("Employee Number");
        EmployeeNumLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        FNLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        FNLabel.setText("First Name");
        FNLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        LNLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        LNLabel.setText("Last Name");
        LNLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        GLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        GLabel.setText("Gender");
        GLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        DRLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        DRLabel.setText("Deduction Rate");
        DRLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        ENField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        ENField.setPreferredSize(new java.awt.Dimension(250, 25));

        FNField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        FNField.setPreferredSize(new java.awt.Dimension(250, 25));

        LNField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        LNField.setPreferredSize(new java.awt.Dimension(250, 25));

        DRField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        DRField.setPreferredSize(new java.awt.Dimension(250, 25));

        GMaleRadio.setBackground(new java.awt.Color(247, 247, 247));
        GMaleRadio.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        GMaleRadio.setText("M");
        GMaleRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GMaleRadioActionPerformed(evt);
            }
        });

        GFemaleRadio.setBackground(new java.awt.Color(247, 247, 247));
        GFemaleRadio.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        GFemaleRadio.setText("F");
        GFemaleRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GFemaleRadioActionPerformed(evt);
            }
        });

        GOtherField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        GOtherField.setPreferredSize(new java.awt.Dimension(6, 25));

        SWLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        SWLabel.setText("Salary");
        SWLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        HPWLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        HPWLabel.setText("Hours per Week");
        HPWLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        WPYLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        WPYLabel.setText("Weeks per Year");
        WPYLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        SWField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        SWField.setPreferredSize(new java.awt.Dimension(250, 25));

        HPWField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        HPWField.setPreferredSize(new java.awt.Dimension(250, 25));

        WPYField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        WPYField.setPreferredSize(new java.awt.Dimension(250, 25));

        AddEmpReturn.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        AddEmpReturn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddEmpReturn.setText("Return");
        AddEmpReturn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AddEmpReturn.setPreferredSize(new java.awt.Dimension(100, 40));
        AddEmpReturn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                AddEmpReturnMouseReleased(evt);
            }
        });

        AddEmpClear.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        AddEmpClear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddEmpClear.setText("Clear All");
        AddEmpClear.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AddEmpClear.setPreferredSize(new java.awt.Dimension(100, 40));
        AddEmpClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                AddEmpClearMouseReleased(evt);
            }
        });

        AddEmployeeButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        AddEmployeeButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddEmployeeButton.setText("Add Employee");
        AddEmployeeButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AddEmployeeButton.setPreferredSize(new java.awt.Dimension(80, 40));
        AddEmployeeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                AddEmployeeButtonMouseReleased(evt);
            }
        });

        GOtherRadio.setBackground(new java.awt.Color(247, 247, 247));
        GOtherRadio.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        GOtherRadio.setText("Other:");
        GOtherRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GOtherRadioActionPerformed(evt);
            }
        });

        LLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        LLabel.setText("Location");
        LLabel.setPreferredSize(new java.awt.Dimension(250, 20));

        LField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        LField.setPreferredSize(new java.awt.Dimension(250, 25));

        javax.swing.GroupLayout AddEmployeePanelLayout = new javax.swing.GroupLayout(AddEmployeePanel);
        AddEmployeePanel.setLayout(AddEmployeePanelLayout);
        AddEmployeePanelLayout.setHorizontalGroup(
            AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                        .addComponent(LLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                            .addComponent(HPWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(HPWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                            .addComponent(SWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(SWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                            .addComponent(WPYLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(WPYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                            .addComponent(GLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(GMaleRadio)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(GFemaleRadio)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(GOtherRadio)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(GOtherField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEmployeePanelLayout.createSequentialGroup()
                            .addComponent(AddEmpReturn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AddEmpClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(AddEmployeeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                                .addComponent(DRLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(DRField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                                .addComponent(LNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(LNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                                .addComponent(FNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(FNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                                .addComponent(EmployeeNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ENField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(AddEmployeeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                                .addComponent(FTEButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(PTEButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(45, 45, 45))
        );
        AddEmployeePanelLayout.setVerticalGroup(
            AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEmployeePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(AddEmployeeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PTEButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FTEButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EmployeeNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ENField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GMaleRadio)
                    .addComponent(GFemaleRadio)
                    .addComponent(GOtherField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GOtherRadio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DRLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DRField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HPWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HPWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(WPYLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(WPYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(AddEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddEmpReturn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddEmpClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddEmployeeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        getContentPane().add(AddEmployeePanel, "AddEmployeeCard");

        WelcomePanel.setBackground(new java.awt.Color(247, 247, 247));
        WelcomePanel.setPreferredSize(new java.awt.Dimension(600, 300));

        ProceedButton.setBackground(new java.awt.Color(247, 247, 247));
        ProceedButton.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        ProceedButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProceedButton.setText("Proceed");
        ProceedButton.setToolTipText("");
        ProceedButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ProceedButton.setOpaque(true);
        ProceedButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProceedButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProceedButtonMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ProceedButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ProceedButtonMouseReleased(evt);
            }
        });

        TitleTextArea.setBackground(new java.awt.Color(247, 247, 247));
        TitleTextArea.setColumns(20);
        TitleTextArea.setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        TitleTextArea.setRows(5);
        TitleTextArea.setText("          Welcome to the \n    Employee Management System!");
        TitleTextArea.setAutoscrolls(false);
        TitleTextArea.setBorder(null);
        TitleTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TitleTextArea.setFocusable(false);
        TitleTextArea.setRequestFocusEnabled(false);

        javax.swing.GroupLayout WelcomePanelLayout = new javax.swing.GroupLayout(WelcomePanel);
        WelcomePanel.setLayout(WelcomePanelLayout);
        WelcomePanelLayout.setHorizontalGroup(
            WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WelcomePanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TitleTextArea)
                    .addGroup(WelcomePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ProceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(50, 50, 50))
        );
        WelcomePanelLayout.setVerticalGroup(
            WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WelcomePanelLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(TitleTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ProceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        getContentPane().add(WelcomePanel, "WelcomeCard");

        MainMenu.setBackground(new java.awt.Color(247, 247, 247));
        MainMenu.setPreferredSize(new java.awt.Dimension(550, 380));

        MainMenuLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        MainMenuLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MainMenuLabel.setText("Main Menu");
        MainMenuLabel.setPreferredSize(new java.awt.Dimension(200, 29));
        MainMenuLabel.setVerifyInputWhenFocusTarget(false);

        AddEmpButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AddEmpButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddEmpButton.setText("Add Employee");
        AddEmpButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AddEmpButton.setPreferredSize(new java.awt.Dimension(220, 100));
        AddEmpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                AddEmpButtonMouseReleased(evt);
            }
        });

        DisplayEmpButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        DisplayEmpButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplayEmpButton.setText("Display Employees");
        DisplayEmpButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DisplayEmpButton.setPreferredSize(new java.awt.Dimension(220, 100));
        DisplayEmpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                DisplayEmpButtonMouseReleased(evt);
            }
        });

        SearchEmpButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        SearchEmpButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SearchEmpButton.setText("Search/Edit Employee");
        SearchEmpButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SearchEmpButton.setPreferredSize(new java.awt.Dimension(220, 100));
        SearchEmpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SearchEmpButtonMouseReleased(evt);
            }
        });

        LoadEmpButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoadEmpButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LoadEmpButton.setText("Load Employees");
        LoadEmpButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        LoadEmpButton.setPreferredSize(new java.awt.Dimension(220, 100));
        LoadEmpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                LoadEmpButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout MainMenuLayout = new javax.swing.GroupLayout(MainMenu);
        MainMenu.setLayout(MainMenuLayout);
        MainMenuLayout.setHorizontalGroup(
            MainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainMenuLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(MainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainMenuLayout.createSequentialGroup()
                        .addGroup(MainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DisplayEmpButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AddEmpButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(MainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SearchEmpButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LoadEmpButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(MainMenuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );
        MainMenuLayout.setVerticalGroup(
            MainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainMenuLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(MainMenuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(MainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddEmpButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchEmpButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(MainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DisplayEmpButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LoadEmpButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );

        getContentPane().add(MainMenu, "MainMenuCard");

        SearchEmployeePanel.setBackground(new java.awt.Color(247, 247, 247));

        SearchEmployeeLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SearchEmployeeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SearchEmployeeLabel.setText("Search Employee");
        SearchEmployeeLabel.setPreferredSize(new java.awt.Dimension(200, 30));

        SearchBasedOnLabel.setText("Enter Employee Number");

        SearchButtonLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SearchButtonLabel.setText("Search");
        SearchButtonLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SearchButtonLabel.setPreferredSize(new java.awt.Dimension(90, 40));
        SearchButtonLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SearchButtonLabelMouseReleased(evt);
            }
        });

        SearchReturnButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SearchReturnButton.setText("Return");
        SearchReturnButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SearchReturnButton.setPreferredSize(new java.awt.Dimension(90, 40));
        SearchReturnButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SearchReturnButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout SearchEmployeePanelLayout = new javax.swing.GroupLayout(SearchEmployeePanel);
        SearchEmployeePanel.setLayout(SearchEmployeePanelLayout);
        SearchEmployeePanelLayout.setHorizontalGroup(
            SearchEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchEmployeePanelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(SearchEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(SearchEmployeePanelLayout.createSequentialGroup()
                        .addComponent(SearchReturnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SearchButtonLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(SearchEmployeeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .addComponent(SearchBasedOnLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SearchTextField))
                .addContainerGap(115, Short.MAX_VALUE))
        );
        SearchEmployeePanelLayout.setVerticalGroup(
            SearchEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchEmployeePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(SearchEmployeeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(SearchBasedOnLabel)
                .addGap(25, 25, 25)
                .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 320, Short.MAX_VALUE)
                .addGroup(SearchEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchReturnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchButtonLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        getContentPane().add(SearchEmployeePanel, "SearchEmployeeCard");

        DisplayEmployeePanel.setBackground(new java.awt.Color(247, 247, 247));
        DisplayEmployeePanel.setPreferredSize(new java.awt.Dimension(600, 420));

        DisplayEmployeeScroll.setBackground(new java.awt.Color(247, 247, 247));
        DisplayEmployeeScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        DisplayEmployeeScroll.setOpaque(false);

        DisplayEmployeeTable.setAutoCreateRowSorter(true);
        DisplayEmployeeTable.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        DisplayEmployeeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee Number", "First Name", "Last Name", "Gender", "Deduction Rate", "Location", "Gross Annual Income"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        DisplayEmployeeTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        DisplayEmployeeTable.setColumnSelectionAllowed(true);
        DisplayEmployeeTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        DisplayEmployeeScroll.setViewportView(DisplayEmployeeTable);
        DisplayEmployeeTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (DisplayEmployeeTable.getColumnModel().getColumnCount() > 0) {
            DisplayEmployeeTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            DisplayEmployeeTable.getColumnModel().getColumn(4).setPreferredWidth(120);
            DisplayEmployeeTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        }

        DisplayReturnButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        DisplayReturnButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplayReturnButton.setText("Add Employee");
        DisplayReturnButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DisplayReturnButton.setPreferredSize(new java.awt.Dimension(90, 40));
        DisplayReturnButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                DisplayReturnButtonMouseReleased(evt);
            }
        });

        DisplayEditButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        DisplayEditButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplayEditButton.setText("Edit");
        DisplayEditButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DisplayEditButton.setPreferredSize(new java.awt.Dimension(90, 40));
        DisplayEditButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                DisplayEditButtonMouseReleased(evt);
            }
        });

        DisplayRemoveButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        DisplayRemoveButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplayRemoveButton.setText("Remove");
        DisplayRemoveButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DisplayRemoveButton.setPreferredSize(new java.awt.Dimension(90, 40));
        DisplayRemoveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                DisplayRemoveButtonMouseReleased(evt);
            }
        });

        AddEmployeeLabel1.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        AddEmployeeLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddEmployeeLabel1.setText("Employee List");
        AddEmployeeLabel1.setPreferredSize(new java.awt.Dimension(510, 30));

        DisplayEmployeeSearchField.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        DisplayEmployeeSearchField.setForeground(new java.awt.Color(153, 153, 153));
        DisplayEmployeeSearchField.setText("Employee Number");
        DisplayEmployeeSearchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                DisplayEmployeeSearchFieldFocusGained(evt);
            }
        });

        DisplaySearchButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        DisplaySearchButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplaySearchButton.setText("Search");
        DisplaySearchButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DisplaySearchButton.setPreferredSize(new java.awt.Dimension(90, 40));
        DisplaySearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                DisplaySearchButtonMouseReleased(evt);
            }
        });

        DisplayUserManual.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        DisplayUserManual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplayUserManual.setText("User Manual");
        DisplayUserManual.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DisplayUserManual.setPreferredSize(new java.awt.Dimension(90, 40));
        DisplayUserManual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                DisplayUserManualMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout DisplayEmployeePanelLayout = new javax.swing.GroupLayout(DisplayEmployeePanel);
        DisplayEmployeePanel.setLayout(DisplayEmployeePanelLayout);
        DisplayEmployeePanelLayout.setHorizontalGroup(
            DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DisplayEmployeePanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(DisplayEmployeeScroll)
                    .addComponent(AddEmployeeLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addGroup(DisplayEmployeePanelLayout.createSequentialGroup()
                        .addGroup(DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DisplayReturnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DisplayUserManual, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DisplayEmployeePanelLayout.createSequentialGroup()
                                .addComponent(DisplayRemoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(DisplayEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DisplayEmployeePanelLayout.createSequentialGroup()
                                .addComponent(DisplayEmployeeSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(25, 25, 25))
        );
        DisplayEmployeePanelLayout.setVerticalGroup(
            DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DisplayEmployeePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(AddEmployeeLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(DisplayEmployeeSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(DisplayUserManual, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(DisplayEmployeeScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(DisplayEmployeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DisplayEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DisplayReturnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DisplayRemoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(DisplayEmployeePanel, "DisplayEmployeeCard");

        EmployeeProfilePanel.setBackground(new java.awt.Color(247, 247, 247));

        EmployeeProfileLabel.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        EmployeeProfileLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EmployeeProfileLabel.setText("Employee Profile");
        EmployeeProfileLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EmployeeProfileLabel.setPreferredSize(new java.awt.Dimension(420, 30));

        EmployeeProfileTable.setBackground(new java.awt.Color(249, 249, 249));
        EmployeeProfileTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        EmployeeProfileTable.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        EmployeeProfileTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Employee Number", null},
                {"First Name", null},
                {"Last Name", null},
                {"Gender", null},
                {"Deduction Rate", null},
                {"Location", null},
                {"Gross Annual Income", ""},
                {"Hourly Wage", null},
                {"Hours Per Week", null},
                {"Weeks Per Year", null}
            },
            new String [] {
                "Employee Info", "Values"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        EmployeeProfileTable.setToolTipText("Double Click cell to edit");
        EmployeeProfileTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        EmployeeProfileTable.setPreferredSize(new java.awt.Dimension(420, 250));
        EmployeeProfileTable.setRequestFocusEnabled(false);
        EmployeeProfileTable.setRowHeight(25);
        EmployeeProfileTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        EmployeeProfileTable.getTableHeader().setResizingAllowed(false);
        EmployeeProfileTable.getTableHeader().setReorderingAllowed(false);

        EmployeeProfileReturnButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        EmployeeProfileReturnButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EmployeeProfileReturnButton.setText("Return");
        EmployeeProfileReturnButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EmployeeProfileReturnButton.setPreferredSize(new java.awt.Dimension(90, 40));
        EmployeeProfileReturnButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                EmployeeProfileReturnButtonMouseReleased(evt);
            }
        });

        EmployeeInfoUpdateButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        EmployeeInfoUpdateButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EmployeeInfoUpdateButton.setText("Update Profile");
        EmployeeInfoUpdateButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EmployeeInfoUpdateButton.setPreferredSize(new java.awt.Dimension(90, 40));
        EmployeeInfoUpdateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                EmployeeInfoUpdateButtonMouseReleased(evt);
            }
        });

        EmployeeRemoveButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        EmployeeRemoveButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EmployeeRemoveButton.setText("Remove Employee");
        EmployeeRemoveButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EmployeeRemoveButton.setPreferredSize(new java.awt.Dimension(90, 40));
        EmployeeRemoveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                EmployeeRemoveButtonMouseReleased(evt);
            }
        });

        EmployeeChangeButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        EmployeeChangeButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EmployeeChangeButton.setText("Change to Full Time");
        EmployeeChangeButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EmployeeChangeButton.setPreferredSize(new java.awt.Dimension(90, 40));
        EmployeeChangeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                EmployeeChangeButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout EmployeeProfilePanelLayout = new javax.swing.GroupLayout(EmployeeProfilePanel);
        EmployeeProfilePanel.setLayout(EmployeeProfilePanelLayout);
        EmployeeProfilePanelLayout.setHorizontalGroup(
            EmployeeProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EmployeeProfilePanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(EmployeeProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EmployeeProfilePanelLayout.createSequentialGroup()
                        .addComponent(EmployeeProfileReturnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(EmployeeInfoUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(EmployeeProfileTable, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(EmployeeProfileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EmployeeProfilePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(EmployeeProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(EmployeeChangeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(EmployeeRemoveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30))
        );
        EmployeeProfilePanelLayout.setVerticalGroup(
            EmployeeProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EmployeeProfilePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(EmployeeProfileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(EmployeeProfileTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(EmployeeChangeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(EmployeeRemoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(EmployeeProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EmployeeInfoUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EmployeeProfileReturnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        EmployeeProfileTable.getAccessibleContext().setAccessibleDescription("");

        getContentPane().add(EmployeeProfilePanel, "EmployeeProfileCard");

        MenuBar.setBackground(new java.awt.Color(244, 244, 244));

        BarFile.setText("File");
        BarFile.add(jSeparator1);

        ItemOpenData.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        ItemOpenData.setText("Open Database");
        ItemOpenData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemOpenDataActionPerformed(evt);
            }
        });
        BarFile.add(ItemOpenData);
        BarFile.add(jSeparator2);

        CheckBoxAutoSave.setText("Auto-Save");
        CheckBoxAutoSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBoxAutoSaveActionPerformed(evt);
            }
        });
        BarFile.add(CheckBoxAutoSave);

        ItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        ItemSave.setText("Save");
        ItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemSaveActionPerformed(evt);
            }
        });
        BarFile.add(ItemSave);

        ItemSaveToNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        ItemSaveToNew.setText("Save To New Database");
        ItemSaveToNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemSaveToNewActionPerformed(evt);
            }
        });
        BarFile.add(ItemSaveToNew);

        MenuBar.add(BarFile);

        BarMenu.setText("Menu");
        BarMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BarMenuMouseClicked(evt);
            }
        });

        ItemAddEmp.setText("Add Employee");
        ItemAddEmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemAddEmpActionPerformed(evt);
            }
        });
        BarMenu.add(ItemAddEmp);

        ItemDispEmp.setText("Display Employees");
        ItemDispEmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemDispEmpActionPerformed(evt);
            }
        });
        BarMenu.add(ItemDispEmp);

        ItemLoadEmp.setText("Load Employees");
        ItemLoadEmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemLoadEmpActionPerformed(evt);
            }
        });
        BarMenu.add(ItemLoadEmp);

        MenuBar.add(BarMenu);

        setJMenuBar(MenuBar);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ProceedButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProceedButtonMouseEntered
        ProceedButton.setBackground(new Color(240, 240, 240));

    }//GEN-LAST:event_ProceedButtonMouseEntered

    private void ProceedButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProceedButtonMouseExited
        ProceedButton.setBackground(new Color(247, 247, 247));

    }//GEN-LAST:event_ProceedButtonMouseExited

    private void ProceedButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProceedButtonMousePressed


    }//GEN-LAST:event_ProceedButtonMousePressed

    private void ProceedButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProceedButtonMouseReleased
//        showMenu();
        showDisplayEmployees();
        popUpMessageTwo("Open An Existing Database?");
        MenuBar.setVisible(true);

    }//GEN-LAST:event_ProceedButtonMouseReleased

    private void AddEmpReturnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddEmpReturnMouseReleased
//        showMenu();
        showDisplayEmployees();

    }//GEN-LAST:event_AddEmpReturnMouseReleased

    private void FTEButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FTEButtonMouseReleased
        if (FTERaised) {
            FTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            PTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            FTERaised = false;
            PTERaised = true;

            this.SWLabel.setText("Salary");
            this.HPWLabel.setVisible(false);
            this.HPWField.setVisible(false);
            this.WPYField.setVisible(false);
            this.WPYLabel.setVisible(false);

            pack();

        }
    }//GEN-LAST:event_FTEButtonMouseReleased

    private void PTEButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PTEButtonMouseReleased
        if (PTERaised) {
            PTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            FTEButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            PTERaised = false;
            FTERaised = true;

            this.SWLabel.setText("Hourly Wage");
            this.HPWLabel.setVisible(true);
            this.HPWField.setVisible(true);
            this.WPYField.setVisible(true);
            this.WPYLabel.setVisible(true);

            pack();

        }

    }//GEN-LAST:event_PTEButtonMouseReleased

    private void AddEmpButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddEmpButtonMouseReleased
        showAddEmployees();
    }//GEN-LAST:event_AddEmpButtonMouseReleased

    private void GMaleRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GMaleRadioActionPerformed
        this.GOtherRadio.setSelected(false);
        this.GFemaleRadio.setSelected(false);
    }//GEN-LAST:event_GMaleRadioActionPerformed

    private void GFemaleRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GFemaleRadioActionPerformed
        // TODO add your handling code here:
        this.GOtherRadio.setSelected(false);
        this.GMaleRadio.setSelected(false);
    }//GEN-LAST:event_GFemaleRadioActionPerformed

    private void GOtherRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GOtherRadioActionPerformed
        // TODO add your handling code here:
        this.GMaleRadio.setSelected(false);
        this.GFemaleRadio.setSelected(false);
        this.GOtherField.requestFocus();
    }//GEN-LAST:event_GOtherRadioActionPerformed

    private void AddEmpClearMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddEmpClearMouseReleased
        // TODO add your handling code here:
        addEmpClear();

    }//GEN-LAST:event_AddEmpClearMouseReleased

    private void AddEmployeeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddEmployeeButtonMouseReleased
        // TODO add your handling code here:
        String G = "Unknown";
        int EN = 0;
        String FN;
        String LN;
        double DR = 0;
        String L;
        double SW = 0;
        int HPW = 0;
        int WPY = 0;

        boolean error = false;

        // Employee Number
        try {
            if (!"".equals(this.ENField.getText())) {
                EN = Integer.parseInt(this.ENField.getText());
            } else {
                command = "Add Employee Error";
                popUpMessageOne("Employee Number Input Error");
                error = true;
            }
        } catch (NumberFormatException e) {
            command = "Add Employee Error";
            popUpMessageOne("Employee Number Input Error");
            error = true;
        }

        // First Name
        FN = this.FNField.getText();

        // Last Name
        LN = this.LNField.getText();

        // Gender
        if (GMaleRadio.isSelected()) {
            G = "Male";
        } else if (GFemaleRadio.isSelected()) {
            G = "Female";
        } else if (GOtherRadio.isSelected()) {
            G = this.GOtherField.getText();
        }

        // Deduction Rate
        try {
            if (!"".equals(this.DRField.getText())) {
                DR = Double.parseDouble(this.DRField.getText());
            }
        } catch (NumberFormatException e) {
            command = "Add Employee Error";
            popUpMessageOne("Deduction Rate Input Error");
            error = true;
        }

        // Location
        L = this.LField.getText();

        // Salary/Wage
        if (PTERaised) {
            try {
                if (!"".equals(this.SWField.getText())) {
                    SW = Double.parseDouble(this.SWField.getText());
                }
            } catch (NumberFormatException e) {
                command = "Add Employee Error";
                popUpMessageOne("Salary Input Error");
                error = true;

            }
            if (!error) {
                theHashTable.addEmployee(new FullTimeEmployee(EN, FN, LN, G, DR, L, SW));
                command = "Add Employee Success";
                popUpMessageOne("Employee Successfully Added");
                addEmpClear();
                if (autoSave) {
                    saveEmployees();
                }
            }
        } else {
            try {
                if (!"".equals(this.SWField.getText())) {
                    SW = Double.parseDouble(this.SWField.getText());
                }
            } catch (NumberFormatException e) {
                command = "Add Employee Error";
                popUpMessageOne("Hourly Wage Input Error");
                error = true;

            }
            try {
                if (!"".equals(this.HPWField.getText())) {
                    HPW = Integer.parseInt(this.HPWField.getText());
                }
            } catch (NumberFormatException e) {
                command = "Add Employee Error";
                popUpMessageOne("Hours Per Week Input Error");
                error = true;

            }
            try {
                if (!"".equals(this.WPYField.getText())) {
                    WPY = Integer.parseInt(this.WPYField.getText());
                }
            } catch (NumberFormatException e) {
                command = "Add Employee Error";
                popUpMessageOne("Weeks Per Year Input Error");
                error = true;

            }
            if (!error) {
                theHashTable.addEmployee(new PartTimeEmployee(EN, FN, LN, G, DR, L, SW, HPW, WPY));
                command = "Add Employee Success";
                popUpMessageOne("Employee Successfully Added");
                addEmpClear();
                if (autoSave) {
                    saveEmployees();
                }
            }
        }

    }//GEN-LAST:event_AddEmployeeButtonMouseReleased

    private void ItemLoadEmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemLoadEmpActionPerformed
        // TODO add your handling code here:
        popUpLoadEmployees();
    }//GEN-LAST:event_ItemLoadEmpActionPerformed

    private void ItemSaveToNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemSaveToNewActionPerformed
        // TODO add your handling code here:
        popUpSaveEmployees();

    }//GEN-LAST:event_ItemSaveToNewActionPerformed

    private void LoadEmpButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoadEmpButtonMouseReleased
        // TODO add your handling code here:
        popUpLoadEmployees();
    }//GEN-LAST:event_LoadEmpButtonMouseReleased

    private void ItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemSaveActionPerformed
        // TODO add your handling code here:
        saveEmployees();
    }//GEN-LAST:event_ItemSaveActionPerformed

    private void CheckBoxAutoSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBoxAutoSaveActionPerformed
        // TODO add your handling code here:
        if (CheckBoxAutoSave.isSelected()) {
            ItemSave.setEnabled(false);

        } else {
            ItemSave.setEnabled(true);
        }
        if (saveFileName == null) {
            popUpSaveEmployees();

        } else {
            saveEmployees();

        }
    }//GEN-LAST:event_CheckBoxAutoSaveActionPerformed

    private void SearchEmpButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SearchEmpButtonMouseReleased
        // TODO add your handling code here:
//        showSearchEmployees();

    }//GEN-LAST:event_SearchEmpButtonMouseReleased

    private void SearchButtonLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SearchButtonLabelMouseReleased
        // TODO add your handling code here:
        int index = parseInt(SearchTextField.getText());
        EmployeeInfo emp = theHashTable.searchEmployee(index);
        if (emp != null) {

            selectedEmployee = emp;

            showEmployeeProfile();
        } else {
            command = "Employee Does Not Exist";
            popUpMessageOne("Employee Does Not Exist");
        }

    }//GEN-LAST:event_SearchButtonLabelMouseReleased

    private void SearchReturnButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SearchReturnButtonMouseReleased
        // TODO add your handling code here:
//        showMenu();
        showEmployeeProfile();
    }//GEN-LAST:event_SearchReturnButtonMouseReleased

    private void EmployeeProfileReturnButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EmployeeProfileReturnButtonMouseReleased
        // TODO add your handling code here:
//        showMenu();
        showDisplayEmployees();
    }//GEN-LAST:event_EmployeeProfileReturnButtonMouseReleased

    private void EmployeeInfoUpdateButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EmployeeInfoUpdateButtonMouseReleased
        // TODO add your handling code here:
        String G;
        int EN = 0;
        String FN;
        String LN;
        double DR = 0;
        String L;
        double SW = 0;
        double HPW = 0;
        double WPY = 0;

        boolean error = false;
        // Gender
        G = EmployeeProfileTable.getModel().getValueAt(3, 1).toString();

        // Employee Number
        try {
            if (!"".equals(EmployeeProfileTable.getModel().getValueAt(0, 1).toString())) {
                EN = Integer.parseInt(EmployeeProfileTable.getModel().getValueAt(0, 1).toString());

            }
        } catch (NumberFormatException e) {
            command = "Edit Employee Error";
            popUpMessageOne("Employee Number Input Error");
            error = true;
        }

        // First Name
        FN = (String) EmployeeProfileTable.getModel().getValueAt(1, 1);

        // Last Name
        LN = (String) EmployeeProfileTable.getModel().getValueAt(2, 1);

        // Deduction Rate
        try {
            if (!"".equals(EmployeeProfileTable.getModel().getValueAt(4, 1).toString())) {
                DR = Double.parseDouble(EmployeeProfileTable.getModel().getValueAt(4, 1).toString());
            }
        } catch (NumberFormatException e) {
            command = "Edit Employee Error";
            popUpMessageOne("Deduction Rate Input Error");
            error = true;

        }

        // Location
        L = (String) EmployeeProfileTable.getModel().getValueAt(5, 1);

        // Salary/Wage
        if ("Yearly Salary".equals(EmployeeProfileTable.getModel().getValueAt(7, 0).toString())) {
            try {
                if (!"".equals(EmployeeProfileTable.getModel().getValueAt(7, 1).toString())) {
                    SW = Double.parseDouble(EmployeeProfileTable.getModel().getValueAt(7, 1).toString());
                }
            } catch (NumberFormatException e) {
                command = "Edit Employee Error";
                popUpMessageOne("Salary Input Error");
                error = true;

            }
            if (!error) {
                theHashTable.removeEmployee(selectedEmployee);
                theHashTable.addEmployee(new FullTimeEmployee(EN, FN, LN, G, DR, L, SW));
                command = "Edit Employee Success";
                popUpMessageOne("Edit Employee Success");
                if (autoSave) {
                    saveEmployees();
                }
            }
        } else {
            try {
                if (!"".equals(EmployeeProfileTable.getModel().getValueAt(7, 1).toString())) {
                    SW = Double.parseDouble(EmployeeProfileTable.getModel().getValueAt(7, 1).toString());
                }
            } catch (NumberFormatException e) {
                command = "Edit Employee Error";
                popUpMessageOne("Hourly Wage Input Error");
                error = true;

            }
            try {
                if (!"".equals(EmployeeProfileTable.getModel().getValueAt(8, 1).toString())) {
                    HPW = Double.parseDouble(EmployeeProfileTable.getModel().getValueAt(8, 1).toString());
                }
            } catch (NumberFormatException e) {
                command = "Edit Employee Error";
                popUpMessageOne("Hours Per Week Input Error");
                error = true;

            }
            try {
                if (!"".equals(EmployeeProfileTable.getModel().getValueAt(9, 1).toString())) {
                    WPY = Double.parseDouble(EmployeeProfileTable.getModel().getValueAt(9   , 1).toString());
                }
            } catch (NumberFormatException e) {
                command = "Edit Employee Error";
                popUpMessageOne("Weeks Per Year Input Error");
                error = true;

            }
            if (!error) {
                theHashTable.removeEmployee(selectedEmployee);
                theHashTable.addEmployee(new PartTimeEmployee(EN, FN, LN, G, DR, L, SW, HPW, WPY));
                command = "Edit Employee Success";
                popUpMessageOne("Edit Employee Success");
                if (autoSave) {
                    saveEmployees();
                }
            }
        }

    }//GEN-LAST:event_EmployeeInfoUpdateButtonMouseReleased

    private void EmployeeRemoveButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EmployeeRemoveButtonMouseReleased
        // TODO add your handling code here: 
        if (selectedEmployee != null) {
            theHashTable.removeEmployee(selectedEmployee);
            if (autoSave) {
                saveEmployees();
            }
        }
        showDisplayEmployees();
    }//GEN-LAST:event_EmployeeRemoveButtonMouseReleased

    private void ItemAddEmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemAddEmpActionPerformed
        // TODO add your handling code here:
        showAddEmployees();
    }//GEN-LAST:event_ItemAddEmpActionPerformed

    private void ItemOpenDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemOpenDataActionPerformed
        // TODO add your handling code here:
        popUpOpenEmployees();
    }//GEN-LAST:event_ItemOpenDataActionPerformed

    private void EmployeeChangeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EmployeeChangeButtonMouseReleased
        // TODO add your handling code here:
        if (selectedEmployee instanceof FullTimeEmployee) {
            PartTimeEmployee s = (new PartTimeEmployee(selectedEmployee.getEmployeeNumber(),
                    selectedEmployee.getFirstName(),
                    selectedEmployee.getLastName(),
                    selectedEmployee.getGender(),
                    selectedEmployee.getDeductionRate(),
                    selectedEmployee.getLocation(), 0, 0, 0));
            theHashTable.removeEmployee(selectedEmployee);
            theHashTable.addEmployee(s);
            selectedEmployee = s;
            EmployeeProfileTable.setPreferredSize(new Dimension(420, 250));

        } else if (selectedEmployee instanceof PartTimeEmployee) {
            FullTimeEmployee s = new FullTimeEmployee(selectedEmployee.getEmployeeNumber(),
                    selectedEmployee.getFirstName(),
                    selectedEmployee.getLastName(),
                    selectedEmployee.getGender(),
                    selectedEmployee.getDeductionRate(),
                    selectedEmployee.getLocation(), 0);
            theHashTable.removeEmployee(selectedEmployee);
            theHashTable.addEmployee(s);
            selectedEmployee = s;
            EmployeeProfileTable.setPreferredSize(new Dimension(420, 200));
        }

        command = "Edit Employee Success";
        popUpMessageOne("Successfully Edited!");

        showEmployeeProfile();
        if (autoSave) {
            saveEmployees();
        }

    }//GEN-LAST:event_EmployeeChangeButtonMouseReleased

    private void DisplayEmpButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DisplayEmpButtonMouseReleased
        showDisplayEmployees();
    }//GEN-LAST:event_DisplayEmpButtonMouseReleased

    private void DisplayReturnButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DisplayReturnButtonMouseReleased
        // TODO add your handling code here:
//        showMenu();
        showAddEmployees();
    }//GEN-LAST:event_DisplayReturnButtonMouseReleased

    private void DisplayEditButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DisplayEditButtonMouseReleased
        // TODO add your handling code here:
        try {
            selectedEmployee = theHashTable.searchEmployee(Integer.parseInt(DisplayEmployeeTable.getModel().getValueAt(DisplayEmployeeTable.getSelectedRow(), 0).toString()));
            this.showEmployeeProfile();
        } catch (ArrayIndexOutOfBoundsException e) {
            command = "No Selection";
            popUpMessageOne("No Employee Is Selected");
        }

    }//GEN-LAST:event_DisplayEditButtonMouseReleased

    private void DisplayRemoveButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DisplayRemoveButtonMouseReleased
        // TODO add your handling code here:
        try {
            theHashTable.removeEmployee((int) DisplayEmployeeTable.getModel().getValueAt(DisplayEmployeeTable.getSelectedRow(), 0));
        } catch (ArrayIndexOutOfBoundsException e) {
            command = "No Selection";
            popUpMessageOne("No Employee Is Selected");
        }
        showDisplayEmployees();
    }//GEN-LAST:event_DisplayRemoveButtonMouseReleased

    private void BarMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BarMenuMouseClicked
        // TODO add your handling code here:
//        showMenu();
//        showDisplayEmployees();
    }//GEN-LAST:event_BarMenuMouseClicked

    private void DisplaySearchButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DisplaySearchButtonMouseReleased
        // TODO add your handling code here:

        try {
            int index = parseInt(DisplayEmployeeSearchField.getText());
            EmployeeInfo emp = theHashTable.searchEmployee(index);
            if (emp != null) {

                selectedEmployee = emp;

                showEmployeeProfile();
            } else {
                command = "Employee Does Not Exist";
                popUpMessageOne("Employee Does Not Exist");
            }
        } catch (NumberFormatException e) {
            command = "Input Error";
            popUpMessageOne("Search Employee Input Error");
        }
    }//GEN-LAST:event_DisplaySearchButtonMouseReleased

    private void DisplayEmployeeSearchFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_DisplayEmployeeSearchFieldFocusGained
        if ("Employee Number".equals(DisplayEmployeeSearchField.getText())) {
            DisplayEmployeeSearchField.setText("");
        }
        DisplayEmployeeSearchField.setForeground(Color.black);
    }//GEN-LAST:event_DisplayEmployeeSearchFieldFocusGained

    private void ItemDispEmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemDispEmpActionPerformed
        // TODO add your handling code here:
        showDisplayEmployees();
    }//GEN-LAST:event_ItemDispEmpActionPerformed

    private void DisplayUserManualMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DisplayUserManualMouseReleased
        try {
            // TODO add your handling code here:
            Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler " + "src\\UserGuide.pdf");
        } catch (IOException ex) {
            System.out.println("File Error");
        }
    }//GEN-LAST:event_DisplayUserManualMouseReleased

    private void addEmpClear() {
        this.ENField.setText("");
        this.FNField.setText("");
        this.LNField.setText("");
        this.GOtherField.setText("");
        this.DRField.setText("");
        this.LField.setText("");
        this.HPWField.setText("");
        this.WPYField.setText("");
        this.SWField.setText("");
        this.GMaleRadio.setSelected(false);
        this.GFemaleRadio.setSelected(false);
        this.GOtherRadio.setSelected(false);
    }

    private void popUpMessageTwo(String s) {
        popUpPanel.setMessage(s);
        popUpPanel.setNo("No");
        popUpPanel.setYes("Yes");
        popUpPanel.numButtons(2);
        popUpFrame.pack();
        popUpFrame.setLocationRelativeTo(null);

        this.setEnabled(false);
        this.setFocusableWindowState(false);
        SLONPanel.setEnabled(false);
        SLONFrame.setFocusableWindowState(false);

        popUpFrame.setVisible(true);

    }

    private void popUpMessageOne(String s) {
        popUpPanel.setMessage(s);
        popUpPanel.setYes("I understand");
        popUpPanel.numButtons(1);
        popUpFrame.pack();
        popUpFrame.setLocationRelativeTo(null);

        this.setEnabled(false);
        this.setFocusableWindowState(false);
        SLONFrame.setEnabled(false);
        SLONFrame.setFocusableWindowState(false);

        popUpFrame.setVisible(true);
    }

    private void popUpSaveEmployees() {
        SLONFrame.setEnabled(true);
        SLONFrame.setFocusableWindowState(true);
        SLONFrame.setTitle("Save Employees");
        SLONPanel.setSave();
        SLONFrame.pack();
        SLONFrame.setLocationRelativeTo(null);

        setEnabled(false);
        setFocusableWindowState(false);

        SLONFrame.setVisible(true);

    }

    private void popUpLoadEmployees() {
        SLONFrame.setEnabled(true);
        SLONFrame.setFocusableWindowState(true);
        SLONFrame.setTitle("Load Employees");
        SLONPanel.setLoad();
        SLONFrame.pack();

        setEnabled(false);
        setFocusableWindowState(false);

        SLONFrame.setLocationRelativeTo(null);
        SLONFrame.setVisible(true);
    }

    private void popUpOpenEmployees() {
        SLONFrame.setEnabled(true);
        SLONFrame.setFocusableWindowState(true);
        SLONFrame.setTitle("Open Employees");
        SLONPanel.setOpen();
        SLONFrame.pack();

        setEnabled(false);
        setFocusableWindowState(false);

        SLONFrame.setLocationRelativeTo(null);
        SLONFrame.setVisible(true);
    }

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
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AddEmpButton;
    private javax.swing.JLabel AddEmpClear;
    private javax.swing.JLabel AddEmpReturn;
    private javax.swing.JLabel AddEmployeeButton;
    private javax.swing.JLabel AddEmployeeLabel;
    private javax.swing.JLabel AddEmployeeLabel1;
    private javax.swing.JPanel AddEmployeePanel;
    private javax.swing.JMenu BarFile;
    private javax.swing.JMenu BarMenu;
    private javax.swing.JCheckBoxMenuItem CheckBoxAutoSave;
    private javax.swing.JTextField DRField;
    private javax.swing.JLabel DRLabel;
    private javax.swing.JLabel DisplayEditButton;
    private javax.swing.JLabel DisplayEmpButton;
    private javax.swing.JPanel DisplayEmployeePanel;
    private javax.swing.JScrollPane DisplayEmployeeScroll;
    private javax.swing.JTextField DisplayEmployeeSearchField;
    private javax.swing.JTable DisplayEmployeeTable;
    private javax.swing.JLabel DisplayRemoveButton;
    private javax.swing.JLabel DisplayReturnButton;
    private javax.swing.JLabel DisplaySearchButton;
    private javax.swing.JLabel DisplayUserManual;
    private javax.swing.JTextField ENField;
    private javax.swing.JLabel EmployeeChangeButton;
    private javax.swing.JLabel EmployeeInfoUpdateButton;
    private javax.swing.JLabel EmployeeNumLabel;
    private javax.swing.JLabel EmployeeProfileLabel;
    private javax.swing.JPanel EmployeeProfilePanel;
    private javax.swing.JLabel EmployeeProfileReturnButton;
    private javax.swing.JTable EmployeeProfileTable;
    private javax.swing.JLabel EmployeeRemoveButton;
    private javax.swing.JTextField FNField;
    private javax.swing.JLabel FNLabel;
    private javax.swing.JLabel FTEButton;
    private javax.swing.JRadioButton GFemaleRadio;
    private javax.swing.JLabel GLabel;
    private javax.swing.JRadioButton GMaleRadio;
    private javax.swing.JTextField GOtherField;
    private javax.swing.JRadioButton GOtherRadio;
    private javax.swing.JTextField HPWField;
    private javax.swing.JLabel HPWLabel;
    private javax.swing.JMenuItem ItemAddEmp;
    private javax.swing.JMenuItem ItemDispEmp;
    private javax.swing.JMenuItem ItemLoadEmp;
    private javax.swing.JMenuItem ItemOpenData;
    private javax.swing.JMenuItem ItemSave;
    private javax.swing.JMenuItem ItemSaveToNew;
    private javax.swing.JTextField LField;
    private javax.swing.JLabel LLabel;
    private javax.swing.JTextField LNField;
    private javax.swing.JLabel LNLabel;
    private javax.swing.JLabel LoadEmpButton;
    private javax.swing.JPanel MainMenu;
    private javax.swing.JLabel MainMenuLabel;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JLabel PTEButton;
    private javax.swing.JLabel ProceedButton;
    private javax.swing.JTextField SWField;
    private javax.swing.JLabel SWLabel;
    private javax.swing.JLabel SearchBasedOnLabel;
    private javax.swing.JLabel SearchButtonLabel;
    private javax.swing.JLabel SearchEmpButton;
    private javax.swing.JLabel SearchEmployeeLabel;
    private javax.swing.JPanel SearchEmployeePanel;
    private javax.swing.JLabel SearchReturnButton;
    private javax.swing.JTextField SearchTextField;
    private javax.swing.JTextArea TitleTextArea;
    private javax.swing.JTextField WPYField;
    private javax.swing.JLabel WPYLabel;
    private javax.swing.JPanel WelcomePanel;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    // End of variables declaration//GEN-END:variables

}
