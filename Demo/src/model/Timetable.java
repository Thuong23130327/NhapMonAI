package model;

import java.util.ArrayList;
import java.util.List;

/**
 * FILE MỚI - Lớp này đại diện cho một "Cá thể" (một TKB hoàn chỉnh). Nó chứa
 * danh sách các lớp học, điểm fitness, và "biên bản vi phạm".
 */
public class Timetable {

	private List<ScheduledClass> schedule;
	private int fitness;
	private List<String> violations; // <-- Đây là danh sách bị thiếu

	/**
	 * Constructor mặc định
	 */
	public Timetable() {
		this.schedule = new ArrayList<>();
		this.violations = new ArrayList<>(); // Khởi tạo danh sách
		this.fitness = Integer.MIN_VALUE; // Khởi tạo điểm fitness thấp nhất
	}

	/**
	 * Copy Constructor (Dùng cho Elitism) QUAN TRỌNG: Xem cảnh báo lỗi logic bên
	 * dưới
	 */
	public Timetable(Timetable other) {
		// Tạm thời, chúng ta sẽ copy sâu (deep copy) danh sách
		this.schedule = new ArrayList<>();
		for (ScheduledClass sc : other.schedule) {
			// Yêu cầu ScheduledClass phải có Copy Constructor
			this.schedule.add(new ScheduledClass(sc));
		}

		this.fitness = other.fitness;
		this.violations = new ArrayList<>(other.violations);
	}

	// --- Các hàm quản lý Lịch học (Schedule) ---
	public void addClass(ScheduledClass sc) {
		this.schedule.add(sc);
	}

	public List<ScheduledClass> getSchedule() {
		return this.schedule;
	}

	// --- Các hàm quản lý Fitness ---
	public int getFitness() {
		return this.fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	// --- CÁC HÀM XỬ LÝ VI PHẠM (SỬA LỖI) ---

	/**
	 * Thêm 1 dòng vào biên bản vi phạm (Hàm này được FitnessCalculator gọi)
	 */
	public void addViolation(String violation) {
		this.violations.add(violation);
	}

	/**
	 * Thêm 1 dòng vào vị trí ĐẦU TIÊN của biên bản
	 */
	public void addViolation(int index, String violation) {
		if (index == 0) {
			this.violations.add(0, violation);
		} else {
			this.violations.add(violation);
		}
	}

	/**
	 * Xóa biên bản cũ (Hàm này được FitnessCalculator gọi)
	 */
	public void clearViolations() {
		this.violations.clear();
	}

	/**
	 * Lấy toàn bộ biên bản vi phạm (Hàm này được TimetableApp gọi - SỬA LỖI CỦA
	 * BẠN)
	 */
	public List<String> getViolations() {
		return this.violations;
	}
}