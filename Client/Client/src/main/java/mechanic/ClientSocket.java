package mechanic;

import message.Message;
import message.Method;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ClientSocket {
    private Socket client;
    private String host;
    private int port;
    private JTextArea textAreaLog;
    private String[] fileList;
    private String user;

    public ClientSocket(String host, int port, String user, JTextArea textAreaLog) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.textAreaLog = textAreaLog;
    }

    public boolean login(String username, String password) {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        boolean success = false;

        try {
            outToServer = new DataOutputStream(client.getOutputStream());
            outToServer.writeUTF(username  + " request login");

            //create message
            Message message = new Message();
            message.setMessageMethod(Method.LOGIN);
            message.setUsername(username);
            message.setPassword(password);

            //send message
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(message);

            //check success
            ois = new ObjectInputStream(client.getInputStream());
            message = (Message) ois.readObject();
            switch (message.getStatus()) {
                case LOGIN_SUCCESS -> {
                    user = username;
                    success = true;
                }
                case LOGIN_FAILURE -> {
                    success = false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return success;
    }

    public String[] putFile(String sourceFilePath) {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            //make inform
            outToServer = new DataOutputStream(client.getOutputStream());
            outToServer.writeUTF(user + " send a file.");

            //create message
            Message message = createPutMessage(sourceFilePath);

            //send file
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(message);

            //get confirmation
            ois = new ObjectInputStream(client.getInputStream());
            message = (Message) ois.readObject();
            if (message != null) {
                switch (message.getStatus()) {
                    case SEND_FILE_SUCCESS -> {
                        textAreaLog.append("Send file successfully.\n");
                    }
                    case SEND_FILE_FAILURE -> {
                        textAreaLog.append("Send file failed.\n");
                    }
                }
            } else {
                textAreaLog.append("Send file failed.\n");
            }

            //update list file
            String[] list = (String[]) ois.readObject();
            if (list != null) {
                fileList = list;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(oos);
            closeStream(ois);
            closeStream(outToServer);
        }
        return fileList;
    }

    public boolean receiveFile(String fileName, String destinationDirectory) {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        boolean success = true;

        try {
            //make inform
            outToServer = new DataOutputStream(client.getOutputStream());
            outToServer.writeUTF(user + " request file " + fileName);

            //create message
            Message message = createGetMessage(fileName);

            //send message
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(message);

            //save file
            ois = new ObjectInputStream(client.getInputStream());
            message = (Message) ois.readObject();
            if (message != null) {
                saveFile(destinationDirectory, message);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            success = false;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            success = false;
        } finally {
            closeStream(outToServer);
            closeStream(oos);
            closeStream(ois);
        }
        return success;
    }

    public String[] getFileList() {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            outToServer = new DataOutputStream(client.getOutputStream());
            outToServer.writeUTF(user + " request file list");

            Message message = createReceiveListMessage();

            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(message);

            ois = new ObjectInputStream(client.getInputStream());
            String list[] = (String[]) ois.readObject();

            if (list != null) {
                fileList = list;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeStream(outToServer);
            closeStream(oos);
            closeStream(ois);
        }
        return fileList;
    }

    private Message createPutMessage(String sourceFilePath) {
        Message message = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(sourceFilePath);
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            message = new Message();
            byte[] fileBytes = new byte[(int) sourceFile.length()];
            bis.read(fileBytes, 0, fileBytes.length);
            message.setMessageMethod(Method.PUT_FILE);
            message.setFileName(sourceFile.getName());
            message.setDataBytes(fileBytes);
            message.setFileSize(sourceFile.length());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return message;
    }

    private Message createGetMessage(String fileName) {
        Message message = new Message();
        message.setFileName(fileName);
        message.setMessageMethod(Method.GET_FILE);
        return message;
    }

    private Message createReceiveListMessage() {
        Message message = new Message();
        message.setMessageMethod(Method.RECEIVE_LIST);
        return message;
    }

    private boolean saveFile(String destinationDirectory, Message message) {
        BufferedOutputStream bos = null;
        try {
            if (message != null) {
                File fileReceive = new File(destinationDirectory + message.getFileName());
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

    public void connectServer() {
        try {
            client = new Socket(host, port);
            textAreaLog.append(("connect to server.\n"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            if (client != null) {
                client.close();
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
