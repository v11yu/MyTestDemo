package greaman.echo;

import java.io.IOException;

import greaman.JobServer;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanServer;

public class EchoServer {
	public static final String ECHO_HOST = "localhost";
	public static final int ECHO_PORT = 4730;

	public static void start() throws IOException {
		Gearman gearman = Gearman.createGearman();
		try {
			GearmanServer server = gearman.startGearmanServer(ECHO_PORT);

		} catch (IOException ioe) {
			gearman.shutdown();
			throw ioe;
		}
	}

	public static void main(String[] args) throws IOException {
		EchoServer.start();
	}
}
