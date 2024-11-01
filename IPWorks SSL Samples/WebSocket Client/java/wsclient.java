/*
 * IPWorks SSL 2024 Java Edition - Sample Project
 *
 * This sample project demonstrates the usage of IPWorks SSL in a 
 * simple, straightforward way. It is not intended to be a complete 
 * application. Error handling and other checks are simplified for clarity.
 *
 * www.nsoftware.com/ipworksssl
 *
 * This code is subject to the terms and conditions specified in the 
 * corresponding product license agreement which outlines the authorized 
 * usage and restrictions.
 */

import java.io.*;
import java.util.Scanner;
import ipworksssl.*;

public class wsclient {
	private static WSClient websocketclient;
	private static boolean received;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String url = "wss://echo.websocket.events/.ws";
		String command;
		String response;
		try {
			websocketclient = new WSClient();
			System.out.println("*******************************************************************************************************************");
			System.out.println("* This is a demo to show how to connect to a remote WebSocket server, send data, and receive the echoed response.*");
			System.out.println("******************************************************************************************************************\n");

			websocketclient.addWSClientEventListener(new DefaultWSClientEventListener() {
				public void dataIn(WSClientDataInEvent e) {
					System.out.println("Received: " + new String(e.text));
					received = true;
				}

				public void connected(WSClientConnectedEvent e) {
					System.out.println("Connected.");
				}

				public void disconnected(WSClientDisconnectedEvent e) {
					System.out.println("Disconnected.");
				}

				public void SSLServerAuthentication(WSClientSSLServerAuthenticationEvent e) {
					e.accept = true;
				}
			});

			System.out.print("Connect to " + url + "? (y/n): ");
			response = scanner.nextLine();
			if (response.charAt(0) == 'n') {
				System.out.println("Please enter the URL: ");
				url = scanner.nextLine();
			}
			websocketclient.setTimeout(10);
			websocketclient.connectTo(url);
			do {
				System.out
						.print("\r\nPlease input command: \r\n1) Send Data \r\n2) Exit\r\n>");
				command = scanner.nextLine();
				if (command.charAt(0) == '1') {
					received = false;
					System.out.print("Please input sending data: ");
					websocketclient.sendText(scanner.nextLine());
					while(!received){websocketclient.doEvents();}; //To ensure we get a response before asking for more input.
				} else if (command.charAt(0) == '2')
					break;
				else
					System.out.println("Invalid command. Please try again.");		
			} while (true);
			scanner.close();
			websocketclient.disconnect();
		} catch (IPWorksSSLException ex) {
			System.out.println("\n\nError: " + ex.getMessage() + "\n");
			System.exit(ex.getCode());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
class ConsoleDemo {
  private static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

  static String input() {
    try {
      return bf.readLine();
    } catch (IOException ioe) {
      return "";
    }
  }
  static char read() {
    return input().charAt(0);
  }

  static String prompt(String label) {
    return prompt(label, ":");
  }
  static String prompt(String label, String punctuation) {
    System.out.print(label + punctuation + " ");
    return input();
  }
  static String prompt(String label, String punctuation, String defaultVal) {
      System.out.print(label + " [" + defaultVal + "]" + punctuation + " ");
      String response = input();
      if (response.equals(""))
        return defaultVal;
      else
        return response;
  }

  static char ask(String label) {
    return ask(label, "?");
  }
  static char ask(String label, String punctuation) {
    return ask(label, punctuation, "(y/n)");
  }
  static char ask(String label, String punctuation, String answers) {
    System.out.print(label + punctuation + " " + answers + " ");
    return Character.toLowerCase(read());
  }

  static void displayError(Exception e) {
    System.out.print("Error");
    if (e instanceof IPWorksSSLException) {
      System.out.print(" (" + ((IPWorksSSLException) e).getCode() + ")");
    }
    System.out.println(": " + e.getMessage());
    e.printStackTrace();
  }

  /**
   * Takes a list of switch arguments or name-value arguments and turns it into a map.
   */
  static java.util.Map<String, String> parseArgs(String[] args) {
    java.util.Map<String, String> map = new java.util.HashMap<String, String>();
    
    for (int i = 0; i < args.length; i++) {
      // Add a key to the map for each argument.
      if (args[i].startsWith("-")) {
        // If the next argument does NOT start with a "-" then it is a value.
        if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
          // Save the value and skip the next entry in the list of arguments.
          map.put(args[i].toLowerCase().replaceFirst("^-+", ""), args[i + 1]);
          i++;
        } else {
          // If the next argument starts with a "-", then we assume the current one is a switch.
          map.put(args[i].toLowerCase().replaceFirst("^-+", ""), "");
        }
      } else {
        // If the argument does not start with a "-", store the argument based on the index.
        map.put(Integer.toString(i), args[i].toLowerCase());
      }
    }
    return map;
  }
}



