package io.github.marcperez06.java_utilities.crypto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class CryptoUtils {
	
	private static AESCrypto aes = new AESCrypto();

	private CryptoUtils() {
		
	}
	
	public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    // hex representation
    public static String transformByteToToHexadecimal(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // print hex with block size split
    public static String hexadecimalWithBlockSize(byte[] bytes, int blockSize) {

        String hex = transformByteToToHexadecimal(bytes);

        // one hex = 2 chars
        blockSize = blockSize * 2;

        // better idea how to print this?
        List<String> result = new ArrayList<String>();
        
        int size = hex.length();
        int index = 0;
        while (index < size) {
        	int endIndex = Math.min(index + blockSize, size);
            result.add(hex.substring(index, endIndex));
            index += blockSize;
        }

        return result.toString();
    }
    
    // --------- AES METHODS -----------

    /**
     * Returns the plain text in base64 encoded using AES encrypted
     * @param plainText - String
     * @param password - String
     * @return Encrypt base64 encoded AES encrypted text
     */
    public static String encryptUsingAES(String plainText, String password) {
    	return aes.encrypt(plainText, password);
    }

    /**
     * Return the encrpypted text decrypted if password is correct
     * @param encryptText - String
     * @param password - String
     * @return String - Decrypted Text if password is correct, otherwise return empty text
     */
    public static String decryptUsingAES(String encryptText, String password) {
    	return aes.decrypt(encryptText, password);
    }
    
    /**
     * Create a new File encrypted based on plain file
     * @param fromFile - String path of original file
     * @param toFile - String path of new file
     * @param password - String AES password for encrypt
     */
    public static void encryptFileUsingAES(String fromFile, String toFile, String password) {
    	aes.encryptFile(fromFile, toFile, password);
    }

    /**
     * Return decrypted content of file encrypted
     * @param fromEncryptedFile - String path of encrypted file
     * @param password - String AES password for encrypt
     * @return String - Decrypted content of file
     */
    public static String decryptFileUsingAES(String fromEncryptedFile, String password) {
    	return aes.decryptFileUsingAES(fromEncryptedFile, password);
    }

}