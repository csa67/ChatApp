TITLE: Internet Chatting

Description:
This project involves creating a chat application that supports text messaging and file transfers using a client-server architecture implemented with Java threads. Each instance of the program will operate as both a client and a server, handling incoming and outgoing messages and files.

Functionality:
- The program starts two threads: a main (reading) thread and a writing thread.
- The writing thread:
  - Takes a port number from the user to connect to another user.
  - Establishes a socket connection to the specified port number.
  - Sends messages entered by the user through the socket.
  - Supports the command "transfer filename" to send files.
- The main thread:
  - Sets up a ServerSocket and listens for incoming connections.
  - Accepts connections and reads messages from the socket, displaying them on the screen.
  - Handles incoming "transfer filename" commands by receiving files and saving them locally.

Execution Commands:
- javac ChatApp.java
- java ChatApp

Execution Steps:
1. Start the program for the first user (Alice). The main thread will display a port number, for example, X.
2. Start the program for the second user (Bob). The main thread will display a different port number, for example, Y.
3. On Alice’s console, enter port Y to connect Alice’s writing thread to Bob’s server.
4. On Bob’s console, enter port X to connect Bob’s writing thread to Alice’s server.
5. Test communication by typing messages in Alice's and Bob's console windows and observe them appearing on the other user's screen.
6. Test file transfer by using the "transfer filename" command to send a file from one user to the other.

