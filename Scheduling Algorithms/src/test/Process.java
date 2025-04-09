package test;

//没有状态管理
public class Process {
	public int id;  // 进程ID
	public int executionOrder; // 执行顺序->表示调度结果
	public int arrivalTime;  // 到达时间
	public int burstTime;  // 执行时间
	public int priority;  // 优先级

	public int startTime;  // 开始时间
	public int completionTime;  // 完成时间
	public int turnaroundTime;  // 周转时间
	public int waitingTime;  // 等待时间
    
	public int remainingTime;   // 剩余执行时间（用于RR调度）
	
    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }
}


