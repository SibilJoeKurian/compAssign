package Driver;

import java.io.IOException;
import java.net.UnknownHostException;

import Controller.Get;
import Controller.Help;
import Controller.Post;

public class Driver {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// controller will recieve the command such as get,post or help
		if (args.length>=2) {
			String controller = args[1];
			switch (controller) {
			case "help":
				Help help = new Help(args);
				help.helpDriver();
				break;
			case "get":
				Get get = new Get(args);
				get.getDriver();
				break;
			case "post":
				Post post = new Post(args);
				post.postDriver();
				break;
			default:
				System.out.println(Help.INVALID);
			}
		} else
			System.out.println(Help.INVALID);

	}
}
