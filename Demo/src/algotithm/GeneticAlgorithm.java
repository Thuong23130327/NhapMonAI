package algotithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import data.ProblemData;
import model.Course;
import model.ElectiveRequirement;
import model.Lecturer;
import model.Population;
import model.Room;
import model.ScheduledClass;
import model.TimeSlot;
import model.Timetable;

/**
 * ========================================================================= BỘ
 * MÁY GA (2, 3, 4, 6) 2. TẠO QUẦN THỂ BAN ĐẦU (1Đ) 3. CROSSOVER (2Đ) - Dùng
 * Uniform Crossover 4. MUTATION (2Đ) - Dùng Swap Mutation 6. GIẢI THÀNH CÔNG
 * (1Đ) - Vòng lặp Elitism
 * =========================================================================
 */
public class GeneticAlgorithm {
	ProblemData data;
	int populationSize;
	double mutationRate;
	int eliteCount;
	int tournamentSize;
	Random random = new Random();
	FitnessCalculator fitnessCalculator;

	// Danh sách "khuôn mẫu" các môn học (đã chọn tự chọn)
	List<Course> baseScheduleCourses;

	public GeneticAlgorithm(ProblemData data, int populationSize, double mutationRate, int eliteCount,
			int tournamentSize) {
		this.data = data;
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.eliteCount = eliteCount;
		this.tournamentSize = tournamentSize;
		this.fitnessCalculator = new FitnessCalculator(data);
		this.baseScheduleCourses = createBaseCourseList();
	}
	// BÊN TRONG FILE GeneticAlgorithm.java

	// ... các hàm khác ...

	/**
	 * 6. GIẢI THÀNH CÔNG (1Đ) - Vòng lặp Tiến hóa (ĐÃ SỬA LỖI LOGIC)
	 */
	public Population runEvolution(int maxGenerations) {
		Population population = createInitialPopulation();
		System.out.println("Generation 0 | Fittest: " + population.getFittest().getFitness());

		for (int gen = 1; gen <= maxGenerations; gen++) {
			Population newPopulation = new Population(populationSize);

			// --- Áp dụng "Chủ nghĩa Tinh hoa" (Elitism) ---
			population.sortPopulationByFitness();
			for (int i = 0; i < eliteCount; i++) {
				// SỬA LỖI: Dùng Copy Constructor để TKB tinh hoa không bị đột biến
				newPopulation.addTimetable(new Timetable(population.getTimetable(i)));
			}

			// --- Vòng lặp sinh sản ---
			for (int i = eliteCount; i < populationSize; i++) {
				Timetable parent1 = tournamentSelection(population, tournamentSize);
				Timetable parent2 = tournamentSelection(population, tournamentSize);

				// Phép lai (crossover) giờ đã tạo ra bản sao sâu (deep copy)
				Timetable child = crossover(parent1, parent2);

				mutate(child, mutationRate);
				fitnessCalculator.calculateFitness(child);
				newPopulation.addTimetable(child);
			}

			population = newPopulation;
			if (gen % 100 == 0 || gen == maxGenerations) {
				System.out.println("Generation " + gen + " | Fittest: " + population.getFittest().getFitness());
			}
			if (population.getFittest().getFitness() >= 0) {
				System.out.println("Generation " + gen + ": Found perfect solution!");
				break;
			}
		}
		return population;
	}

	// ... các hàm createInitialPopulation, createBaseCourseList ...

	/**
	 * 3. CROSSOVER (2Đ) - Dùng Uniform Crossover (ĐÃ SỬA LỖI LOGIC)
	 */
	private Timetable crossover(Timetable parent1, Timetable parent2) {
		Timetable child = new Timetable();
		for (int i = 0; i < parent1.getSchedule().size(); i++) {

			ScheduledClass geneToCopy;
			if (Math.random() < 0.5) {
				geneToCopy = parent1.getSchedule().get(i);
			} else {
				geneToCopy = parent2.getSchedule().get(i);
			}

			// SỬA LỖI: Tạo một "gen" mới (ScheduledClass)
			// thay vì copy tham chiếu.
			// Điều này yêu cầu bạn PHẢI CÓ Copy Constructor trong ScheduledClass
			child.addClass(new ScheduledClass(geneToCopy));
		}
		return child;
	}

	// ... các hàm mutate, tournamentSelection ...
	/**
	 * 2. TẠO QUẦN THỂ BAN ĐẦU (1Đ)
	 */
	private Population createInitialPopulation() {
		Population population = new Population(populationSize);
		for (int i = 0; i < populationSize; i++) {
			Timetable timetable = createRandomTimetable();
			fitnessCalculator.calculateFitness(timetable);
			population.addTimetable(timetable);
		}
		return population;
	}

	// Hàm phụ: Quyết định xem sẽ xếp lịch cho những môn nào
	private List<Course> createBaseCourseList() {
		List<Course> coursesToSchedule = new ArrayList<>(data.getMandatoryCourses());

		for (ElectiveRequirement req : data.getElectiveRequirements()) {
			if (req.getCreditsRequired() == 3) {
				List<Course> choicePool = data.getAllPossibleElectives().stream()
						.filter(c -> req.getElectiveCourseIds().contains(c.getId())
								&& c.getStudentGroup().equals(req.getStudentGroupId()))
						.collect(Collectors.toList());

				if (!choicePool.isEmpty()) {
					coursesToSchedule.add(choicePool.get(random.nextInt(choicePool.size())));
				}
			}
		}
		return coursesToSchedule;
	}
	// (Bên trong GeneticAlgorithm.java)

	/**
	 * Hàm phụ: Tạo 1 TKB ngẫu nhiên (PHIÊN BẢN SIÊU THÔNG MINH - Đã sửa lỗi) * Nâng
	 * cấp: Sẽ lọc phòng ỐC theo CẢ LOẠI PHÒNG VÀ SỨC CHỨA
	 */
	private Timetable createRandomTimetable() {
		Timetable timetable = new Timetable();

		// Luôn dùng 'baseScheduleCourses' để đảm bảo TKB nào cũng có cùng bộ môn
		for (Course course : this.baseScheduleCourses) {

			Lecturer lecturer;
			Room room;
			TimeSlot timeSlot;

			// --- 1. Gán Giảng viên (Đã an toàn) ---
			List<Lecturer> qualifiedLecturers = data.getLecturers().stream()
					.filter(l -> l.getQualifiedCourseIds().contains(course.getId())).collect(Collectors.toList());

			if (qualifiedLecturers.isEmpty()) {
				lecturer = data.getLecturers().get(random.nextInt(data.getLecturers().size()));
			} else {
				lecturer = qualifiedLecturers.get(random.nextInt(qualifiedLecturers.size()));
			}

			// --- 2. Gán Phòng (NÂNG CẤP SIÊU THÔNG MINH) ---
			List<Room> qualifiedRooms = data.getRooms().stream().filter(r ->
			// Điều kiện 1: Đúng Loại phòng
			r.getType().equals(course.getRequiredRoomType()) &&
			// Điều kiện 2: Đúng Sức chứa
					r.getCapacity() >= course.getStudentCount()).collect(Collectors.toList());

			if (qualifiedRooms.isEmpty()) {
				// **Xử lý lỗi**: Nếu KHÔNG có phòng nào phù hợp?
				// (Ví dụ: Môn 500 SV mà phòng lớn nhất 200)
				// -> Gán bừa 1 phòng (để TKB bị -1000 và bị đào thải)
				room = data.getRooms().get(random.nextInt(data.getRooms().size()));
			} else {
				// Lấy 1 phòng ngẫu nhiên TRONG SỐ CÁC PHÒNG HỢP LỆ
				room = qualifiedRooms.get(random.nextInt(qualifiedRooms.size()));
			}

			// --- 3. Gán Giờ (Luôn an toàn) ---
			timeSlot = data.getTimeSlots().get(random.nextInt(data.getTimeSlots().size()));

			// Thêm "gen" đã gán vào TKB
			timetable.addClass(new ScheduledClass(course, lecturer, room, timeSlot));
		}
		return timetable;
	}

	/**
	 * 4. MUTATION (2Đ) - Dùng Swap Mutation
	 */
	private void mutate(Timetable timetable, double mutationRate) {
		if (Math.random() < mutationRate) {
			List<ScheduledClass> schedule = timetable.getSchedule();
			int scheduleSize = schedule.size();

			int indexA = random.nextInt(scheduleSize);
			int indexB = random.nextInt(scheduleSize);

			if (indexA == indexB)
				return;

			ScheduledClass geneA = schedule.get(indexA);
			ScheduledClass geneB = schedule.get(indexB);

			// Hoán vị TimeSlot
			TimeSlot tempTimeSlot = geneA.getTimeSlot();
			geneA.setTimeSlot(geneB.getTimeSlot());
			geneB.setTimeSlot(tempTimeSlot);
		}
	}

	/**
	 * CHỌN LỌC (Selection) - Dùng Tournament
	 */
	private Timetable tournamentSelection(Population population, int tournamentSize) {
		Population tournament = new Population(tournamentSize);
		for (int i = 0; i < tournamentSize; i++) {
			int randomIndex = random.nextInt(population.getSize());
			tournament.addTimetable(population.getTimetable(randomIndex));
		}
		return tournament.getFittest();
	}
}