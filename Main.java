import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Main extends JFrame {

    // =============== COLORS ===============

    static final Color NAVY = new Color(15, 23, 42);
    static final Color SIDEBAR = new Color(20, 30, 55);
    static final Color BLUE = new Color(59, 130, 246);
    static final Color BG = new Color(241, 245, 249);
    static final Color WHITE = Color.WHITE;
    static final Color TEXT = new Color(30, 41, 59);
    static final Color GREEN = new Color(22, 163, 74);
    static final Color RED = new Color(220, 55, 65);
    static final Color ORANGE = new Color(217, 119, 6);

    // =============== EMPLOYEE CLASS ===============

    static class Employee implements Serializable {
        private static final long serialVersionUID = 1L;

        String id, name, department;
        double basic;
        boolean present = true;
        boolean paid = false;

        Employee(String id, String name, String department, double basic) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.basic = basic;
        }

        double hra() { return basic * 0.20; }
        double da()  { return basic * 0.10; }
        double pf()  { return basic * 0.12; }
        double gross() { return basic + hra() + da(); }
        double net() { return gross() - pf(); }
    }

    // =============== VARIABLES ===============

    ArrayList<Employee> employees = new ArrayList<Employee>();

    CardLayout cardLayout = new CardLayout();
    JPanel content = new JPanel(cardLayout);

    DefaultTableModel employeeModel;
    DefaultTableModel attendanceModel;
    DefaultTableModel payrollModel;

    JTable employeeTable;
    JTable attendanceTable;
    JTable payrollTable;

    JLabel totalEmployees;
    JLabel totalPayroll;
    JLabel paidAmount;
    JLabel pendingAmount;

    final String DATA_FILE = "employees.dat";

    // =============== CONSTRUCTOR ===============

    public Main() {
        loadData();

        setTitle("Employee Payroll Management System");
        setSize(1250, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createInterface();

        setVisible(true);
        showPage("Dashboard");
    }

    // =============== LOGIN SCREEN ===============

    static class LoginPage extends JFrame {

        JTextField username;
        JPasswordField password;

        LoginPage() {
            setTitle("Payroll Pro - Admin Login");
            setSize(460, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);

            JPanel background = new JPanel(new GridBagLayout());
            background.setBackground(NAVY);

            JPanel box = new JPanel();
            box.setPreferredSize(new Dimension(350, 390));
            box.setBackground(WHITE);
            box.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

            JLabel logo = new JLabel("PAYROLL PRO");
            logo.setFont(new Font("Arial", Font.BOLD, 27));
            logo.setForeground(BLUE);
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtitle = new JLabel("EMPLOYEE MANAGEMENT SYSTEM");
            subtitle.setFont(new Font("Arial", Font.PLAIN, 11));
            subtitle.setForeground(Color.GRAY);
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel loginTitle = new JLabel("Admin Login");
            loginTitle.setFont(new Font("Arial", Font.BOLD, 22));
            loginTitle.setForeground(NAVY);
            loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel userLabel = new JLabel("Username");
            username = new JTextField();
            username.setMaximumSize(new Dimension(300, 40));

            JLabel passLabel = new JLabel("Password");
            password = new JPasswordField();
            password.setMaximumSize(new Dimension(300, 40));

            JButton login = new JButton("LOGIN");
            login.setMaximumSize(new Dimension(300, 45));
            login.setBackground(BLUE);
            login.setForeground(WHITE);
            login.setFont(new Font("Arial", Font.BOLD, 14));
            login.setFocusPainted(false);
            login.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel hint = new JLabel("Demo: admin / admin123");
            hint.setForeground(Color.GRAY);
            hint.setFont(new Font("Arial", Font.PLAIN, 12));
            hint.setAlignmentX(Component.CENTER_ALIGNMENT);

            box.add(logo);
            box.add(Box.createVerticalStrut(5));
            box.add(subtitle);
            box.add(Box.createVerticalStrut(30));
            box.add(loginTitle);
            box.add(Box.createVerticalStrut(20));
            box.add(userLabel);
            box.add(Box.createVerticalStrut(5));
            box.add(username);
            box.add(Box.createVerticalStrut(15));
            box.add(passLabel);
            box.add(Box.createVerticalStrut(5));
            box.add(password);
            box.add(Box.createVerticalStrut(25));
            box.add(login);
            box.add(Box.createVerticalStrut(15));
            box.add(hint);

            background.add(box);
            add(background);

            login.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String u = username.getText().trim();
                    String p = new String(password.getPassword());

                    if (u.equals("admin") && p.equals("admin123")) {
                        dispose();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                new Main();
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(
                            LoginPage.this,
                            "Incorrect username or password."
                        );
                    }
                }
            });

            getRootPane().setDefaultButton(login);
            setVisible(true);
        }
    }

    // =============== MAIN INTERFACE ===============

    void createInterface() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(225, 750));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("  PAYROLL PRO");
        logo.setForeground(WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 23));
        logo.setBorder(BorderFactory.createEmptyBorder(30, 10, 35, 5));
        sidebar.add(logo);

        addNavigationButton(sidebar, "Dashboard", "Dashboard");
        addNavigationButton(sidebar, "Employees", "Employees");
        addNavigationButton(sidebar, "Attendance", "Attendance");
        addNavigationButton(sidebar, "Payroll", "Payroll");
        addNavigationButton(sidebar, "Reports", "Reports");
        addNavigationButton(sidebar, "Settings", "Settings");

        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton("  Logout");
        styleNavigationButton(logout);
        logout.setBackground(new Color(150, 45, 60));

        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveData();
                dispose();
                new LoginPage();
            }
        });

        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(25));

        // Create all pages only once.
        content.setBackground(BG);

        content.add(createDashboardPage(), "Dashboard");
        content.add(createEmployeePage(), "Employees");
        content.add(createAttendancePage(), "Attendance");
        content.add(createPayrollPage(), "Payroll");
        content.add(createReportsPage(), "Reports");
        content.add(createSettingsPage(), "Settings");

        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);
    }

    void addNavigationButton(JPanel sidebar, String text, String page) {
        JButton button = new JButton("  " + text);
        styleNavigationButton(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPage(page);
            }
        });

        sidebar.add(button);
    }

    void styleNavigationButton(JButton button) {
        button.setMaximumSize(new Dimension(225, 50));
        button.setPreferredSize(new Dimension(225, 50));
        button.setMinimumSize(new Dimension(225, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(WHITE);
        button.setBackground(SIDEBAR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 5));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusable(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    void showPage(String page) {
        cardLayout.show(content, page);

        if (page.equals("Dashboard")) {
            updateDashboard();
        } else if (page.equals("Employees")) {
            refreshEmployeeTable();
        } else if (page.equals("Attendance")) {
            refreshAttendanceTable();
        } else if (page.equals("Payroll")) {
            refreshPayrollTable();
        }

        content.revalidate();
        content.repaint();
    }

    // =============== PAGE HEADER ===============

    JPanel createPage(String title, String subtitle) {
        JPanel page = new JPanel(new BorderLayout(15, 20));
        page.setBackground(BG);
        page.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel heading = new JPanel();
        heading.setBackground(BG);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(NAVY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(7));
        heading.add(subtitleLabel);

        page.add(heading, BorderLayout.NORTH);
        return page;
    }

    // =============== DASHBOARD PAGE ===============

    JPanel createDashboardPage() {
        JPanel page = createPage(
            "Dashboard",
            "Welcome to your Employee Payroll Management System"
        );

        JPanel body = new JPanel(new BorderLayout(15, 25));
        body.setBackground(BG);

        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 15));
        cards.setBackground(BG);

        totalEmployees = new JLabel("0");
        totalPayroll = new JLabel("Rs. 0");
        paidAmount = new JLabel("Rs. 0");
        pendingAmount = new JLabel("Rs. 0");

        cards.add(createSummaryCard("TOTAL EMPLOYEES",
            totalEmployees, BLUE));
        cards.add(createSummaryCard("TOTAL PAYROLL",
            totalPayroll, new Color(14, 150, 130)));
        cards.add(createSummaryCard("PAID SALARIES",
            paidAmount, GREEN));
        cards.add(createSummaryCard("PENDING SALARIES",
            pendingAmount, ORANGE));

        JPanel overview = new JPanel();
        overview.setBackground(WHITE);
        overview.setLayout(new BoxLayout(overview, BoxLayout.Y_AXIS));
        overview.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel overviewTitle = new JLabel("Payroll Overview");
        overviewTitle.setFont(new Font("Arial", Font.BOLD, 21));
        overviewTitle.setForeground(NAVY);

        JLabel message = new JLabel(
            "<html><br>Manage all your employee information from one place."
            + "<br><br>• Employee records and salary details"
            + "<br>• Attendance management"
            + "<br>• Monthly payroll calculations"
            + "<br>• Salary payment status and reports"
            + "<br><br>Choose a section from the left sidebar to get started.</html>"
        );
        message.setFont(new Font("Arial", Font.PLAIN, 15));
        message.setForeground(TEXT);

        overview.add(overviewTitle);
        overview.add(message);

        body.add(cards, BorderLayout.NORTH);
        body.add(overview, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    JPanel createSummaryCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
            BorderFactory.createEmptyBorder(20, 15, 20, 10)
        ));

        JLabel heading = new JLabel(title);
        heading.setForeground(Color.GRAY);
        heading.setFont(new Font("Arial", Font.BOLD, 12));

        value.setForeground(NAVY);
        value.setFont(new Font("Arial", Font.BOLD, 22));

        card.add(heading);
        card.add(Box.createVerticalStrut(20));
        card.add(value);

        return card;
    }

    void updateDashboard() {
        double total = 0;
        double paid = 0;
        double pending = 0;

        for (Employee emp : employees) {
            total += emp.net();

            if (emp.paid) paid += emp.net();
            else pending += emp.net();
        }

        totalEmployees.setText(String.valueOf(employees.size()));
        totalPayroll.setText(String.format("Rs. %.2f", total));
        paidAmount.setText(String.format("Rs. %.2f", paid));
        pendingAmount.setText(String.format("Rs. %.2f", pending));
    }

    // =============== EMPLOYEE PAGE ===============

    JPanel createEmployeePage() {
        JPanel page = createPage(
            "Employee Management",
            "Add, update, delete and search employee records"
        );

        JPanel body = new JPanel(new BorderLayout(10, 15));
        body.setBackground(BG);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(BG);

        JButton add = createButton("Add Employee", BLUE);
        JButton edit = createButton("Edit Employee", GREEN);
        JButton delete = createButton("Delete Employee", RED);

        JTextField search = new JTextField(12);
        JButton searchButton = createButton("Search", NAVY);
        JButton all = createButton("Show All", Color.GRAY);

        toolbar.add(add);
        toolbar.add(edit);
        toolbar.add(delete);
        toolbar.add(search);
        toolbar.add(searchButton);
        toolbar.add(all);

        String[] columns = {
            "ID", "Employee Name", "Department",
            "Basic Salary", "Attendance", "Net Salary"
        };

        employeeModel = createModel(columns);
        employeeTable = new JTable(employeeModel);
        styleTable(employeeTable);

        body.add(toolbar, BorderLayout.NORTH);
        body.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);

        add.addActionListener(e -> employeeForm(null));

        edit.addActionListener(e -> {
            Employee emp = getSelectedEmployee(employeeTable);
            if (emp != null) employeeForm(emp);
        });

        delete.addActionListener(e -> {
            Employee emp = getSelectedEmployee(employeeTable);

            if (emp != null) {
                int choice = JOptionPane.showConfirmDialog(
                    this, "Delete " + emp.name + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    employees.remove(emp);
                    saveData();
                    refreshAllTables();
                }
            }
        });

        searchButton.addActionListener(e ->
            searchEmployees(search.getText())
        );

        all.addActionListener(e -> {
            search.setText("");
            refreshEmployeeTable();
        });

        return page;
    }

    void employeeForm(Employee emp) {
        JTextField id = new JTextField(emp == null ? "" : emp.id);
        JTextField name = new JTextField(emp == null ? "" : emp.name);
        JTextField department = new JTextField(
            emp == null ? "" : emp.department
        );
        JTextField salary = new JTextField(
            emp == null ? "" : String.valueOf(emp.basic)
        );

        if (emp != null) id.setEditable(false);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));

        form.add(new JLabel("Employee ID:"));
        form.add(id);
        form.add(new JLabel("Employee Name:"));
        form.add(name);
        form.add(new JLabel("Department:"));
        form.add(department);
        form.add(new JLabel("Basic Salary:"));
        form.add(salary);

        int result = JOptionPane.showConfirmDialog(
            this, form,
            emp == null ? "Add Employee" : "Edit Employee",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String employeeId = id.getText().trim();
            String employeeName = name.getText().trim();
            String dept = department.getText().trim();
            double basic = Double.parseDouble(salary.getText().trim());

            if (employeeId.isEmpty() || employeeName.isEmpty()
                    || dept.isEmpty() || basic <= 0) {
                throw new Exception();
            }

            if (emp == null) {
                if (findEmployee(employeeId) != null) {
                    JOptionPane.showMessageDialog(
                        this, "This Employee ID already exists."
                    );
                    return;
                }

                employees.add(new Employee(
                    employeeId, employeeName, dept, basic
                ));
            } else {
                emp.name = employeeName;
                emp.department = dept;
                emp.basic = basic;
            }

            saveData();
            refreshAllTables();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Please enter valid employee details."
            );
        }
    }

    // =============== ATTENDANCE PAGE ===============

    JPanel createAttendancePage() {
        JPanel page = createPage(
            "Attendance Management",
            "Mark employees as present or absent"
        );

        JPanel body = new JPanel(new BorderLayout(10, 15));
        body.setBackground(BG);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(BG);

        JButton present = createButton("Mark Present", GREEN);
        JButton absent = createButton("Mark Absent", RED);

        toolbar.add(present);
        toolbar.add(absent);

        String[] columns = {
            "Employee ID", "Name", "Department", "Attendance"
        };

        attendanceModel = createModel(columns);
        attendanceTable = new JTable(attendanceModel);
        styleTable(attendanceTable);

        body.add(toolbar, BorderLayout.NORTH);
        body.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);

        present.addActionListener(e -> markAttendance(true));
        absent.addActionListener(e -> markAttendance(false));

        return page;
    }

    void markAttendance(boolean status) {
        Employee emp = getSelectedEmployee(attendanceTable);

        if (emp == null) return;

        emp.present = status;
        saveData();
        refreshAttendanceTable();

        JOptionPane.showMessageDialog(
            this, "Attendance updated successfully."
        );
    }

    // =============== PAYROLL PAGE ===============

    JPanel createPayrollPage() {
        JPanel page = createPage(
            "Payroll Management",
            "Calculate salaries and manage payment status"
        );

        JPanel body = new JPanel(new BorderLayout(10, 15));
        body.setBackground(BG);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(BG);

        JButton paid = createButton("Mark as Paid", GREEN);
        JButton pending = createButton("Mark as Pending", ORANGE);
        JButton slip = createButton("Salary Slip PDF", BLUE);

        toolbar.add(paid);
        toolbar.add(pending);
        toolbar.add(slip);

        String[] columns = {
            "ID", "Name", "Basic", "HRA", "DA", "PF",
            "Gross", "Net Salary", "Payment Status"
        };

        payrollModel = createModel(columns);
        payrollTable = new JTable(payrollModel);
        styleTable(payrollTable);

        body.add(toolbar, BorderLayout.NORTH);
        body.add(new JScrollPane(payrollTable), BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);

        paid.addActionListener(e -> updatePayment(true));
        pending.addActionListener(e -> updatePayment(false));

        slip.addActionListener(e -> {
            Employee emp = getSelectedEmployee(payrollTable);
            if (emp != null) generateSalarySlip(emp);
        });

        return page;
    }

    void updatePayment(boolean status) {
        Employee emp = getSelectedEmployee(payrollTable);

        if (emp == null) return;

        emp.paid = status;
        saveData();
        refreshAllTables();

        JOptionPane.showMessageDialog(
            this, "Payment status updated successfully."
        );
    }

    // =============== REPORTS PAGE ===============

    JPanel createReportsPage() {
        JPanel page = createPage(
            "Reports",
            "Generate attendance and payroll reports"
        );

        JPanel body = new JPanel();
        body.setBackground(WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JButton attendance = createButton(
            "Generate Attendance Report", BLUE
        );

        JButton payroll = createButton(
            "Generate Payroll Report", GREEN
        );

        attendance.setAlignmentX(Component.LEFT_ALIGNMENT);
        payroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(attendance);
        body.add(Box.createVerticalStrut(20));
        body.add(payroll);

        page.add(body, BorderLayout.CENTER);

        attendance.addActionListener(e -> generateAttendanceReport());
        payroll.addActionListener(e -> generatePayrollReport());

        return page;
    }

    // =============== SETTINGS PAGE ===============

    JPanel createSettingsPage() {
        JPanel page = createPage(
            "Settings",
            "Payroll configuration and application information"
        );

        JPanel body = new JPanel();
        body.setBackground(WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel heading = new JLabel("Salary Configuration");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(NAVY);

        body.add(heading);
        body.add(Box.createVerticalStrut(25));
        body.add(new JLabel("HRA: 20% of Basic Salary"));
        body.add(Box.createVerticalStrut(15));
        body.add(new JLabel("DA: 10% of Basic Salary"));
        body.add(Box.createVerticalStrut(15));
        body.add(new JLabel("PF: 12% of Basic Salary"));
        body.add(Box.createVerticalStrut(30));
        body.add(new JLabel("Data file: employees.dat"));
        body.add(Box.createVerticalStrut(15));
        body.add(new JLabel("Admin username: admin"));
        body.add(Box.createVerticalStrut(10));
        body.add(new JLabel("Admin password: admin123"));

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // =============== TABLE HELPERS ===============

    DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(NAVY);
        table.setGridColor(new Color(226, 232, 240));

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(WHITE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    Employee findEmployee(String id) {
        for (Employee emp : employees) {
            if (emp.id.equalsIgnoreCase(id)) return emp;
        }
        return null;
    }

    Employee getSelectedEmployee(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Please select an employee first."
            );
            return null;
        }

        String id = table.getValueAt(row, 0).toString();
        return findEmployee(id);
    }

    // =============== REFRESH TABLES ===============

    void refreshEmployeeTable() {
        employeeModel.setRowCount(0);

        for (Employee emp : employees) {
            employeeModel.addRow(new Object[] {
                emp.id, emp.name, emp.department,
                String.format("%.2f", emp.basic),
                emp.present ? "Present" : "Absent",
                String.format("%.2f", emp.net())
            });
        }
    }

    void refreshAttendanceTable() {
        attendanceModel.setRowCount(0);

        for (Employee emp : employees) {
            attendanceModel.addRow(new Object[] {
                emp.id, emp.name, emp.department,
                emp.present ? "Present" : "Absent"
            });
        }
    }

    void refreshPayrollTable() {
        payrollModel.setRowCount(0);

        for (Employee emp : employees) {
            payrollModel.addRow(new Object[] {
                emp.id,
                emp.name,
                String.format("%.2f", emp.basic),
                String.format("%.2f", emp.hra()),
                String.format("%.2f", emp.da()),
                String.format("%.2f", emp.pf()),
                String.format("%.2f", emp.gross()),
                String.format("%.2f", emp.net()),
                emp.paid ? "Paid" : "Pending"
            });
        }
    }

    void refreshAllTables() {
        refreshEmployeeTable();
        refreshAttendanceTable();
        refreshPayrollTable();
        updateDashboard();
    }

    void searchEmployees(String keyword) {
        employeeModel.setRowCount(0);
        String key = keyword.trim().toLowerCase();

        for (Employee emp : employees) {
            if (emp.id.toLowerCase().contains(key)
                    || emp.name.toLowerCase().contains(key)
                    || emp.department.toLowerCase().contains(key)) {

                employeeModel.addRow(new Object[] {
                    emp.id, emp.name, emp.department,
                    String.format("%.2f", emp.basic),
                    emp.present ? "Present" : "Absent",
                    String.format("%.2f", emp.net())
                });
            }
        }
    }

    // =============== SALARY SLIP PDF ===============

    void generateSalarySlip(Employee emp) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(
            new File("SalarySlip_" + emp.id + ".pdf")
        );

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String text =
            "EMPLOYEE PAYROLL MANAGEMENT SYSTEM\n" +
            "SALARY SLIP\n\n" +
            "Employee ID: " + emp.id + "\n" +
            "Employee Name: " + emp.name + "\n" +
            "Department: " + emp.department + "\n\n" +
            "Basic Salary: Rs. " + emp.basic + "\n" +
            "HRA: Rs. " + emp.hra() + "\n" +
            "DA: Rs. " + emp.da() + "\n" +
            "PF: Rs. " + emp.pf() + "\n" +
            "Gross Salary: Rs. " + emp.gross() + "\n" +
            "Net Salary: Rs. " + emp.net() + "\n" +
            "Payment Status: " + (emp.paid ? "Paid" : "Pending");

        try {
            writePDF(chooser.getSelectedFile(), text);
            JOptionPane.showMessageDialog(
                this, "Salary slip PDF created successfully!"
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "PDF creation failed: " + ex.getMessage()
            );
        }
    }

    void writePDF(File file, String text) throws Exception {
        String[] lines = text.split("\n");
        StringBuilder stream = new StringBuilder();

        stream.append("BT\n/F1 12 Tf\n50 780 Td\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].replace("\\", "\\\\")
                .replace("(", "\\(").replace(")", "\\)");

            if (i > 0) stream.append("0 -25 Td\n");

            stream.append("(").append(line).append(") Tj\n");
        }

        stream.append("ET");

        byte[] data = stream.toString().getBytes("ISO-8859-1");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int[] offsets = new int[6];

        pdfWrite(out, "%PDF-1.4\n");

        offsets[1] = out.size();
        pdfWrite(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets[2] = out.size();
        pdfWrite(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        offsets[3] = out.size();
        pdfWrite(out,
            "3 0 obj\n<< /Type /Page /Parent 2 0 R " +
            "/MediaBox [0 0 595 842] " +
            "/Resources << /Font << /F1 4 0 R >> >> " +
            "/Contents 5 0 R >>\nendobj\n"
        );

        offsets[4] = out.size();
        pdfWrite(out,
            "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n"
        );

        offsets[5] = out.size();
        pdfWrite(out, "5 0 obj\n<< /Length " + data.length + " >>\nstream\n");
        out.write(data);
        pdfWrite(out, "\nendstream\nendobj\n");

        int xref = out.size();

        pdfWrite(out, "xref\n0 6\n0000000000 65535 f \n");

        for (int i = 1; i <= 5; i++) {
            pdfWrite(out, String.format("%010d 00000 n \n", offsets[i]));
        }

        pdfWrite(out,
            "trailer\n<< /Size 6 /Root 1 0 R >>\n" +
            "startxref\n" + xref + "\n%%EOF"
        );

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(out.toByteArray());
        fos.close();
    }

    void pdfWrite(OutputStream out, String text) throws IOException {
        out.write(text.getBytes("ISO-8859-1"));
    }

    // =============== REPORT FILES ===============

    void generateAttendanceReport() {
        saveReport("AttendanceReport.txt", false);
    }

    void generatePayrollReport() {
        saveReport("PayrollReport.txt", true);
    }

    void saveReport(String filename, boolean payroll) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(filename));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            PrintWriter writer = new PrintWriter(chooser.getSelectedFile());

            if (payroll) {
                writer.println("EMPLOYEE PAYROLL REPORT");
            } else {
                writer.println("EMPLOYEE ATTENDANCE REPORT");
            }

            writer.println("----------------------------------");

            for (Employee emp : employees) {
                writer.println("Employee ID: " + emp.id);
                writer.println("Name: " + emp.name);
                writer.println("Department: " + emp.department);

                if (payroll) {
                    writer.println("Basic Salary: Rs. " + emp.basic);
                    writer.println("HRA: Rs. " + emp.hra());
                    writer.println("DA: Rs. " + emp.da());
                    writer.println("PF: Rs. " + emp.pf());
                    writer.println("Net Salary: Rs. " + emp.net());
                    writer.println("Payment: " + (emp.paid ? "Paid" : "Pending"));
                } else {
                    writer.println("Attendance: " +
                        (emp.present ? "Present" : "Absent"));
                }

                writer.println("----------------------------------");
            }

            writer.close();

            JOptionPane.showMessageDialog(
                this, "Report saved successfully!"
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Unable to create report."
            );
        }
    }

    // =============== DATA STORAGE ===============

    void saveData() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE)
            );

            out.writeObject(employees);
            out.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Unable to save employee data."
            );
        }
    }

    @SuppressWarnings("unchecked")
    void loadData() {
        File file = new File(DATA_FILE);

        if (file.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(file)
                );

                employees = (ArrayList<Employee>) in.readObject();
                in.close();
                return;

            } catch (Exception ex) {
                employees = new ArrayList<Employee>();
            }
        }

        addSampleEmployees();
        saveData();
    }

    void addSampleEmployees() {
        employees.add(new Employee("EMP001", "Rahul Sharma", "IT", 30000));
        employees.add(new Employee("EMP002", "Priya Patel", "HR", 28000));
        employees.add(new Employee("EMP003", "Amit Verma", "Finance", 35000));
        employees.add(new Employee("EMP004", "Sneha Joshi", "Marketing", 32000));
        employees.add(new Employee("EMP005", "Rohan Shah", "IT", 40000));
        employees.add(new Employee("EMP006", "Neha Gupta", "HR", 29000));
        employees.add(new Employee("EMP007", "Karan Mehta", "Finance", 38000));
        employees.add(new Employee("EMP008", "Pooja Desai", "IT", 33000));
        employees.add(new Employee("EMP009", "Arjun Patil", "Sales", 27000));
        employees.add(new Employee("EMP010", "Ananya Kulkarni", "Marketing", 31000));
    }

    // =============== MAIN METHOD ===============

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginPage();
            }
        });
    }
}