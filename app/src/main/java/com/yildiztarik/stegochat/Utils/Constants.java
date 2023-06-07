package com.yildiztarik.stegochat.Utils;

public class Constants {
    public static final String DEBUG_TAG="DEBUG_TARIK";
    public static final String DEBUG_TAG2="URI_CHECK";
    public static final String DEBUG_DECODE="ACTIVITY_CRYPTO";

    // intent extras
    public static final String USER_ID="USER_ID";
    public static final String OTHER_ID="OTHER_ID";
    public static final String IMAGE_URL="IMAGE_URL";

    //firebaseDatabase child constraints
    public static final String CHILD_USERS="Users";

    public static final String CHILD_FRIENDS="Friends";


    public static final String CHILD_MESSAGES="Messages";
    public static final String CHILD_MESSAGE_TEXT="MESSAGE_TEXT";
    public static final String CHILD_MESSAGE_DATE="DATE";
    public static final String CHILD_MESSAGE_TYPE="TYPE";
    public static final String CHILD_MESSAGE_TYPE_TEXT="TEXT_MSG";
    public static final String CHILD_MESSAGE_TYPE_IMG="IMG_MSG";
    public static final String CHILD_MESSAGE_FROM="FROM";




    public static final String CHILD_FRIEND_REQUEST="Friend_request";
    public static final String CHILD_FRIEND_REQ_TYPE="TYPE";
    //req types
    public static final String FRIEND_REQUEST_TYPE_SENT ="SENT";
    public static final String FRIEND_REQUEST_TYPE_RECEIVED ="RECEIVED";
    public static final String FRIEND_REQUEST_TYPE_ACCEPTED ="FRIENDS";
    public static final String FRIEND_REQUEST_TYPE_BLOCKED ="BLOCKED";

    //friendREq status

    public static final int FRIEND_REQ_RECEIVED=0;
    public static final int FRIEND_REQ_SENT=1;
    public static final int FRIEND_REQ_NULL=-1;
    public static final int FRIENDS=2;

    //firebaseStorage
    public static final String S_CHILD_IMAGE_MESSAGES ="IMAGE_MESSAGES";



}
