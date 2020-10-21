package net.runelite.client.plugins.chatbox.dax_path;


public class ServerResponse {


    private boolean success;


    private int code;


    private String contents;

    public ServerResponse(boolean success, int code, String contents) {
        this.success = success;
        this.code = code;
        this.contents = contents;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getContents() {
        return contents;
    }

//    @Override
//    public String toString() {
//        return new Gson().toJson(this);
//    }
}
