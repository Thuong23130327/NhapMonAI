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
 * Lớp này chứa TOÀN BỘ dữ liệu đầu vào của bài toán.
 * (CẬP NHẬT: THÊM MÔN PYTHON VÀ TRÍ TUỆ NHÂN TẠO)
 * * Mô phỏng Lớp "DH23DTC" (khoảng 100 SV)
 * - Môn Lý thuyết: 1 lớp chung (sĩ số 100) -> studentGroup="DH23DTC-LT"
 * - Môn Thực hành: 2 lớp song song (sĩ số 50) -> studentGroup="DH23DTC-TH1" và "DH23DTC-TH2"
 * * TỔNG CỘNG: 7 Môn học -> 7 (LT) + 12 (TH) = 19 Lớp học (events) cần xếp.
 */
public class ProblemData {

	// Hằng số cho các Cụm
	public static final String CLUSTER_A_TV_HD_CT = "CLUSTER_A"; // Gần
	public static final String CLUSTER_B_RD_PV = "CLUSTER_B"; // Xa
	public static final String CLUSTER_C_A1_A2 = "CLUSTER_C"; // Hơi xa
	public static final String CLUSTER_D_TT_TN = "CLUSTER_D"; // Rất xa

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
		// 1. GIẢNG VIÊN (Thêm GV Dũng và Châu)
		Lecturer l_Binh = new Lecturer("GV01", "L.T.B.Nga", Arrays.asList("C001"));
		Lecturer l_Tinh = new Lecturer("GV02", "P.V.Tinh", Arrays.asList("C002_LT", "C002_TH1", "C003_LT"));
		Lecturer l_Tong = new Lecturer("GV03", "N.M.Tống", Arrays.asList("C002_TH2"));
		Lecturer l_Toan = new Lecturer("GV04", "V.T.Toàn", Arrays.asList("C003_LT", "C003_TH1", "C003_TH2"));
		Lecturer l_Long = new Lecturer("GV05", "P.B.Long", Arrays.asList("C004_LT", "C004_TH1", "C004_TH2", "C002_LT"));
		Lecturer l_Hanh = new Lecturer("GV06", "N.T.M.Hương", Arrays.asList("C005_LT", "C005_TH1"));
		Lecturer l_Dien = new Lecturer("GV07", "L.C.Diện", Arrays.asList("C005_TH2", "C005_LT"));
		
		// GV MỚI
		Lecturer l_Du = new Lecturer("GV08", "N.V.Dũng", Arrays.asList("C006_LT", "C006_TH1", "C006_TH2"));
		Lecturer l_Chau = new Lecturer("GV09", "K.H.Châu", Arrays.asList("C007_LT", "C007_TH1", "C007_TH2"));

		// Thêm sở thích
		l_Long.setUndesiredTimeSlotIds(Arrays.asList("T1", "T13")); // Không muốn dạy sáng T2, T6
		l_Toan.setPreferredTimeSlotIds(Arrays.asList("T5", "T8")); // Thích dạy chiều T3, T4

		this.lecturers = Arrays.asList(l_Binh, l_Tinh, l_Tong, l_Toan, l_Long, l_Hanh, l_Dien, l_Du, l_Chau);

		// 2. PHÒNG HỌC (Thêm 5 phòng mới)
		Room p_HD303 = new Room("HD303", 120, "LECTURE", CLUSTER_A_TV_HD_CT);
		Room p_TV202 = new Room("TV202", 60, "LAB", CLUSTER_A_TV_HD_CT);
		Room p_P6 = new Room("P6", 60, "LAB", CLUSTER_D_TT_TN); // Xa
		Room p_P5 = new Room("P5", 60, "LAB", CLUSTER_D_TT_TN); // Xa
		Room p_TV103 = new Room("TV103", 120, "LECTURE", CLUSTER_A_TV_HD_CT);
		Room p_P2 = new Room("P2", 60, "LAB", CLUSTER_D_TT_TN); // Xa
		Room p_RD306 = new Room("RD306", 120, "LECTURE", CLUSTER_B_RD_PV); // Rất xa
		Room p_P1 = new Room("P1", 60, "LAB", CLUSTER_D_TT_TN); // Xa
		Room p_RD204 = new Room("RD204", 120, "LECTURE", CLUSTER_B_RD_PV); // Rất xa
		Room p_P4 = new Room("P4", 60, "LAB", CLUSTER_D_TT_TN); // Xa
		
		// PHÒNG MỚI
		Room p_A101 = new Room("A101", 120, "LECTURE", CLUSTER_C_A1_A2);
		Room p_A102 = new Room("A102", 120, "LECTURE", CLUSTER_C_A1_A2);
		Room p_C101 = new Room("C101", 60, "LAB", CLUSTER_A_TV_HD_CT);
		Room p_C102 = new Room("C102", 60, "LAB", CLUSTER_A_TV_HD_CT);
		Room p_C103 = new Room("C103", 60, "LAB", CLUSTER_A_TV_HD_CT);
		
		this.rooms = Arrays.asList(
			p_HD303, p_TV202, p_P6, p_P5, p_TV103, p_P2, p_RD306, p_P1, p_RD204, p_P4,
			p_A101, p_A102, p_C101, p_C102, p_C103
		);

		// 3. TIMESLOTS (Chia nhỏ 15 tiết)
		TimeSlot t_2_1_3 = new TimeSlot("T1", DayOfWeek.MONDAY, 1, 3);
		TimeSlot t_2_4_6 = new TimeSlot("T2", DayOfWeek.MONDAY, 4, 6);
		TimeSlot t_2_7_9 = new TimeSlot("T3", DayOfWeek.MONDAY, 7, 9);
		
		TimeSlot t_3_1_3 = new TimeSlot("T4", DayOfWeek.TUESDAY, 1, 3);
		TimeSlot t_3_4_6 = new TimeSlot("T5", DayOfWeek.TUESDAY, 4, 6);
		TimeSlot t_3_7_9 = new TimeSlot("T6", DayOfWeek.TUESDAY, 7, 9);

		TimeSlot t_4_1_3 = new TimeSlot("T7", DayOfWeek.WEDNESDAY, 1, 3);
		TimeSlot t_4_4_6 = new TimeSlot("T8", DayOfWeek.WEDNESDAY, 4, 6);
		TimeSlot t_4_7_9 = new TimeSlot("T9", DayOfWeek.WEDNESDAY, 7, 9);
		
		TimeSlot t_5_1_3 = new TimeSlot("T10", DayOfWeek.THURSDAY, 1, 3);
		TimeSlot t_5_4_6 = new TimeSlot("T11", DayOfWeek.THURSDAY, 4, 6);
		TimeSlot t_5_7_9 = new TimeSlot("T12", DayOfWeek.THURSDAY, 7, 9);
		
		TimeSlot t_6_1_3 = new TimeSlot("T13", DayOfWeek.FRIDAY, 1, 3);
		TimeSlot t_6_4_6 = new TimeSlot("T14", DayOfWeek.FRIDAY, 4, 6);
		TimeSlot t_6_7_9 = new TimeSlot("T15", DayOfWeek.FRIDAY, 7, 9);

		this.timeSlots = Arrays.asList(
			t_2_1_3, t_2_4_6, t_2_7_9, t_3_1_3, t_3_4_6, t_3_7_9,
			t_4_1_3, t_4_4_6, t_4_7_9, t_5_1_3, t_5_4_6, t_5_7_9,
			t_6_1_3, t_6_4_6, t_6_7_9
		);

		// 4. MÔN HỌC (ĐÃ CẬP NHẬT)
		String thoiGianHocMacDinh = "15/09/25 - 01/01/26";

		// (id, name, studentCount, studentGroup, requiredRoomType, credits, toNhom, thoiGianHoc)
		
		// Môn 1: Lịch sử Đảng (1 lớp LT)
		Course c001 = new Course("C001", "Lịch sử Đảng", 100, "DH23DTC-LT", "LECTURE", 3, "19", thoiGianHocMacDinh);
		
		// Môn 2: Lập trình mạng (1 LT, 2 TH)
		Course c002_LT = new Course("C002_LT", "Lập trình mạng (LT)", 100, "DH23DTC-LT", "LECTURE", 2, "08", thoiGianHocMacDinh);
		Course c002_TH1 = new Course("C002_TH1", "Lập trình mạng (TH)", 50, "DH23DTC-TH1", "LAB", 2, "03-01", thoiGianHocMacDinh);
		Course c002_TH2 = new Course("C002_TH2", "Lập trình mạng (TH)", 50, "DH23DTC-TH2", "LAB", 2, "03-02", thoiGianHocMacDinh);

		// Môn 3: Lập trình .NET (1 LT, 2 TH)
		Course c003_LT = new Course("C003_LT", "Lập trình .NET (LT)", 100, "DH23DTC-LT", "LECTURE", 2, "01", thoiGianHocMacDinh);
		Course c003_TH1 = new Course("C003_TH1", "Lập trình .NET (TH)", 50, "DH23DTC-TH1", "LAB", 2, "01-01", thoiGianHocMacDinh);
		Course c003_TH2 = new Course("C003_TH2", "Lập trình .NET (TH)", 50, "DH23DTC-TH2", "LAB", 2, "01-02", thoiGianHocMacDinh);
		
		// Môn 4: Lập trình Web (1 LT, 2 TH)
		Course c004_LT = new Course("C004_LT", "Lập trình Web (LT)", 100, "DH23DTC-LT", "LECTURE", 2, "03", thoiGianHocMacDinh);
		Course c004_TH1 = new Course("C004_TH1", "Lập trình Web (TH)", 50, "DH23DTC-TH1", "LAB", 2, "03-01", thoiGianHocMacDinh);
		Course c004_TH2 = new Course("C004_TH2", "Lập trình Web (TH)", 50, "DH23DTC-TH2", "LAB", 2, "03-02", thoiGianHocMacDinh);
		
		// Môn 5: Hệ quản trị CSDL (1 LT, 2 TH)
		Course c005_LT = new Course("C005_LT", "Hệ quản trị CSDL (LT)", 100, "DH23DTC-LT", "LECTURE", 2, "03", thoiGianHocMacDinh);
		Course c005_TH1 = new Course("C005_TH1", "Hệ quản trị CSDL (TH)", 50, "DH23DTC-TH1", "LAB", 1, "03-01", thoiGianHocMacDinh);
		Course c005_TH2 = new Course("C005_TH2", "Hệ quản trị CSDL (TH)", 50, "DH23DTC-TH2", "LAB", 1, "03-02", thoiGianHocMacDinh);
		
		// --- MÔN HỌC MỚI ---
		// Môn 6: Lập trình Python (1 LT, 2 TH)
		Course c006_LT = new Course("C006_LT", "Lập trình Python (LT)", 100, "DH23DTC-LT", "LECTURE", 2, "01", thoiGianHocMacDinh);
		Course c006_TH1 = new Course("C006_TH1", "Lập trình Python (TH)", 50, "DH23DTC-TH1", "LAB", 2, "01-01", thoiGianHocMacDinh);
		Course c006_TH2 = new Course("C006_TH2", "Lập trình Python (TH)", 50, "DH23DTC-TH2", "LAB", 2, "01-02", thoiGianHocMacDinh);
		
		// Môn 7: Nhập môn TTNT (1 LT, 2 TH)
		Course c007_LT = new Course("C007_LT", "Nhập môn TTNT (LT)", 100, "DH23DTC-LT", "LECTURE", 2, "03", thoiGianHocMacDinh);
		Course c007_TH1 = new Course("C007_TH1", "Nhập môn TTNT (TH)", 50, "DH23DTC-TH1", "LAB", 2, "03-01", thoiGianHocMacDinh);
		Course c007_TH2 = new Course("C007_TH2", "Nhập môn TTNT (TH)", 50, "DH23DTC-TH2", "LAB", 2, "03-02", thoiGianHocMacDinh);
		// --- KẾT THÚC MÔN HỌC MỚI ---

		this.mandatoryCourses = Arrays.asList(
			c001, 
			c002_LT, c002_TH1, c002_TH2,
			c003_LT, c003_TH1, c003_TH2,
			c004_LT, c004_TH1, c004_TH2,
			c005_LT, c005_TH1, c005_TH2,
			c006_LT, c006_TH1, c006_TH2, // Thêm
			c007_LT, c007_TH1, c007_TH2  // Thêm
		);
		
		// 5. MÔN HỌC TỰ CHỌN (Bỏ qua cho data này)
		this.allPossibleElectives = new ArrayList<>();

		// 6. LUẬT TỰ CHỌN (Bỏ qua cho data này)
		this.electiveRequirements = new ArrayList<>();
	}

	// Getters
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