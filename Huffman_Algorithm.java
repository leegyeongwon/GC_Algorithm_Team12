package Testing_package;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
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
	
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		HashMap<Character, Integer> frequencyMap = new HashMap<>();    //문자와 빈도를 저장하는데 hashmap 타입 사용
		
		System.out.println("Enter the name of file(include Path) : ");
		String file = keyboard.nextLine();
		Scanner inputStream = null;
		String file_content = "";
		
		try {
			inputStream = new Scanner(new File(file));                     //파일 열기
		}
		catch(FileNotFoundException e){
			System.out.println("Error : FileNotFoundException" + file);      //파일 예외처리
			System.exit(0);
		}
		
		while(inputStream.hasNextLine()) {
			String line = inputStream.nextLine();
            for (int i = 0; i < line.length(); i++) {
                char singleChar = line.charAt(i);
                file_content = file_content + singleChar;
                frequencyMap.put(singleChar, frequencyMap.getOrDefault(singleChar, 0) + 1);       //덮어쓰게 됨(해시맵은 key 중복 불가한 타입)
            }
		}
		
		for(Entry<Character, Integer> entry : frequencyMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " | Value : " + entry.getValue());     //하나하나 출력해보는 테스트 코드
		}
		
		PriorityQueue<HuffmanNode> priority_queue = new PriorityQueue<>();  //PriorityQueue 데이터 타입은 자동으로 정렬해 줌
		for(char key : frequencyMap.keySet()) {								//정렬 기준은 compareTo함수(구조체에 선언 되어있음)
			HuffmanNode node = new HuffmanNode();							//자료 구조는 레드-블랙 트리. 즉, root위치에 있는 것만 뽑아낼 수 있음
			node.data = key;												//우리는 작은값 -> 큰값 순으로 출력됨
			node.frequency = frequencyMap.get(key);
			node.left = null;
			node.right = null;
			priority_queue.add(node);
		}
		
		//frequency만 출력하는 테스트 코드
		while(priority_queue.isEmpty()) {
			System.out.println("frequency : " + priority_queue.peek().frequency);
		}
		
		//바이너리 트리 만들기
		HuffmanNode root = BuildBinaryTree(priority_queue);
		
		//트리를 만들고 바이너리 값을 각각 할당 해야함
		HashMap<Character, String> BinaryCode = new HashMap<>();
		GenerateBinaryCode(root, "", BinaryCode);
		
		for(Entry<Character, String> entry : BinaryCode.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
		
		//압축단계
		String compressed_string = Compress(BinaryCode, file_content);
		System.out.println(compressed_string);
		
		// 2진수 형태의 compressed_string 을 암호화하기 위해 10진수로 변환
		// BigInteger 형태는 무한대 범위까지의 정수를 저장하는 것이 가능 
		BigInteger plain_num = new BigInteger(compressed_string, 2); 
		System.out.println("Plain number : " + plain_num); 
		
		// public key & secret key 생성
		BigInteger[] keylist = new BigInteger[3];  

		keyGenerator(keylist);  

		// keyGenerator()에서 생성된 public key와 secret key를 main으로 가져옴
		BigInteger p_key = keylist[0];   
		BigInteger s_key = keylist[1];  
		BigInteger n = keylist[2];      

		System.out.println("Public Key : (" + p_key + ", " + n + ")");
		System.out.println("Secret Key : (" + s_key + ", " + n + ")");

		// plain_num을 4자리씩 분할하여 각각에 대해 암호화 진행
		String[] plain_divide = splitNumber(plain_num.toString(), 4);  // plain_num을 4자리씩 분할
		String[] cipher_con = new String[plain_divide.length];  // plain_divide 배열과 동일한 크기를 가진 문자 배열 생성
		
		BigInteger plainDiv;  // plain_num을 4자리씩 분할하여 임시로 저장해놓을 변수
		BigInteger cipher;  // 분할된 plain_num을 암호화한 형태를 저장해놓을 cipher number

		for (int i = 0; i < plain_divide.length; i++) {
			plainDiv = new BigInteger(plain_divide[i]);
			cipher = (BigInteger) Encode(plainDiv, p_key, n);
			cipher_con[i] = cipher.toString();  // 분할된 수들을 암호화하여 각 인덱스에 저장
		}
		
		// 암호화가 완료된 결과를 출력
		System.out.print("Cipher : ");
		for (String part : cipher_con) {
			System.out.print(part);
		}

		// 사용자가 secret key를 알고있는지 확인하는 절차
		// 5회 이내에 알맞은 secret key를 입력하지 못하면 프로그램 종료
		BigInteger password = new BigInteger("0");
		int count = 0;  // 남은 입력 기회

		while (true) {
			if (count == 0) {
				System.out.print("\n\nEnter the proper secret key : ");
			} else {
				System.out.print("Please enter again(" + (5 - count) + "chances left" + ") : ");
			}
			password = keyboard.nextBigInteger();
			if (password.equals(s_key)) {
				break;
			}
			count++;
			if (count == 5) {
				System.out.println("Decryption failed");
				System.exit(0);
			}
		}
		
		// 복호화 진행 (사용자가 알맞은 secret key 입력 시)
		String[] decrypt_div = new String[cipher_con.length];
		BigInteger cipherCon;
		BigInteger Decrypt;

		for (int i = 0; i < cipher_con.length; i++) {
			cipherCon = new BigInteger(cipher_con[i]);
			Decrypt = (BigInteger) Decode(cipherCon, s_key, n);
			decrypt_div[i] = Decrypt.toString();
		}
		
		// 복호화 완료된 결과를 출력
		System.out.print("Decrypted : ");
		for (String part : decrypt_div) {
			System.out.print(part);
		}

		// 복호화된 여러 결과들을 하나의 문자열로 합치는 단계
		String decrypt_con = "";
		for (int i = 0; i < decrypt_div.length; i++) {
			decrypt_con = decrypt_con.concat(decrypt_div[i]);
		}

		BigInteger decrypt_int = new BigInteger(decrypt_con);  // 합쳐진 문자열을 BigInteger 형태로 변환 

		// 복호화된 상태의 10진수를 2진수 형태로 변환 (압축이 완료된 직후의 형태)
		String origin_binary = decrypt_int.toString(2);  

		System.out.println();
		System.out.println("Original binary : " + origin_binary);
		
		//헤더를 추가해서 압축한 내용과 같이 텍스트 파일을 저장

		
		keyboard.close();
		inputStream.close();
	}
		
	//=============================여기서 부터 함수======================================
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
	public static String Compress(HashMap<Character, String> binaryCode, String file_content) {
		String compressed_string = "";
		for (int i = 0; i < file_content.length(); i++) {
            char singleChar = file_content.charAt(i);
            compressed_string = compressed_string + binaryCode.get(singleChar);
        }
		return compressed_string;
	}
	
        // 암호화 하기
	public static BigInteger Encode(BigInteger plain, BigInteger p_key, BigInteger n) {
		BigInteger result = plain.modPow(p_key, n);
		return result;
	}

	// 암호화에 필요한 public key와 secret key를 무작위로 생성하는 메소드
	public static void keyGenerator(BigInteger[] keyArr) {
		// 두 개의 소수 p, q를 생성
		BigInteger p = primeGenerator();
		BigInteger q = primeGenerator();

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

	// key 생성을 위한 소수판별 메소드
	public static boolean isPrime(BigInteger n) {
		// 0과 1은 소수가 아님
		if (n.compareTo(BigInteger.ONE) <= 0) {
			return false;
		}

		// 2부터 n의 제곱근까지 나누어 떨어지는지 확인
		for (BigInteger i = BigInteger.valueOf(2); i.compareTo(n.sqrt()) <= 0; i = i.add(BigInteger.ONE)) {
			if (n.mod(i).equals(BigInteger.ZERO)) {
				return false;
			}
		}

		return true;
	}

	// 100과 1000사이의 소수들 중 하나를 random으로 얻는 메소드
	public static BigInteger primeGenerator() {
		while (true) {
			BigInteger randP = BigInteger.valueOf((long) (Math.random() * 1000) + 1); // randP는 최대 1000의 값을 가질 수 있음
			if (randP.compareTo(BigInteger.valueOf(100)) < 0) { // randP의 값이 100보다 작다면 compareTo는 -1을 반환
				continue;
			}
			if (isPrime(randP) == true) { // randP가 소수인지 판별
				return randP;
			}
		}
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

	// 암호화 풀기(암호화된 ciphertext를 원래 plaintext로 변환)
	public static BigInteger Decode(BigInteger cipher, BigInteger s_key, BigInteger n) {
		BigInteger result = cipher.modPow(s_key, n); // "cipher^s_key mod n"
		return result;
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
	
	// 압축 풀기
	public static HuffmanNode UnCompress() {
		return null;
	}
	
}
