package model;

import org.json.JSONObject;

public class Response {
    int status;
    JSONObject answer;

    public Response(int status, JSONObject answer) {
        this.status = status;
        this.answer = answer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JSONObject getAnswer() {
        return answer;
    }

    public void setAnswer(JSONObject answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", answer=" + answer +
                '}';
    }
}
