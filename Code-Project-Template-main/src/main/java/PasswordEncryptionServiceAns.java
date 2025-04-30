import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordEncryptionServiceAns {

	//string to store OTP
	private static String currentOTP = "";

	private PasswordEncryptionServiceAns() {

	}

	protected static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {

		// Encrypt the clear-text password using the same salt that was used to encrypt
		// the original password
		byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

		// Authentication succeeds if encrypted password that the user entered is equal
		// to the stored hash
		return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
	}

	protected static byte[] getEncryptedPassword(final String password, final byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {

		// PBKDF2 with SHA-1 as the hashing algorithm.
		// Note that the NIST specifically names SHA-1 as an acceptable hashing
		// algorithm for PBKDF2
		String algorithm = "PBKDF2WithHmacSHA1";

		// SHA-1 generates 160 bit hashes, so that's what makes sense here
		int derivedKeyLength = 160;

		// Pick an iteration count that works for you. The NIST recommends at east 1,000
		// iterations:
		// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
		int iterations = 20000;

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

		return f.generateSecret(spec).getEncoded();
	}

	protected static byte[] generateSalt() throws NoSuchAlgorithmException {

		// Salt generation make it 16 bytes and use SHA1PRNG algorithm
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		random.nextBytes(salt);

		return salt;
	}

	// Validate the OTP
	protected static boolean validateOTP(String userOTP) {
		// currentOTP = generateNumericOTP(6);
		// Return true if the user has entered the correct OTP
		return userOTP.equals(currentOTP);
	}

	// Method to generateOTP
	private static String generateNumericOTP(int length) {
		// Use a secure Random
		SecureRandom random = new SecureRandom();
		// Build a string for the OTP
		StringBuilder sb = new StringBuilder();
		// Generate 6 Secure Random integers
		for (int i = 0; i < length; i++) {
			int digit = random.nextInt(10);
			// appened to string builder
			sb.append(digit);
		}
		return sb.toString();
	}

	protected static String sendOTP() {
		// generate the OTP and display it to the user logging in
		currentOTP = generateNumericOTP(6);
		System.out.println("OTP has been sent: " + currentOTP);
		return currentOTP;
	}

}
