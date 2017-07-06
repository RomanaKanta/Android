package com.smartmux.couriermoc.api;


/**
 * class ResponseHandle to support description from HttpReponse
 * @author lent5
 *
 */
public class ResponseHandle {
    public static final int CODE_200_OK = 200;
    public static final int CODE_400 = 400;
    public static final int CODE_404 = 404;
    public static final int CODE_403 = 403;
    public static final int CODE_406 = 406;
    public static final int CODE_INVALID_PARAMS = 999;
    public static final int CODE_HTTP_FAIL = 900;
    public static final int CODE_FILE_COMPRESS_FAIL = 905;

    /** Constructor Reponse */
    public static class Response{
        public int errorCode = 200;
        public String decription = "";

        /** Contructor Response */
        public Response() {
        }

        /**
         * Contructor Response 
         * @param errorCode
         * @param decription
         */
        public Response(int errorCode, String decription) {
            this.errorCode = errorCode;
            this.decription = decription;
        }
    }

    
}
