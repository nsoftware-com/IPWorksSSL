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
import ipworksssl.*;

public class echoclient extends ConsoleDemo {

	private static SSLClient sslclient;

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("usage: echoclient server port");
			System.out.println("");
			System.out.println("  server  the address of the remote host");
			System.out.println("  port    the TCP port in the remote host");
			System.out.println("\r\nExample: echoclient localhost 777");
		} else {
			try {
				sslclient = new SSLClient();
				System.out.println("*************************************************************************************************************");
				System.out.println("* This is a demo to show how to connect to a remote echo server, send data, and receive the echoed response.*");
				System.out.println("*************************************************************************************************************\n");
				sslclient.addSSLClientEventListener(new DefaultSSLClientEventListener() {
					public void SSLServerAuthentication(SSLClientSSLServerAuthenticationEvent e) {
						e.accept = true;
					}

					public void connected(SSLClientConnectedEvent e) {
						System.out.println("\r\n" + sslclient.getRemoteHost() + " has connected.");
						System.out.print(">");
					}

					public void dataIn(SSLClientDataInEvent e) {
						System.out.println("Received " + new String(e.text) + " from " + sslclient.getRemoteHost());
						System.out.print(">");
					}

					public void disconnected(SSLClientDisconnectedEvent e) {
						System.out.println("Disconnected " + e.description + " from " + sslclient.getRemoteHost() + ".");
						System.out.print(">");
					}
				});
				sslclient.setRemoteHost(args[0]);
				sslclient.setRemotePort(Integer.parseInt(args[1]));
				sslclient.connect();
				sslclient.doEvents();
				if (sslclient.isConnected()) {
					System.out.println("\r\nPlease input command: \r\n- 1 Send Data \r\n- 2 Exit");
					System.out.print(">");

					while (true) {
						if (System.in.available() > 0) {
							String command = String.valueOf(read());
							if ("1".equals(command)) {
								sslclient.sendText(prompt("Please input sending data") + "\r\n");
								System.out.println("Sending success.");
								System.out.println("\r\nPlease input command: \r\n- 1 Send Data \r\n- 2 Exit");
								System.out.print(">");
							} else if ("2".equals(command)) {
								break;
							}
						}
					}
					sslclient.disconnect();
				} else {
					System.out.println("\r\nCan't connect to server '" + args[0] + ":" + args[1] + "'");
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
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



