package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import data.ProblemData;
import model.ScheduledClass;
import model.TimeSlot;
import model.Timetable;

import java.awt.*;
import java.time.DayOfWeek;
import java.util.List;

/**
 * Giao diện Swing (NÂNG CẤP) Hiển thị TKB (trên) và Danh sách Lỗi (dưới) ĐÃ
 * SỬA: Hỗ trợ 4 ca học (Sáng, Chiều, Tối 1, Tối 2)
 */
public class TimetableFrame extends JFrame {

	public TimetableFrame(Timetable timetable, ProblemData data) {

		// 1. Cài đặt Cửa sổ (JFrame)
		setTitle("Kết quả TKB Tối Ưu (Fitness: " + timetable.getFitness() + ")");
		setSize(1200, 900); // Tăng chiều cao lên
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// --- 2. PHẦN TRÊN: Bảng TKB (JTable) ---
		String[] columnNames = { "Khung giờ", "THỨ 2", "THỨ 3", "THỨ 4", "THỨ 5", "THỨ 6" };

		// Gọi hàm "cầu nối" (ĐÃ SỬA)
		Object[][] tableData = createTableData(timetable, data);
		JTable table = new JTable(tableData, columnNames);
		table.setRowHeight(200); // <-- SỬA DÒNG NÀY (hoặc cao hơn nếu cần)
		table.setDefaultRenderer(Object.class, new MultiLineHtmlRenderer());

		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			{
				setHorizontalAlignment(JLabel.CENTER);
				setFont(getFont().deriveFont(Font.BOLD));
				setBackground(Color.LIGHT_GRAY);
			}
		});
		JScrollPane tableScrollPane = new JScrollPane(table);

		// --- 3. PHẦN DƯỚI: Danh sách Lỗi (JTextArea) ---
		JTextArea violationArea = new JTextArea();
		violationArea.setEditable(false);
		violationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		violationArea.setMargin(new Insets(5, 5, 5, 5));

		List<String> violations = timetable.getViolations();

		if (violations.isEmpty() || violations.size() <= 1) {
			violationArea.setText("TUYỆT VỜI! KHÔNG CÓ LỖI NÀO (hoặc chỉ còn lỗi mềm).");
			violationArea.setForeground(new Color(0, 100, 0));
		} else {
			violationArea.setText("DANH SÁCH CÁC VẤN ĐỀ CÒN TỒN TẠI:\n");
			violationArea.setForeground(Color.RED);
			for (String v : violations) {
				violationArea.append("• " + v + "\n");
			}
		}

		JScrollPane textScrollPane = new JScrollPane(violationArea);
		textScrollPane.setPreferredSize(new Dimension(0, 250));

		// --- 4. GHÉP 2 PHẦN: JSplitPane ---
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, textScrollPane);
		splitPane.setResizeWeight(0.70); // Cho TKB 70% không gian

		add(splitPane);
	}

	/**
	 * HÀM "CẦU NỐI" (ĐÃ SỬA) Biến đổi List<ScheduledClass> thành một mảng 2D
	 * (String[][])
	 */
	private Object[][] createTableData(Timetable timetable, ProblemData data) {

		// === THAY ĐỔI CHÍNH BẮT ĐẦU TỪ ĐÂY ===

		int numRows = 4; // SỬA: Sáng (1-3), Chiều (4-6), Tối (7-9), Tối (10-12)
		int numCols = 6;
		String[][] tableData = new String[numRows][numCols];

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				tableData[i][j] = "<html>";
			}
		}

		// SỬA: Thêm 4 hàng
		tableData[0][0] += "Sáng (1-3)</html>";
		tableData[1][0] += "Chiều (4-6)</html>";
		tableData[2][0] += "Tối (7-9)</html>"; // <-- DÒNG MỚI
		tableData[3][0] += "Tối (10-12)</html>"; // <-- DÒNG MỚI

		for (ScheduledClass sc : timetable.getSchedule()) {
			TimeSlot ts = sc.getTimeSlot();

			// SỬA: Logic tìm hàng (row)
			int row = -1;
			if (ts.getStartPeriod() == 1)
				row = 0; // Sáng
			else if (ts.getStartPeriod() == 4)
				row = 1; // Chiều
			else if (ts.getStartPeriod() == 7)
				row = 2; // Tối 1
			else if (ts.getStartPeriod() == 10)
				row = 3; // Tối 2

			int col = -1;
			if (ts.getDay() == DayOfWeek.MONDAY)
				col = 1;
			else if (ts.getDay() == DayOfWeek.TUESDAY)
				col = 2;
			else if (ts.getDay() == DayOfWeek.WEDNESDAY)
				col = 3;
			else if (ts.getDay() == DayOfWeek.THURSDAY)
				col = 4;
			else if (ts.getDay() == DayOfWeek.FRIDAY)
				col = 5;

			if (row != -1 && col != -1) {
				// (Code điền dữ liệu vẫn như cũ)
				tableData[row][col] += String.format(
						"<b>%s</b> (P: %s, Cụm: %s)<br>GV: %s<br><i>Nhóm: %s (%d SV)</i><br>---<br>",
						sc.getCourse().getName(), sc.getRoom().getId(), sc.getRoom().getLocationCluster(),
						sc.getLecturer().getName(), sc.getCourse().getStudentGroup(), sc.getCourse().getStudentCount());
			}
		}

		// SỬA: Vòng lặp này tự động đúng vì numRows = 4
		for (int i = 0; i < numRows; i++) {
			for (int j = 1; j < numCols; j++) {
				if (tableData[i][j].equals("<html>")) {
					tableData[i][j] = "";
				} else {
					tableData[i][j] += "</html>";
				}
			}
		}

		return tableData;
	}

}