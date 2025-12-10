package algotithm;

import javax.swing.SwingUtilities;

import data.ProblemData;
import model.Population;
import model.Timetable;
import view.TimetableFrame;

/**
 * ========================================================================= LỚP
 * CHẠY CHÍNH (MAIN CLASS)
 * =========================================================================
 * Nhiệm vụ: 1. Khởi tạo dữ liệu (ProblemData) và Thuật toán (GeneticAlgorithm).
 * 2. Chạy thuật toán. 3. Lấy TKB tốt nhất và "gửi" cho GUI (TimetableFrame) để
 * hiển thị.
 */
public class TimetableGA {

	public static void main(String[] args) {
		System.out.println("Bắt đầu quá trình tiến hóa...");

		// 1. Chuẩn bị dữ liệu
		ProblemData data = new ProblemData();

		// 2. Khởi tạo Thuật toán (Tinh chỉnh các thông số này)
		int populationSize = 500;
		double mutationRate = 0.05; // 5% (dùng cho Swap Mutation)
		int eliteCount = 2; // Giữ lại 2 cá thể tốt nhất
		int tournamentSize = 5; // Kích thước giải đấu
		int maxGenerations = 10000; // Chạy 1000 thế hệ

		GeneticAlgorithm ga = new GeneticAlgorithm(data, populationSize, mutationRate, eliteCount, tournamentSize);

		// 3. Chạy quá trình tiến hóa (Phần này có thể mất vài giây)
		Population finalPopulation = ga.runEvolution(maxGenerations);

		// 4. Lấy TKB tốt nhất (Giải thành công - 1đ)
		Timetable fittest = finalPopulation.getFittest();

		System.out.println("==================================================");
		System.out.println("ĐÃ TÌM THẤY GIẢI PHÁP TỐT NHẤT!");
		System.out.println("FINAL FITNESS: " + fittest.getFitness());
		System.out.println("==================================================");

		// 5. Hiển thị Giao diện Swing (Giao diện - 1đ)
		// Chạy Swing trên Event Dispatch Thread để đảm bảo an toàn
		SwingUtilities.invokeLater(() -> {
			TimetableFrame frame = new TimetableFrame(fittest, data);
			frame.setVisible(true); // Hiển thị cửa sổ
		});
	}
}