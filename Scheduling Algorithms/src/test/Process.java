package test;

//û��״̬����
public class Process {
	public int id;  // ����ID
	public int executionOrder; // ִ��˳��->��ʾ���Ƚ��
	public int arrivalTime;  // ����ʱ��
	public int burstTime;  // ִ��ʱ��
	public int priority;  // ���ȼ�

	public int startTime;  // ��ʼʱ��
	public int completionTime;  // ���ʱ��
	public int turnaroundTime;  // ��תʱ��
	public int waitingTime;  // �ȴ�ʱ��
    
	public int remainingTime;   // ʣ��ִ��ʱ�䣨����RR���ȣ�
	
    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }
}


