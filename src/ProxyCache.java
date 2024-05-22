import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ProxyCache {

    //This port is used by proxy 
    private static int port;
    //Client connections are done by this socket
    private static ServerSocket socket;

    /**
     * Socket and proxy cache objects are created
     */
    public static void init(int p) {
        port = p;
        try {
            //Code for creation of socket for the input port 
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error creating socket: " + e);
            System.exit(-1);
        }
    }

    public static void handle(Socket client) {
        //Creating objects for Socket, HttpRequest and HttpResponse initialised to null 
        Socket server = null;
        HttpRequest request = null;
        HttpResponse response = null;

        //Read request done here
        try {
            /* Using Input Stream to take the input from client */
            BufferedReader readClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("Reading Request has been started");

            /* Calling object with the input taken passed as parameter */
            request = new HttpRequest(readClient);
            System.out.println("Request information obtained");
        } catch (IOException e) {
            System.out.println("Error reading request from client: " + e);
            return;
        }
	/* Obtained requests sent to server */
	try
        {
	    /* Opening Socket and sending request to server*/
            System.out.println("Request sending to server");
            /* Creating socket for server using the host and port number obtained by extracting the input request */
        if (request.getHost().equals("www.stackoverflow.com") || request.getHost().equals("www.github.com")) {
            //port 443 is port for HTTPS, which is the protocol used nowadays after 2004 
            //before 2004 , HTTP was used that has port 80
            server = new Socket("www.plagiarism.com", 443);
            System.out.println("Do not go to these pages while studying!!");
            // If we want proxy to redirect to youtube.com
        } else if (request.getHost().equals("www.facebook.com") || request.getHost().equals("www.instagram.com")) {
            //port 443 is port for HTTPS, which is the protocol used nowadays after 2004 
            //before 2004 , HTTP was used that has port 80
            server = new Socket("https://www.youtube.com/watch?v=91sFlP6aa5Q", 443);
            System.out.println("Do not go to these time wasting pages!!");
            //If we want proxy to redirect to Normal Pages
        } else {
            server = new Socket(request.getHost(), request.getPort());
            System.out.println("This page is a normal page!");
        }

            /* Data Output Stream ready for sending data */
	    DataOutputStream writeServer = new DataOutputStream(server.getOutputStream());
            /* Calling toString function to convert the request into string to send it to server */
	    writeServer.writeBytes(request.toString());
            System.out.println("Request sent to server");
	} 
        catch (UnknownHostException e)
        {
	    System.out.println("Unknown host: " + request.getHost());
	    System.out.println(e);
	    return;
	} 
        catch (IOException e)
        {
	    System.out.println("Error writing request to server: " + e);
	    return;
	}
	/* response is read and sent to client*/
	try
        {
            System.out.println("Response being received from Server");
            /* Using Data Input Stream to receive the data from server */
	    DataInputStream inputServer = new DataInputStream(server.getInputStream());
            /* Assigning parameter for the object created for Http Response */
	    response = new HttpResponse(inputServer);
            
	    DataOutputStream outputClient = new DataOutputStream(client.getOutputStream());
	    /* Sending Response Headers */
            outputClient.writeBytes(response.toString());
            /* Sending Response Body */
	    outputClient.write(response.body);
            /* Closing Sockets */
	    client.close();
	    server.close();
	} 
        catch (IOException e)
        {
	    System.out.println("Error writing response to client: " + e);
	}
    }
    //port number should be read through command line arguments and proxy server is started
    public static void main(String args[]) {
        /* variable myPort used for storing port number - initiated with 0 */
        int myPort = 0;

        try {
            /* Input taken through command line is stored in the variable myPort */
            myPort = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Need port number as argument");
            System.exit(-1);
        } catch (NumberFormatException e) {
            System.out.println("Please give port number as integer.");
            System.exit(-1);
        }

        /* Calling function for creating socket */
        init(myPort);

        System.out.println("Socket created succesfully");

        //Socket to take care of incoming connections
    
        Socket client = null;
        /* Loop Running continously to listen */
        while (true) {
            try {
                /* Receiving connection */
                client = socket.accept();
                System.out.println("Receievd Connection " + client);
                /* Function for handling client */
                handle(client);
            } catch (IOException e) {
                System.out.println("Error reading request from client: " + e);
                continue;
            }
        }

    }
}
