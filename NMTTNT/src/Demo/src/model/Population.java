package model;

//File: Population.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {
	List<Timetable> timetables;

	public Population(int size) {
		this.timetables = new ArrayList<>(size);
	}

	public void addTimetable(Timetable tt) {
		this.timetables.add(tt);
	}

	public int getSize() {
		return this.timetables.size();
	}

	public Timetable getTimetable(int index) {
		return this.timetables.get(index);
	}

	public Timetable getFittest() {
		Timetable fittest = timetables.get(0);
		for (int i = 1; i < timetables.size(); i++) {
			if (timetables.get(i).getFitness() > fittest.getFitness()) {
				fittest = timetables.get(i);
			}
		}
		return fittest;
	}

	public void sortPopulationByFitness() {
		Collections.sort(this.timetables, (t1, t2) -> Integer.compare(t2.getFitness(), t1.getFitness()));
	}
}
