package view;

import javax.swing.table.AbstractTableModel;

import model.Course;
import model.ScheduledClass;
import model.TimeSlot;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// FILE MỚI - Bộ điều hợp (Adapter) cho JTable
public class TimetableTableModel extends AbstractTableModel {

	private List<ScheduledClass> schedule = new ArrayList<>();

	private String[] columnNames = { "Mã MH", "Tên môn học", "Nhóm TỔ", "Số Tín chỉ", "Lớp", "Thứ", "Tiết bắt đầu",
			"Số tiết", "Phòng", "Giảng viên", "Thời gian học" };

	public void setSchedule(List<ScheduledClass> schedule) {
		// Sắp xếp lịch theo Mã MH trước khi hiển thị
		schedule.sort(Comparator.comparing(sc -> sc.getCourse().getId()));
		this.schedule = schedule;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return schedule.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ScheduledClass sc = schedule.get(rowIndex);
		Course course = sc.getCourse();
		TimeSlot ts = sc.getTimeSlot();

		switch (columnIndex) {
		case 0:
			return course.getId();
		case 1:
			return course.getName();
		case 2:
			return course.getToNhom(); // Dùng getter mới
		case 3:
			return course.getCredits();
		case 4:
			return course.getStudentGroup();
		case 5:
			return convertDay(ts.getDay()); // Chuyển đổi Thứ
		case 6:
			return ts.getStartPeriod();
		case 7:
			return (ts.getEndPeriod() - ts.getStartPeriod()) + 1;
		case 8:
			return sc.getRoom().getId();
		case 9:
			return sc.getLecturer().getName();
		case 10:
			return course.getThoiGianHoc(); // Dùng getter mới
		default:
			return null;
		}
	}

	// Hàm phụ trợ để hiển thị "Thứ" cho đẹp
	private String convertDay(DayOfWeek day) {
		switch (day) {
		case MONDAY:
			return "Hai";
		case TUESDAY:
			return "Ba";
		case WEDNESDAY:
			return "Tư";
		case THURSDAY:
			return "Năm";
		case FRIDAY:
			return "Sáu";
		case SATURDAY:
			return "Bảy";
		case SUNDAY:
			return "CN";
		default:
			return "";
		}
	}
}