package test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scheduler {
	
	 // 通用算法模板：在就绪队列的基础上 -> FCFS的基础上（到达时间）
    private static void schedule(List<Process> processes, Comparator<Process> comparator) {
    	processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
		int time = 0;
		int order = 0;
		int index = 0;
		List<Process> readyQueue = new ArrayList<>(); // 就绪队列

		while (index < processes.size() || !readyQueue.isEmpty()) { 
			// 将所有到达的进程添加到就绪队列
			while (index < processes.size() && processes.get(index).arrivalTime <= time) {
				readyQueue.add(processes.get(index));
				index++;
			}

			if (!readyQueue.isEmpty()) {
				// 关键代码:不同调度算法根据不同属性来决定优先级
				readyQueue.sort(comparator);

				// 计算
				Process p = readyQueue.remove(0);
				p.startTime = Math.max(time, p.arrivalTime);
				p.completionTime = p.startTime + p.burstTime;
				p.turnaroundTime = p.completionTime - p.arrivalTime;
				p.waitingTime = p.startTime - p.arrivalTime;
				p.executionOrder = order++;
				time = p.completionTime;
			} else {
				// 如果没有就绪进程，跳到下一个进程的到达时间
				time = processes.get(index).arrivalTime;
			}
		}
    }
    
  	// FCFS调度：从就绪队列中选择最先到达的进程 ->按照作业到达的先后顺序 -> 每个进程结束之后，下一个进程才开始运行
    public static void FCFS(List<Process> processes) {
 		schedule(processes, Comparator.comparingInt(p -> p.arrivalTime));
 	}

 	// 非抢占SJF 调度：从就绪队列中选择估计运行时间最短的进程 -> 每个进程结束之后，下一个进程才开始运行
    public static void SJF(List<Process> processes) {
 		schedule(processes, Comparator.comparingInt(p -> p.burstTime));
 	}

 	// 优先级调度：从就绪队列中选择估计优先级最高的进程 -> 每个进程结束之后，下一个进程才开始运行 -> 静态非抢占式优先级调度
    public static void Priority(List<Process> processes) {
 		schedule(processes, Comparator.comparingInt(p -> p.priority));
 	}

 	/*
 	 * RR调度 ->将所有的就绪进程按照FCFS策略排成一个就绪队列，然后为他们分配时间片。
 	 * 1.若一个时间片尚未用完而正在运行的进程便已经完成，即立即激活调度程序，将已经运行完成的进程从就绪队列中删除，再调度就绪队列中新的队首进程运行，
 	 * 并且启动一个新的时间片；
 	 *  2.当一个时间片用完时，计时器中断处理程序会被激活，此时，如果进程尚未运行完毕，调度程序就把它送往就绪队列的末尾。
 	 */
    public static void RR(List<Process> processes, int timeQuantum,GUI gui) {
    	//关键 -> 要先按到达时间排序,然后根据当前时间time与到达时间来添加就绪队列
    	processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
 		// LinkedList:方便处理队头-队尾
 		Queue<Process> readyQueue = new LinkedList<>();
 		int time = 0;
 		int index = 0;
 		for (Process p : processes){
 			p.executionOrder = 0;
 			//关键：初始化
 			p.remainingTime = p.burstTime;
 		}
 		
 		//列表中还有未处理的进程，或者就绪队列中有待调度的进程
 		while (index < processes.size() || !readyQueue.isEmpty()) {
 			while (index < processes.size() && processes.get(index).arrivalTime <= time) {
 				readyQueue.add(processes.get(index));
 				index++;
 			}

 			if (!readyQueue.isEmpty()) {

 				String infoStr1 = "当前时间： " + time + "\n";
 				infoStr1 += "就绪队列中的进程有：  ";
 				for (Process p : readyQueue)
 					infoStr1 += "进程 " + p.id + " ";
 				infoStr1 += "\n";
 				gui.appendToResultTextArea(infoStr1);

 				// 取出队列中第一个进程
 				Process p = readyQueue.poll();
 				String infoStr2 = "正在运行进程 " + p.id + " ，分配时间片:" + timeQuantum + "\n";
 				gui.appendToResultTextArea(infoStr2);

 				// 进程的执行时间，最多执行 timeQuantum 时间片
 				int executionTime = Math.min(p.remainingTime, timeQuantum);
 				p.remainingTime -= executionTime;
 				time += executionTime; // 关键:更新当前时间增加"实际执行时间"

 				// 关键代码->再将进程放回队列前，更新就绪队列->保证未完成的进程放在队尾
 				while (index < processes.size() && processes.get(index).arrivalTime <= time) {
 					readyQueue.add(processes.get(index));
 					index++;
 				}

 				// 进程尚未完成，放回队尾，等待下一轮
 				if (p.remainingTime > 0) {
 					readyQueue.add(p);
 					String infoStr3 = "进程 " + p.id + " 还需服务时间：" + p.remainingTime + "\n";
 					infoStr3 += "进程 " + p.id + " 尚未完成，返回就绪队列等待下一轮调度。\n";
 					gui.appendToResultTextArea(infoStr3);
 				} else {// 进程执行完成
// 					周转时间 = 完成时刻 - 到达时刻;
// 					带权周转时间 = 周转时间 / 运行时间;
// 					等待时间 = 运行时刻 - 到达时刻;
// 					等待时间（计算型进程） = 周转时间 – 运行时间;(这里用这个)
 					p.completionTime = time;
 					p.turnaroundTime = p.completionTime - p.arrivalTime;
 					p.waitingTime = p.turnaroundTime - p.burstTime;
 					String finishedInfo = "进程 " + p.id + " 已完成。\n";
 					gui.appendToResultTextArea(finishedInfo);
 				}
 			} else {
 				// 如果没有就绪进程，直接跳到下一个进程的到达时间
 				time = processes.get(index).arrivalTime;
 				gui.appendToResultTextArea("没有就绪进程，跳到下一个进程的到达时间 " + time + "\n");
 			}
 		}
 	}

    
    
}
