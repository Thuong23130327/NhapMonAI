package algotithm;

import java.time.DayOfWeek; // <-- THÊM IMPORT NÀY
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import data.ProblemData;
import model.ElectiveRequirement;
import model.Lecturer;
import model.ScheduledClass;
import model.Timetable;

/**
 * ========================================================================= 5.
 * FITNESS FUNCTION (ĐÃ SỬA LỖI TYPE MISMATCH) - Sửa lỗi Map<Integer, ...> thành
 * Map<DayOfWeek, ...> - Sửa 1 lỗi logic nhỏ ở Mềm 4 (Di chuyển xa GV)
 * =========================================================================
 */
public class FitnessCalculator {
	// Các mức điểm
	final int HARD_CONSTRAINT_PENALTY = -1000; // Phạt Cứng
	final int SOFT_CONSTRAINT_PENALTY = -10; // Phạt Mềm
	final int SOFT_CONSTRAINT_BONUS = 5; // Thưởng Mềm

	ProblemData data;

	public FitnessCalculator(ProblemData data) {
		this.data = data;
	}

	private boolean isFar(String cluster1, String cluster2) {
		if (cluster1 == null || cluster2 == null || cluster1.equals(cluster2)) {
			return false;
		}
		String c_A = ProblemData.CLUSTER_A_TV_HD_CT;
		String c_B = ProblemData.CLUSTER_B_RD_PV;
		String c_C = ProblemData.CLUSTER_C_A1_A2;
		if (cluster1.equals(c_A) && (cluster2.equals(c_B) || cluster2.equals(c_C)))
			return true;
		if (cluster2.equals(c_A) && (cluster1.equals(c_B) || cluster1.equals(c_C)))
			return true;
		if (cluster1.equals(c_B) && cluster2.equals(c_C))
			return true;
		if (cluster2.equals(c_B) && cluster1.equals(c_C))
			return true;
		return false;
	}

	public void calculateFitness(Timetable timetable) {
		int currentFitness = 0;
		int hardViolations = 0;
		int softViolations = 0;
		int bonusPoints = 0;

		timetable.clearViolations();
		List<ScheduledClass> schedule = timetable.getSchedule();

		// --- 2. KIỂM TRA RÀNG BUỘC CỨNG (HARD CONSTRAINTS) ---

		for (int i = 0; i < schedule.size(); i++) {
			ScheduledClass classA = schedule.get(i);

			// Lỗi 1: Sức chứa
			if (classA.getCourse().getStudentCount() > classA.getRoom().getCapacity()) {
				currentFitness += HARD_CONSTRAINT_PENALTY;
				hardViolations++;
				timetable.addViolation(String.format("CỨNG: %s (%d SV) quá tải cho P: %s (chứa %d)",
						classA.getCourse().getName(), classA.getCourse().getStudentCount(), classA.getRoom().getId(),
						classA.getRoom().getCapacity()));
			}

			// Lỗi 2: Loại phòng
			if (!classA.getCourse().getRequiredRoomType().equals(classA.getRoom().getType())) {
				currentFitness += HARD_CONSTRAINT_PENALTY;
				hardViolations++;
				timetable.addViolation(String.format("CỨNG: %s (cần phòng %s) bị xếp vào P: %s (loại %s)",
						classA.getCourse().getName(), classA.getCourse().getRequiredRoomType(),
						classA.getRoom().getId(), classA.getRoom().getType()));
			}

			// Lỗi 3: Chuyên môn
			if (!classA.getLecturer().getQualifiedCourseIds().contains(classA.getCourse().getId())) {
				currentFitness += HARD_CONSTRAINT_PENALTY;
				hardViolations++;
				timetable.addViolation(String.format("CỨNG: GV %s không có chuyên môn dạy môn %s",
						classA.getLecturer().getName(), classA.getCourse().getName()));
			}

			// Lỗi 4, 5, 6: Xung đột (Trùng GV, Phòng, SV)
			for (int j = i + 1; j < schedule.size(); j++) {
				ScheduledClass classB = schedule.get(j);
				if (classA.getTimeSlot().overlapsWith(classB.getTimeSlot())) {
					if (classA.getLecturer().getId().equals(classB.getLecturer().getId())) {
						currentFitness += HARD_CONSTRAINT_PENALTY;
						hardViolations++;
						timetable.addViolation(
								String.format("CỨNG: TRÙNG LỊCH GV! %s (dạy %s và %s) tại %s (Tiết %d-%d)",
										classA.getLecturer().getName(), classA.getCourse().getName(),
										classB.getCourse().getName(), classA.getTimeSlot().getDay(),
										classA.getTimeSlot().getStartPeriod(), classA.getTimeSlot().getEndPeriod()));
					}
					if (classA.getRoom().getId().equals(classB.getRoom().getId())) {
						currentFitness += HARD_CONSTRAINT_PENALTY;
						hardViolations++;
						timetable.addViolation(
								String.format("CỨNG: TRÙNG PHÒNG! P: %s (lớp %s và %s) tại %s (Tiết %d-%d)",
										classA.getRoom().getId(), classA.getCourse().getName(),
										classB.getCourse().getName(), classA.getTimeSlot().getDay(),
										classA.getTimeSlot().getStartPeriod(), classA.getTimeSlot().getEndPeriod()));
					}
					if (classA.getCourse().getStudentGroup().equals(classB.getCourse().getStudentGroup())) {
						currentFitness += HARD_CONSTRAINT_PENALTY;
						hardViolations++;
						timetable.addViolation(
								String.format("CỨNG: TRÙNG LỊCH SV! Nhóm %s (học %s và %s) tại %s (Tiết %d-%d)",
										classA.getCourse().getStudentGroup(), classA.getCourse().getName(),
										classB.getCourse().getName(), classA.getTimeSlot().getDay(),
										classA.getTimeSlot().getStartPeriod(), classA.getTimeSlot().getEndPeriod()));
					}
				}
			}
		}

		Map<String, List<ScheduledClass>> classesByGroup = schedule.stream()
				.collect(Collectors.groupingBy(sc -> sc.getCourse().getStudentGroup()));

		// Lỗi 7: Tiên quyết
		for (Map.Entry<String, List<ScheduledClass>> entry : classesByGroup.entrySet()) {
			String studentGroup = entry.getKey();
			List<ScheduledClass> groupSchedule = entry.getValue();
			Set<String> courseIdsInSemester = groupSchedule.stream().map(sc -> sc.getCourse().getId())
					.collect(Collectors.toSet());

			for (ScheduledClass sc : groupSchedule) {
				for (String prereqId : sc.getCourse().getPrerequisiteCourseIds()) {
					if (courseIdsInSemester.contains(prereqId)) {
						currentFitness += HARD_CONSTRAINT_PENALTY;
						hardViolations++;
						String prereqName = data.getCourseById(prereqId) != null
								? data.getCourseById(prereqId).getName()
								: prereqId;
						timetable.addViolation(String.format("CỨNG: VI PHẠM TIÊN QUYẾT! Nhóm %s học %s cùng kỳ với %s",
								studentGroup, sc.getCourse().getName(), prereqName));
					}
				}
			}
		}

		// Lỗi CỨNG 8: Tín chỉ tự chọn
		for (ElectiveRequirement req : data.getElectiveRequirements()) {
			String studentGroup = req.getStudentGroupId();
			List<ScheduledClass> groupSchedule = classesByGroup.get(studentGroup);

			if (groupSchedule == null)
				continue; // Nhóm này không học kỳ này

			int totalElectiveCredits = 0;
			for (ScheduledClass sc : groupSchedule) {
				if (req.getElectiveCourseIds().contains(sc.getCourse().getId())) {
					totalElectiveCredits += sc.getCourse().getCredits();
				}
			}

			if (totalElectiveCredits < req.getCreditsRequired()) {
				currentFitness += HARD_CONSTRAINT_PENALTY;
				hardViolations++;
				timetable.addViolation(String.format("CỨNG: Nhóm %s thiếu tín chỉ tự chọn! (Có %d / Yêu cầu %d)",
						studentGroup, totalElectiveCredits, req.getCreditsRequired()));
			}
		}

		// --- 3. KIỂM TRA RÀNG BUỘC MỀM (SOFT CONSTRAINTS) ---

		// Mềm 1 & 2: Sở thích Giảng viên
		for (ScheduledClass sc : schedule) {
			String timeSlotId = sc.getTimeSlot().getId();
			if (sc.getLecturer().getPreferredTimeSlotIds().contains(timeSlotId)) {
				currentFitness += SOFT_CONSTRAINT_BONUS;
				bonusPoints++;
			}
			if (sc.getLecturer().getUndesiredTimeSlotIds().contains(timeSlotId)) {
				currentFitness += SOFT_CONSTRAINT_PENALTY;
				softViolations++;
				timetable.addViolation(String.format("MỀM: GV %s bị xếp vào giờ 'không muốn' (%s, %s Tiết %d-%d)",
						sc.getLecturer().getName(), sc.getTimeSlot().getId(), sc.getTimeSlot().getDay(),
						sc.getTimeSlot().getStartPeriod(), sc.getTimeSlot().getEndPeriod()));
			}
		}

		// Mềm 3, 5, 6: Di chuyển xa, Lỗ hổng, Quá tải (Sinh viên)
		for (Map.Entry<String, List<ScheduledClass>> entry : classesByGroup.entrySet()) {
			String studentGroup = entry.getKey();

			// ****** SỬA LỖI 1: Đổi Map<Integer, ...> thành Map<DayOfWeek, ...> ******
			Map<DayOfWeek, List<ScheduledClass>> classesByDay = entry.getValue().stream()
					.collect(Collectors.groupingBy(sc -> sc.getTimeSlot().getDay()));

			for (Map.Entry<DayOfWeek, List<ScheduledClass>> dayEntry : classesByDay.entrySet()) {
				DayOfWeek day = dayEntry.getKey(); // <-- SỬA LỖI 2: Đổi kiểu 'day'
				List<ScheduledClass> classesOnDay = dayEntry.getValue();
				classesOnDay.sort(Comparator.comparingInt(sc -> sc.getTimeSlot().getStartPeriod()));

				// (Mềm 6: Quá tải)
				if (classesOnDay.size() > 3) {
					currentFitness += SOFT_CONSTRAINT_PENALTY * (classesOnDay.size() - 3);
					softViolations++;
					timetable.addViolation(String.format("MỀM: Nhóm %s học quá tải (%d lớp) vào %s", // <-- SỬA LỖI 3:
																										// Dùng %s
							studentGroup, classesOnDay.size(), day));
				}

				for (int i = 0; i < classesOnDay.size() - 1; i++) {
					ScheduledClass classA = classesOnDay.get(i);
					ScheduledClass classB = classesOnDay.get(i + 1);
					int gapSize = classB.getTimeSlot().getStartPeriod() - (classA.getTimeSlot().getEndPeriod() + 1);

					// (Mềm 5: Lỗ hổng)
					if (gapSize >= 2 && gapSize <= 4) {
						currentFitness += SOFT_CONSTRAINT_PENALTY;
						softViolations++;
						timetable.addViolation(String.format("MỀM: Nhóm %s có 'lỗ hổng' %d tiết vào %s (giữa %s và %s)", // <--
																															// SỬA
																															// LỖI
																															// 4
								studentGroup, gapSize, day, classA.getCourse().getName(),
								classB.getCourse().getName()));
					}

					// (Mềm 3: Di chuyển xa)
					if (gapSize < 3) {
						String locA = classA.getRoom().getLocationCluster();
						String locB = classB.getRoom().getLocationCluster(); // Sửa lỗi logic (was classA)
						if (isFar(locA, locB)) {
							currentFitness += SOFT_CONSTRAINT_PENALTY;
							softViolations++;
							timetable.addViolation(String.format(
									"MỀM: Nhóm %s phải di chuyển xa (nghỉ %d tiết) trong %s (giữa %s và %s)", // <-- SỬA
																												// LỖI 5
									studentGroup, gapSize, day, locA, locB));
						}
					}
				}
			}
		}

		// Mềm 4: Khoảng cách di chuyển (Giảng viên)
		Map<Lecturer, List<ScheduledClass>> classesByLecturer = schedule.stream()
				.collect(Collectors.groupingBy(ScheduledClass::getLecturer));

		for (List<ScheduledClass> lecturerSchedule : classesByLecturer.values()) {
			// ****** SỬA LỖI 1 (Giống ở trên) ******
			Map<DayOfWeek, List<ScheduledClass>> classesByDay = lecturerSchedule.stream()
					.collect(Collectors.groupingBy(sc -> sc.getTimeSlot().getDay()));

			for (Map.Entry<DayOfWeek, List<ScheduledClass>> dayEntry : classesByDay.entrySet()) {
				DayOfWeek day = dayEntry.getKey(); // <-- SỬA LỖI 2
				List<ScheduledClass> classesOnDay = dayEntry.getValue();
				classesOnDay.sort(Comparator.comparingInt(sc -> sc.getTimeSlot().getStartPeriod()));

				for (int i = 0; i < classesOnDay.size() - 1; i++) {
					ScheduledClass classA = classesOnDay.get(i);
					ScheduledClass classB = classesOnDay.get(i + 1);
					int gapSize = classB.getTimeSlot().getStartPeriod() - (classA.getTimeSlot().getEndPeriod() + 1);

					if (gapSize < 3) {
						String locA = classA.getRoom().getLocationCluster();
						// ****** SỬA LỖI LOGIC NHỎ (was classA) ******
						String locB = classB.getRoom().getLocationCluster();
						if (isFar(locA, locB)) {
							currentFitness += SOFT_CONSTRAINT_PENALTY;
							softViolations++;
							timetable.addViolation(String.format(
									"MỀM: GV %s phải di chuyển xa (nghỉ %d tiết) trong %s (giữa %s và %s)", // <-- SỬA
																											// LỖI 3
									classA.getLecturer().getName(), gapSize, day, locA, locB));
						}
					}
				}
			}
		}

		// --- 4. TỔNG KẾT ---
		String summary = String.format("Tóm tắt: %d lỗi Cứng | %d lỗi Mềm | %d điểm Thưởng", hardViolations,
				softViolations, bonusPoints);
		timetable.addViolation(0, summary);
		timetable.setFitness(currentFitness);
	}
}