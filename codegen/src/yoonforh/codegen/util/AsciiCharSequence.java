/*
 * $Id: AsciiCharSequence.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
 *
 * Copyright (c) 2003 by Yoon Kyung Koo.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Yoon Kyung Koo("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Yoon Kyung Koo.
 */


package yoonforh.codegen.util;

/**
 * byte array form char sequence in US-ASCII encoding
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-02 10:58:35
 * @author   Yoon Kyung Koo
 */

public class AsciiCharSequence implements CharSequence {
    private byte[] bytes = null;

    public AsciiCharSequence(byte[] bytes) {
	this.bytes = bytes;
    }

    /**
     * Returns the length of this character sequence.  The length is the number
     * of 16-bit Unicode characters in the sequence. </p>
     *
     * @return  the number of characters in this sequence
     */
    public int length() {
	return bytes.length;
    }

    /**
     * Returns the character at the specified index.  An index ranges from zero
     * to <tt>length() - 1</tt>.  The first character of the sequence is at
     * index zero, the next at index one, and so on, as for array
     * indexing. </p>
     *
     * @param   index   the index of the character to be returned
     *
     * @return  the specified character
     *
     * @throws  IndexOutOfBoundsException
     *          if the <tt>index</tt> argument is negative or not less than
     *          <tt>length()</tt>
     */
    public char charAt(int index) {
	return (char) bytes[index];
    }

    /**
     * Returns a new character sequence that is a subsequence of this sequence.
     * The subsequence starts with the character at the specified index and
     * ends with the character at index <tt>end - 1</tt>.  The length of the
     * returned sequence is <tt>end - start</tt>, so if <tt>start == end</tt>
     * then an empty sequence is returned. </p>
     * 
     * @param   start   the start index, inclusive
     * @param   end     the end index, exclusive
     *
     * @return  the specified subsequence
     *
     * @throws  IndexOutOfBoundsException
     *          if <tt>start</tt> or <tt>end</tt> are negative,
     *          if <tt>end</tt> is greater than <tt>length()</tt>,
     *          or if <tt>start</tt> is greater than <tt>end</tt>
     */
    public CharSequence subSequence(int start, int end) {
	byte[] subBytes = new byte[end - start];
	System.arraycopy(bytes, start, subBytes, 0, end - start);
	return new AsciiCharSequence(subBytes);
    }

    /**
     * Returns a string containing the characters in this sequence in the same
     * order as this sequence.  The length of the string will be the length of
     * this sequence. </p>
     *
     * @return  a string consisting of exactly this sequence of characters
     */
    public String toString() {
	try {
	    return new String(bytes, "US-ASCII");
	} catch (java.io.UnsupportedEncodingException e) {
	    e.printStackTrace();
	    assert false : "US-ASCII is not supported";
	}

	return null;
    }
}
