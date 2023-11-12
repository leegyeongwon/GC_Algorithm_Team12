package Testing_package;

import java.io.File;
import java.io.FileNotFoundException;
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
		
		//암호화
		int plain_num = Integer.parseInt(compressed_string, 2);       // 2진수 형태의 compressed_string 을 암호화하기 위해 10진수로 변환
		System.out.println("The plain number" + " : " + plain_num);   // 프로그래밍 단계에서는 일단 출력, 추후 출력하는 코드는 삭제 예정
		
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
	
	//암호화 하기
	public static HuffmanNode Encode() {
		return null;
	}

	// key 생성을 위한 소수판별 메소드
	public static int isPrime(int n) {
		for (int i = 2; i <= (int) Math.sqrt(n); i++) {
			if (n % i == 0) {
				return 0;
			}
		}
		return 1;
	}

	// 1과 1000사이의 소수들 중 하나를 random으로 얻는 메소드
	public static int primeGenerator() {
		while(true) {
			int randP = (int) (Math.random() * 1000) + 1;
			if(isPrime(randP) == 1) {
				return randP;
			}
		}
	}
	
	//압축 풀기
	public static HuffmanNode UnCompress() {
		return null;
	}
	
	//암호화 풀기
	public static HuffmanNode Decode() {
		return null;
	}
	

}
