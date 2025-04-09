package test;

import java.awt.event.*;
import java.awt.*;

import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private JComboBox<String> algorithmComboBox; // �����㷨ѡ��
	private JComboBox<Integer> processCountComboBox; // ������ѡ��
	private JTextField timeQuantumField; // ʱ��Ƭ�����
	private JTable processTable; // ���̱��
	private JTable resultTable; // ���Ƚ�����
	private DefaultTableModel processTableModel; // ���̱���ģ��
	private DefaultTableModel resultTableModel; // ���Ƚ������ģ��
	private JTextArea resultTextArea; // �㷨����
	private List<Process> processes; // �����洢���ɵĽ�������

	private static String[] TYPE = { "FCFS", "SJF", "Priority", "RR" };
	private static Integer[] PROCESSNUM = { 5, 6, 7, 8, 9, 10 };
	private static String GENERATEBUTTON_TEXT = "���ɽ���";
	private static String SCHEDULEBUTTON_TEXT = "��ʼ����";
	private static String EDITBUTTON_TEXT = "�༭����";
	private static String SAVEBUTTON_TEXT = "�����޸�";

	public GUI() {
		setTitle("CPU�����㷨");
		setSize(900, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// ������ѡ��������
		processCountComboBox = new JComboBox<>(PROCESSNUM);
		processCountComboBox.setSelectedIndex(0);

		// �����㷨ѡ��������
		algorithmComboBox = new JComboBox<>(TYPE);
		algorithmComboBox.setSelectedIndex(0);

		// ʱ��Ƭ�����
		timeQuantumField = new JTextField(5);

		// ���ɽ��̰�ť
		JButton generateButton = new JButton(GENERATEBUTTON_TEXT);
		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateProcesses();
			}
		});

		// ���Ȱ�ť
		JButton scheduleButton = new JButton(SCHEDULEBUTTON_TEXT);
		scheduleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scheduleProcesses();
			}
		});

		JButton editButton = new JButton(EDITBUTTON_TEXT);
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editProcessTable();
			}
		});

		// �����޸İ�ť
		JButton saveButton = new JButton(SAVEBUTTON_TEXT);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveEdits();
			}
		});

		// ʾ�㷨���۽��
		resultTextArea = new JTextArea(100, 30);
		resultTextArea.setEditable(false);
		resultTextArea.setLineWrap(true);
		resultTextArea.setWrapStyleWord(true);

		// ��岼��
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// ��������
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("������:"));
		topPanel.add(processCountComboBox);
		topPanel.add(new JLabel("�����㷨:"));
		topPanel.add(algorithmComboBox);
		topPanel.add(new JLabel("����ʱ��Ƭ��RR�㷨��:"));
		topPanel.add(timeQuantumField);
		panel.add(topPanel);

		// ��ť
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(generateButton);
		buttonPanel.add(scheduleButton);
		buttonPanel.add(editButton);
		buttonPanel.add(saveButton);
		panel.add(buttonPanel);

		// ���ԭʼ���̱�����ǩ
		JPanel processPanel = new JPanel();
		processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));
		processPanel.add(new JLabel("ԭʼ����:"));
		processPanel.add(new JScrollPane(processTable = new JTable()));
		panel.add(processPanel);

		// ��ӵ��Ƚ��������ǩ
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.add(new JLabel("���Ƚ��:"));
		resultPanel.add(new JScrollPane(resultTable = new JTable()));
		panel.add(resultPanel);

		// ����㷨�����ı���
		JPanel evaluationPanel = new JPanel();
		evaluationPanel.setLayout(new BoxLayout(evaluationPanel, BoxLayout.Y_AXIS));
		evaluationPanel.add(new JLabel("�㷨����ָ��:��RR�����ȹ�����Ϣ��"));
		evaluationPanel.add(new JScrollPane(resultTextArea));
		panel.add(evaluationPanel);

		processPanel.setPreferredSize(new Dimension(900, 400));
		resultPanel.setPreferredSize(new Dimension(900, 400));
		evaluationPanel.setPreferredSize(new Dimension(900, 200));
		add(panel);
		adjustStyle(this, 18);
	}
	
	//������С
	private void adjustStyle(Component component, int fontSize) {
		component.setFont(new Font("Microsoft YaHei", Font.PLAIN, fontSize));
		if (component instanceof JTable) {
			JTable table = (JTable) component;
			table.setFont(new Font("Microsoft YaHei", Font.PLAIN, fontSize));
			table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.PLAIN, fontSize));

			int rowHeight = fontSize + 10;
			table.setRowHeight(rowHeight);
		}
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				adjustStyle(child, fontSize);
			}
		}
	}

	// ���ɽ���
	private void generateProcesses() {
		//��ȡ����
		int numProcesses = (int) processCountComboBox.getSelectedItem();
		//���̶���
		processes = new ArrayList<>();
		Random rand = new Random();
		//������ɽ���
		for (int i = 0; i < numProcesses; i++) {
			int arrivalTime = rand.nextInt(10);
			int burstTime = rand.nextInt(10) + 1;
			int priority = rand.nextInt(10);
			processes.add(new Process(i, arrivalTime, burstTime, priority));
		}
		updateProcessTable();//���±��
	}

	// ���½��̱��
	private void updateProcessTable() {
		//����ȡ����
		processTableModel = new DefaultTableModel(new String[] { "����ID", "����ʱ��", "����ʱ��", "���ȼ�" }, 0);
		for (Process p : processes) {
			processTableModel.addRow(new Object[] { p.id, p.arrivalTime, p.burstTime, p.priority });
		}
		processTable.setModel(processTableModel);
		processTable.setEnabled(false);
	}

	// ѡ������㷨��ִ��
	private void scheduleProcesses() {
		//��֤�пɴ���Ľ���
		if (processes == null || processes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "�����ɽ��̣�", "����", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
		//RR�㷨ʹ��ʱ��Ƭ
		int timeQuantum = 0;
		if ("RR".equals(selectedAlgorithm)) {
			try {
				timeQuantum = Integer.parseInt(timeQuantumField.getText());
				if (timeQuantum <= 0) {
					JOptionPane.showMessageDialog(this, "���������0��ʱ��Ƭֵ", "������Ч", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "���������0��ʱ��Ƭֵ", "������Ч", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		//����ı���
		resultTextArea.setText("");
		switch (selectedAlgorithm) {
		case "FCFS":
			Scheduler.FCFS(processes);
			break;
		case "SJF":
			Scheduler.SJF(processes);
			break;
		case "Priority":
			Scheduler.Priority(processes);
			break;
		case "RR":
			Scheduler.RR(processes, timeQuantum, this);
			break;
		}

		updateResultTable();//���½����
	}

	//�༭���̱��
	private void editProcessTable() {
		if (processes == null || processes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "�����ɽ��̣�", "����", JOptionPane.ERROR_MESSAGE);
			return;
		}
		processTable.setEnabled(true);
	}
	//������̱��
    private void saveEdits() {
    	//��֤�пɴ���Ľ���
        if (processes == null || processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "�����ɽ��̣�", "����", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //��ȡ�������
        for (int i = 0; i < processTableModel.getRowCount(); i++) {
            try {
            	String idStr = processTableModel.getValueAt(i, 0).toString();
                String arrivalTimeStr = processTableModel.getValueAt(i, 1).toString();
                String burstTimeStr = processTableModel.getValueAt(i, 2).toString();
                String priorityStr = processTableModel.getValueAt(i, 3).toString();

                int id = Integer.parseInt(idStr);
                int arrivalTime = Integer.parseInt(arrivalTimeStr);
                int burstTime = Integer.parseInt(burstTimeStr);
                int priority = Integer.parseInt(priorityStr);
                
                if (id < 0||arrivalTime < 0 || burstTime <= 0 || priority < 0) {
					JOptionPane.showMessageDialog(this, "��������ڵ���0��ֵ������ʱ�������0��", "������Ч", JOptionPane.ERROR_MESSAGE);
					return;
				}
                //���½�������
                Process process = processes.get(i);
                process.id = id;
                process.arrivalTime = arrivalTime;
                process.burstTime = burstTime;
                process.priority = priority;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "��������Ч�����֣�", "����", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        updateProcessTable();//ˢ�±�
        scheduleProcesses();//���Ƚ���
    }

    //�ı���������� -> RR�㷨ʹ��
	public void appendToResultTextArea(String text) {
		resultTextArea.append(text);
		resultTextArea.setCaretPosition(resultTextArea.getDocument().getLength());
	}
	
	//���µ��Ƚ������㷨����ָ��
	private void updateResultTable() {
		//����鿴�������̰���ִ��˳������
		processes.sort(Comparator.comparingInt(p -> p.executionOrder));
		//����ȡ����
		resultTableModel = new DefaultTableModel(
				new String[] { "����ID", "����ʱ��", "����ʱ��", "���ȼ�", "��ʼʱ��", "���ʱ��", "��תʱ��", "�ȴ�ʱ��" }, 0);
		for (Process p : processes) {
			resultTableModel.addRow(new Object[] { p.id, p.arrivalTime, p.burstTime, p.priority, p.startTime,
					p.completionTime, p.turnaroundTime, p.waitingTime });
		}
		resultTable.setModel(resultTableModel);
		resultTable.setEnabled(false);

		// ���㲢��ʾ����ָ��
		double avgTurnaroundTime = ProcessUtils.getAverageTurnaroundTime(processes);
		double avgWaitingTime = ProcessUtils.getAverageWaitingTime(processes);

		String result = String.format("ƽ����תʱ��: %.2f\nƽ���ȴ�ʱ��: %.2f\n", avgTurnaroundTime, avgWaitingTime);
		appendToResultTextArea(result);

		resultTable.revalidate();
		resultTable.repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GUI().setVisible(true);
			}
		});
	}
}