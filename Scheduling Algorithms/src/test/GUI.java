package test;

import java.awt.event.*;
import java.awt.*;

import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private JComboBox<String> algorithmComboBox; // 调度算法选择
	private JComboBox<Integer> processCountComboBox; // 进程数选择
	private JTextField timeQuantumField; // 时间片输入框
	private JTable processTable; // 进程表格
	private JTable resultTable; // 调度结果表格
	private DefaultTableModel processTableModel; // 进程表格的模型
	private DefaultTableModel resultTableModel; // 调度结果表格的模型
	private JTextArea resultTextArea; // 算法评价
	private List<Process> processes; // 用来存储生成的进程数据

	private static String[] TYPE = { "FCFS", "SJF", "Priority", "RR" };
	private static Integer[] PROCESSNUM = { 5, 6, 7, 8, 9, 10 };
	private static String GENERATEBUTTON_TEXT = "生成进程";
	private static String SCHEDULEBUTTON_TEXT = "开始调度";
	private static String EDITBUTTON_TEXT = "编辑进程";
	private static String SAVEBUTTON_TEXT = "保存修改";

	public GUI() {
		setTitle("CPU调度算法");
		setSize(900, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// 进程数选择下拉框
		processCountComboBox = new JComboBox<>(PROCESSNUM);
		processCountComboBox.setSelectedIndex(0);

		// 调度算法选择下拉框
		algorithmComboBox = new JComboBox<>(TYPE);
		algorithmComboBox.setSelectedIndex(0);

		// 时间片输入框
		timeQuantumField = new JTextField(5);

		// 生成进程按钮
		JButton generateButton = new JButton(GENERATEBUTTON_TEXT);
		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateProcesses();
			}
		});

		// 调度按钮
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

		// 保存修改按钮
		JButton saveButton = new JButton(SAVEBUTTON_TEXT);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveEdits();
			}
		});

		// 示算法评价结果
		resultTextArea = new JTextArea(100, 30);
		resultTextArea.setEditable(false);
		resultTextArea.setLineWrap(true);
		resultTextArea.setWrapStyleWord(true);

		// 面板布局
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// 参数设置
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("进程数:"));
		topPanel.add(processCountComboBox);
		topPanel.add(new JLabel("调度算法:"));
		topPanel.add(algorithmComboBox);
		topPanel.add(new JLabel("输入时间片（RR算法）:"));
		topPanel.add(timeQuantumField);
		panel.add(topPanel);

		// 按钮
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(generateButton);
		buttonPanel.add(scheduleButton);
		buttonPanel.add(editButton);
		buttonPanel.add(saveButton);
		panel.add(buttonPanel);

		// 添加原始进程表格及其标签
		JPanel processPanel = new JPanel();
		processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));
		processPanel.add(new JLabel("原始进程:"));
		processPanel.add(new JScrollPane(processTable = new JTable()));
		panel.add(processPanel);

		// 添加调度结果表格及其标签
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.add(new JLabel("调度结果:"));
		resultPanel.add(new JScrollPane(resultTable = new JTable()));
		panel.add(resultPanel);

		// 添加算法评价文本框
		JPanel evaluationPanel = new JPanel();
		evaluationPanel.setLayout(new BoxLayout(evaluationPanel, BoxLayout.Y_AXIS));
		evaluationPanel.add(new JLabel("算法评价指标:（RR含调度过程信息）"));
		evaluationPanel.add(new JScrollPane(resultTextArea));
		panel.add(evaluationPanel);

		processPanel.setPreferredSize(new Dimension(900, 400));
		resultPanel.setPreferredSize(new Dimension(900, 400));
		evaluationPanel.setPreferredSize(new Dimension(900, 200));
		add(panel);
		adjustStyle(this, 18);
	}
	
	//调整大小
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

	// 生成进程
	private void generateProcesses() {
		//获取参数
		int numProcesses = (int) processCountComboBox.getSelectedItem();
		//进程队列
		processes = new ArrayList<>();
		Random rand = new Random();
		//随机生成进程
		for (int i = 0; i < numProcesses; i++) {
			int arrivalTime = rand.nextInt(10);
			int burstTime = rand.nextInt(10) + 1;
			int priority = rand.nextInt(10);
			processes.add(new Process(i, arrivalTime, burstTime, priority));
		}
		updateProcessTable();//更新表格
	}

	// 更新进程表格
	private void updateProcessTable() {
		//表格获取数据
		processTableModel = new DefaultTableModel(new String[] { "进程ID", "到达时间", "服务时间", "优先级" }, 0);
		for (Process p : processes) {
			processTableModel.addRow(new Object[] { p.id, p.arrivalTime, p.burstTime, p.priority });
		}
		processTable.setModel(processTableModel);
		processTable.setEnabled(false);
	}

	// 选择调度算法并执行
	private void scheduleProcesses() {
		//保证有可处理的进程
		if (processes == null || processes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "请生成进程！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
		//RR算法使用时间片
		int timeQuantum = 0;
		if ("RR".equals(selectedAlgorithm)) {
			try {
				timeQuantum = Integer.parseInt(timeQuantumField.getText());
				if (timeQuantum <= 0) {
					JOptionPane.showMessageDialog(this, "请输入大于0的时间片值", "输入无效", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "请输入大于0的时间片值", "输入无效", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		//清空文本框
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

		updateResultTable();//更新结果表
	}

	//编辑进程表格
	private void editProcessTable() {
		if (processes == null || processes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "请生成进程！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		processTable.setEnabled(true);
	}
	//保存进程表格
    private void saveEdits() {
    	//保证有可处理的进程
        if (processes == null || processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请生成进程！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //获取表格数据
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
					JOptionPane.showMessageDialog(this, "请输入大于等于0的值（服务时间需大于0）", "输入无效", JOptionPane.ERROR_MESSAGE);
					return;
				}
                //更新进程属性
                Process process = processes.get(i);
                process.id = id;
                process.arrivalTime = arrivalTime;
                process.burstTime = burstTime;
                process.priority = priority;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        updateProcessTable();//刷新表
        scheduleProcesses();//调度进程
    }

    //文本框内容添加 -> RR算法使用
	public void appendToResultTextArea(String text) {
		resultTextArea.append(text);
		resultTextArea.setCaretPosition(resultTextArea.getDocument().getLength());
	}
	
	//更新调度结果表格及算法评价指标
	private void updateResultTable() {
		//方便查看，将进程按照执行顺序排序
		processes.sort(Comparator.comparingInt(p -> p.executionOrder));
		//表格获取数据
		resultTableModel = new DefaultTableModel(
				new String[] { "进程ID", "到达时间", "服务时间", "优先级", "开始时间", "完成时间", "周转时间", "等待时间" }, 0);
		for (Process p : processes) {
			resultTableModel.addRow(new Object[] { p.id, p.arrivalTime, p.burstTime, p.priority, p.startTime,
					p.completionTime, p.turnaroundTime, p.waitingTime });
		}
		resultTable.setModel(resultTableModel);
		resultTable.setEnabled(false);

		// 计算并显示评价指标
		double avgTurnaroundTime = ProcessUtils.getAverageTurnaroundTime(processes);
		double avgWaitingTime = ProcessUtils.getAverageWaitingTime(processes);

		String result = String.format("平均周转时间: %.2f\n平均等待时间: %.2f\n", avgTurnaroundTime, avgWaitingTime);
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