package Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Utils.Attributes;

/**
 * @author Sibil
 *
 */

public class Get {
	String[] arguments;
	int port = 80;
	
	String request = null;
	BufferedWriter write;
	static boolean containsVerboseFlag;
	static boolean containsSaveFlag;
	String filePath = "F:\\eclipse\\AssignmentComp6461\\src\\";
	static String fileName;
	static ArrayList<String> headerList = new ArrayList<String>();
	static String response = "";
	static int redirect = 0;

	public Get(String[] args) {
		arguments = args;
	}

	/**
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void getDriver() throws UnknownHostException, IOException {
		// simple get function
		analyzeCommands();
		// analyzeURL(splitURL);
		getRequest();
	}

	/**
	 * This function parse the command from argument array
	 * 
	 * 
	 */
	private boolean analyzeCommands() {
		boolean checkCommand = true;
		for (int i = 2; i < arguments.length; i++) {
			
			if (arguments[i].equals("-v")) {
				containsVerboseFlag = true;
			} else if (arguments[i].equals("-o")) {
				containsSaveFlag = true;
				fileName = arguments[++i];
			} else if (arguments[i].equals("-h")) {
				headerList.add(arguments[++i]);
			} else if (arguments[i].equals("-f")||(arguments[i].equals("-d"))) {
				System.out.println(Help.INVALID);
				break;
			} else {
				Attributes.analyzeURL(arguments[i]);
			}
		}
		return checkCommand;

	}

	/**
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void getRequest() throws UnknownHostException, IOException {
		Socket socket = null;
		BufferedWriter bufferWriter = null;
		BufferedReader bufferReader = null;
		try {
			request = "";
			socket = new Socket(Attributes.getHost(), port);
			bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			makeRequest();
			System.out.println("Printing request");
			System.out.println(request);
			bufferWriter.write(request);
			bufferWriter.flush();

			bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String line;

			while ((line = bufferReader.readLine()) != null) {
				if (containsVerboseFlag) {
					response += line + "\n";
				} else if (!containsVerboseFlag && line.trim().isEmpty()) {
					containsVerboseFlag = true;
					response += line + "\n";
				}
			}
			System.out.println(response);
			if (containsSaveFlag)
				writeFile();
			if (response.contains("302") && redirect < 5) {
				if (redirect == 5) {
					System.out.println("Cannot be redirected");
				} else
					redirect();
			}

		} finally {
			request = null;
			bufferReader.close();
			bufferWriter.close();
			socket.close();
		}

	}

	private void redirect() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		redirect += 1;
		System.out.println("Redirecting" + redirect);

		// analyzeURL(splitURL);
		getRequest();
	}

	private void writeFile() throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file = new File(filePath += fileName);
		PrintWriter printWriter = new PrintWriter(file);
		printWriter.println(response);
		printWriter.close();
	}

	private void makeRequest() {
		// TODO Auto-generated method stub
		request += "GET " + Attributes.getPath() + " HTTP/1.0\r\n";
		request += "Host: " + Attributes.getHost() + "\r\n";
		if (headerList.size() != 0)
			for (String header : headerList)
				request += header + "\r\n";
		request += "\r\n";
	}

}
