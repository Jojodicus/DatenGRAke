import java.io.File;
import java.io.IOException;

public class FileSaver {
    public static boolean createDirectory(String path) {
        // check if directory exists
        File directory = new File(path);
        if (directory.exists()) {
            return true;
        }

        // create directory
        return directory.mkdirs();
    }

    public static boolean saveFile(String idm, String data) {
        // create idm directory
        String idmPath = Globals.FILE_PATH + "/" + idm;
        if (!createDirectory(idmPath)) {
            return false;
        }

        // create file
        String filePath = idmPath + "/" + System.currentTimeMillis() + ".asm";
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // write data to file
        try {
            java.io.FileWriter fileWriter = new java.io.FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}