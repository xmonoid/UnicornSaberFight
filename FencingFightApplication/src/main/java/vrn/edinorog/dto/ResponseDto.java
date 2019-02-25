package vrn.edinorog.dto;

import java.util.HashMap;

public class ResponseDto extends HashMap<String, Object> {

    private ResponseDto() {}

    public static ResponseDto create() {
        return new ResponseDto();
    }

    public ResponseDto data(Object data) {
        this.put("data", data);
        return this;
    }

    public ResponseDto message(String message) {
        this.put("message", message);
        return this;
    }

    public Object getData() {
        return this.get("data");
    }

    public String getMessage() {
        return (String) this.get("message");
    }

}