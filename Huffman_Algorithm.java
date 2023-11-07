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
		BuildBinaryTree(priority_queue);
		
		
		
		keyboard.close();
		inputStream.close();
	}
	
	
	//=============================여기서 부터 함수======================================
	//바이너리 트리 만들기
	public static HuffmanNode BuildBinaryTree(PriorityQueue<HuffmanNode> node) {
		return null;
	}
	
	//압축하기
	public static HuffmanNode Compress() {
		return null;	
	}
	
	//압축 풀기
	public static HuffmanNode UnCompress() {
		return null;
	}
	
	//암호화 하기
	public static HuffmanNode Encode() {
		return null;
	}
	
	//암호화 풀기
	public static HuffmanNode Decode() {
		return null;
	}
	

}
