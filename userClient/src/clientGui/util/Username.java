package clientGui.util;

public class Username {
    // Custom delimiter to wrap the username
    private static final String DELIMITER = "<>";

    // Method to wrap a username with the custom delimiter, escaping any occurrences of the delimiter
    public static String wrap(String username) {
        String escapedUsername = username.replaceAll(DELIMITER, "\\\\" + DELIMITER);
        return DELIMITER + escapedUsername + DELIMITER;
    }

    // Method to unwrap a username using the custom delimiter, unescaping any escaped delimiters
    public static String unwrap(String wrappedUsername) {
        if (wrappedUsername.startsWith(DELIMITER) && wrappedUsername.endsWith(DELIMITER)) {
            String content = wrappedUsername.substring(DELIMITER.length(), wrappedUsername.length() - DELIMITER.length());
            return content.replaceAll("\\\\" + DELIMITER, DELIMITER);
        } else {
            throw new IllegalArgumentException("Invalid wrapped username format.");
        }
    }

    public static void main(String[] args) {
        String username = "user<><user>";
        String wrappedUsername = wrap(username);
        String unwrappedUsername = unwrap(wrappedUsername);
        System.out.println(unwrappedUsername);
    }
}
