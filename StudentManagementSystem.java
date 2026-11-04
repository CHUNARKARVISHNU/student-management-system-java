import java.io.*;
import java.util.*;

class Student {
    private int id;
    private String name;
    private String course;
    private double marks;

    public Student(int id, String name, String course, double marks) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.marks = marks;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Name: %-15s | Course: %-12s | Marks: %.2f", id, name, course, marks);
    }
}

public class StudentManagementSystem {
    private static Map<Integer, Student> studentMap = new HashMap<>();
    private static Set<Integer> studentIds = new HashSet<>();
    private static final String DATA_FILE = "students.txt";

    public static void main(String[] args) {
        loadFromFile();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("==================================================");
        System.out.println("         STUDENT MANAGEMENT SYSTEM               ");
        System.out.println("==================================================");

        while (running) {
            System.out.println("\n1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Update Student Record");
            System.out.println("5. Delete Student Record");
            System.out.println("6. Save & Exit");
            System.out.print("Select an option (1-6): ");

            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                switch (choice) {
                    case 1 -> addStudent(sc);
                    case 2 -> viewAllStudents();
                    case 3 -> searchStudent(sc);
                    case 4 -> updateStudent(sc);
                    case 5 -> deleteStudent(sc);
                    case 6 -> {
                        saveToFile();
                        running = false;
                        System.out.println("Data saved successfully. Exiting system!");
                    }
                    default -> System.out.println("Invalid option. Enter a number between 1 and 6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input error: Please enter a valid numerical choice.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void displayRecord(Student student) {
        System.out.println(student);
    }

    private static void displayRecord(Student student, String header) {
        System.out.println("[" + header + "] " + student);
    }

    private static void addStudent(Scanner sc) {
        try {
            System.out.print("Enter Student ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            if (studentIds.contains(id)) {
                System.out.println("Error: Student ID already exists!");
                return;
            }

            System.out.print("Enter Student Name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter Course: ");
            String course = sc.nextLine().trim();

            System.out.print("Enter Marks: ");
            double marks = Double.parseDouble(sc.nextLine().trim());

            if (marks < 0 || marks > 100) {
                throw new IllegalArgumentException("Marks must be between 0 and 100.");
            }

            Student student = new Student(id, name, course, marks);
            studentMap.put(id, student);
            studentIds.add(id);

            System.out.println("Student added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Validation Error: ID and Marks must be numeric values.");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    private static void viewAllStudents() {
        if (studentMap.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        List<Student> studentList = new ArrayList<>(studentMap.values());
        System.out.println("\n------------------------------------------------------------");
        for (Student s : studentList) {
            displayRecord(s);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Total Records: " + studentList.size());
    }

    private static void searchStudent(Scanner sc) {
        try {
            System.out.print("Enter Student ID to search: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            Student s = studentMap.get(id);
            if (s != null) {
                displayRecord(s, "FOUND");
            } else {
                System.out.println("No student found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be a number.");
        }
    }

    private static void updateStudent(Scanner sc) {
        try {
            System.out.print("Enter Student ID to update: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            Student student = studentMap.get(id);
            if (student == null) {
                System.out.println("Student not found.");
                return;
            }

            System.out.print("Enter New Name (leave blank to keep current): ");
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) student.setName(name);

            System.out.print("Enter New Course (leave blank to keep current): ");
            String course = sc.nextLine().trim();
            if (!course.isEmpty()) student.setCourse(course);

            System.out.print("Enter New Marks (leave blank to keep current): ");
            String marksStr = sc.nextLine().trim();
            if (!marksStr.isEmpty()) {
                double marks = Double.parseDouble(marksStr);
                if (marks < 0 || marks > 100) throw new IllegalArgumentException("Marks must be between 0 and 100.");
                student.setMarks(marks);
            }

            System.out.println("Record updated successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Validation Error: Marks must be a number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    private static void deleteStudent(Scanner sc) {
        try {
            System.out.print("Enter Student ID to delete: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            if (studentMap.containsKey(id)) {
                studentMap.remove(id);
                studentIds.remove(id);
                System.out.println("Student record deleted.");
            } else {
                System.out.println("Student ID not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be numeric.");
        }
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Student s : studentMap.values()) {
                writer.write(s.getId() + "," + s.getName() + "," + s.getCourse() + "," + s.getMarks());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("File write error: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String course = parts[2];
                    double marks = Double.parseDouble(parts[3]);
                    studentMap.put(id, new Student(id, name, course, marks));
                    studentIds.add(id);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading saved student records: " + e.getMessage());
        }
    }
  }
