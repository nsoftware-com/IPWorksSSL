(*
 * IPWorks SSL 2024 Delphi Edition - Sample Project
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
 *)

program htmlmailer;

uses
  Forms,
  htmlmailerf in 'htmlmailerf.pas' {FormHtmlmailer};

begin
  Application.Initialize;

  Application.CreateForm(TFormHtmlmailer, FormHtmlmailer);
  Application.Run;
end.


         