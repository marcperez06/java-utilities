package io.github.marcperez06.java_utilities.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class AESCrypto {
	
	private static final String ENCRYPT_ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12; // GCM recommended 12 bytes iv
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int AES_KEY_BIT = 256;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private Cipher cipher;
    
	public AESCrypto() {
		this(ENCRYPT_ALGORITHM);
	}
	
	public AESCrypto(String algorithm) {
		try {
			this.cipher = Cipher.getInstance(algorithm);
		} catch (Throwable e) {
			this.cipher = null;
			e.printStackTrace();
		}
	}
	
	private boolean chiperIsNotNull() {
		return (this.cipher != null);
	}
	
	/**
	 * Return Random AES Key with size specified
	 * @param keysize - int
	 * @return SecretKey - AES key
	 * @throws NoSuchAlgorithmException
	 */
	public SecretKey getAESKey(int keysize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keysize, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

    /**
     * Returns AES Key
     * @param password - String
     * @param salt - byte[]
     * @return SecretKey - AES password with salt (256 byte secret key)
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public SecretKey getAESKeyFromPassword(String password, byte[] salt) 
    								throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, AES_KEY_BIT);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }
    
    /**
     * Returns the plain text in base64 encoded using AES encrypted
     * @param plainText - String
     * @param password - String
     * @return String - Encrypt base64 encoded AES encrypted text
     */
    public String encrypt(String plainText, String password) {
    	String encryptText = "";
    	
    	if (this.chiperIsNotNull()) {

    		try {
        		
        		byte[] salt = CryptoUtils.getRandomNonce(SALT_LENGTH_BYTE);
        		byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        		SecretKey aesKeyFromPassword = this.getAESKeyFromPassword(password, salt);

                // ASE-GCM needs GCMParameterSpec
                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
                this.cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, gcmParameterSpec);

                byte[] cipherText = this.cipher.doFinal(plainText.getBytes(UTF_8));

                // prefix IV and Salt to cipher text
                int encryptSize = iv.length + salt.length + cipherText.length;
                byte[] cipherTextWithIvSalt = ByteBuffer.allocate(encryptSize).put(iv)
    														                    .put(salt)
    														                    .put(cipherText)
    														                    .array();

                // string representation, base64, send this string to other for decryption.
                encryptText = Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
        		
        	} catch (Throwable e) {
        		e.printStackTrace();
        	}

    	} else {
    		System.out.println("Can not encrypt, because AES Chiper is Null");
    	}

    	return encryptText;

    }

    /**
     * Return the encrpypted text decrypted if password is correct
     * @param encryptText - String
     * @param password - String
     * @return String - Decrypted Text if password is correct, otherwise return empty text
     */
    public String decrypt(String encryptText, String password) {
    	String decryptText = "";
    	
    	if (this.chiperIsNotNull()) {

    		try {
        		
        		byte[] decode = Base64.getDecoder().decode(encryptText.getBytes(UTF_8));

                // get back the iv and salt from the cipher text
                ByteBuffer bufferBytes = ByteBuffer.wrap(decode);

                byte[] iv = new byte[IV_LENGTH_BYTE];
                bufferBytes.get(iv);

                byte[] salt = new byte[SALT_LENGTH_BYTE];
                bufferBytes.get(salt);

                byte[] cipherText = new byte[bufferBytes.remaining()];
                bufferBytes.get(cipherText);

                // get back the AES key from the same password and salt
                SecretKey aesKeyFromPassword = this.getAESKeyFromPassword(password, salt);

                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
                this.cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, gcmParameterSpec);

                byte[] plainText = this.cipher.doFinal(cipherText);

                decryptText = new String(plainText, UTF_8);
        		
        	} catch (Throwable e) {
        		e.printStackTrace();
        	}

    	} else {
    		System.out.println("Can not decrypt, because AES Chiper is Null");
    	}

    	return decryptText;
    }
    
    /**
     * Create a new File encrypted based on plain file
     * @param fromFile - String path of original file
     * @param toFile - String path of new file
     * @param password - String AES password for encrypt
     */
    public void encryptFile(String fromFile, String toFile, String password) {

    	if (this.chiperIsNotNull()) {
    		
    		try {
    			byte[] fileContentInBytes = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(fromFile).toURI()));
                String fileContent = new String(fileContentInBytes, UTF_8);
                
                String encryptString = this.encrypt(fileContent, password);
                byte[] encryptedText = encryptString.getBytes(UTF_8);

                // save a file
                Path path = Paths.get(toFile);

                Files.write(path, encryptedText);
    		} catch (Exception e) {
    			System.out.print("Can not encrypt the file " + fromFile);
    			e.printStackTrace();
    		}

    	} else {
    		System.out.println("Can not encrypt file, because AES Chiper is Null");
    	}

    }

    /**
     * Return decrypted content of file encrypted
     * @param fromEncryptedFile - String path of encrypted file
     * @param password - String AES password for encrypt
     * @return String - Decrypted content of file
     */
    public String decryptFileUsingAES(String fromEncryptedFile, String password) {
    	String decryptFile = "";
    	
    	if (this.chiperIsNotNull()) {

    		try {
        		byte[] fileContentInBytes = Files.readAllBytes(Paths.get(fromEncryptedFile));
                String fileContent = new String(fileContentInBytes, UTF_8);
                decryptFile = this.decrypt(fileContent, password);
        	} catch (Exception e) {
        		System.out.print("Can not decrypt the file " + fromEncryptedFile);
        		e.printStackTrace();
        	}

    	} else {
    		System.out.println("Can not decrypt file, because AES Chiper is Null");
    	}

    	return decryptFile;
    }

}
