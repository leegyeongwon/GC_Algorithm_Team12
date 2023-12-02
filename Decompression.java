package Testing_package;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Testing_for_decompressing {

	public static void main_decompression(String decom_file, String password_string, String save_file_path) {
		HashMap<String, Character> hash_map_for_decom = new HashMap<>();		//prefix를 저장할 해시맵
		
		System.out.print("Enter the path of file to de-compress : ");
		Scanner keyboard = new Scanner(System.in);
		
		try (FileInputStream inputStream_decom = new FileInputStream(decom_file)) {
			int data;
			String binaryString = "";
			
			
			
			
			BigInteger secret_key = new BigInteger("23863");
			BigInteger n = new BigInteger("42173");
			BigInteger password = new BigInteger(password_string);
			int count = 0;  // 남은 입력 기회

			if(!password.equals(secret_key))
			{
				System.out.println("Wrong key");
				System.exit(0);
			}
			

			// 파일에서 한 바이트씩 읽어서 이진 문자열로 변환
            while ((data = inputStream_decom.read()) != -1) {
                binaryString += String.format("%8s", Integer.toBinaryString(data & 0xFF)).replace(' ', '0');
            }
            
            //2비트 읽어서 변수에 저장
            int num_of_zero = Integer.parseInt(binaryString.substring(0, 2), 2);
            
            //16비트 읽어와서 암화화 prefix의 길이 찾기
            int prefixLength = Integer.parseInt(binaryString.substring(2, 18), 2);
            //System.out.println("Length of prefix : " + Integer.parseInt(binaryString.substring(2, 18), 2));
            
            String body = binaryString.substring(18 + prefixLength);
            //System.out.println("body : " + body);
			int cur_index = 18;
			
			//암호화 prefix의 길이만큼 읽기
			//암호화 prefix를 BigInteger로 변경
			BigInteger Encoded_prefix = new BigInteger(binaryString.substring(cur_index, cur_index + prefixLength), 2);
			String Encoded_prefix_string = Encoded_prefix.toString();
			
			cur_index = 0;
			ArrayList<String> cipher_con = new ArrayList<>();
			while(cur_index < Encoded_prefix_string.length()) {
				int length_of_part = Encoded_prefix_string.charAt(cur_index) - '0';
				cur_index++;
				cipher_con.add(Encoded_prefix_string.substring(cur_index, cur_index + length_of_part).toString());
				cur_index += length_of_part;
			}
			
			
			// 복호화 진행 (사용자가 알맞은 secret key 입력 시)
			String[] decrypt_div = new String[cipher_con.size()];
			BigInteger cipherCon;
			BigInteger Decrypt;
			
			// Plain_num 과 복호화된 수의 총 자릿수를 일치시키기 위한 과정 포함
			
			for (int i = 0; i < cipher_con.size(); i++) {
				cipherCon = new BigInteger(cipher_con.get(i));
				Decrypt = (BigInteger) Decode(cipherCon, secret_key, n);
				decrypt_div[i] = Decrypt.toString();
				if (i == cipher_con.size() - 1) {
					if (decrypt_div[i].length() != 4) {
						for(int k = 0; k < 4 - decrypt_div[i].length(); k++) {
							decrypt_div[i] = "0" + decrypt_div[i];
						}
					}
				}
				else if (decrypt_div[i].length() != 4) {
					for (int j = 0; j <= 4 - decrypt_div[i].length(); j++) {
						decrypt_div[i] = "0" + decrypt_div[i];
					}
				}
			}


			// 복호화된 여러 결과들을 하나의 문자열로 합치는 단계
			String decrypt_con = "";
			for (int i = 0; i < decrypt_div.length; i++) {
				decrypt_con = decrypt_con.concat(decrypt_div[i]);
			}
			
			BigInteger decrypt_int = new BigInteger(decrypt_con);  // 합쳐진 문자열을 BigInteger 형태로 변환 
			
			// 복호화된 상태의 10진수를 2진수 형태로 변환 (압축이 완료된 직후의 형태)
			String origin_binary = decrypt_int.toString(2);  

			
			
			
			
			for(int i = 0; i < num_of_zero; i++)
				origin_binary = "0" + origin_binary;
			
			//System.out.println("Original binary : " + origin_binary);
			
			String prepared_binary_string = origin_binary + body;
			
			
			
			
			
			//허프만 코드를 해석하는 단계
        	//첫 4비트는 [허프만 코드의 길이]를 나타냄. 그다음 8비트는 문자, 그다음은 허프만 코드.
			int current_index = 0;
        	int len_of_huffmancode = -1;
        	char character;
        	String HuffmanCode;
        	
        	while(len_of_huffmancode != 0) {
        		len_of_huffmancode = Integer.parseInt(prepared_binary_string.substring(current_index, current_index +5), 2);
        		current_index += 5;
            	
            	character = (char)Integer.parseInt(prepared_binary_string.substring(current_index, current_index +8), 2);
            	current_index += 8;
            	
            	HuffmanCode = prepared_binary_string.substring(current_index, current_index + len_of_huffmancode);
            	current_index += len_of_huffmancode;
            	
            	hash_map_for_decom.put(HuffmanCode, character);
        	}
        	//위 과정에서 5비트 만큼 밀려서 다시 당겨줌
        	current_index -= 5;
        	
        	//허프만 코드 출력
        	//System.out.println("\nResult");
        	//for(Entry<String, Character> entry : hash_map_for_decom.entrySet()) {
        	//	System.out.println("Key : " + entry.getKey() + "\t\t | Value : " + entry.getValue());
        	//}
        	
        	String ASCII_Character = "";
        	String content = "";
        	for(int i = current_index; i < prepared_binary_string.length(); i++) {
        		ASCII_Character += prepared_binary_string.charAt(i);
        		if(hash_map_for_decom.containsKey(ASCII_Character)) {
        			content += hash_map_for_decom.get(ASCII_Character);
        			ASCII_Character = "";
        		}
        	}
        	
        	save_text_file(content, save_file_path);    
		}
		catch (IOException e) {
            e.printStackTrace();
        }
		
		keyboard.close();
	}

	
	private static void save_text_file(String content, String text_file_path) {
		System.out.print("\nEnter the location where the file will be saved : ");
		
		try(PrintWriter outputStream = new PrintWriter(text_file_path)) {
			outputStream.print(content);
			System.out.print("\nContent is saved in " + text_file_path);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// 암호화 풀기(암호화된 ciphertext를 원래 plaintext로 변환)
		public static BigInteger Decode(BigInteger cipher, BigInteger s_key, BigInteger n) {
			BigInteger result = cipher.modPow(s_key, n); // "cipher^s_key mod n"
			return result;
		}
}
