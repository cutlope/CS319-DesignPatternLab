import java.util.*;
import java.text.SimpleDateFormat;

//Handling Options
interface Task {
    public String toString();

    public String getDate();

    public Calendar getCreationDate();
}

class BaseTask implements Task {
    String description;
    TaskState state;
    String formatedTargetDate;
    Calendar creationDate;

    public BaseTask(String description, TaskState state, int year, int month, int day) {
        this.description = description;
        this.state = state;
        Calendar targetDate = Calendar.getInstance();
        targetDate.set(year, (month - 1), day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.formatedTargetDate = sdf.format(targetDate.getTime());
        this.creationDate = Calendar.getInstance();
    }

    public TaskState getState() {
        return state;
    }

    public String toString() {
        return "-" + description + " " + formatedTargetDate + " [" + state.toString() + "]";
    }

    public String getDate() {
        return formatedTargetDate;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

}

// Interface for decorators
abstract class TaskOption implements Task {
    protected Task task;

    TaskOption(Task task) {
        this.task = task;
    }

    public String getDate() {
        return task.getDate();
    }

    public Calendar getCreationDate() {
        return task.getCreationDate();
    }
}

class TrackElapsedTime extends TaskOption {

    String elapsedTime;

    public TrackElapsedTime(Task task) {
        super(task);

    }

    public void calculateElapsedTime() {
        Calendar currentDate = Calendar.getInstance();
        long elapsedTimeInMilliSecs = currentDate.getTimeInMillis() - task.getCreationDate().getTimeInMillis();
        long elapsedTimeInDays = elapsedTimeInMilliSecs / (1000 * 60 * 60 * 24);
        elapsedTime = "[Elapsed time: " + elapsedTimeInDays + " day(s)]";
    }

    @Override
    public String toString() {
        calculateElapsedTime();
        return task.toString() + " " + elapsedTime;
    }
}

class TrackStatusHistory extends TaskOption {
    String statusHistory;
    TaskState state;

    public TrackStatusHistory(Task task, TaskState State) {
        super(task);
        this.state = State;
    }

    public void setStatusHistory() {
        if (state.toString().equals("Created")) {
            this.statusHistory = "[Status history: Created]";
        } else if (state.toString().equals("In Progress")) {
            this.statusHistory = "[Status history: Created -> In Progress]";
        } else if (state.toString().equals("Completed")) {
            this.statusHistory = "[Status history: Created -> In Progress -> Completed]";
        }
    }

    @Override
    public String toString() {
        setStatusHistory();
        return task.toString() + " " + statusHistory;
    }
}

// Interface for all states
interface TaskState {
    public String toString();
}

class TaskCreated implements TaskState {
    String state = "Created";

    public String toString() {
        return state;
    }
}

class TaskInProgress implements TaskState {
    String state = "In Progress";

    public String toString() {
        return state;
    }
}

class TaskCompleted implements TaskState {
    String state = "Completed";

    public String toString() {
        return state;
    }
}

// Sort Strategy

interface SortStrategy {
    public List<Task> sort(List<Task> tasks);
}

class AlphabeticalSort implements SortStrategy {
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<Task>();
        for (int i = 0; i < tasks.size(); i++) {
            sortedTasks.add(tasks.get(i));
        }
        sortedTasks.sort(new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                return t1.toString().compareTo(t2.toString());
            }
        });
        return sortedTasks;
    }
}

class AddOrderSort implements SortStrategy {
    public List<Task> sort(List<Task> tasks) {
        return tasks;
    }
}

class TargetDateSort implements SortStrategy {
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<Task>();
        for (int i = 0; i < tasks.size(); i++) {
            sortedTasks.add(tasks.get(i));
        }
        sortedTasks.sort(new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                return t1.getDate().compareTo(t2.getDate());
            }
        });
        return sortedTasks;
    }
}

class ToDoList {
    String name;
    String description;
    List<Task> tasksList;
    SortStrategy sortStrategy;
    List<ToDoList> subLists;

    public ToDoList(String name, String order) {
        this.name = name;
        this.description = order;
        this.tasksList = new ArrayList<Task>();
        this.subLists = new ArrayList<ToDoList>();
    }

    public void setSortStrategy(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    public void addTask(Task task) {
        this.tasksList.add(task);
    }

    public void addSubList(ToDoList subList) {
        this.subLists.add(subList);
    }

    public void printTasks() {
        List<Task> sortedTasks = sortStrategy.sort(tasksList);
        System.out.println(name + " [" + this.description + "] {");
        for (Task task : sortedTasks) {
            System.out.println(task.toString());
        }

        for (ToDoList subList : subLists) {
            subList.printTasks();
        }
        System.out.println("}");
    }

}

public class Solution {
    public static void main(String[] args) {

        ToDoList toDoList = new ToDoList("My Todos", "Add Order"); // Main List
        toDoList.setSortStrategy(new AddOrderSort()); // Setting Sort Strategy for the main list
        Task task1 = new BaseTask("Fix lights", new TaskInProgress(), 2022, 5, 22);
        Task task2 = new BaseTask("Attend Seminar", new TaskCreated(), 2022, 5, 10);

        // Adding tasks to mainlist
        toDoList.addTask(task1);
        toDoList.addTask(task2);

        ToDoList cs319 = new ToDoList("CS 319", "Target Date Order"); // Sub List
        cs319.setSortStrategy(new TargetDateSort()); // Setting Sort Strategy for the sub list

        // Creating Tasks for sublist
        Task task3 = new BaseTask("Prepare iteration 1 reports", new TaskCompleted(), 2022, 4, 10);
        Task task4 = new BaseTask("Submit design patterns", new TaskInProgress(), 2022, 4, 26);
        Task task5 = new TrackElapsedTime(
                new BaseTask("Address TA/Instructor feedback", new TaskCreated(), 2022, 5, 02));

        // Adding tasks to sublist
        cs319.addTask(task3);
        cs319.addTask(task4);
        cs319.addTask(task5);

        // Creating sublist for cs319 sublist
        ToDoList implement = new ToDoList("Implementation", "Target Date Order"); // Sub List for cs319 sublist
        implement.setSortStrategy(new TargetDateSort()); // Setting Sort Strategy for the sub list
        Task task6 = new TrackStatusHistory(new BaseTask("Define classes", new TaskCompleted(), 2022, 4, 20),
                new TaskCompleted());
        Task task7 = new BaseTask("Design backend APIs", new TaskInProgress(), 2022, 4, 30);
        Task task8 = new TrackStatusHistory(
                new TrackElapsedTime(
                        new BaseTask("Implement front-end components", new TaskInProgress(), 2022, 05, 01)),
                new TaskInProgress());

        // Adding tasks to Implementation sublist
        implement.addTask(task6);
        implement.addTask(task7);
        implement.addTask(task8);

        // Adding sublist to cs319
        cs319.addSubList(implement);

        // Adding cs319 to main list
        toDoList.addSubList(cs319);

        ToDoList grocery = new ToDoList("Grocery", "Add Order"); // Sub List
        grocery.setSortStrategy(new AddOrderSort()); // Setting Sort Strategy for the sub list

        ToDoList Fruit = new ToDoList("Fruits", "Alphabetical Order"); // Sub List
        Fruit.setSortStrategy(new AlphabeticalSort()); // Setting Sort Strategy for the sub list
        Task task9 = new BaseTask("Apples", new TaskCreated(), 2022, 4, 27);
        Task task10 = new BaseTask("Bananas", new TaskCompleted(), 2022, 4, 25);
        Task task11 = new BaseTask("Oranges", new TaskCompleted(), 2022, 4, 22);

        // Adding tasks to sublist
        Fruit.addTask(task9);
        Fruit.addTask(task10);
        Fruit.addTask(task11);

        ToDoList Dairy = new ToDoList("Dairy", "Add Order"); // Sub List
        Dairy.setSortStrategy(new AddOrderSort()); // Setting Sort Strategy for the sub list
        Task task12 = new BaseTask("Milk", new TaskCompleted(), 2022, 4, 29);
        Task task13 = new BaseTask("Yoghurt", new TaskCreated(), 2022, 4, 23);

        // Adding tasks to sublist
        Dairy.addTask(task12);
        Dairy.addTask(task13);

        // Adding sublists to grocery
        grocery.addSubList(Fruit);
        grocery.addSubList(Dairy);

        // Adding grocery to main list
        toDoList.addSubList(grocery);

        // Printing the tasks
        toDoList.printTasks();
    }
}
