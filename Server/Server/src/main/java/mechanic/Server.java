package mechanic;

import message.Message;
import message.Method;
import message.Status;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Server extends Thread {
    private final int PORT = 8884;
    private final String source = "/home/ubuntu/upload/";
    private ServerSocket serverSocket;
    private Database database;
    private String currentUser;

    public void open() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is open in port " + PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.database = new Database();
        Connection connection = this.database.getConnection();

        while (true) {
            DataInputStream inFromClient = null;
            Socket server = null;
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;

            try {
                server = serverSocket.accept();
                // get greeting from client
                inFromClient = new DataInputStream(server.getInputStream());
                System.out.println(inFromClient.readUTF());

                // receive file info
                ois = new ObjectInputStream(server.getInputStream());
                Message message = (Message) ois.readObject();

                if (message != null) {
                    oos = new ObjectOutputStream(server.getOutputStream());
                    switch (message.getMessageMethod()) {
                        case LOGIN:
                            String username = message.getUsername();
                            String password = message.getPassword();

                            if (username == null || password == null) {
                                message.setStatus(Status.LOGIN_FAILURE);
                                oos.writeObject(message);
                            } else {
                                try {
                                    boolean success = false;
                                    Statement statement = connection.createStatement();
                                    String query = "SELECT password from client WHERE username=\"" + username + "\"";
                                    ResultSet resultSet = statement.executeQuery(query);
                                    if (resultSet.next()) {
                                        if (password.equals(resultSet.getString("password"))) {
                                            success = true;
                                            currentUser = username;
                                        }
                                    }
                                    if (success) {
                                        message.setStatus(Status.LOGIN_SUCCESS);
                                    } else {
                                        message.setStatus(Status.LOGIN_FAILURE);
                                    }
                                    oos.writeObject(message);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case PUT_FILE:
                            boolean saveResult = saveFile(message);
                            if (saveResult) {
                                System.out.println("Save file successfully");
                                database.addFile(currentUser, message.getFileName(), message.getFileSize());
                                message.setStatus(Status.SEND_FILE_SUCCESS);
                            } else {
                                System.out.println("Save file failed");
                                message.setStatus(Status.SEND_FILE_FAILURE);
                            }

                            //confirm that file is received
                            message.setDataBytes(null);
                            oos.writeObject(message);

                            //update list file
                            String[] newList = getFileList();
                            oos.writeObject(newList);
                            break;
                        case GET_FILE:
                            Message requestFile = createSendFileMessage(message.getFileName());
                            oos.writeObject(requestFile);
                            break;
                        case RECEIVE_LIST:
                            String[] list = getFileList();
                            oos.writeObject(list);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeStream(ois);
                closeStream(oos);
                closeStream(inFromClient);
                closeSocket(server);
            }
        }
    }

    private boolean saveFile(Message message) {
        BufferedOutputStream bos = null;
        try {
            if (message != null) {
                File fileReceive = new File(source + message.getFileName());
                bos = new BufferedOutputStream(new FileOutputStream(fileReceive));
                bos.write(message.getDataBytes());
                bos.flush();
            } else {
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private Message createSendFileMessage(String fileName) {
        Message message = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(source + fileName);
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            message = new Message();
            byte[] fileBytes = new byte[(int) sourceFile.length()];
            bis.read(fileBytes, 0, fileBytes.length);
            message.setMessageMethod(Method.PUT_FILE);
            message.setFileName(fileName);
            message.setDataBytes(fileBytes);
            message.setFileSize(fileBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public String[] getFileList() {
        return this.database.getFileList(currentUser);
    }

    public void closeSocket(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
