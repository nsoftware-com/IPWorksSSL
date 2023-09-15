/*
 * IPWorks SSL 2022 Java Edition - Sample Project
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

public class ldap extends ConsoleDemo {

	public static void main(String[] args) {

		String binddn = "";
		String password = "";
		String searchdn = "";

		if (args.length < 1) {
			System.out.println("usage: ldap server binddn password");
			System.out.println("Options:");
			System.out.println("  -s        the dn used as the base for operations (optional)");
			System.out.println("  -u        the dn used for authentication (optional)");
			System.out.println("  -p				the password used to authenticate to the server (optional)");
			System.out.println("  server    the name or address of the LDAP server");
			System.out.println("\r\nExample: ldap -s CN=Users,DC=Domain -u DOMAIN\\Username -p password server");
		} else {
			Ldap ldap1 = new Ldap();
			try {
				ldap1.addLdapEventListener(new DefaultLdapEventListener() {

					public void SSLServerAuthentication(LdapSSLServerAuthenticationEvent arg0) {
						arg0.accept = true; // this will trust all certificates and it is not recommended for production use
					}

					public void error(LdapErrorEvent e) {
						System.out.println("\nError " + e.errorCode + ": " + e.description);
					}

					public void result(LdapResultEvent e) {
						System.out.println(e.resultCode + "  " + e.resultDescription);
					}

					public void searchComplete(LdapSearchCompleteEvent e) {
						System.out.println(e.resultCode + "  " + e.resultDescription);
					}

					public void searchResult(LdapSearchResultEvent e) {
						System.out.println(e.DN);
					}
				});

				for (int i = 0; i < args.length; i++) {
					if (args[i].startsWith("-")) {
						if (args[i].equals("-s"))
							searchdn = args[i + 1]; // args[i+1] corresponds to the value of argument [i]
						if (args[i].equals("-u"))
							binddn = args[i + 1];
						if (args[i].equals("-p"))
							password = args[i + 1];
					}
				}

				ldap1.setServerName(args[args.length - 1]);
				if (binddn.length() > 0) {
					ldap1.setDN(binddn);
					ldap1.setPassword(password);
					ldap1.bind();
				}
				do {
					ldap1.setLDAPVersion(3);
					ldap1.setSearchSizeLimit(100);
					ldap1.getAttributes().add(new LDAPAttribute("mail", ""));
					String cn = prompt("Search for");
					System.out.println("Sending search request...");
					ldap1.setTimeout(10); // synchronous operation - to use asynchronous, set timeout to zero
					ldap1.setDN(searchdn);
					ldap1.search("CN=" + cn);
				} while (ask("Perform another search") != 'n');
			} catch (Exception e) {
				displayError(e);
			}
			try {
				ldap1.unbind();
			} catch (Exception e) {
				displayError(e);
				System.exit(1);
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

  static String prompt(String label, String punctuation, String defaultVal)
  {
	System.out.print(label + " [" + defaultVal + "] " + punctuation + " ");
	String response = input();
	if(response.equals(""))
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
}



