<?php
/*
 * IPWorks SSL 2024 PHP Edition - Sample Project
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
require_once('../include/ipworksssl_pop.php');
require_once('../include/ipworksssl_smtp.php');
require_once('../include/ipworksssl_htmlmailer.php');
require_once('../include/ipworksssl_const.php');
?>
<?php
class MyPOP extends IPWorksSSL_POP{
  function FireSSLServerAuthentication($param) {
    $param['accept'] = true;
    return $param;
  }
  function FirePITrail($param) {
    // echo $param['direction'] . ": " . $param['message'] . "\n";
  }
}

$pop = new MyPOP();

if ($argc < 4) {
  echo "Usage: php pop_email_client.php [options] server user password\n";
  echo "Options:\n";
  echo "  -ssl       SSL mode to use: 0=automatic (default), 1=implicit, 2=explicit\n";
  echo "  -port      the port of a POP mail server (default 995) \n";
  echo "  server     the name or address of a POP mail server\n";
  echo "  user       the user identifier for the mailbox\n";
  echo "  password   the password for the mailbox user\n\n";
  echo "Example: php pop_email_client.php -ssl 0 -port 995 mypopserver myusername mypassword\n";
} else {
  try {
    $pop->setMailServer($argv[$argc - 3]);
    $pop->setUser($argv[$argc - 2]);
    $pop->setPassword($argv[$argc - 1]);
    $pop->setMailPort(995);

    for ($i = 1; $i < $argc - 3; $i++) {
      if (str_starts_with( $argv[$i], "-" )) {
        if ( $argv[$i] == "-ssl" ) {
          if ( in_array($argv[++$i], array(0, 1, 2) ) ) {
            $pop->setSSLStartMode($argv[i]); //none
          }
        }
        else if ( $argv[$i] == "-port" ) {
          if ( ctype_digit($argv[++$i]) ) { // Check conversion to integer
          $pop->setMailPort($argv[$i]);
          }
        }
      }
    }

    echo "Attempting to connect:\n";
    $pop->doConnect();

    $msgCount = $pop->getMessageCount();
    echo "Found $msgCount messages.\n";

    echo "\nRetrieving message list from POP server...\n";

    $pop->setMaxLines(20); //only headers

    $startidx = 1; // Starting index of the max 5 to display at a time
    $input = "";
    $doDisplay = true;

    while (true) {
      if ($doDisplay) {
        echo "Displaying messages " . $startidx . "-" . min($startidx+4, $msgCount) . " of $msgCount\n--------------------\n";
        for ($i = $startidx; $i <= min($startidx+4, $msgCount); $i++) {
          $pop->setMessageNumber($i);
          $pop->doRetrieve();
          echo "Index:   $i\n";
          echo "Subject: " . $pop->getMessageSubject() . "\n";
          echo "From:    " . $pop->getMessageFrom() . "\n";
          echo "Date:    " . $pop->getMessageDate() . "\n";
          echo "--------------------\n";
        }
        echo "Type next or back to navigate. Type quit to exit.\n";
      }

      echo "pop> ";
      $input = trim(fgets(STDIN));

      $doDisplay = false;

      if ($input == "next") {
        if ($msgCount > $startidx+4) {
          $startidx += 5;
          $doDisplay = true;
        } else {
          $input = "";
          echo "Already at end.\n";
        }
      } else if ($input == "back") {
        if ($startidx != 1) {
          $startidx -= 5;
          $doDisplay = true;
        } else {
          $input = "";
          echo "Already at beginning.\n";
        }
      } else if ($input == "quit" || $input == "exit") {
        break;
      }
    }
  } catch (Exception $e) {
  echo "Error: " . $e->getMessage() . "\n";  
  }

  $pop->doDisconnect();
  echo "Disconnected from POP server.\n";
}
?>