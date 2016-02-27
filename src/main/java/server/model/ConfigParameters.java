package server.model;

/**
 * Class that describes parameters of configuration file.
 */
class  ConfigParameters {
    private boolean isGUI;
    private int     port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isGUI() {
        return isGUI;
    }

    public void setGUI(boolean GUI) {
        isGUI = GUI;
    }
}
