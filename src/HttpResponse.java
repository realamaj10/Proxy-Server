import java.io.DataInputStream;
import java.io.IOException;

public class HttpResponse{
    final static String CRLF = "\r\n";
    /** How big is the buffer used for reading the object */
    final static int BUF_SIZE = 8192;
    /** Maximum size of objects that this proxy can handle. For the
     * moment set to 100 KB */
    final static int MAX_OBJECT_SIZE = 100000;
    /** Reply status and headers */
    String version;
    int status;
    String statusLine = "";
    String headers = "";
    /* Body of reply in chunks of 100kb */
    byte[] body = new byte[MAX_OBJECT_SIZE];

    /** Read response from server. */
    public HttpResponse(DataInputStream fromServer)
    {
    
	int length = -1;
	boolean gotStatusLine = false;

	/* First read status line and response headers */
	try
        {
	    String line = fromServer.readLine();
	    while (line.length() != 0)
            {
                /* Status line retrieval */
		if (!gotStatusLine)
                {
		    statusLine = line;
		    gotStatusLine = true;
                    System.out.println("Status Line Retrieved");
		} 
                else
                {
                    /* Headers retrieval */
		    headers += line + CRLF;
		}
                    
		/* Length of content retrieval using Content-Length header */
		if (line.startsWith("Content-Length:") ||
		    line.startsWith("Content-length:"))
                {
                    /* Splitting based on space */
		    String[] tmp = line.split(" ");
                    /* Retriving length parameter */
		    length = Integer.parseInt(tmp[1]);
                    System.out.println("Length  :" +length);
		}
		line = fromServer.readLine();
	    }
            System.out.println("Header Retrieved");
	} 
        catch (IOException e)
        {
	    System.out.println("Error reading headers from server: " + e);
	    return;
	}

	try
        {
	    int bytesRead = 0;
	    byte buf[] = new byte[BUF_SIZE];
	    boolean loop = false;

	    /* If we didn't get Content-Length header, just loop until
	     * the connection is closed. */
	    if (length == -1)
            {
		loop = true;
	    }

	    /* Read the body in chunks of BUF_SIZE and copy the chunk
	     * into body. Usually replies come back in smaller chunks
	     * than BUF_SIZE. The while-loop ends when either we have
	     * read Content-Length bytes or when the connection is
	     * closed when there is no Connection-Length in the
	     * response. */
	    while (bytesRead < length || loop)
            {
		/* Read it in as binary data */
		int res = fromServer.read(buf, 0, BUF_SIZE);
		
		if (res == -1)
                {
		    break;
		}
		/* Copying of bytes into body without exceeding object size */
		for (int i = 0;i < res && (i + bytesRead) < MAX_OBJECT_SIZE;i++)
                {
		    body[bytesRead + i] = buf[i];
		}
		bytesRead += res;
	    }
 	} 
        catch (IOException e)
        {
 	    System.out.println("Error reading response body: " + e);
 	    
 	}


    }

    /* Conversion of response into a string for easy re-sending */
    /* Conversion of only response headers */
    @Override
    public String toString()
    {
	String res = "";
        /* according to http response message format */
	res = statusLine + CRLF;
	res += headers;
	res += CRLF;

	return res;
    }
}