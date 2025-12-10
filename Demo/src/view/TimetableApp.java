package view;

import javax.swing.*;

import algotithm.GeneticAlgorithm;
import data.ProblemData;
import model.Population;
import model.Timetable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// FILE MỚI - Dùng để chạy ứng dụng
public class TimetableApp extends JFrame {
	private GeneticAlgorithm ga;
	private ProblemData data;
	private JTable table;
	private TimetableTableModel tableModel;
	private JTextArea violationLog; // Thêm JTextArea để xem biên bản vi phạm

	public TimetableApp() {
		// 1. KHỞI TẠO BACKEND (Logic GA của bạn)
		// TODO: Bạn phải nạp dữ liệu thật vào ProblemData()
		this.data = new ProblemData();

		// Khởi tạo GA
		this.ga = new GeneticAlgorithm(data, 100, 0.01, 2, 5);

		// 2. CÀI ĐẶT CỬA SỔ
		setTitle("Xếp Thời Khóa Biểu Bằng Giải Thuật Di Truyền");
		setSize(1600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 3. TẠO BẢNG HIỂN THỊ TKB
		tableModel = new TimetableTableModel();
		table = new JTable(tableModel);
		table.setRowHeight(25);
		table.setFont(new Font("Arial", Font.PLAIN, 14));
		JScrollPane tableScrollPane = new JScrollPane(table);

		// 4. TẠO KHU VỰC HIỂN THỊ LỖI (BIÊN BẢN VI PHẠM)
		violationLog = new JTextArea();
		violationLog.setEditable(false);
		violationLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
		violationLog.setMargin(new Insets(5, 5, 5, 5));
		JScrollPane logScrollPane = new JScrollPane(violationLog);
		logScrollPane.setPreferredSize(new Dimension(400, 800));

		// 5. TẠO NÚT BẤM
		JButton runButton = new JButton("Chạy GA và Xếp TKB (Chạy 1000 thế hệ)");
		runButton.setFont(new Font("Arial", Font.BOLD, 16));
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runGATask();
			}
		});

		// 6. GHÉP GIAO DIỆN
		// Dùng SplitPane để chia đôi cửa sổ
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, logScrollPane);
		splitPane.setDividerLocation(1150); // Vị trí chia

		add(splitPane, BorderLayout.CENTER);
		add(runButton, BorderLayout.SOUTH);
	}

	private void runGATask() {
		// Chạy GA trên một Thread riêng để GUI không bị "đơ"
		new SwingWorker<Timetable, Void>() {
			@Override
			protected Timetable doInBackground() throws Exception {
				System.out.println("Bắt đầu chạy GA...");
				// Chạy thuật toán
				Population pop = ga.runEvolution(1000);
				// Lấy TKB tốt nhất
				Timetable bestTimetable = pop.getFittest();
				return bestTimetable;
			}

			@Override
			protected void done() {
				try {
					Timetable bestTimetable = get();
					System.out.println("GA chạy xong! Fitness tốt nhất: " + bestTimetable.getFitness());

					// Cập nhật TKB lên JTable
					tableModel.setSchedule(bestTimetable.getSchedule());

					// Cập nhật "Biên bản vi phạm" lên JTextArea
					List<String> violations = bestTimetable.getViolations();
					violationLog.setText(String.join("\n", violations));

				} catch (Exception e) {
					e.printStackTrace();
					violationLog.setText("Đã xảy ra lỗi khi chạy GA:\n" + e.getMessage());
				}
			}
		}.execute();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new TimetableApp().setVisible(true);
		});
	}
}