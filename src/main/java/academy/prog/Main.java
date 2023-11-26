package academy.prog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Enter your login: ");
			String login = scanner.nextLine();
			PrivateClientChat privateClientChat = new PrivateClientChat(login);
			privateClientChat.startRealtimeHistoryUpdate();
			privateClientChat.startChatting();
		} finally {
			scanner.close();
		}
	}
}
