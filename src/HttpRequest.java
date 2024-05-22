import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
     /** Help variables */
    final static String CRLF = "\r\n";
    final static int HTTP_PORT = 80;
    /**  To Store the request parameters from the buffered reader*/
    String method;
    String URI;
    String version;
    String headers = "";
    /** Server and port */
    private String host;
    private int port;

    /** Creation of Http Request for the request received from client socket */
    public HttpRequest(BufferedReader from)
    {
        System.out.println("Creating Http Request and validating the request");
	String firstLine = "";
	try
        {
            firstLine = from.readLine();
	} 
        catch (IOException e)
        {
	    System.out.println("Error reading request line: " + e);
	}

        /* Splitting based on Space and storing in the string array tmp */
	String[] tmp = firstLine.split(" ");
        /*According to http request message format*/
        /* First element in the string array contains method */
	method = tmp[0];
        /* Second element in the string array contains URI */
	URI = tmp[1];
        /* Third element in the string array contains version */
	version = tmp[2];
        
        System.out.println("    Method is   : " + method);
	System.out.println("    URI is      : " + URI);
        System.out.println("    Version is  : " + version);

        /* If statement to validate whether it is a GET method or not */
	if (!method.equals("GET"))
        {
	    System.out.println("Error: Method not GET");
	}
        else
        {
            try
            {
                /* Reading line by line till the end and send the same to the Server */
                String line = from.readLine();
                while (line.length() != 0)
                {
                    /* Inserting the line into headers variable and adding a blank line */
                    headers += line + CRLF;
                    /* If statement to find whether it starts with Host: */
                    if (line.startsWith("Host:"))
                    {
                        /* Obtaining host address and port from the input received */
                        tmp = line.split(" ");
                        if (tmp[1].indexOf(':') > 0)
                        {
                            String[] reqHeader = tmp[1].split(":");
                            /* Saving the host and port numbers in variables for reference */
                            host = reqHeader[0];
                            port = Integer.parseInt(reqHeader[1]);
                        }
                        else
                        {
                            host = tmp[1];
                            port = HTTP_PORT;
                        }
                    }
                    /* Reading next line */
                    //System.out.println("Reading Next line input through input buffer reader");
                    line = from.readLine();
                }
            }
            catch (IOException e)
            {
                System.out.println("Error reading from socket: " + e);
                return;
            }
            System.out.println("Host to contact is: " + host + " at port " + port);
        }
    }

    /** Get methods for getting Host and Port */ 
    /* host is returned to server */
    public String getHost()
    {
	return host;
    }

    /** port is returned to server */
    public int getPort()
    {
	return port;
    }

    /** Conversion of request again to String to send it to server */
    @Override
    public String toString()
    {
	String req = "";
        /* Concactination of method, uri, version and headers information according to http request message format */
	req = method + " " + URI + " " + version + CRLF;
	req += headers;
	req += "Connection: close" + CRLF;
	req += CRLF;
        /* Returning the string back to the called function */
	return req;
    }
}