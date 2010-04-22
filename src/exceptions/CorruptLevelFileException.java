package exceptions;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

public class CorruptLevelFileException extends Exception {
    public CorruptLevelFileException() {
        super();
    }
    public CorruptLevelFileException(String m) {
        super(m);
    }
}
