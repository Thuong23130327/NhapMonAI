package model;

import java.util.ArrayList;
import java.util.List;

public class Course {
	private String id;
	private String name;
	private int studentCount;
	private String studentGroup;
	private String requiredRoomType;
	private int credits;
	private String toNhom; // Ví dụ: "19", "08", "03-01"
	private String thoiGianHoc; // Ví dụ: "16/09/25 đến 06/01/26"

	public Course(String id, String name, int studentCount, String studentGroup, String requiredRoomType, int credits,
			String toNhom, String thoiGianHoc) {
		this.id = id;
		this.name = name;
		this.studentCount = studentCount;
		this.studentGroup = studentGroup;
		this.requiredRoomType = requiredRoomType;
		this.credits = credits;
		this.toNhom = toNhom;
		this.thoiGianHoc = thoiGianHoc;
	}

	// Get setter
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public String getStudentGroup() {
		return studentGroup;
	}

	public String getRequiredRoomType() {
		return requiredRoomType;
	}

	public int getCredits() {
		return credits;
	}

	public String getToNhom() {
		return toNhom;
	}

	public String getThoiGianHoc() {
		return thoiGianHoc;
	}

}