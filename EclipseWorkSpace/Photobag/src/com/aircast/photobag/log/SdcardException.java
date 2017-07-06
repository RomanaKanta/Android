package com.aircast.photobag.log;

public class SdcardException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String mMessageString = "ERROR: sdcard is invalid!";
    
    public SdcardException(String msgString) {
        mMessageString = msgString;
    }

    public SdcardException() {
    }

    @Override
    public String toString() {
        return mMessageString;
    }
}
