import java.net.*;
import java.io.*;
import java.util.*;

public class Bitlab {
	private static final String TERMINATE = "Exit";
	static String login;
	private static String password;
	static volatile boolean finished = false;
	static User user_logged_in;
	static boolean value1 = false;
	static boolean value2 = false;
	public static String valueFromUser = "";
	public static boolean flag_send = false;

	public static void main(String[] args) 
	{
		Manage.addUsers();//deklaracja i definicja wstêpnych danych
		if (args.length != 2)
			System.out.println("Wymagane 2 argumenty: <adres_rozgloszeniowy> <numer_portu>");
		else {
			try {
				InetAddress group = InetAddress.getByName(args[0]);//reprezentuje ip hosta
				//tworzy obiekt na podstawie nazwy hosta
				int port = Integer.parseInt(args[1]);
				Scanner sc = new Scanner(System.in);
				//autoryzacja
				System.out.print("Logowanie\n");
				do {
					System.out.print("\nLogin: ");
					login = sc.nextLine();
					if (Manage.isExistLogin(login))
						value1 = true;
				} while (value1 == false);

				do {
					System.out.print("\nHas³o: ");
					password = sc.nextLine();
					if (Manage.isValidPassword(login, password)) {
						value2 = true;
						user_logged_in = Manage.getUser(login);
					}
				} while (value2 == false);

				MulticastSocket socket = new MulticastSocket(port);//gniazdo
				socket.setTimeToLive(0);
				socket.joinGroup(group);//dolaczenie 'uzytkownika' do danej grupy/adresu ip
				
				Thread t = new Thread(new ReadThread(socket, group, port));
				//klasa ReadThread zdefiniowana ponizej				
				//w¹tek sluzacy do odczytu otrzymanych informacji od innych uzytkownikow
				//wysylanych do bie¿¹cej 'grupy'
				t.start();
				while (true) {
					String message = Manage.doSomething();
					if (message.equalsIgnoreCase(Bitlab.TERMINATE)) {//zamkniêcie po³¹czenia i aplikacji
						finished = true;
						byte[] buffer = message.getBytes();
						DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
						socket.send(datagram);
						socket.leaveGroup(group);
						socket.close();
						break;
					}
					byte[] buffer = message.getBytes();
					DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);				
					//konstruuje pakiet datagramów do wysy³ania pakietów o danej d³ugoœci do 
					//okreœlonego numeru portu na okreœlonym hoœcie
					socket.send(datagram);//wyslanie datagramu (message)
				}
			} catch (SocketException se) {
				System.out.println("Wyst¹pi³ b³¹d podczas tworzenia gniazda.");
				se.printStackTrace();
			} catch (IOException ie) {
				System.out.println("Wyst¹pi³ b³¹d podczas odczytu/zapisu z/do gniazda.");
				ie.printStackTrace();
			}
		}
	}
}

class ReadThread implements Runnable {
	private MulticastSocket socket;
	private InetAddress group;
	private int port;
	private static final int MAX_LEN = 1000;

	ReadThread(MulticastSocket socket, InetAddress group, int port) {
		this.socket = socket;
		this.group = group;
		this.port = port;
	}

	@Override
	public void run() {
		while (!Bitlab.finished) {
			byte[] buffer = new byte[ReadThread.MAX_LEN];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
			String message;
			try {
				socket.receive(datagram);
				message = new String(buffer, 0, datagram.getLength(), "UTF-8");
				// wyslal user1.login user2.login value
					if (message.contains("wyslal")) {
						if (message.contains(Bitlab.user_logged_in.getLogin()))
						{
							Manage.updateBalance(message);
						}
						else message = "";
					}
					if (!message.contains("wyslal")) {
						message = "";
					}
				
			} catch (IOException e) {
				System.out.println("Gniazdo zosta³o zamkniête!");
			}
		}
	}
}