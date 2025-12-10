package algotithm;

import model.*;
import data.ProblemData;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FITNESS FUNCTION (CẬP NHẬT LOGIC LỚP & NHÓM THỰC HÀNH)
 */
public class FitnessCalculator {
	final int HARD_CONSTRAINT_PENALTY = -1000;
	final int SOFT_CONSTRAINT_PENALTY = -10;
	final int SOFT_CONSTRAINT_BONUS = 5;

	ProblemData data;

	public FitnessCalculator(ProblemData data) {
		this.data = data;
	}

	// Lớp gốc
	private String getBaseClass(String groupName) {
		if (groupName.contains("_")) {
			return groupName.split("_")[0];
		}
		return groupName;
	}

	// Check xem co xa nhau qua ko
	private boolean isFar(String cluster1, String cluster2) {
		if (cluster1 == null || cluster2 == null || cluster1.equalsIgnoreCase(cluster2))
			return false;
		String cumTV = ProblemData.CLUSTER_A_TV_HD_CT;
		String cumRD = ProblemData.CLUSTER_B_RD_PV;
		String cumA1 = ProblemData.CLUSTER_C_A1_A2;
		if (cluster1.equals(cumTV) && (cluster2.equals(cumRD) || cluster2.equals(cumA1)))
			return true;
		if (cluster2.equals(cumTV) && (cluster1.equals(cumRD) || cluster1.equals(cumA1)))
			return true;
		if (cluster1.equals(cumRD) && cluster2.equals(cumA1))
			return false;
		if (cluster2.equals(cumRD) && cluster1.equals(cumA1))
			return false;
		return false;
	}

	public void calculateFitness(Timetable timetable) {
		int currentFitness = 0;
		int hardViolations = 0;
		int softViolations = 0;
		int bonusPoints = 0;

		timetable.clearViolations();
		List<ScheduledClass> schedule = timetable.getSchedule();

		// --- KIỂM TRA RÀNG BUỘC CỨNG ---
		for (int i = 0; i < schedule.size(); i++) {
			ScheduledClass classA = schedule.get(i);

			// 1, 2, 3. Lỗi Sức chứa, Loại phòng, Chuyên môn (Giữ nguyên)
			if (classA.getCourse().getStudentCount() > classA.getRoom().getCapacity()) {
				currentFitness += HARD_CONSTRAINT_PENALTY;
				hardViolations++;
				timetable.addViolation("CỨNG: Quá tải phòng " + classA.getRoom().getId());
			}
			if (!classA.getCourse().getRequiredRoomType().equalsIgnoreCase(classA.getRoom().getType())) {
				currentFitness += HARD_CONSTRAINT_PENALTY;
				hardViolations++;
				timetable.addViolation("CỨNG: Sai loại phòng môn " + classA.getCourse().getName());
			}
			if (!classA.getLecturer().getQualifiedCourseIds().contains(classA.getCourse().getId())) {
				currentFitness += HARD_CONSTRAINT_PENALTY;
				hardViolations++;
				timetable.addViolation("CỨNG: GV " + classA.getLecturer().getName() + " trái chuyên môn");
			}
			// Lặp lòng
			// Check với cái ca học tiếp theo
			for (int j = i + 1; j < schedule.size(); j++) {
				ScheduledClass classB = schedule.get(j);

				if (classA.getTimeSlot().overlapsWith(classB.getTimeSlot())) {
					// Lỗi 4: Trùng GV
					if (classA.getLecturer().getId().equals(classB.getLecturer().getId())) {
						currentFitness += HARD_CONSTRAINT_PENALTY;
						hardViolations++;
						timetable.addViolation("CỨNG: GV " + classA.getLecturer().getName() + " bị trùng lịch.");
					}
					// Lỗi 5: Trùng Phòng
					if (classA.getRoom().getId().equals(classB.getRoom().getId())) {
						currentFitness += HARD_CONSTRAINT_PENALTY;
						hardViolations++;
						timetable.addViolation("CỨNG: Phòng " + classA.getRoom().getId() + " bị trùng.");
					}

					// --- LỖI 6: TRÙNG LỊCH SINH VIÊN (LOGIC MỚI QUAN TRỌNG) ---
					String groupA = classA.getCourse().getStudentGroup(); // VD: DH23DTC
					String groupB = classB.getCourse().getStudentGroup(); // VD: DH23DTC_N1

					// Trường hợp 1: Trùng hoàn toàn (VD: 2 môn Lý thuyết của DH23DTC cùng giờ) ->
					// PHẠT
					boolean exactMatch = groupA.equals(groupB);

					// Trường hợp 2: Quan hệ Cha-Con (Lý thuyết vs Thực hành) -> PHẠT
					// (DH23DTC trùng DH23DTC_N1) -> Sinh viên không thể phân thân
					boolean parentChildConflict = groupA.equals(getBaseClass(groupB))
							|| groupB.equals(getBaseClass(groupA));

					// Trường hợp 3: Anh em song sinh (Thực hành N1 vs Thực hành N2) -> KHÔNG PHẠT
					// (DH23DTC_N1 và DH23DTC_N2 khác nhau và không ai chứa ai)

					if (exactMatch || parentChildConflict) {
						currentFitness += HARD_CONSTRAINT_PENALTY;
						hardViolations++;
						timetable.addViolation(String.format("CỨNG: Trùng lịch SV! Nhóm %s và %s tại %s", groupA,
								groupB, classA.getTimeSlot().getDay()));
					}
				}
			}
		}

		// --- KIỂM TRA RÀNG BUỘC MỀM ---

		// Gom nhóm theo LỚP GỐC (Base Class) để xét lỗ hổng/đi xa
		// Nghĩa là DH23DTC, DH23DTC_N1, DH23DTC_N2 đều được gom vào 1 lịch chung để
		// kiểm tra
		Map<String, List<ScheduledClass>> classesByBaseGroup = schedule.stream()
				.collect(Collectors.groupingBy(sc -> getBaseClass(sc.getCourse().getStudentGroup())));

		for (Map.Entry<String, List<ScheduledClass>> entry : classesByBaseGroup.entrySet()) {
			String baseGroup = entry.getKey(); // VD: DH23DTC

			Map<DayOfWeek, List<ScheduledClass>> classesByDay = entry.getValue().stream()
					.collect(Collectors.groupingBy(sc -> sc.getTimeSlot().getDay()));

			for (Map.Entry<DayOfWeek, List<ScheduledClass>> dayEntry : classesByDay.entrySet()) {
				List<ScheduledClass> classesOnDay = dayEntry.getValue();
				classesOnDay.sort(Comparator.comparingInt(sc -> sc.getTimeSlot().getStartPeriod()));

				// Logic kiểm tra Mềm (Lỗ hổng, đi xa)
				for (int i = 0; i < classesOnDay.size() - 1; i++) {
					ScheduledClass classA = classesOnDay.get(i);
					ScheduledClass classB = classesOnDay.get(i + 1);

					// Chỉ so sánh nếu chúng có liên quan logic sinh viên (Cha-Con hoặc cùng nhóm)
					// Bỏ qua so sánh giữa N1 và N2 (vì là 2 nhóm người khác nhau)
					String gA = classA.getCourse().getStudentGroup();
					String gB = classB.getCourse().getStudentGroup();

					// Nếu là N1 vs N2 -> Bỏ qua
					if (!gA.equals(gB) && !gA.equals(baseGroup) && !gB.equals(baseGroup))
						continue;

					int gap = classB.getTimeSlot().getStartPeriod() - (classA.getTimeSlot().getEndPeriod() + 1);

					// Phạt lỗ hổng
					if (gap >= 2 && gap <= 4) {
						currentFitness += SOFT_CONSTRAINT_PENALTY;
						softViolations++;
						timetable.addViolation("MỀM: Lỗ hổng lịch học " + baseGroup);
					}
					// Phạt đi xa
					if (gap < 3
							&& isFar(classA.getRoom().getLocationCluster(), classB.getRoom().getLocationCluster())) {
						currentFitness += SOFT_CONSTRAINT_PENALTY;
						softViolations++;
						timetable.addViolation("MỀM: Di chuyển xa " + baseGroup);
					}
				}
			}
		}

		// --- TỔNG KẾT ---
		String summary = String.format("Kết quả: %d Lỗi Cứng | %d Lỗi Mềm", hardViolations, softViolations);
		timetable.addViolation(0, summary);
		timetable.setFitness(currentFitness);
	}
}