package test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scheduler {
	
	 // ͨ���㷨ģ�壺�ھ������еĻ����� -> FCFS�Ļ����ϣ�����ʱ�䣩
    private static void schedule(List<Process> processes, Comparator<Process> comparator) {
    	processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
		int time = 0;
		int order = 0;
		int index = 0;
		List<Process> readyQueue = new ArrayList<>(); // ��������

		while (index < processes.size() || !readyQueue.isEmpty()) { 
			// �����е���Ľ�����ӵ���������
			while (index < processes.size() && processes.get(index).arrivalTime <= time) {
				readyQueue.add(processes.get(index));
				index++;
			}

			if (!readyQueue.isEmpty()) {
				// �ؼ�����:��ͬ�����㷨���ݲ�ͬ�������������ȼ�
				readyQueue.sort(comparator);

				// ����
				Process p = readyQueue.remove(0);
				p.startTime = Math.max(time, p.arrivalTime);
				p.completionTime = p.startTime + p.burstTime;
				p.turnaroundTime = p.completionTime - p.arrivalTime;
				p.waitingTime = p.startTime - p.arrivalTime;
				p.executionOrder = order++;
				time = p.completionTime;
			} else {
				// ���û�о������̣�������һ�����̵ĵ���ʱ��
				time = processes.get(index).arrivalTime;
			}
		}
    }
    
  	// FCFS���ȣ��Ӿ���������ѡ�����ȵ���Ľ��� ->������ҵ������Ⱥ�˳�� -> ÿ�����̽���֮����һ�����̲ſ�ʼ����
    public static void FCFS(List<Process> processes) {
 		schedule(processes, Comparator.comparingInt(p -> p.arrivalTime));
 	}

 	// ����ռSJF ���ȣ��Ӿ���������ѡ���������ʱ����̵Ľ��� -> ÿ�����̽���֮����һ�����̲ſ�ʼ����
    public static void SJF(List<Process> processes) {
 		schedule(processes, Comparator.comparingInt(p -> p.burstTime));
 	}

 	// ���ȼ����ȣ��Ӿ���������ѡ��������ȼ���ߵĽ��� -> ÿ�����̽���֮����һ�����̲ſ�ʼ���� -> ��̬����ռʽ���ȼ�����
    public static void Priority(List<Process> processes) {
 		schedule(processes, Comparator.comparingInt(p -> p.priority));
 	}

 	/*
 	 * RR���� ->�����еľ������̰���FCFS�����ų�һ���������У�Ȼ��Ϊ���Ƿ���ʱ��Ƭ��
 	 * 1.��һ��ʱ��Ƭ��δ������������еĽ��̱��Ѿ���ɣ�������������ȳ��򣬽��Ѿ�������ɵĽ��̴Ӿ���������ɾ�����ٵ��Ⱦ����������µĶ��׽������У�
 	 * ��������һ���µ�ʱ��Ƭ��
 	 *  2.��һ��ʱ��Ƭ����ʱ����ʱ���жϴ������ᱻ�����ʱ�����������δ������ϣ����ȳ���Ͱ��������������е�ĩβ��
 	 */
    public static void RR(List<Process> processes, int timeQuantum,GUI gui) {
    	//�ؼ� -> Ҫ�Ȱ�����ʱ������,Ȼ����ݵ�ǰʱ��time�뵽��ʱ������Ӿ�������
    	processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
 		// LinkedList:���㴦���ͷ-��β
 		Queue<Process> readyQueue = new LinkedList<>();
 		int time = 0;
 		int index = 0;
 		for (Process p : processes){
 			p.executionOrder = 0;
 			//�ؼ�����ʼ��
 			p.remainingTime = p.burstTime;
 		}
 		
 		//�б��л���δ����Ľ��̣����߾����������д����ȵĽ���
 		while (index < processes.size() || !readyQueue.isEmpty()) {
 			while (index < processes.size() && processes.get(index).arrivalTime <= time) {
 				readyQueue.add(processes.get(index));
 				index++;
 			}

 			if (!readyQueue.isEmpty()) {

 				String infoStr1 = "��ǰʱ�䣺 " + time + "\n";
 				infoStr1 += "���������еĽ����У�  ";
 				for (Process p : readyQueue)
 					infoStr1 += "���� " + p.id + " ";
 				infoStr1 += "\n";
 				gui.appendToResultTextArea(infoStr1);

 				// ȡ�������е�һ������
 				Process p = readyQueue.poll();
 				String infoStr2 = "�������н��� " + p.id + " ������ʱ��Ƭ:" + timeQuantum + "\n";
 				gui.appendToResultTextArea(infoStr2);

 				// ���̵�ִ��ʱ�䣬���ִ�� timeQuantum ʱ��Ƭ
 				int executionTime = Math.min(p.remainingTime, timeQuantum);
 				p.remainingTime -= executionTime;
 				time += executionTime; // �ؼ�:���µ�ǰʱ������"ʵ��ִ��ʱ��"

 				// �ؼ�����->�ٽ����̷Żض���ǰ�����¾�������->��֤δ��ɵĽ��̷��ڶ�β
 				while (index < processes.size() && processes.get(index).arrivalTime <= time) {
 					readyQueue.add(processes.get(index));
 					index++;
 				}

 				// ������δ��ɣ��Żض�β���ȴ���һ��
 				if (p.remainingTime > 0) {
 					readyQueue.add(p);
 					String infoStr3 = "���� " + p.id + " �������ʱ�䣺" + p.remainingTime + "\n";
 					infoStr3 += "���� " + p.id + " ��δ��ɣ����ؾ������еȴ���һ�ֵ��ȡ�\n";
 					gui.appendToResultTextArea(infoStr3);
 				} else {// ����ִ�����
// 					��תʱ�� = ���ʱ�� - ����ʱ��;
// 					��Ȩ��תʱ�� = ��תʱ�� / ����ʱ��;
// 					�ȴ�ʱ�� = ����ʱ�� - ����ʱ��;
// 					�ȴ�ʱ�䣨�����ͽ��̣� = ��תʱ�� �C ����ʱ��;(���������)
 					p.completionTime = time;
 					p.turnaroundTime = p.completionTime - p.arrivalTime;
 					p.waitingTime = p.turnaroundTime - p.burstTime;
 					String finishedInfo = "���� " + p.id + " ����ɡ�\n";
 					gui.appendToResultTextArea(finishedInfo);
 				}
 			} else {
 				// ���û�о������̣�ֱ��������һ�����̵ĵ���ʱ��
 				time = processes.get(index).arrivalTime;
 				gui.appendToResultTextArea("û�о������̣�������һ�����̵ĵ���ʱ�� " + time + "\n");
 			}
 		}
 	}

    
    
}
