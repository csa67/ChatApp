import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatApp {
    private static Socket socket;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(0); // Bind to any available port
            System.out.println("Server started on port: " + serverSocket.getLocalPort());

            Scanner scanner = new Scanner(System.in);

            // Getting the user name for the local user
            System.out.print("Enter your name: ");
            String localUserName = scanner.nextLine();

            System.out.print("Enter port to connect to: ");
            int port = Integer.parseInt(scanner.nextLine());

            // Thread for connecting to a peer and handling sending messages
            new Thread(() -> {
                try {
                    socket = new Socket("localhost", port);
                    System.out.println(localUserName + " connected to port: " + port);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    while (true) {
                        String input = scanner.nextLine();
                        if (input.startsWith("transfer ")) {
                            String filename = input.split(" ")[1];
                            File file = new File(filename);
                            if (file.exists()) {
                                out.writeUTF(localUserName + ": transfer " + file.getName());

                                // Sending file
                                try (FileInputStream fis = new FileInputStream(file)) {
                                    byte[] buffer = new byte[4096];
                                    int count;
                                    long fileSize = file.length();
                                    out.writeLong(fileSize);
                                    while ((count = fis.read(buffer)) > 0) {
                                        out.write(buffer, 0, count);
                                    }
                                    out.flush();
                                    System.out
                                            .println(localUserName + " sent a file from port " + socket.getLocalPort());
                                }
                            } else {
                                System.out.println("Given file does not exist!");
                            }
                        } else if (input.equalsIgnoreCase("exit")) {
                            out.writeUTF(localUserName + ": bye");
                            System.out.println(
                                    localUserName + " is closing the connection from port " + socket.getLocalPort());
                            socket.close();
                            serverSocket.close();
                            scanner.close();
                            break;

                        } else {
                            out.writeUTF(localUserName + ": " + input);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Thread for accepting a connection and handling receiving messages
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection accepted from " + clientSocket.getRemoteSocketAddress());

            new Thread(() -> {
                try {
                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());

                    while (true) {
                        String msg = in.readUTF();
                        if (msg.contains("transfer ")) {
                            String[] parts = msg.split(": ", 2);
                            String senderName = parts[0];
                            String filename = "new" + parts[1].trim().substring(9); // Filename extraction corrected

                            // Receiving file
                            try (FileOutputStream fos = new FileOutputStream(filename)) {
                                byte[] buffer = new byte[4096];
                                long fileSize = in.readLong();
                                long received = 0;
                                int count;
                                while (received < fileSize) {
                                    count = in.read(buffer);
                                    fos.write(buffer, 0, count);
                                    received += count;
                                }
                                System.out.println(senderName + " sent a file and was successfully received.");
                            }
                        } else if (msg.endsWith("exit")) {
                            System.out.println(msg);
                            clientSocket.close();
                            break;
                        } else {
                            System.out.println(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
