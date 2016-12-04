package com.sf.banbanle.bean;

/**
 * Created by mac on 16/12/3.
 */

public class BaiduSingleDevicePushBean {

    private long request_id;

    private Response_params response_params;

    private String error_code;
    private String error_msg;

    public void setRequest_id(long request_id) {
        this.request_id = request_id;
    }

    public long getRequest_id() {
        return this.request_id;
    }

    public void setResponse_params(Response_params response_params) {
        this.response_params = response_params;
    }

    public Response_params getResponse_params() {
        return this.response_params;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    @Override
    public String toString() {
        return "BaiduSingleDevicePushBean{" +
                "request_id=" + request_id +
                ", response_params=" + response_params +
                ", error_code='" + error_code + '\'' +
                ", error_msg='" + error_msg + '\'' +
                '}';
    }

    class Response_params {
        private String msg_id;

        private long send_time;

        public void setMsg_id(String msg_id) {
            this.msg_id = msg_id;
        }

        public String getMsg_id() {
            return this.msg_id;
        }

        public void setSend_time(long send_time) {
            this.send_time = send_time;
        }

        public long getSend_time() {
            return this.send_time;
        }

        @Override
        public String toString() {
            return "Response_params{" +
                    "msg_id=" + msg_id +
                    ", send_time=" + send_time +
                    '}';
        }
    }
}
