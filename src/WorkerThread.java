import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class WorkerThread extends Thread {
    private Socket socket;

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // get input stream
            InputStream inputStream = socket.getInputStream();

            // read IdM
            byte[] idm = new byte[Globals.IDM_LENGTH];
            if (inputStream.read(idm) != Globals.IDM_LENGTH) {
                System.err.println("Could not read IdM");
                return;
            }
            String idmString = new String(idm);

            // check if IdM is valid
            if (!Globals.CRASH_IDENTIFIER.equals(idmString) && !Globals.IDM_PATTERN.matcher(idmString).matches()) {
                System.err.println("Invalid IdM: " + idmString);
                return;
            }

            // read rest of data (file contents)
            byte[] data = new byte[Globals.MAX_FILE_SIZE];
            int bytesRead = inputStream.read(data);
            String dataString = new String(data, 0, bytesRead);

            // save file
            if (!FileSaver.saveFile(idmString, dataString)) {
                System.err.println("Could not save file");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close socket
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
