package Utils;

public class Attributes {
	public static String host;
	static String path;
	static String URL;

	/*
	 * @return the host
	 */
	public static String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public static void setHost(String host) {
		Attributes.host = host;
	}

	/**
	 * @return the path
	 */
	public static String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public static void setPath(String path) {
		Attributes.path = path;
	}

	/**
	 * @param url
	 */
	public static void analyzeURL(String url) {

		url = url.replaceAll("\'", "");
		url = url.replaceAll("\"", "");
		if (url.startsWith("www."))
			url = url.substring(4, url.length());
		else if (url.contains("://"))
			url = url.substring((url.indexOf(":") + 3), url.length());
		host = url.substring(0, url.indexOf("/"));
		path = url.substring((url.indexOf("/")), url.length());
		System.out.println("Printing host " + host);
		System.out.println("Printing path " + path);
	}
}
