import java.util.regex.Pattern;

public class Globals {
    public static final String DOMAIN = "gra.dittrich.pro";
    public static final int PORT = 7181;
    public static final int IDM_LENGTH = 8;
    public static final Pattern IDM_PATTERN = Pattern.compile("[a-z]{2}[0-9]{2}[a-z]{4}");
    public static final int MAX_FILE_SIZE = 1024 * 1024; // 1 MiB
    public static String FILE_PATH = "telemetry-data"; // can be changed via command line argument
    public static final String CRASH_IDENTIFIER = "crashing"; // note: has to be IDM_LENGTH characters long
    public static final String POISON_PILL = "POISON_PILL";
}
