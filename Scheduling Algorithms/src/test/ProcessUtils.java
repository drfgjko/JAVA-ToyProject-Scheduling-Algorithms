package test;

import java.util.List;

public class ProcessUtils {
	// ����ƽ����תʱ��
	public static double getAverageTurnaroundTime(List<Process> processes) {
		double totalTurnaroundTime = 0;
		for (Process p : processes) {
			totalTurnaroundTime += p.turnaroundTime;
		}
		return totalTurnaroundTime / processes.size();
	}

	// ����ƽ���ȴ�ʱ��
	public static double getAverageWaitingTime(List<Process> processes) {
		double totalWaitingTime = 0;
		for (Process p : processes) {
			totalWaitingTime += p.waitingTime;
		}
		return totalWaitingTime / processes.size();
	}
}


