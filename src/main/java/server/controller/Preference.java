package server.controller;

/**
 * Enum of preferences or results of preferences.
 * @author Veleri Rechembei
 * @version %I%, %G%
 */
public enum Preference {
    Authentication,
    Registration,
    MessageForAll,
    PrivateMessage,
    ActiveUsers,
    Ban,
    BanUsers,
    Edit,
    Remove,
    Close,
    Admin,
    UnBan,
    IncorrectValue,
    Stop,
    Successfully;

    /**
     * Method, that transforms String command to  preference.
     * @param command preference in String format.
     * @return preference of client message.
     */
    public static Preference fromString(String command) {
        if (command != null) {
            for (Preference preference : Preference.values()) {
                if (preference.name().equals(command)) {
                    return preference;
                }
            }

        }
        return null;
    }
}


