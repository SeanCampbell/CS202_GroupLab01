import java.io.*;
import java.nio.charset.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;


public class GuessPassword {
	public static void main(String[] args) throws IOException {
		System.out.println(guessSimplePassword("username", "password1"));
		System.out.println(guessSimplePassword("username", "abc"));
	}

	public static String guessSimplePassword(String username, String password) throws IOException {
		return guessPassword(username, password, "...");
	}

	public static String guessComplexPassword(String username, String password) throws IOException {
		return guessPassword(username, password, "...");
	}

	private static String guessPassword(String username, String password, String passwordEndpoint) throws IOException {
		String identityToken = getBearerToken();
		URL url = new URL(passwordEndpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", "Bearer " + identityToken);
		connection.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes("{\"username\": \"" + username + "\",\"password\": \"" + password + "\"}");
		out.flush();
		out.close();
		int status = connection.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		in.close();
		return content.toString();
	}

	private static String getBearerToken() {
		try {
			String oauthEndpoint = "https://oauth2.googleapis.com/token";
			long iat = new Date().getTime() / 1000;
			long expiration = iat + 3600;
			String privateKeyString = "...";
			String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
			String claimSet = "{\"iss\":\"...\",\"aud\":\"" + oauthEndpoint + "\",\"target_audience\":\"32555940559.apps.googleusercontent.com\",\"exp\": " + expiration + ",\"iat\": " + iat + "}";
			String encodedHeader = Base64.getEncoder().encodeToString(header.getBytes());
			String encodedClaimSet = Base64.getEncoder().encodeToString(claimSet.getBytes());
			byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKeyString);
	        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        PrivateKey privateKey = kf.generatePrivate(keySpec);
			Signature signer = Signature.getInstance("SHA256withRSA");
			signer.initSign(privateKey);
			signer.update((encodedHeader + "." + encodedClaimSet).getBytes());
			byte[] signature = signer.sign();
			String encodedSignature = Base64.getEncoder().encodeToString(signature);
			String jwtToken = encodedHeader + "." + encodedClaimSet + "." + encodedSignature;
			jwtToken = URLEncoder.encode(jwtToken, StandardCharsets.UTF_8.toString());

			URL url = new URL(oauthEndpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes("grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=" + jwtToken);
			out.flush();
			out.close();
			BufferedReader in;
			int status = connection.getResponseCode();
			if (status >= 200 && status < 300) {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			String bearerToken = content.toString().split(":")[1];
			bearerToken = bearerToken.substring(1, bearerToken.length() - 2);
			return bearerToken;
		} catch (Exception e) {
			return "";
		}
	}
}
