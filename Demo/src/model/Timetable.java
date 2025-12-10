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
	private List<String> violations; //Lí do cộng trừ điểm

	/**
	 * Constructor mặc định
	 */
	public Timetable() {
		this.schedule = new ArrayList<>();
		this.violations = new ArrayList<>(); 
		this.fitness = Integer.MIN_VALUE; // Khởi tạo điểm fitness thấp nhất
	}

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

	public void addViolation(int index, String violation) {
		if (index == 0) {
			this.violations.add(0, violation);
		} else {
			this.violations.add(violation);
		}
	}

	// Dọn ds lỗi
	public void clearViolations() {
		this.violations.clear();
	}

	public List<String> getViolations() {
		return this.violations;
	}
}