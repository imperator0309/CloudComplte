package GUI;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private CardLayout cardLayout;
    private Container container;
    //upload tab
    private JLabel labelHost;
    private JTextField textFieldHost;
    private JLabel labelPort;
    private JTextField textFieldPort;
    private JButton btnBrowse;
    private JTextField textFieldFilePath;
    private JButton btnSendFile;
    private JTextArea textAreaResult;
    private JButton btnLogout1;

    //download tab
    private JButton btnGetFile;
    private JButton btnGetList;
    private JList<String> fileList;
    private JButton btnLogout2;

    //login layout
    JLabel usernameLabel;
    JLabel passwordLabel;
    JTextField usernameField;
    JTextField passwordField;
    JButton btnLogin;
    private String currentUser;

    public ClientGUI() {
        init();
    }

    public void init() {
        setTitle("MicroDrive");

        //upload tab
        labelHost = new JLabel("Host:");
        textFieldHost = new JTextField();
        getTextFieldHost().setText("172.24.4.243");
        labelPort = new JLabel("Port:");
        textFieldPort = new JTextField();
        textFieldPort.setText("8884");
        labelHost.setBounds(20, 20, 50, 25);
        textFieldHost.setBounds(60, 20, 120, 25);
        labelPort.setBounds(190, 20, 50, 25);
        textFieldPort.setBounds(225, 20, 50, 25);

        textFieldFilePath = new JTextField();
        textFieldFilePath.setBounds(20, 50, 450, 25);
        btnBrowse = new JButton("Browse");
        btnBrowse.setBounds(470, 50, 80, 25);
        btnSendFile = new JButton("Send File");
        btnSendFile.setBounds(20, 80, 100, 25);
        textAreaResult = new JTextArea();
        JScrollPane textAreaScroll = new JScrollPane(textAreaResult);
        textAreaScroll.setBounds(20, 110, 550, 125);
        //textAreaResult.setBounds(20, 110, 550, 150);

        btnLogout1 = new JButton("Logout");
        btnLogout1.setBounds(470, 250, 100, 25);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel tab1 = new JPanel();

        tab1.add(labelHost);
        tab1.add(textFieldHost);
        tab1.add(labelPort);
        tab1.add(textFieldPort);
        tab1.add(textFieldFilePath);
        tab1.add(btnBrowse);
        tab1.add(btnSendFile);
        //tab1.add(textAreaResult);
        tab1.add(textAreaScroll);
        tab1.add(btnLogout1);
        tab1.setLayout(null);

        //download tab
        btnGetList = new JButton("List");
        btnGetFile = new JButton("Get");
        fileList = new JList<String>();

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBounds(20, 50, 350, 200);
        btnGetList.setBounds(380, 50, 80, 25);
        btnGetFile.setBounds(470, 50, 80, 25);

        btnLogout2 = new JButton("Logout");
        btnLogout2.setBounds(470, 250, 100, 25);

        JPanel tab2 = new JPanel();
        tab2.add(btnGetList);
        tab2.add(btnGetFile);
        tab2.add(btnLogout2);
        tab2.add(scrollPane);
        tab2.setLayout(null);

        tabbedPane.addTab("Upload", tab1);
        tabbedPane.addTab("Download", tab2);

        //login layout
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 125, 100, 30);
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 175, 100, 30);

        usernameField = new JTextField();
        usernameField.setBounds(150, 125, 300, 30);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 175, 300, 30);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(240, 220, 120, 40);

        JLabel loginLabel = new JLabel("LOGIN");
        loginLabel.setBounds(225, 20, 250, 75);
        Font titleFont = new Font("Arial", Font.BOLD, 40);
        loginLabel.setFont(titleFont);

        JPanel loginPanel = new JPanel();
        loginPanel.add(loginLabel);
        loginPanel.add(usernameLabel);
        loginPanel.add(passwordLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordField);
        loginPanel.add(btnLogin);
        loginPanel.setLayout(null);

        //add(tabbedPane);
        cardLayout = new CardLayout();
        container = getContentPane();
        container.setLayout(cardLayout);
        container.add(loginPanel);
        container.add(tabbedPane);

        setSize(600, 350);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void chooseFile() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(this);

        try {
            if (fileChooser.getSelectedFile() != null) {
                textFieldFilePath.setText(fileChooser.getSelectedFile().getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String folderChooser() {
        final JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String path = null;
        folderChooser.showOpenDialog(this);
        try {
            if (folderChooser.getSelectedFiles() != null) {
                path = folderChooser.getCurrentDirectory().getPath();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return path;
    }

    public void updateFileList(String[] list) {
        if (list != null) {
            fileList.setListData(list);
        }
    }

    public void login() {
        this.cardLayout.next(container);
        currentUser = usernameField.getText();
        usernameField.setText("");
        passwordField.setText("");
    }

    public void logout() {
        cardLayout.next(container);
        fileList = new JList<String>();
    }

    public JTextField getTextFieldFilePath() {
        return textFieldFilePath;
    }

    public void setTextFieldFilePath(JTextField textFieldFilePath) {
        this.textFieldFilePath = textFieldFilePath;
    }

    public JTextField getTextFieldHost() {
        return textFieldHost;
    }

    public void setTextFieldHost(JTextField textFieldHost) {
        this.textFieldHost = textFieldHost;
    }

    public JTextField getTextFieldPort() {
        return textFieldPort;
    }

    public void setTextFieldPort(JTextField textFieldPort) {
        this.textFieldPort = textFieldPort;
    }

    public JTextArea getTextAreaResult() {
        return textAreaResult;
    }

    public JButton getBtnSendFile() {
        return btnSendFile;
    }

    public void setBtnSendFile(JButton btnSendFile) {
        this.btnSendFile = btnSendFile;
    }

    public JButton getBtnBrowse() {
        return btnBrowse;
    }

    public void setBtnBrowse(JButton btnBrowse) {
        this.btnBrowse = btnBrowse;
    }

    public JButton getBtnGetFile() {
        return btnGetFile;
    }

    public void setBtnGetFile(JButton btnGetFile) {
        this.btnGetFile = btnGetFile;
    }

    public JButton getBtnGetList() {
        return btnGetList;
    }

    public void setBtnGetList(JButton btnGetList) {
        this.btnGetList = btnGetList;
    }

    public JList<String> getFileList() {
        return fileList;
    }

    public void setFileList(JList<String> fileList) {
        this.fileList = fileList;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnLogout1() {
        return btnLogout1;
    }

    public JButton getBtnLogout2() {
        return btnLogout2;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public Container getContainer() {
        return container;
    }

    public JTextField getPasswordField() {
        return passwordField;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
