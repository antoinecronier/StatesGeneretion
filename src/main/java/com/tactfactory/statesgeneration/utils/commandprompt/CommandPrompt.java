package com.tactfactory.statesgeneration.utils.commandprompt;

import java.util.Scanner;

public class CommandPrompt {
	private static CommandPrompt instance = null;

	protected CommandPrompt() {
		// Exists only to defeat instantiation.
	}

	public static CommandPrompt getInstance() {
		if (instance == null) {
			instance = new CommandPrompt();
		}
		return instance;
	}

	private Scanner in = new Scanner(System.in);

	public int CallIntMessage(int valMin, int valMax,
			String message) {

		int result;

		do {
			System.out.println(message);
			while (!in.hasNextInt())
				in.next();

			result = in.nextInt();
		} while (result > valMax || result < valMin);

		return result;
	}

	public String CallStringMessage(String[] acceptable,
			String message) {
		String result;
		boolean isNOK = true;

		do {
			System.out.println(message);
			result = in.next();
			if (acceptable != null) {
				for (String string : acceptable) {
					if (result.equals(string)) {
						isNOK = false;
					}
				}
			}else{
				isNOK = false;
			}
		} while (isNOK);

		return result;
	}
}
