package data;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Course;
import model.ElectiveRequirement;
import model.Lecturer;
import model.Room;
import model.TimeSlot;

/**
 * DỮ LIỆU ĐẦU VÀO (PHIÊN BẢN FULL TRƯỜNG) - Khóa 21, 22, 23. - Mỗi khóa có 3
 * lớp: DTA, DTB, DTC. - Tổng cộng: Hơn 40 đầu lớp học phần để xếp lịch.
 */
public class ProblemData {

	// --- CẤU HÌNH CỤM PHÒNG ---
	public static final String CLUSTER_A_TV_HD_CT = "CLUSTER_A"; // Khu A (Giảng đường chính)
	public static final String CLUSTER_B_RD_PV = "CLUSTER_B"; // Khu B (Rạng Đông - Xa)
	public static final String CLUSTER_C_A1_A2 = "CLUSTER_C"; // Khu C (Cẩm Tú - Hơi xa)
	public static final String CLUSTER_D_TT_TN = "CLUSTER_D"; // Khu D (Thực hành - Rất xa)

	List<Course> mandatoryCourses;
	List<Course> allPossibleElectives;
	List<ElectiveRequirement> electiveRequirements;
	List<Lecturer> lecturers;
	List<Room> rooms;
	List<TimeSlot> timeSlots;
	Map<String, Course> courseMap = new HashMap<>();

	public ProblemData() {
		initializeData();
		for (Course c : mandatoryCourses)
			courseMap.put(c.getId(), c);
		for (Course c : allPossibleElectives)
			courseMap.put(c.getId(), c);
	}

	public Course getCourseById(String id) {
		return courseMap.get(id);
	}

	private void initializeData() {
		// =============================================================
		// 1. GIẢNG VIÊN (Cập nhật chuyên môn "khủng" để dạy 3 khóa)
		// =============================================================

		// Cô Nga: Chuyên dạy các môn Lý luận & Đạo đức (Dạy K23, K21)
		Lecturer l_Nga = new Lecturer("GV01", "L.T.B.Nga",
				Arrays.asList("K23_LSD_A", "K23_LSD_B", "K23_LSD_C", "K21_ETHICS_A", "K21_ETHICS_B", "K21_ETHICS_C"));

		// Thầy Tỉnh: Chuyên Mạng & Bảo mật (Dạy K23, K22)
		Lecturer l_Tinh = new Lecturer("GV02", "P.V.Tinh", Arrays.asList("K23_NET_LT_A", "K23_NET_LT_B", "K23_NET_LT_C",
				"K23_NET_TH_A1", "K23_NET_TH_B1", "K23_NET_TH_C1", "K22_SEC_LT_A", "K22_SEC_LT_B"));

		// Thầy Tống: Chuyên Công nghệ PM & Thực hành Mạng (Dạy K23, K22)
		Lecturer l_Tong = new Lecturer("GV03", "N.M.Tống",
				Arrays.asList("K23_NET_TH_A2", "K23_NET_TH_B2", "K23_NET_TH_C2", "K22_SE_LT_A", "K22_SE_LT_B",
						"K22_SE_LT_C", "K22_SE_TH_A1", "K22_SE_TH_B1", "K22_SE_TH_C1"));

		// Thầy Toàn: Chuyên Web & Mobile (Dạy K23, K22)
		Lecturer l_Toan = new Lecturer("GV04", "V.T.Toàn", Arrays.asList("K23_WEB_LT_A", "K23_WEB_LT_B", "K23_WEB_LT_C",
				"K22_MOB_LT_A", "K22_MOB_LT_B", "K22_MOB_LT_C"));

		// Thầy Long: Chuyên Python & Quản lý dự án (Dạy K23, K21)
		Lecturer l_Long = new Lecturer("GV05", "P.B.Long", Arrays.asList("K23_PY_LT_A", "K23_PY_LT_B", "K23_PY_LT_C",
				"K23_PY_TH_A1", "K23_PY_TH_B1", "K23_PY_TH_C1", "K21_PM_LT_A", "K21_PM_LT_B", "K21_PM_LT_C"));

		// Thầy Dũng: Chuyên dạy Thực hành (Hỗ trợ tất cả các môn TH)
		Lecturer l_Dung = new Lecturer("GV06", "N.V.Dũng",
				Arrays.asList("K23_WEB_TH_A1", "K23_WEB_TH_B1", "K23_WEB_TH_C1", "K22_MOB_TH_A1", "K22_MOB_TH_B1",
						"K22_MOB_TH_C1", "K22_SE_TH_A2", "K22_SE_TH_B2", "K22_SE_TH_C2"));

		// Cô Châu: Chuyên AI & Đồ án (Dạy K23, K21)
		Lecturer l_Chau = new Lecturer("GV07", "K.H.Châu",
				Arrays.asList("K23_AI_LT_A", "K23_AI_LT_B", "K23_AI_LT_C", "K21_CAP_A", "K21_CAP_B", "K21_CAP_C"));

		// Thêm sở thích GV
		l_Long.setUndesiredTimeSlotIds(Arrays.asList("T1", "T2")); // Né sáng Thứ 2
		l_Toan.setPreferredTimeSlotIds(Arrays.asList("T7", "T8", "T9")); // Thích dạy Thứ 4

		this.lecturers = Arrays.asList(l_Nga, l_Tinh, l_Tong, l_Toan, l_Long, l_Dung, l_Chau);

		// =============================================================
		// 2. PHÒNG HỌC (15 Phòng - Đủ sức chứa cho 3 khóa)
		// =============================================================
		this.rooms = Arrays.asList(
				// Giảng đường lớn (Lý thuyết)
				new Room("HD303", 150, "LECTURE", CLUSTER_A_TV_HD_CT),
				new Room("TV103", 120, "LECTURE", CLUSTER_A_TV_HD_CT),
				new Room("RD306", 120, "LECTURE", CLUSTER_B_RD_PV), new Room("RD204", 100, "LECTURE", CLUSTER_B_RD_PV),
				new Room("A101", 100, "LECTURE", CLUSTER_C_A1_A2), new Room("A102", 100, "LECTURE", CLUSTER_C_A1_A2),

				// Phòng máy (Thực hành)
				new Room("PM_01", 60, "LAB", CLUSTER_D_TT_TN), new Room("PM_02", 60, "LAB", CLUSTER_D_TT_TN),
				new Room("PM_03", 60, "LAB", CLUSTER_D_TT_TN), new Room("PM_04", 60, "LAB", CLUSTER_A_TV_HD_CT), // Phòng
																													// máy
																													// khu
																													// A
				new Room("PM_05", 60, "LAB", CLUSTER_A_TV_HD_CT), new Room("PM_06", 60, "LAB", CLUSTER_C_A1_A2) // Phòng
																												// máy
																												// khu C
		);

		// =============================================================
		// 3. TIMESLOTS (5 Ngày x 3 Ca = 15 Slots)
		// =============================================================
		List<TimeSlot> slots = new ArrayList<>();
		int slotId = 1;
		DayOfWeek[] days = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
				DayOfWeek.FRIDAY };
		for (DayOfWeek day : days) {
			slots.add(new TimeSlot("T" + slotId++, day, 1, 3)); // Sáng sớm
			slots.add(new TimeSlot("T" + slotId++, day, 4, 6)); // Sáng muộn
			slots.add(new TimeSlot("T" + slotId++, day, 7, 9)); // Chiều
		}
		this.timeSlots = slots;

		// =============================================================
		// 4. MÔN HỌC (PHẦN QUAN TRỌNG NHẤT)
		// =============================================================
		List<Course> courses = new ArrayList<>();
		String tK23 = "15/09 - 30/12/2025";
		String tK22 = "01/10 - 15/01/2026";
		String tK21 = "15/09 - 30/12/2025";

		// ---------------------------------------------------------
		// KHÓA 23 (Năm 2): 3 Lớp (DTA, DTB, DTC)
		// Môn học: Lịch sử Đảng, Mạng máy tính, Lập trình Web, Python
		// ---------------------------------------------------------

		// >> LỚP DH23DTA
		courses.add(new Course("K23_LSD_A", "Lịch sử Đảng", 80, "DH23DTA", "LECTURE", 3, "23A", tK23));
		courses.add(new Course("K23_NET_LT_A", "Mạng máy tính (LT)", 80, "DH23DTA", "LECTURE", 3, "23A", tK23));
		courses.add(new Course("K23_NET_TH_A1", "Mạng máy tính (TH)", 40, "DH23DTA_N1", "LAB", 2, "23A-1", tK23));
		courses.add(new Course("K23_NET_TH_A2", "Mạng máy tính (TH)", 40, "DH23DTA_N2", "LAB", 2, "23A-2", tK23));
		courses.add(new Course("K23_WEB_LT_A", "Lập trình Web (LT)", 80, "DH23DTA", "LECTURE", 3, "23A", tK23));
		courses.add(new Course("K23_WEB_TH_A1", "Lập trình Web (TH)", 40, "DH23DTA_N1", "LAB", 2, "23A-1", tK23));

		// >> LỚP DH23DTB
		courses.add(new Course("K23_LSD_B", "Lịch sử Đảng", 80, "DH23DTB", "LECTURE", 3, "23B", tK23));
		courses.add(new Course("K23_NET_LT_B", "Mạng máy tính (LT)", 80, "DH23DTB", "LECTURE", 3, "23B", tK23));
		courses.add(new Course("K23_NET_TH_B1", "Mạng máy tính (TH)", 40, "DH23DTB_N1", "LAB", 2, "23B-1", tK23));
		courses.add(new Course("K23_NET_TH_B2", "Mạng máy tính (TH)", 40, "DH23DTB_N2", "LAB", 2, "23B-2", tK23));
		courses.add(new Course("K23_PY_LT_B", "Python (LT)", 80, "DH23DTB", "LECTURE", 3, "23B", tK23));
		courses.add(new Course("K23_PY_TH_B1", "Python (TH)", 40, "DH23DTB_N1", "LAB", 2, "23B-1", tK23));

		// >> LỚP DH23DTC
		courses.add(new Course("K23_LSD_C", "Lịch sử Đảng", 80, "DH23DTC", "LECTURE", 3, "23C", tK23));
		courses.add(new Course("K23_NET_LT_C", "Mạng máy tính (LT)", 80, "DH23DTC", "LECTURE", 3, "23C", tK23));
		courses.add(new Course("K23_NET_TH_C1", "Mạng máy tính (TH)", 40, "DH23DTC_N1", "LAB", 2, "23C-1", tK23));
		courses.add(new Course("K23_NET_TH_C2", "Mạng máy tính (TH)", 40, "DH23DTC_N2", "LAB", 2, "23C-2", tK23));
		courses.add(new Course("K23_AI_LT_C", "Trí tuệ nhân tạo", 80, "DH23DTC", "LECTURE", 3, "23C", tK23));

		// ---------------------------------------------------------
		// KHÓA 22 (Năm 3): 3 Lớp (DTA, DTB, DTC)
		// Môn học: Công nghệ PM, An toàn TT, Lập trình Mobile
		// ---------------------------------------------------------

		// >> LỚP DH22DTA
		courses.add(new Course("K22_SE_LT_A", "Công nghệ PM (LT)", 70, "DH22DTA", "LECTURE", 3, "22A", tK22));
		courses.add(new Course("K22_SE_TH_A1", "Công nghệ PM (TH)", 35, "DH22DTA_N1", "LAB", 2, "22A-1", tK22));
		courses.add(new Course("K22_SE_TH_A2", "Công nghệ PM (TH)", 35, "DH22DTA_N2", "LAB", 2, "22A-2", tK22));
		courses.add(new Course("K22_MOB_LT_A", "Lập trình Mobile (LT)", 70, "DH22DTA", "LECTURE", 3, "22A", tK22));
		courses.add(new Course("K22_MOB_TH_A1", "Lập trình Mobile (TH)", 35, "DH22DTA_N1", "LAB", 2, "22A-1", tK22));

		// >> LỚP DH22DTB
		courses.add(new Course("K22_SE_LT_B", "Công nghệ PM (LT)", 70, "DH22DTB", "LECTURE", 3, "22B", tK22));
		courses.add(new Course("K22_SE_TH_B1", "Công nghệ PM (TH)", 35, "DH22DTB_N1", "LAB", 2, "22B-1", tK22));
		courses.add(new Course("K22_SE_TH_B2", "Công nghệ PM (TH)", 35, "DH22DTB_N2", "LAB", 2, "22B-2", tK22));
		courses.add(new Course("K22_SEC_LT_B", "An toàn thông tin", 70, "DH22DTB", "LECTURE", 3, "22B", tK22));

		// >> LỚP DH22DTC
		courses.add(new Course("K22_SE_LT_C", "Công nghệ PM (LT)", 70, "DH22DTC", "LECTURE", 3, "22C", tK22));
		courses.add(new Course("K22_SE_TH_C1", "Công nghệ PM (TH)", 35, "DH22DTC_N1", "LAB", 2, "22C-1", tK22));
		courses.add(new Course("K22_SE_TH_C2", "Công nghệ PM (TH)", 35, "DH22DTC_N2", "LAB", 2, "22C-2", tK22));
		courses.add(new Course("K22_MOB_LT_C", "Lập trình Mobile (LT)", 70, "DH22DTC", "LECTURE", 3, "22C", tK22));
		courses.add(new Course("K22_MOB_TH_C1", "Lập trình Mobile (TH)", 35, "DH22DTC_N1", "LAB", 2, "22C-1", tK22));

		// ---------------------------------------------------------
		// KHÓA 21 (Năm 4): 3 Lớp (DTA, DTB, DTC)
		// Môn học: Quản lý dự án, Đạo đức nghề nghiệp, Đồ án tốt nghiệp
		// ---------------------------------------------------------

		// >> LỚP DH21DTA
		courses.add(new Course("K21_PM_LT_A", "Quản lý dự án", 60, "DH21DTA", "LECTURE", 3, "21A", tK21));
		courses.add(new Course("K21_ETHICS_A", "Đạo đức nghề nghiệp", 60, "DH21DTA", "LECTURE", 2, "21A", tK21));
		courses.add(new Course("K21_CAP_A", "Đồ án tốt nghiệp", 60, "DH21DTA", "LECTURE", 5, "21A", tK21));

		// >> LỚP DH21DTB
		courses.add(new Course("K21_PM_LT_B", "Quản lý dự án", 60, "DH21DTB", "LECTURE", 3, "21B", tK21));
		courses.add(new Course("K21_ETHICS_B", "Đạo đức nghề nghiệp", 60, "DH21DTB", "LECTURE", 2, "21B", tK21));
		courses.add(new Course("K21_CAP_B", "Đồ án tốt nghiệp", 60, "DH21DTB", "LECTURE", 5, "21B", tK21));

		// >> LỚP DH21DTC
		courses.add(new Course("K21_PM_LT_C", "Quản lý dự án", 60, "DH21DTC", "LECTURE", 3, "21C", tK21));
		courses.add(new Course("K21_ETHICS_C", "Đạo đức nghề nghiệp", 60, "DH21DTC", "LECTURE", 2, "21C", tK21));
		courses.add(new Course("K21_CAP_C", "Đồ án tốt nghiệp", 60, "DH21DTC", "LECTURE", 5, "21C", tK21));

		this.mandatoryCourses = courses;
		this.allPossibleElectives = new ArrayList<>();
		this.electiveRequirements = new ArrayList<>();
	}

	// Getters giữ nguyên
	public List<Course> getMandatoryCourses() {
		return mandatoryCourses;
	}

	public List<Course> getAllPossibleElectives() {
		return allPossibleElectives;
	}

	public List<ElectiveRequirement> getElectiveRequirements() {
		return electiveRequirements;
	}

	public List<Lecturer> getLecturers() {
		return lecturers;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public List<TimeSlot> getTimeSlots() {
		return timeSlots;
	}
}