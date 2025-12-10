package view;

import data.ProblemData;
import model.Population;
import model.ScheduledClass;
import model.Timetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import algotithm.GeneticAlgorithm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimetableApp extends JFrame {

	// --- CÁC THÀNH PHẦN LOGIC ---
	private GeneticAlgorithm ga;
	private ProblemData data;

	// --- CÁC THÀNH PHẦN DỮ LIỆU ---
	// Quan trọng: Lưu giữ bản sao đầy đủ của TKB để lọc đi lọc lại
	private List<ScheduledClass> masterSchedule = new ArrayList<>();

	// --- CÁC THÀNH PHẦN GIAO DIỆN ---
	private JTable table;
	private TimetableTableModel tableModel;
	private JTextArea violationLog;
	private JButton btnFilterAll, btnFilterK23, btnFilterK22; // Các nút lọc

	public TimetableApp() {
		// 1. KHỞI TẠO BACKEND
		this.data = new ProblemData();
		this.ga = new GeneticAlgorithm(data, 100, 0.01, 2, 5);

		// 2. CÀI ĐẶT CỬA SỔ
		setTitle("Hệ Thống Xếp Thời Khóa Biểu Thông Minh");
		setSize(1600, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// --- PHẦN TRÊN: THANH CÔNG CỤ (TOOLBAR) & LỌC ---
		JPanel topPanel = new JPanel(new BorderLayout());

		// Khu vực nút chạy GA
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton runButton = new JButton("▶ CHẠY XẾP LỊCH (GA)");
		runButton.setFont(new Font("Arial", Font.BOLD, 14));
		runButton.setBackground(new Color(0, 153, 76)); // Màu xanh lá
		runButton.setForeground(Color.WHITE);
		runButton.addActionListener(e -> runGATask());
		actionPanel.add(runButton);

		// Khu vực nút LỌC (Filter)
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		filterPanel.add(new JLabel("Lọc theo khóa: "));

		btnFilterAll = createFilterButton("Hiển thị Tất cả");
		btnFilterK23 = createFilterButton("Khóa 23 (DH23)");
		btnFilterK22 = createFilterButton("Khóa 22 (DH22)");

		// Mặc định ẩn các nút lọc cho đến khi chạy xong
		enableFilterButtons(false);

		filterPanel.add(btnFilterAll);
		filterPanel.add(btnFilterK23);
		filterPanel.add(btnFilterK22);

		topPanel.add(actionPanel, BorderLayout.WEST);
		topPanel.add(filterPanel, BorderLayout.EAST);
		topPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

		add(topPanel, BorderLayout.NORTH);

		// --- PHẦN GIỮA: BẢNG VÀ LOG ---
		// 3. TẠO BẢNG HIỂN THỊ TKB
		tableModel = new TimetableTableModel();
		table = new JTable(tableModel);
		table.setRowHeight(30);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		JScrollPane tableScrollPane = new JScrollPane(table);

		// 4. TẠO KHU VỰC HIỂN THỊ LỖI
		violationLog = new JTextArea();
		violationLog.setEditable(false);
		violationLog.setFont(new Font("Monospaced", Font.PLAIN, 13));
		violationLog.setMargin(new Insets(10, 10, 10, 10));
		JScrollPane logScrollPane = new JScrollPane(violationLog);

		// Tiêu đề cho vùng Log
		JPanel logPanel = new JPanel(new BorderLayout());
		JLabel logLabel = new JLabel("  Biên bản Vi phạm & Đánh giá chất lượng:");
		logLabel.setFont(new Font("Arial", Font.BOLD, 14));
		logLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
		logPanel.add(logLabel, BorderLayout.NORTH);
		logPanel.add(logScrollPane, BorderLayout.CENTER);
		logPanel.setPreferredSize(new Dimension(400, 0));

		// 5. GHÉP GIAO DIỆN (SPLIT PANE)
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, logPanel);
		splitPane.setDividerLocation(1100);
		splitPane.setResizeWeight(0.8); // Ưu tiên không gian cho bảng

		add(splitPane, BorderLayout.CENTER);

		// --- SỰ KIỆN CHO CÁC NÚT LỌC ---
		btnFilterAll.addActionListener(e -> filterSchedule("ALL"));
		btnFilterK23.addActionListener(e -> filterSchedule("DH23"));
		btnFilterK22.addActionListener(e -> filterSchedule("DH22")); // Ví dụ nếu có DH22
	}

	// --- HÀM TẠO NÚT CHO ĐẸP ---
	private JButton createFilterButton(String text) {
		JButton btn = new JButton(text);
		btn.setFocusPainted(false);
		return btn;
	}

	private void enableFilterButtons(boolean enable) {
		btnFilterAll.setEnabled(enable);
		btnFilterK23.setEnabled(enable);
		btnFilterK22.setEnabled(enable);
	}

	// --- LOGIC LỌC DỮ LIỆU (QUAN TRỌNG) ---
	private void filterSchedule(String keyword) {
		if (masterSchedule.isEmpty())
			return;

		List<ScheduledClass> filteredList;

		if (keyword.equals("ALL")) {
			// Nếu chọn tất cả -> Lấy lại danh sách gốc
			filteredList = new ArrayList<>(masterSchedule);
		} else {
			// Lọc: Chỉ lấy lớp nào mà Nhóm SV có chứa từ khóa (VD: "DH23")
			filteredList = masterSchedule.stream().filter(sc -> sc.getCourse().getStudentGroup().contains(keyword))
					.collect(Collectors.toList());
		}

		// Cập nhật lên bảng
		tableModel.setSchedule(filteredList);

		// Cập nhật tiêu đề cửa sổ để biết đang xem cái gì
		setTitle("Hệ Thống Xếp TKB - Đang xem: " + (keyword.equals("ALL") ? "Toàn bộ" : keyword));
	}

	// --- LOGIC CHẠY GA ---
	private void runGATask() {
		// Khóa nút lọc khi đang chạy
		enableFilterButtons(false);
		violationLog.setText("Đang khởi động thuật toán tiến hóa...\nVui lòng đợi...");

		new SwingWorker<Timetable, String>() {
			@Override
			protected Timetable doInBackground() throws Exception {
				// Chạy thuật toán
				Population pop = ga.runEvolution(2000);
				return pop.getFittest();
			}

			@Override
			protected void done() {
				try {
					Timetable bestTimetable = get();

					// 1. LƯU KẾT QUẢ VÀO BIẾN GỐC (Master Data)
					masterSchedule = bestTimetable.getSchedule();

					// 2. HIỂN THỊ TOÀN BỘ LÊN BẢNG
					tableModel.setSchedule(masterSchedule);

					// 3. HIỂN THỊ LOG
					List<String> violations = bestTimetable.getViolations();
					violationLog.setText(String.join("\n", violations));

					// 4. MỞ KHÓA CÁC NÚT LỌC
					enableFilterButtons(true);

					JOptionPane.showMessageDialog(TimetableApp.this,
							"Xếp lịch hoàn tất!\nFitness: " + bestTimetable.getFitness(), "Thành công",
							JOptionPane.INFORMATION_MESSAGE);

				} catch (Exception e) {
					e.printStackTrace();
					violationLog.setText("Lỗi: " + e.getMessage());
				}
			}
		}.execute();
	}

	public static void main(String[] args) {
		// Set giao diện đẹp theo hệ điều hành
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		SwingUtilities.invokeLater(() -> {
			new TimetableApp().setVisible(true);
		});
	}
}