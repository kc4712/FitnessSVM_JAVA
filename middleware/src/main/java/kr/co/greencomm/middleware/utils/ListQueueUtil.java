package kr.co.greencomm.middleware.utils;

public final class ListQueueUtil<T> {
	
	private class Node{
		
		// 노드는 data와 다음 노드를 가진다.
		private T  data;
		private Node nextNode;
	
		Node(T data){
			this.data = data;
			this.nextNode = null;
		}
	}
	// 사이즈
	private int size;
	
	// 큐는 front노드와 rear노드를 가진다.
	private Node front;
	private Node rear;
	
	public ListQueueUtil(){
		this.front = null;
		this.rear = null;
		this.size=0;
	}
	
	// 큐가 비어있는지 확인
	public boolean empty(){
		return (front==null);
	}
	
	// item을 큐의 rear에 넣는다.
	public void insert(T item){
		
		Node node = new Node(item);
		node.nextNode = null;
		size++;
		
		if(empty()){
			
			rear = node;
			front = node;
		
		}else{
			
			rear.nextNode = node;
			rear = node;
			
		}
	}
	
	// front 의 데이터를 반환한다.
	public T peek(){
		if(empty()) throw new ArrayIndexOutOfBoundsException();
		return front.data;
	}
	
	// front 를 큐에서 제거한다.
	public T remove(){
		
		T item = peek();
		front = front.nextNode;
		size--;
		
		if(front == null){
			rear = null;
		}
		
		return item;
	}
	
	public void clear() {
		/*while(this.size < 1)
			remove();*/
		this.front = null;
		this.rear = null;
		this.size=0;
	}
	
	public int size() {
		return this.size;
	}
}
