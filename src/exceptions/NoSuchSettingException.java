package exceptions;

/*
* © Copyright 2010 Martin Yrjölä. All Rights Reserved.
* See COPYING for information on licensing.
*/
public class NoSuchSettingException extends Exception {
    public NoSuchSettingException() {
        super();
    }

    public NoSuchSettingException(String message) {
        super(message);
    }
}
