import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            OutputStream outputStream = socket.getOutputStream();

            // handshake
            Crypto.handshake(inputStream, outputStream);

            // read encypted data
            byte[] encryptedData = new byte[Globals.MAX_LENGTH];
            int encryptedDataLength = inputStream.read(encryptedData);

            // decrypt data
            byte[] data = Crypto.decrypt(encryptedData, encryptedDataLength);

            // convert to string
            String dataString = new String(data);

            // read IdM
            String idmString = dataString.substring(0, Globals.IDM_LENGTH);

            // check if IdM is valid
            if (!Globals.CRASH_IDENTIFIER.equals(idmString) && !Globals.IDM_PATTERN.matcher(idmString).matches()) {
                System.err.println("Invalid IdM: " + idmString);
                return;
            }

            // read rest of data (file contents)
            String restString = dataString.substring(Globals.IDM_LENGTH);

            // save file
            if (!FileSaver.saveFile(idmString, restString)) {
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
