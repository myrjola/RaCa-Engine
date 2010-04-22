package exceptions;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */
public class CorruptSettingsException extends Exception {
    public CorruptSettingsException() {
        super();
    }

    public CorruptSettingsException(String message) {
        super(message);
    }
}
