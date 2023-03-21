package GUI;

import mechanic.ClientSocket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientController implements ActionListener {
    private ClientGUI clientGUI;
    private String host;
    private int port;

    public ClientController(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
        clientGUI.getBtnBrowse().addActionListener(this);
        clientGUI.getBtnSendFile().addActionListener(this);
        clientGUI.getBtnGetFile().addActionListener(this);
        clientGUI.getBtnGetList().addActionListener(this);
        clientGUI.getBtnLogin().addActionListener(this);
        clientGUI.getBtnLogout1().addActionListener(this);
        clientGUI.getBtnLogout2().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(clientGUI.getBtnBrowse().getText())) {
            clientGUI.chooseFile();
        }

        if (actionEvent.getActionCommand().equals(clientGUI.getBtnSendFile().getText())) {
            host = clientGUI.getTextFieldHost().getText();
            String sourceFilePath = clientGUI.getTextFieldFilePath().getText();
            if (!host.equals("") && !sourceFilePath.equals("") && !clientGUI.getTextFieldPort().getText().equals("")) {
                port = Integer.parseInt(clientGUI.getTextFieldPort().getText());
                ClientSocket clientSocket = new ClientSocket(host, port, clientGUI.getCurrentUser(),
                        clientGUI.getTextAreaResult());
                clientSocket.connectServer();
                String[] list = clientSocket.putFile(sourceFilePath);
                clientGUI.updateFileList(list);
                clientSocket.closeSocket();
            } else {
                JOptionPane.showMessageDialog(clientGUI, "Host, Port and File's Path must not be empty!");
            }
        }

        if (actionEvent.getActionCommand().equals(clientGUI.getBtnGetFile().getText())) {
            if (clientGUI.getTextFieldHost().getText().equals("") ||
            clientGUI.getTextFieldPort().getText().equals("")) {
                JOptionPane.showMessageDialog(clientGUI, "Host and Port must not be empty!");
            } else {
                if (clientGUI.getFileList().isSelectionEmpty()) {
                    JOptionPane.showMessageDialog(clientGUI, "Please choose a file");
                } else {
                    host = clientGUI.getTextFieldHost().getText();
                    port = Integer.parseInt(clientGUI.getTextFieldPort().getText());
                    String destinationDirectory = clientGUI.folderChooser() + "//";
                    String fileName = clientGUI.getFileList().getSelectedValue();

                    ClientSocket clientSocket = new ClientSocket(host, port, clientGUI.getCurrentUser(),
                            clientGUI.getTextAreaResult());
                    clientSocket.connectServer();
                    clientSocket.receiveFile(fileName, destinationDirectory);
                    clientSocket.closeSocket();
                }
            }
        }

        if (actionEvent.getActionCommand().equals(clientGUI.getBtnGetList().getText())) {
            if (clientGUI.getTextFieldHost().getText().equals("") ||
                    clientGUI.getTextFieldPort().getText().equals("")) {
                JOptionPane.showMessageDialog(clientGUI, "Host and Port must not be empty!");
            } else {
                host = clientGUI.getTextFieldHost().getText();
                port = Integer.parseInt(clientGUI.getTextFieldPort().getText());

                ClientSocket clientSocket = new ClientSocket(host, port, clientGUI.getCurrentUser(),
                        clientGUI.getTextAreaResult());
                clientSocket.connectServer();
                String list[] = clientSocket.getFileList();
                clientGUI.updateFileList(list);
                clientSocket.closeSocket();
            }
        }

        if (actionEvent.getActionCommand().equals(clientGUI.getBtnLogin().getText())) {
            try {
                host = "172.24.4.97";
                port = 8884;

                ClientSocket clientSocket = new ClientSocket(host, port, clientGUI.getCurrentUser(),
                        clientGUI.getTextAreaResult());
                clientSocket.connectServer();
                boolean sucess = clientSocket.login(clientGUI.getUsernameField().getText(),
                        clientGUI.getPasswordField().getText());

                if (sucess) {
                    clientGUI.login();
                } else {
                    JOptionPane.showMessageDialog(clientGUI, "username or password incorrect!");
                }
                clientSocket.closeSocket();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(clientGUI, "Cannot login!");
            }
        }

        if (actionEvent.getActionCommand().equals(clientGUI.getBtnLogout1().getText()) ||
                actionEvent.getActionCommand().equals(clientGUI.getBtnLogout2().getText())) {
            clientGUI.logout();
        }
    }
}
