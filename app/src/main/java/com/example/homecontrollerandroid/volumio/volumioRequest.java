package com.example.homecontrollerandroid.volumio;

public class volumioRequest {

    private static final String VOLUMIO_GET_STATE = "/api/getState";
    public static final String STOP = "/api/v1/commands/?cmd=stop";
    public static final String PLAY = "/api/v1/commands/?cmd=play";
    public static final String GET_RADIOS = "/api/v1/browse?uri=radio/favourites";

    private static StringBuffer volumio_address = new StringBuffer();

    public static String getINFO(String address){
        if(volumio_address.length() !=0)
            volumio_address.delete(0,volumio_address.length());
        volumio_address.append(address);
        volumio_address.append(VOLUMIO_GET_STATE);

       return volumio_address.toString();
    }







}
