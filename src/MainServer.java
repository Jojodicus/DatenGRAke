import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    public static void main(String[] args) {
        // parse (optinal) command line arguments
        if (args.length > 0) {
            Globals.FILE_PATH = args[0];
        }

        // check if file path exists, create if not
        if (!FileSaver.createDirectory(Globals.FILE_PATH)) {
            System.err.println("Could not create directory " + Globals.FILE_PATH);
            return;
        }

        // initialize crypto
        Crypto.init();

        // open server socket
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Globals.PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // ensure server socket is closed on shutdown (e.g. CTRL+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        // start worker pool
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Starting server with " + cores + " cores");
        ExecutorService executorService = Executors.newFixedThreadPool(cores);

        // main loop
        while (true) {
            // accept new connections
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            // relay to workers
            executorService.execute(new WorkerThread(socket));
        }
    }
}
