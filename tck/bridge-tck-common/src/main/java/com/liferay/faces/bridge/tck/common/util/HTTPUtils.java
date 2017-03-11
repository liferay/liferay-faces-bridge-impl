/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.tck.common.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;


/**
 * This class contains various utility methods for encoding and decoding URIs <code>String</code> using the set of
 * characters allowed in a URI, as defined in <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396 - "Uniform Resource
 * Identifiers (URI): Generic Syntax"</a>. The method does not suffer from the limitations of the <code>
 * java.net.URLEncoder</code> in that it provides control over the character encoding used for character to byte
 * conversions and is capable of encoding characters that are encoded by a sequence of several bytes.
 *
 * <p>To convert a <code>String</code>, each character is examined in turn:
 *
 * <ul>
 *   <li>The ASCII characters '<code>a</code>' through '<code>z</code>', '<code>A</code>' through '<code>Z</code>', and
 *     '<code>0</code>' through '<code>9</code>' remain the same.</li>
 *   <li>Additional 'mark characters', i.e. '<code>-</code>', '<code>_</code>', '<code>.</code>', '<code>!</code>',
 *     '<code>~</code>', '<code>*</code>', '<code>'</code>' , '<code>(</code>', '<code>)</code>', remain the same.</li>
 *   <li>The space character '<code>&nbsp;</code>' is converted into a plus sign '<code>+</code>'.</li>
 *   <li>All other characters are converted into a sequence of bytes using the specified character encoding, and each of
 *     these bytes is then encoded as a string "<code>%<i>xy</i></code>", where <i>xy</i> is the two-digit hexadecimal
 *     representation of the byte value.</li>
 * </ul>
 */
public class HTTPUtils {

	// An array mapping characters to booleans. True means the character can be
	// included without Hex encoding
	private static final boolean[] sValidChar = new boolean[128];

	// An array mapping 4 bit values to uppercase Hex digits to speed things up
	// a bit
	private static final char[] sHexLookup = new char[16];

	// Lookup arrays used during base64 encoding/decoding
	private static char[] sCharLookup;
	private static byte[] sByteLookup;

	// Static initializer block
	static {

		// First initialize sValidChar
		// Allow lower case alphabetic characters
		for (char c = 'a'; c <= 'z'; c++) {
			sValidChar[c] = true;
		}

		// Allow upper case alphabetic characters
		for (char c = 'A'; c <= 'Z'; c++) {
			sValidChar[c] = true;
		}

		// Allow numeric characters
		for (char c = '0'; c <= '9'; c++) {
			sValidChar[c] = true;
		}

		// Allow various 'mark' characters
		sValidChar['-'] = true;
		sValidChar['_'] = true;
		sValidChar['.'] = true;
		sValidChar['~'] = true;
		sValidChar['\''] = true;

		// Also initialize the sHexLookup table
		for (byte b = 0; b < 16; b++) {
			sHexLookup[b] = Character.toUpperCase(Character.forDigit(b, 16));
		}

		// Populate arrays for base64 encoding/decoding
		sCharLookup = new char[64];
		sByteLookup = new byte[127];

		byte i = 0;
		char c;

		for (c = 'A'; c <= 'Z'; c++) {
			sCharLookup[i] = c;
			sByteLookup[c] = i++;
		}

		for (c = 'a'; c <= 'z'; c++) {
			sCharLookup[i] = c;
			sByteLookup[c] = i++;
		}

		for (c = '0'; c <= '9'; c++) {
			sCharLookup[i] = c;
			sByteLookup[c] = i++;
		}

		sCharLookup[i] = '+';
		sByteLookup['+'] = i++;
		sCharLookup[i] = '/';
		sByteLookup['/'] = i;

		// Signal EOF with -1 (It's safe because the other bytes are only 6
		// bits)
		sByteLookup['='] = -1;
	}

	/**
	 * Decodes the supplied base 64 encoded string into its original byte array, using the standard base 64 decoding
	 * algorithm.
	 *
	 * @param   string  The base 64 encoded string to decode
	 *
	 * @return  The decoded byte array
	 */
	public static byte[] base64Decode(String string) {

		// Blocks of encoded data may have newline characters which
		// must be ignored
		string = string.replace("\n", "");
		string = string.replace("\r", "");

		char[] chars = string.toCharArray();
		int i = 0;
		int charsToWrite = chars.length;
		ByteArrayOutputStream buff = new ByteArrayOutputStream(chars.length);
		byte[] b = new byte[4];

		while (charsToWrite >= 4) {

			try {

				// If we can't get one complete byte, then something has gone
				// wrong
				if (((b[0] = sByteLookup[chars[i++]]) == -1) || ((b[1] = sByteLookup[chars[i++]]) == -1)) {
					throw new IllegalArgumentException(string);
				}

				buff.write((b[0] << 2) | (b[1] >>> 4));

				if ((b[2] = sByteLookup[chars[i++]]) == -1) {
					charsToWrite -= 4;

					break;
				}

				buff.write((b[1] << 4) | (b[2] >>> 2));

				if ((b[3] = sByteLookup[chars[i++]]) == -1) {
					charsToWrite -= 4;

					break;
				}

				buff.write((b[2] << 6) | b[3]);
				charsToWrite -= 4;
			}

			// If any of the byte lookups go out of bounds, this can't be a
			// valid base 64 encoding
			catch (ArrayIndexOutOfBoundsException aiobe) {
				throw new IllegalArgumentException(string);
			}
		}

		// If we have any odd characters at the end, then something has gone
		// wrong
		if (charsToWrite > 0) {
			throw new IllegalArgumentException(string);
		}

		return buff.toByteArray();
	}

	/**
	 * Base64 encodes the supplied bytes array, using the standard base 64 encoding algorithm.
	 *
	 * @param   bytes  The byte array to encode
	 *
	 * @return  The base 64 encoded string representing the byte array
	 */
	public static String base64Encode(byte[] bytes) {

		/*
		 * The base 64 encoding algorithm works as follows:
		 *
		 * Divide the input bytes stream into blocks of 3 bytes. Divide the 24 bits of a 3-byte block into 4 groups of 6
		 * bits. Map each group of 6 bits to 1 printable character, based on the 6-bit value. If the last 3-byte block
		 * has only 1 byte of input data, pad 2 bytes of zero (\x0000). After encoding it as a normal block, override
		 * the last 2 characters with 2 equal signs (==), so the decoding process knows 2 bytes of zero were padded. If
		 * the last 3-byte block has only 2 bytes of input data, pad 1 byte of zero (\x00). After encoding it as a
		 * normal block, override the last 1 character with 1 equal signs (=), so the decoding process knows 1 byte of
		 * zero was padded.
		 */

		int i = 0;
		int bytesToWrite = bytes.length;

		StringBuilder buff = new StringBuilder(bytes.length * 4 / 3);

		while (bytesToWrite >= 3) {
			buff.append(sCharLookup[(bytes[i] >>> 2) & 63]);

			buff.append(sCharLookup[((bytes[i] & 3) << 4) + ((bytes[i + 1] >>> 4) & 15)]);
			buff.append(sCharLookup[((bytes[i + 1] & 15) << 2) + ((bytes[i + 2] >>> 6) & 3)]);
			buff.append(sCharLookup[bytes[i + 2] & 63]);

			bytesToWrite -= 3;
			i = i + 3;
		}

		switch (bytesToWrite) {

		case 2: {
			buff.append(sCharLookup[(bytes[i] >>> 2) & 63]);
			buff.append(sCharLookup[((bytes[i] & 3) << 4) + ((bytes[i + 1] >>> 4) & 15)]);
			buff.append(sCharLookup[((bytes[i + 1] & 15) << 2)]);
			buff.append('=');

			break;
		}

		case 1: {
			buff.append(sCharLookup[(bytes[i] >> 2) & 63]);
			buff.append(sCharLookup[(bytes[i] & 3) << 4]);
			buff.append('=');
			buff.append('=');
		}
		}

		return buff.toString();
	}

	/**
	 * Build a fully qualified URL from the pieces provided. This method does not call any encoding methods, so the path
	 * argument has to be properly encoded.
	 *
	 * @param   scheme  server protocol.
	 * @param   host    server name.
	 * @param   port    server port.
	 * @param   path    properly encoded relative URL.
	 *
	 * @throws  IllegalArgumentException  if scheme, host or port is null.
	 */
	public static String buildUrlAsString(String scheme, String host, int port, String path) {

		if ((scheme == null) || scheme.equals("") || (host == null) || host.equals("") || (port == 0)) {
			throw new IllegalArgumentException("Cannot build a URL using following scheme: " + scheme + " host: " +
				host + " port: " + port + " path: " + path);
		}

		StringBuilder url = new StringBuilder(200);

		url.append(scheme).append("://").append(host);

		// check for protocol default port number
		if ((scheme.equalsIgnoreCase("http") && (port != 80)) || (scheme.equalsIgnoreCase("https") && (port != 443))) {
			url.append(":").append(port);
		}

		if (path != null) {
			url.append(path);
		}

		return url.toString();
	}

	/**
	 * Decodes an encoded String.
	 *
	 * @param      value     <code>String</code> to be translated.
	 * @param      encoding  the Java alias for the character encoding to be used to convert byte sequences into
	 *                       characters(e.g. <code>"UTF8"</code>).
	 *
	 * @return     the translated <code>String</code>.
	 *
	 * @exception  UnsupportedEncodingException  if the given encoding is not a recognised character encoding.
	 */
	public static String decode(String value, String encoding) throws UnsupportedEncodingException {

		// optimization!
		// determine if decoding is actually required
		if (!needsDecoding(value)) {
			return value;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int charIndex = 0;
		int length = value.length();

		while (charIndex < length) {
			char aChar = value.charAt(charIndex);

			if (aChar == '%') {

				do {
					int byteVal = (Character.digit(value.charAt(charIndex + 1), 16) << 4) |
						Character.digit(value.charAt(charIndex + 2), 16);
					bos.write(byteVal);
					charIndex += 3;
				}
				while ((charIndex < length) && ((aChar = value.charAt(charIndex)) == '%'));
			}
			else {

				if (aChar == '+') {
					bos.write(' ');
				}
				else {
					bos.write(aChar);
				}

				charIndex++;
			}
		}

		return bos.toString(encoding);
	}

	/**
	 * Decodes an encoded UTF8 String.
	 *
	 * @param   value  <code>String</code> to be translated.
	 *
	 * @return  the translated <code>String</code>.
	 */
	public static String decodeUTF(String value) {
		String decodedValue = null;

		try {
			decodedValue = decode(value, "UTF8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO - error handling
			// should never happen for this method because the
			// character encoding is constant
		}

		return decodedValue;
	}

	/**
	 * Encodes a String using the set of characters allowed in a URI.
	 *
	 * @param      value     <code>String</code> to be translated.
	 * @param      encoding  the Java alias for the character encoding to be used to convert non-ASCII characters into
	 *                       bytes (e.g. <code>"UTF8"</code>).
	 *
	 * @return     the translated <code>String</code>.
	 *
	 * @exception  UnsupportedEncodingException  if the given encoding is not a recognised character encoding.
	 */
	public static String encode(String value, String encoding) throws UnsupportedEncodingException {

		// Create a buffer that is roughly 1.5 times bigger than the value to
		// account for possible expansion of the resulting encoded string
		int len = value.length();
		StringBuilder out = new StringBuilder(len * 3 / 2);

		for (int charIndex = 0; charIndex < len; charIndex++) {
			char aChar = value.charAt(charIndex);

			if ((aChar <= 127) && sValidChar[aChar]) {
				out.append(aChar);
			}
			else if (aChar == ' ') {
				out.append('+');
			}
			else {
				byte[] charBytes = String.valueOf(aChar).getBytes(encoding);

				// For each byte to encode this character, write a '%',
				// followed by a 2 digit uppercase hex representation of the
				// byte value
				for (byte element : charBytes) {
					out.append('%');

					// Convert into two Hex digits (and don't worry about the
					// sign bit, unlike Integer.toHexString()
					out.append(sHexLookup[(element & 0xF0) >> 4]);
					out.append(sHexLookup[element & 0x0F]);
				}
			}
		}

		// The result string should be encodable in pure ASCII
		return out.toString();
	}

	/**
	 * Encodes a String using the set of characters allowed in a URI. This method encodes a multibyte string in UTF8
	 *
	 * @param   value  <code>String</code> to be translated.
	 *
	 * @return  the translated <code>String</code>.
	 */
	public static String encodeUTF(String value) {
		String encodedValue = null;

		try {
			encodedValue = encode(value, "UTF8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO - error handling
			// should never happen for this method because the
			// character encoding is constant
		}

		return encodedValue;
	}

	/**
	 * Extracts a cookie value from a list of cookies based on the name.
	 *
	 * @param   cookieName  Name of the cookie to search for.
	 * @param   cookies     List of cookies of the form "name1=value1; name2=value2; ...; nameN=valueN".
	 *
	 * @return  The cookie value stored as a String object. null if cookie not found.
	 */
	public static String getCookie(String cookieName, String cookies) {
		return getParameterValue(cookieName, cookies);
	}

	/**
	 * Returns the date format used for expiry times in type zero (Netscape) cookies. Also used as the date format for
	 * portlet session expiries. Creates a new instance, so thread safe.
	 *
	 * @return  the date format used for expiry times in type zero (Netscape) cookies
	 */
	public static DateFormat getCookieDateFormat() {
		DateFormat format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);

		// Cookies use GMT Time zone by convention (Rule Britania!)
		format.setTimeZone(TimeZone.getTimeZone("GMT"));

		return format;
	}

	/**
	 * Determines whether the given HTTP status code denotes a client or server error. According to the HTTP 1.1 spec,
	 * the codes have the following categories:
	 *
	 * <ul>
	 *   <li>1xx - Informational (new in HTTP/1.1)</li>
	 *   <li>2xx - Success</li>
	 *   <li>3xx - Redirection</li>
	 *   <li>4xx - Client Error</li>
	 *   <li>5xx - Server Error</li>
	 * </ul>
	 *
	 * @param   statusCode  an HTTP response status code
	 *
	 * @return  true if the given status code denotes a client or server error
	 */
	public static boolean isErrorStatusCode(int statusCode) {
		return (statusCode >= 400) && (statusCode < 600);
	}

	/**
	 * Determine if a value needs to be decoded using HTTPUtils.decode() This method assumes that a value is encoded. As
	 * such, the existence of '%' (leading character of an encoded sequence) or '+' (sometimes used to replace <space>
	 * characters indicates that decoding IS required.
	 */
	public static final boolean needsDecoding(String token) {

		if (token == null) {
			return false;
		}

		return (token.indexOf('+') != -1) || (token.indexOf('%') != -1);
	}

	/**
	 * Sets a cookie value in a list of cookies based on the name. If the cookie value is null, the cookie is removed
	 * from the list.
	 *
	 * @param   cookieName   Name of the cookie to set.
	 * @param   cookieValue  Value of the cookie to set.
	 * @param   cookies      List of cookies of the form "name1=value1; name2=value2; ...; nameN=valueN".
	 *
	 * @return  The new cookie string.
	 */
	public static String setCookie(String cookieName, String cookieValue, String cookies) {
		int cookieNameLen;
		int cookiesLen;
		String newCookies = cookies;

		// Obvious error in arguments?
		if ((cookieName == null) || ((cookieNameLen = cookieName.length()) == 0)) {
			return cookies;
		}

		if ((cookies == null) || ((cookiesLen = cookies.length()) == 0)) {

			if (cookieValue != null) {
				newCookies = cookieName + "=" + cookieValue;
			}
		}
		else {
			int index = 0;

			// Set the cookie in the list of cookies
			// Cookie is a string of the form
			// name1=value1; name2=value2; ...; nameN=valueN
			//

			for (;;) {

				// First, eat all the white spaces
				while ((index < cookiesLen) && (cookies.charAt(index) == ' ')) {
					index++;
				}

				if (cookies.startsWith(cookieName, index)) {
					int equalsIndex = cookies.indexOf('=', index);

					if (equalsIndex != -1) {
						int i = equalsIndex - 1;

						while ((cookies.charAt(i) == ' ') && (i > index)) {
							i--;
						}

						if (cookieNameLen == (i - index + 1)) {
							// We've found our cookie

							// Move 1 char past '='
							int beginIndex = equalsIndex + 1;

							// Find end of token
							int endIndex = cookies.indexOf(';', beginIndex);

							if (endIndex == -1) {
								endIndex = cookiesLen - 1;
							}
							else {
								endIndex--;
							}

							if (cookieValue == null) {
								newCookies = cookies.substring(0, index) + cookies.substring(endIndex + 1);
							}
							else {
								newCookies = cookies.substring(0, beginIndex) + cookieValue +
									cookies.substring(endIndex + 1);
							}

							return newCookies;
						}
					}
				}

				// no match - advance to next cookie
				index = cookies.indexOf(';', index);

				// Check for end cases
				if ((index == -1) || // no more tokens
						(index == (cookiesLen - 1))) // nothing following last

				// ';'
				{

					if (cookieValue != null) {

						if (newCookies.length() != 0) {
							newCookies = newCookies + ";";
						}

						newCookies = newCookies + cookieName + "=" + cookieValue;
					}

					return newCookies;
				}

				index++;
			}
		}

		return newCookies;
	}

	/**
	 * Converts a Java Locale to a String in the format specified for the Accept-Language and xml:lang attribute, i.e.
	 * <a href="http://www.ietf.org/rfc/rfc1766.txt">RFC 1766</a>.
	 */
	public static String toHTTPLocale(Locale locale) {
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if ((country.length() > 0) || (variant.length() > 0)) {
			StringBuilder buff = new StringBuilder(20).append(language).append('-').append(country);

			if (variant.length() > 0) {
				buff.append('-').append(variant);
			}

			return buff.toString();
		}
		else {
			return language;
		}
	}

	/**
	 * Extracts a parameter value from a list of parameters based on the name.
	 *
	 * @param   paramName  name of the parameter to search forn
	 * @param   params     list of parameters as a String of the form: "name1=value1; name2=value2; ...; nameN=valueN".
	 *
	 * @return  the parameter value or <code>null</code> if not found
	 */
	protected static String getParameterValue(String paramName, String params) {
		int paramNameLen;
		int paramsLen;

		// Obvious error in arguments?
		if ((paramName == null) || ((paramNameLen = paramName.length()) == 0)) {
			return null;
		}

		if ((params == null) || ((paramsLen = params.length()) == 0)) {
			return null;
		}

		// Try to extract the parameter from the list of parameters
		// Parameters are defined in a string of the form
		// name1=value1; name2=value2; ...; nameN=valueN
		//
		// The following parse copies the behaviour of
		// wpugcinb_GetCookieInNewBuffer in /m/wwg/src/wpu.c
		// and
		// wpdsesgcv_get_cookie_value in /m/wwg/src/wpdses.c
		//

		int index = 0;

		for (;;) {

			// First, eat all the white spaces
			while ((params.charAt(index) == ' ') && (index < paramsLen)) {
				index++;
			}

			if (params.startsWith(paramName, index)) {
				int equalsIndex = params.indexOf('=', index);

				if (equalsIndex != -1) {
					int i = equalsIndex - 1;

					while ((params.charAt(i) == ' ') && (i > index)) {
						i--;
					}

					if (paramNameLen == (i - index + 1)) {
						// We've found our parameter

						// Move 1 char past '='
						int beginIndex = equalsIndex + 1;

						// Check for empty parameter - if so return ""
						if (beginIndex == paramsLen) {
							return "";
						}

						// Find end of token
						int endIndex = params.indexOf(';', beginIndex);

						if (endIndex == -1) {
							endIndex = paramsLen - 1;
						}
						else {

							// Check for empty parameter - if so return ""
							if (beginIndex == endIndex) {
								return "";
							}

							endIndex--;
						}

						// Eat white spaces out in front
						while ((params.charAt(beginIndex) == ' ') && (beginIndex < endIndex)) {
							beginIndex++;
						}

						// Eat white spaces out at end
						while ((params.charAt(endIndex) == ' ') && (endIndex > beginIndex)) {
							endIndex--;
						}

						// Please note that substring takes the endIndex and
						// subtracts 1
						// from it. Since our endIndex points to where we want
						// it to be,
						// we need to add 1 to it so that everything works out.
						String paramValue = params.substring(beginIndex, endIndex + 1);

						// check for empty cookie - if so return ""
						if (0 == paramValue.length()) {
							return "";
						}

						return paramValue;
					}
				}
			}

			// no match - advance to next parameter
			index = params.indexOf(';', index);

			// Check for end cases
			if (index == -1) // no more tokens
			{
				return null;
			}

			if (index == (paramsLen - 1)) // nothing following last ';'
			{
				return null;
			}

			index++;
		}
	}
}
