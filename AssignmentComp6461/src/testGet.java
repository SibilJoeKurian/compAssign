

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class testGet {
	BufferedWriter write;

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = null;
		BufferedWriter bufferWriter = null;
		BufferedReader bufferReader = null;
		String request = null;

		try {
			String s="httpbin.org";
			socket = new Socket(s, 80);
			bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			request = "";
			request = "GET /get?course=networking&assignment=1 HTTP/1.0\r\n"
			+"Host: httpbin.org\r\n"
			+"Content-Type:application/json\r\n";
			
			
			request += "\r\n";
			
			System.out.println("Printing request" );
			System.out.println(request);
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
			System.out.println(response);
		} finally {
			request = null;
			bufferReader.close();
			bufferWriter.close();
			socket.close();
		}
	}
}
