package model;

public class ScheduledClass {
	Course course;
	Lecturer lecturer;
	Room room;
	TimeSlot timeSlot;

	public ScheduledClass(Course course, Lecturer lecturer, Room room, TimeSlot timeSlot) {
		this.course = course;
		this.lecturer = lecturer;
		this.room = room;
		this.timeSlot = timeSlot;
	}

	public ScheduledClass(ScheduledClass other) {
		this.course = other.course; // OK để copy tham chiếu (vì không đổi)
		this.lecturer = other.lecturer; // OK để copy tham chiếu (vì không đổi)
		this.room = other.room; // OK để copy tham chiếu (vì không đổi)

		// TimeSlot BỊ THAY ĐỔI bởi phép đột biến,
		// nhưng phép đột biến của bạn là swap (hoán vị)
		// nên chúng ta vẫn giữ tham chiếu
		this.timeSlot = other.timeSlot;
	}

	public Course getCourse() {
		return course;
	}

	public Lecturer getLecturer() {
		return lecturer;
	}

	public Room getRoom() {
		return room;
	}

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setLecturer(Lecturer lecturer) {
		this.lecturer = lecturer;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}
}
