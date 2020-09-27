import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class WorkLoad {


    public static void main(String[] args) {
        ArrayList<Integer> inputs = readFile("1000input.txt");

        int n = inputs.get(0); // number of jobs
        int m = inputs.get(1); // number of workStations

        Job[] jobs = new Job[n];

        for (int i = 0; i < n; i++) {
            int startTimeAccess = inputs.get(i * (m + 1) + 2);
            Task[] tasks = new Task[m];
            for (int j = 0; j < m; j++) {
                tasks[j] = new Task(inputs.get(i * (m + 1) + j + 3));
            }
            jobs[i] = new Job(startTimeAccess, tasks, i + 1);
        }
        jobs = Job.sortJob(jobs);

        Factory factory = new Factory(m, jobs);

        WorkStation[] workStations = factory.proccess();

        PrintResult(workStations,jobs);
    }

    public static ArrayList<Integer> readFile(String s) {
        ArrayList<String> strings = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(s));
            String line;
            while ((line = br.readLine()) != null) {
                strings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            String[] split = strings.get(i).split("\\s+");
            for (int i1 = 0; i1 < split.length; i1++) {
                integers.add(Integer.valueOf(split[i1]));
            }
        }
        return integers;
    }


    private static void PrintResult(WorkStation[] workStations,Job[] jobs) {
        int max = workStations[0].getTime().size();
        int[][] starts = new int[jobs.length][workStations.length];
        for (int i = 1; i < workStations.length; i++) {
            if (max < workStations[i].getTime().size())
                max = workStations[i].getTime().size();
        }
        System.out.println(max);


        for (int i = 0; i < workStations.length; i++) {
            int temp =-1;
            for (int j = 0; j < workStations[i].getTime().size(); j++) {
                if (workStations[i].getTime().get(j)!=-1)
                {
                    if (temp !=workStations[i].getTime().get(j))
                    {
                        temp = workStations[i].getTime().get(j);
                        starts [workStations[i].getTime().get(j)-1] [i]= j;
                    }
                }
            }
        }


        for (int i = 0; i < jobs.length; i++) {
            for (int j = 0; j < workStations.length; j++) {
                System.out.print(starts[i][j] + " ");
            }
            System.out.println();
        }
    }
}


class Task {
    private int duration;
    private boolean hasDone;

    public Task(int duration) {
        this.duration = duration;
        this.hasDone = false;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isHasDone() {
        return hasDone;
    }

    public void setHasDone(boolean hasDone) {
        this.hasDone = hasDone;
    }
}

class Job {
    private int startTimeAccess;
    private int term;
    private Task[] tasks;

    public Job(int startTimeAccess, Task[] tasks, int term) {
        this.startTimeAccess = startTimeAccess;
        this.tasks = tasks;
        this.term = term;
    }

    public int getStartTimeAccess() {
        return startTimeAccess;
    }

    public Task[] getTasks() {
        return tasks;
    }

    public int getTerm() {
        return term;
    }

    public static Job[] sortJob(Job[] jobs) {
        for (int i = 0; i < jobs.length; i++) {
            for (int j = i + 1; j < jobs.length; j++) {
                if (jobs[i].getStartTimeAccess() > jobs[j].getStartTimeAccess()) {
                    Job temp = jobs[j];
                    jobs[j] = jobs[i];
                    jobs[i] = temp;
                }

            }
        }
        return jobs;
    }

}

class WorkStation {
    private ArrayList<Integer> time;
    private boolean hasDone;
    private Job[] jobs;
    private int jobAdded = 0;

    public WorkStation(Job[] jobs) {
        this.time = new ArrayList<>();
        this.jobs = jobs;
        hasDone = false;
    }

    public void insertJob(int indexJob, int duration, int term) {
        for (int i = 0; i < duration; i++) {
            time.add(term);
        }
        if (term != -1) {
            jobAdded++;
            if (jobAdded == jobs.length)
                hasDone = true;
        }
    }


    public ArrayList<Integer> getTime() {
        return time;
    }

    public boolean isHasDone() {
        return hasDone;
    }
}

class Factory {
    private WorkStation[] workStations;
    private Job[] jobs;

    public Factory(int m, Job[] jobs) {
        this.jobs = jobs;
        this.workStations = new WorkStation[m];
        for (int i = 0; i < m; i++) {
            workStations[i] = new WorkStation(jobs);
        }
    }

    public WorkStation[] proccess() {
        int counter = 0;
        while (counter <= jobs.length * workStations.length - 1) {
            int indexMin = findMin(workStations);
            int availableLastJob = availbleJobs(workStations[indexMin], jobs);
            boolean noJob = true;
            for (int i = 0; i <= availableLastJob; i++) {
                if (!checkConflict(i, jobs, workStations, indexMin, workStations[indexMin].getTime().size())) {
                    workStations[indexMin].insertJob(i, jobs[i].getTasks()[indexMin].getDuration(), jobs[i].getTerm());
                    noJob = false;
                    jobs[i].getTasks()[indexMin].setHasDone(true);
                    counter++;
                    break;
                }
            }
            if (noJob) {
                workStations[indexMin].insertJob(-1, 1, -1);
            }
        }
        return workStations;
    }

    public WorkStation[] proccess2() {
        int counter = 0;
        WorkStation[] ws = null;
        while (counter <= jobs.length) {
            int availableLastJob[] = new int[workStations.length];
            for (int i = 0; i < workStations.length; i++) {
                availableLastJob[i] = availbleJobs(workStations[0], jobs);
            }
            for (int i = 0; i < availableLastJob[0] ; i++) {
                WorkStation[] tmp = workStations;
                int choosenJob0 = 0 ,choosenJob1 ,choosenJob2 = 0;
                if (!checkConflict(i, jobs, tmp, 0, tmp[0].getTime().size())) {
                    tmp[0].insertJob(i, jobs[i].getTasks()[0].getDuration(), jobs[i].getTerm());
                    //jobs[i].getTasks()[0].setHasDone(true);
                    for (int j = 0; j < availableLastJob[1] ; j++) {
                        WorkStation[] tmp1 = tmp;
                        if (!checkConflict(j, jobs, tmp1, 1, tmp1[1].getTime().size())) {
                            tmp1[1].insertJob(j, jobs[j].getTasks()[1].getDuration(), jobs[j].getTerm());
                            //jobs[j].getTasks()[1].setHasDone(true);
                            for (int k = 0; k < availableLastJob[2]; k++) {
                                WorkStation[] tmp2 = tmp1;
                                if (!checkConflict(k, jobs, tmp2, 2, tmp2[2].getTime().size())) {
                                    if(ws == null) {
                                        tmp2[2].insertJob(k, jobs[k].getTasks()[2].getDuration(), jobs[k].getTerm());

                                        jobs[k].getTasks()[2].setHasDone(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            /*boolean noJob = true;
            if (noJob) {
                workStations[indexMin].insertJob(-1, 1, -1);
            }*/
        }
        return workStations;
    }


    private boolean checkConflict(int jobIndex, Job[] jobs, WorkStation[] workStations, int workStationIndex, int time) {
        for (int i = 0; i < workStations.length; i++) {
            if (i == workStationIndex) {
                if (jobs[jobIndex].getTasks()[workStationIndex].isHasDone())
                    return true;
            } else {
                if (workStations[i].getTime().size() > time) {
                    int duration = jobs[jobIndex].getTasks()[workStationIndex].getDuration();
                    for (int i1 = 0; workStations[i].getTime().size() > i1 + time && i1 < duration; i1++) {
                        if (workStations[i].getTime().get(time + i1) == jobs[jobIndex].getTerm())
                            return true;
                    }
                }

            }
        }

        return false;
    }

    private int availbleJobs(WorkStation workStation, Job[] jobs) {
        for (int i = 0; i < jobs.length; i++) {
            if (workStation.getTime().size() < jobs[i].getStartTimeAccess())
                return i - 1;
        }
        return jobs.length - 1;
    }

    private int findMin(WorkStation[] workStations) {
        int min = workStations[0].getTime().size();
        int indexMin = 0;
        for (int i = 1; i < workStations.length; i++) {
            int temp = workStations[i].getTime().size();
            if (min > temp && !workStations[i].isHasDone()) {
                indexMin = i;
                min = temp;
            }
        }

        return indexMin;
    }

}

