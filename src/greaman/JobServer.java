package greaman;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanServer;

public class JobServer {
	private static final String HOST = "127.0.0.1";
	private static final Integer POST = 4730;
	public static void start(){
		/*
         * Create a Gearman instance
         */
        Gearman gearman = Gearman.createGearman();

        /*
         * Create a new gearman client.
         * 
         * The client is used to submit requests the job server.
         */
        GearmanClient client = gearman.createGearmanClient();

        /*
         * Create the job server object. This call creates an object represents
         * a remote job server.
         * 
         * Parameter 1: the host address of the job server.
         * Parameter 2: the port number the job server is listening on.
         * 
         * A job server receives jobs from clients and distributes them to
         * registered workers.
         */
        GearmanServer server = gearman.createGearmanServer(HOST, POST);
        
        //client.addServer(server);
        //gearman.startGearmanServer(port)
	}
	public static void main(String[] args) {
		JobServer.start();
	}
}
