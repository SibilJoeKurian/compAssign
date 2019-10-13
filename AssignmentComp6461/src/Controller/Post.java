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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import Utils.Attributes;
import Utils.checkArguments;

/**
 * @author Sibil
 *
 */
public class Post {
	static String[] arguments;
	static String data;
	static String passedData;
	static String request;
	static String splitURL = "";
	static boolean containsVerboseFlag;
	static ArrayList<String> headerList = new ArrayList<String>();
	static String response = "";

	public Post(String[] args) {
		arguments = args;
	}

	public void postDriver() throws UnknownHostException, IOException {
		analyzeCommands();
		//analyzeURL(splitURL);
		setContentLength();
		postRequest();
	}

	private boolean analyzeCommands() throws IOException {
		boolean checkCommand = true;
		for (int i = 2; i < arguments.length; i++) {
			if (arguments[i].contains("://"))
				splitURL = arguments[i];
			else if (arguments[i].equals("-v")) {
				containsVerboseFlag = true;
			} else if (arguments[i].equals("-o")) {
				Attributes.setContainsSaveFlag(true);
				Attributes.setFileName(arguments[++i]);
			}else if (arguments[i].equals("-h")) {
				headerList.add(arguments[++i]);
			} else if (arguments[i].equals("-d")) {
				i = setData(i);
			} else if (arguments[i].equals("-f")) {
				readFileData(arguments[++i]);
			} else {
				Attributes.analyzeURL(arguments[i]);
			}
		}
		return checkCommand;
	}

	private void readFileData(String fileLocation) throws IOException {
		data = new String(Files.readAllBytes(Paths.get(fileLocation)));
		data=data.replaceAll("\r", "");
		data=data.replaceAll("\n", "");
		if (data.contains("\""))
			data = data.replaceAll("\"", "");
		
		makeDataJSONStandard();
		
	}

	/**
	 * This fucntion will set the content length if it is not provided with the
	 * arguments
	 */
	private void setContentLength() {
		// TODO Auto-generated method stub
		boolean containsContentFiled = false;
		for (String header : headerList) {
			if (header.contains("Content-Length")) {
				containsContentFiled = true;
				break;
			}
		}
		if (containsContentFiled == false) {
			headerList.add("Content-Length:" + data.length());
		}
	}

	/**
	 * This fucntion deals with the data filed if the data arguments are in
	 * different line this function will parse it into the same line and returns
	 * position of arguments
	 * 
	 * @param position statrting index of the data field
	 * @return args postion =>after extracting the data
	 */
	private int setData(int position) {
		String value1=arguments[position];
		if(!value1.contains("}"))
		while (++position < arguments.length) {
			String value = arguments[position];
			value.replaceAll("\"", "");
			value.replaceAll("'", "");
			if (value.startsWith("{") && (value.endsWith("}"))) {
				data = value;
				break;
			} else if (value.startsWith("{") && !(value.endsWith("}"))) {
				data += value;
			} else if (value.endsWith("}")) {
				data = data + " " + value;
				break;
			} else {
				data = data + value;
			}
		}
		System.out.println("data=> " + data);
		makeDataJSONStandard();

		return position;
	}

	private void makeDataJSONStandard() {
		String newData = "";
		// TODO Auto-generated method stub

		for (int j = 0; j < data.length(); j++) {
			char ch = data.charAt(j);
			if (ch == '{') {
				newData = newData + ch + "\"";
			} else if (ch == ':') {
				newData = newData + "\"" + ch + "\"";
			} else if (ch == ',') {
				newData = newData + "\"" + ch + "\"";
			} else if (ch == '}') {
				newData = newData + "\"" + ch;
			} else if (!String.valueOf(ch).equals("'"))
				newData = newData + ch;
		}
		data = newData;
		System.out.println("data=> " + data);
	}

	/**
	 * This function make the request for the server
	 */
	private static void makeRequest() {
		// TODO Auto-generated method stub
		request = "POST " + Attributes.getPath() + " HTTP/1.0\r\n" +
		"Host:" + " " + Attributes.getHost() + " \r\n";
		if (headerList.size() != 0)
			for (String header : headerList)
				request += header + "\r\n";
		request += "Connection: close\r\n";
		request += "\r\n";

		request += data;
	}

	
	

	public static void postRequest() throws UnknownHostException, IOException {
		Socket socket = null;
		BufferedWriter bufferWriter = null;
		BufferedReader bufferReader = null;

		try {
			socket = new Socket(Attributes.getHost(), 80);
			bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			makeRequest();
			bufferWriter.write(request.toCharArray());

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
			if (Attributes.isContainsSaveFlag())
				Attributes.writeFile(response);

		} finally {
			request = null;
			bufferReader.close();
			bufferWriter.close();
			socket.close();
		}
	}


}
