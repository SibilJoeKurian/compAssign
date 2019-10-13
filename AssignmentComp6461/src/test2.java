import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class test2 {
	static String url;
	static String host;
	static String path;
	static String contentType;
	static int contentLength;
	static String requestLine1 = "";
	static String requestLine2 = "";
	static String requestLine3 = "";
	static String requestLine4 = "";
	static String requestLine5 = "";
	static int inLineDataStart;
	static int inLineDataEnd;
	static int URLPosition;
	static String data;
	static String passedData;
	static String request;

	public static void main(String[] args) throws UnknownHostException, IOException {
		String doubleQuotes = "\\";

		System.out.println("Printing " + doubleQuotes);
		for (int i = 0; i < args.length; i++) {
			//System.out.println(i + "= " + args[i]);
			
			if (args[i].equals("--d")) {
				args[i + 1] = args[i + 1].substring(2, args[i + 1].length() - 1);
				args[i + 2] = args[i + 2].substring(0, args[i + 2].length() - 1);
				data = "{" + doubleQuotes + "\"" + args[i + 1] + doubleQuotes + "\": " + args[i + 2];
				passedData="{"  + "\"" + args[i + 1] +  "\": " + args[i + 2];
			}

		}

		// System.out.println(data);
		contentLength = data.length() - 2;
		// System.out.println(data.length() - 2);
		// if(args[4].equals("-d")) {
		// if(args[])
		// }
		// setIndexes(args);

		url = args[args.length - 1];
		setContentType(args[3]);
		splitURL();
		makeURL();
		postRequest();
	}

	private static void setContentType(String arg) {
		String[] array=arg.split(":");
		contentType=array[0]+": "+array[1];
	}

	private static void setIndexes(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("'"))
				inLineDataStart = i;
			if (args[i].endsWith("'"))
				inLineDataEnd = i;
		}
		URLPosition = inLineDataEnd + 1;

	}

	private static void makeURL() {
		// TODO Auto-generated method stub
		request = "POST" +" "+path+  " HTTP/1.0\r\n" 
				+ "Host:"+" "+host+" \r\n";
				
		request+=contentType + "\r\n";
		request+="Content-Length: " + contentLength + "\r\n";
		request+="Connection: close\r\n";
		//request = requestLine1 + requestLine2 + requestLine3 + requestLine4 + requestLine5;
		request += "\r\n";
		request+="{\"Assignment\": 1}";
		//request += "\r\n";
		//System.out.println(request);
	}

	private static void splitURL() {
		// TODO Auto-generated method stub
		int index = url.indexOf(":");
		url = url.substring(index + 3, url.length());
		int index1 = url.indexOf("/");
		host = url.substring(0, index1);
		path = url.substring(index1, url.length());
//		System.out.println(url);
//		System.out.println(path);
		// System.out.println(url.charAt(index+));
	}

	public static void postRequest() throws UnknownHostException, IOException {
		Socket socket = null;
		BufferedWriter bufferWriter = null;
		BufferedReader bufferReader = null;

		try {
			socket = new Socket(host, 80);
			bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			bufferWriter.write(request);
			
			bufferWriter.flush();
			bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String response = "";

			String line;
			boolean isVerbose = true;
			while ((line = bufferReader.readLine()) != null) {
				System.out.println(line);
				if (line.trim().isEmpty()) {
					isVerbose = false;
					continue;
				}
				if (!isVerbose) {
					response += line + "\n";
				}
			}
			//System.out.println(response);

		} finally {
			request = null;
			bufferReader.close();
			bufferWriter.close();
			socket.close();
		}
	}
}
