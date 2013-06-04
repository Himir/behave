package br.gov.frameworkdemoiselle.behave.integration.alm.autenticator;


public class AutenticatorClientTests {

	public static void main(String[] args) {
		AutenticatorClient autenticator = new AutenticatorClient(9990, "localhost");
		autenticator.open();
		String user = autenticator.getUser();
		String password = autenticator.getPassword();
		System.out.println(user);
		System.out.println(password);
		autenticator.close();
	}
}
