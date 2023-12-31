package Testing_package;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Map.Entry;

class HuffmanNode implements Comparable<HuffmanNode> {      //구조체(클래스) 선언
	char data;
	int frequency;
	HuffmanNode left, right;
	
	public int compareTo(HuffmanNode node) {                //compareTo : priority queue를 정렬할 때 이 함수를 기준으로 자동 정렬함
		return this.frequency - node.frequency;
	}
}

public class Testing {
	
	public static int main_compress(String file, String path_of_bin_file) {
		Scanner keyboard = new Scanner(System.in);
		HashMap<Character, Integer> frequencyMap = new HashMap<>();    //문자와 빈도를 저장하는데 hashmap 타입 사용
		
		Scanner inputStream = null;
		String file_content = "";
		
		try {
			inputStream = new Scanner(new File(file));                     //파일 열기
		}
		catch(FileNotFoundException e){
			System.out.println("Error : FileNotFoundException" + file);      //파일 예외처리
			System.exit(0);
		}
		
		ArrayList<Integer> newLine = new ArrayList<>(); 		// 줄 바꿈 기호가 들어있는 인덱스 리스트
		int newLine_index = 0;									// hasNextLine()메서드를 사용했기 때문에 줄 바꿈은 따로 처리함
		while(inputStream.hasNextLine()) {
			String line = inputStream.nextLine();
            for (int i = 0; i < line.length(); i++) {
                char singleChar = line.charAt(i);
                file_content = file_content + singleChar;
                frequencyMap.put(singleChar, frequencyMap.getOrDefault(singleChar, 0) + 1);       //덮어쓰게 됨(해시맵은 key 중복 불가한 타입)
                newLine_index++;
            }
            newLine.add(newLine_index);				// 줄 바꿈 기호가 들어갈 위치				
		}
		
		frequencyMap.put((char)10, newLine.size());
		
		PriorityQueue<HuffmanNode> priority_queue = new PriorityQueue<>();
		priority_queue = Make_Priority_Queue(frequencyMap);
		
		//바이너리 트리 만들기
		HuffmanNode root = BuildBinaryTree(priority_queue);
		
		//트리를 만들고 바이너리 값을 각각 할당 해야함
		HashMap<Character, String> BinaryCode = new HashMap<>();
		GenerateBinaryCode(root, "", BinaryCode);
		
		
		//헤더에 해당되는 해시맵 만들기
		HashMap<String, String> HashMapHeader = new HashMap<>();
		for(Entry<Character, String> entry : BinaryCode.entrySet()) {
			HashMapHeader.put(padBinaryString(Integer.toBinaryString((int)entry.getKey()),8), entry.getValue());
		}
		
		for(Entry<String, String> entry : HashMapHeader.entrySet()) {
			System.out.println("Key :" + entry.getKey() + " value :" + entry.getValue() + " value's len in binary : " + Integer.toBinaryString(entry.getValue().length()));
			//System.out.println(" value :" + entry.getValue());
		}
		
		//압축단계
		String compressed_string = Compress(BinaryCode, file_content, newLine);
		System.out.println("Compressed_string : " + compressed_string);
		
		
		// public key & secret key 생성
		BigInteger[] keylist = new BigInteger[3];  

		keyGenerator(keylist);  

		// keyGenerator()에서 생성된 public key와 secret key를 main으로 가져옴
		BigInteger p_key = keylist[0];   
		BigInteger s_key = keylist[1];  
		BigInteger n = keylist[2];      
		
		String BinaryHashMap = Make_HashMap_To_Binary(HashMapHeader);
		//현재 단계에서는 해시맵 정보만 들어있음. 여기서 해시맵 정보를 암호화 해야함
		
		System.out.println("BinaryHashMap : " + BinaryHashMap);
		
		//암호를 풀고 압축을 풀 때 보정을 위한 'num_of_zero'. 설명하자면 복잡함
		int zero = 0;
		int zero_index = 0;
		while(!((int)BinaryHashMap.charAt(zero_index) - '0' == 1)) {
			zero++;
			zero_index++;
		}
		System.out.println("zero : " + zero);
		//3비트를 차지할거임
		String num_of_zero = Integer.toBinaryString(zero);
		System.out.println(padBinaryString(num_of_zero,2));
		
		
		// 2진수 형태의 prefix를 암호화하기 위해 10진수로 변환
		// BigInteger 형태는 무한대 범위까지의 정수를 저장하는 것이 가능 
		System.out.println("BinaryHashMap : " + BinaryHashMap);
		BigInteger plain_num = new BigInteger(BinaryHashMap, 2); 
		System.out.println("Plain number : " + plain_num); 
		
		// plain_num을 4자리씩 분할하여 각각에 대해 암호화 진행
		String[] plain_divide = splitNumber(plain_num.toString(), 4);  // plain_num을 4자리씩 분할
		String[] cipher_con = new String[plain_divide.length];         // plain_divide 배열과 동일한 크기를 가진 문자 배열 생성
		
		
		BigInteger plainDiv;  // plain_num을 4자리씩 분할하여 임시로 저장해놓을 변수
		BigInteger cipher;  // 분할된 plain_num을 암호화한 형태를 저장해놓을 cipher number

		for (int i = 0; i < plain_divide.length; i++) {
			plainDiv = new BigInteger(plain_divide[i]);
			cipher = (BigInteger) Encode(plainDiv, p_key, n);
			cipher_con[i] = cipher.toString();  // 분할된 수들을 암호화하여 각 인덱스에 저장
		}
		
		String HashMapHeaderCompletion = "";
		for (String part : cipher_con) {
			System.out.println(part);
			HashMapHeaderCompletion = HashMapHeaderCompletion + part.length() + part;
		}
		
		//BigInteger로
		BigInteger bigInteger = new BigInteger(HashMapHeaderCompletion, 10);
		
		//바이너리로
		HashMapHeaderCompletion = bigInteger.toString(2);
		
		HashMapHeaderCompletion = padBinaryString(num_of_zero,3) + padBinaryString(Integer.toBinaryString((HashMapHeaderCompletion.length())), 16) + HashMapHeaderCompletion;
		
		System.out.println("num_of_zero : " + padBinaryString(num_of_zero,3) + "\nlen : " + padBinaryString(Integer.toBinaryString((HashMapHeaderCompletion.length())), 16));
		
		inputStream.close();
		keyboard.close();
		//헤더를 추가해서 압축한 내용과 같이 텍스트 파일을 저장
		return save_the_data(HashMapHeaderCompletion, compressed_string, keyboard, file, path_of_bin_file);
	}
		
	
	
	//=============================여기서 부터 함수======================================
	public static String Make_HashMap_To_Binary(HashMap<String, String> map) {
		String MapHeaderString = "";
		
	
		for(Entry<String, String> entry : map.entrySet()) {
			MapHeaderString = MapHeaderString + padBinaryString(Integer.toBinaryString(entry.getValue().length()), 5) + entry.getKey() + entry.getValue();
		}
		
		for(int i = 0; i < 5; i++) {
			MapHeaderString += "0";
		}

		return MapHeaderString;
	}
	
	public static int save_the_data(String BinaryHashMap, String compressed_data, Scanner keyboard, String OriginFile, String file) {
		
		String Header = "";
		
		file = file + ".bin";
		
		// 해시 맵 정보를 합쳐서 하나의 바이너리 스트링으로 만듦
		try (BufferedOutputStream bin_file = new BufferedOutputStream(new FileOutputStream(file))) {

			//해시맵 정보 + 원문
			Header = BinaryHashMap + compressed_data;
			
			byte[] FinalHeader = binaryStringToByteArray(Header);
			
			bin_file.write(FinalHeader);
			bin_file.close();
			System.out.print("\nContent is saved in " + file + "\n");
			System.out.println("Compressed : " + (int)getCompressibility(OriginFile, file) + "% of original.");

			return (int)getCompressibility(OriginFile, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return 0;
	}
	
	//String 타입을 byte[]타입으로 바꾸는 함수. byte[]타입은 0과 1이 8비트씩 쪼개 들어감.
	private static byte[] binaryStringToByteArray(String binaryString) {
        int length = binaryString.length();
        int numOfBytes = (int) Math.ceil((double) length / 8);
        byte[] byteArray = new byte[numOfBytes];

        for (int i = 0; i < numOfBytes; i++) {
            int endIndex = Math.min((i + 1) * 8, length);
            String byteString = binaryString.substring(i * 8, endIndex);
            byteArray[i] = (byte) Integer.parseInt(byteString, 2);
        }

        return byteArray;
    }
	
	// 이진 문자열을 일정한 길이로 만들어주는 메서드
    private static String padBinaryString(String binaryString, int fixedLength) {
        String result = "";

        // 입력된 이진 문자열이 길이보다 짧을 경우 앞에 0을 채움
        int paddingLength = fixedLength - binaryString.length();
        for (int i = 0; i < paddingLength; i++) {
            result = result + "0";
        }

        // 입력된 이진 문자열의 나머지 부분을 추가
        result = result + binaryString;

        // 만들어진 결과를 문자열로 반환
        return result.toString();
    }
	
	//priority queue 만들기
	public static PriorityQueue<HuffmanNode> Make_Priority_Queue(HashMap<Character, Integer> frequencyMap) {
		PriorityQueue<HuffmanNode> priority_queue = new PriorityQueue<>();  //PriorityQueue 데이터 타입은 자동으로 정렬해 줌
		for(char key : frequencyMap.keySet()) {								//정렬 기준은 compareTo함수(구조체에 선언 되어있음)
			HuffmanNode node = new HuffmanNode();							//자료 구조는 힙. root위치에 있는 것만 뽑아낼 수 있음
			node.data = key;												//우리는 작은값 -> 큰값 순으로 출력됨
			node.frequency = frequencyMap.get(key);
			node.left = null;
			node.right = null;
			priority_queue.add(node);
		}
		return priority_queue;
	}
	
	//바이너리 트리 만들기
	public static HuffmanNode BuildBinaryTree(PriorityQueue<HuffmanNode> queue) {
		while(queue.size() != 1) {                             //queue가 1이 아닐 때 까지 반복(queue가 1이면 root 1개만 남은 것임)
			HuffmanNode parent = new HuffmanNode();            //가장 작은 노드 2개를 parent 노드를 중심으로 합침
			HuffmanNode left = queue.poll(); 	 	 	 	   //이때 poll()함수는 가장 작은 값을 뽑으므로 left, right 순으로 선언하면 알어서 정렬됨
			HuffmanNode right = queue.poll();
			parent.data = 007;                 					   //안쓸것 같은 문자를 임시로 넣음(ASCII code에서 007이 뭔 문자인지는 모르겠음). 
			parent.frequency = left.frequency + right.frequency;   //이 문자(007)는 텍스트 파일에 있으면 안됨 있으면 오류남
			parent.left = left;
			parent.right = right;
			queue.add(parent);           //parent를 다시 queue로 넣음. queue안의 요소가 1이 될 떄 까지 반복시킬 때 까지 반복됨
		}
		return queue.poll();          //root를 반환하고(HuffmanNode 형태로) 함수 종료.
	}
	
	public static void GenerateBinaryCode(HuffmanNode node, String data, HashMap<Character, String> BinaryCode) {
		//inorder, preorder, postorder중 하나를 선택해서 0,1을 할당하는 형식으로 작성 recursion을 사용. inorder로 하려고 생각하고 있음
		if(node == null) return;         //노드가 존재하지 않으면 그냥 리턴
		if(node.data != 007) {           //끝노드 즉, 어떤 한 문자에 도달하면 새로운 해시맵에 데이터를 넣음
			BinaryCode.put(node.data, data);
		}
		GenerateBinaryCode(node.left, data + "0", BinaryCode);        //inorder로 하기 위해 recursion
		GenerateBinaryCode(node.right, data + "1", BinaryCode);       //왼쪽 한 다음 오른쪽
	}
	
	//압축하기
	public static String Compress(HashMap<Character, String> binaryCode, String file_content, ArrayList<Integer> newLine) {
		String compressed_string = "";
		int newLine_count = 0;
		for (int i = 0; i < file_content.length(); i++) {
            char singleChar = file_content.charAt(i);
            
            while (newLine_count < newLine.size() && newLine.get(newLine_count).equals(i)) {
                compressed_string += binaryCode.get((char) 10);
                newLine_count++;
            }
            
            compressed_string += binaryCode.get(singleChar);
        }
		return compressed_string;
	}
	
	public static float getCompressibility(String originFile, String BinFile) {
		// 파일 경로 지정
        Path OriginPath = FileSystems.getDefault().getPath(originFile);
        Path BinPath = FileSystems.getDefault().getPath(BinFile);

        try {
            // 파일의 크기 가져오기
            long OrifileSizeInBytes = Files.size(OriginPath);
            long BinfileSizeInBytes = Files.size(BinPath);

            return ((float)BinfileSizeInBytes/(float)OrifileSizeInBytes) * 100;   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
		return 0;
	}
	
    // 암호화 하기
	public static BigInteger Encode(BigInteger plain, BigInteger p_key, BigInteger n) {
		BigInteger result = plain.modPow(p_key, n);
		return result;
	}

	// 암호화에 필요한 public key와 secret key
	public static void keyGenerator(BigInteger[] keyArr) {
		// 두 개의 소수 p, q를 생성
		BigInteger p = BigInteger.valueOf(233);
		BigInteger q = BigInteger.valueOf(181);

		BigInteger n = p.multiply(q); // n = p * q
		BigInteger ET = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // ET = (p-1) * (q-1), ET = "Euler Totient"

		BigInteger e = smallestPr(ET);  // ET와 서로소인 홀수인 정수 중 최솟값을 구하여 e에 할당 
		BigInteger d = BigInteger.ZERO; // d를 0으로 초기화

		d = e.modInverse(ET); // secret key의 d값을 구하기 위해 "e mod ET" 의 모듈러 역수를 구해줌

		// keyArr에 public key와 secret key에 들어가는 세 값을 저장해줌
		keyArr[0] = e;
		keyArr[1] = d;
		keyArr[2] = n;
	}

	// 두 수의 최대공약수를 반환해주는 메소드
	public static BigInteger Euclid(BigInteger a, BigInteger b) {
		if (b.equals(BigInteger.ZERO)) { // b가 0일 때
			return a;
		} else {
			return Euclid(b, a.mod(b));
		}
	}

	// Euler totient 값과 서로소인 작은 홀수인 정수를 구하는 메소드
	public static BigInteger smallestPr(BigInteger Euler) {
		BigInteger num = BigInteger.valueOf(3);
		while (true) {
			if (Euclid(Euler, num).equals(BigInteger.ONE)) { // 최대공약수가 1일 경우
				break;
			}
			num = num.add(BigInteger.valueOf(2)); // num의 값에 2를 더함
		}
		return num;
	}

	// 문자열을 일정 길이(chunkSize)로 분할하여 문자 배열의 각 인덱스에 저장하는 메소드
	public static String[] splitNumber(String number, int chunkSize) {
		int length = number.length();
		int numOfChunks = (int) Math.ceil((double) length / chunkSize); // 문자 배열의 크기를 결정
		String[] result = new String[numOfChunks]; // 메소드에서 반환할 문자 배열 선언

		// 정해진 크기로 문자열을 자르는 과정
		for (int i = 0; i < numOfChunks; i++) {
			int start = i * chunkSize;
			int end = Math.min((i + 1) * chunkSize, length); // 문자 배열의 마지막 인덱스에 저장되는 숫자의 개수가 chunksize보다 작을 경우를 고려
			result[i] = number.substring(start, end);
		}

		return result;
	}
	
}
