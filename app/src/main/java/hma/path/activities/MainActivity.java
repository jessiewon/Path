package hma.path.activities;



import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import hma.path.fragments.TaskFragment;
import hma.path.mapcontrol.Location;
import hma.path.R;
import hma.path.mapcontrol.ShortestPath;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        TaskFragment.OnFragmentInteractionListener {
    LinkedList<TaskFragment> taskFragments;
    LinkedList<Location> tasks;
    LinkedList<Location> priorityTasks;

    Location endLocation;
    CheckBox endLocCheckBox;
    boolean demo = true;



    public int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    public int minute = Calendar.getInstance().get(Calendar.MINUTE);

    public Button timeSetUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tasks = new LinkedList<>();
        priorityTasks = new LinkedList<>();
        taskFragments = new LinkedList<>();

        final LinearLayout LLayout = (LinearLayout)findViewById(R.id.entry_layout);

        timeSetUp = (Button)findViewById(R.id.time_setup);
        formatDepartTime();
        timeSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, MainActivity.this, hour, minute, false);
                tpd.show();
            }
        });


        FloatingActionButton addEntry = (FloatingActionButton) findViewById(R.id.addentry_btn);
        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int entryCount = taskFragments.size();
                boolean makeEndLoc = (entryCount == 0);

                if (entryCount < 10) {
                    /*
                    EditText[] entry = addTask(LLayout, tasks.size()+1);
                    tasks.add(entry);
                    */
                    TaskFragment task = TaskFragment.newInstance(makeEndLoc, entryCount+1);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(LLayout.getId(), task);
                    transaction.commit();
                    taskFragments.add(task);
                    if (makeEndLoc) {
                        endLocCheckBox = task.getEndLocCB();
                        endLocation = task.getLocation();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Cannot have more than 10 entries.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        FloatingActionButton submitFAB = (FloatingActionButton)findViewById(R.id.submit_fab);
        submitFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Location> locations = null;
                ArrayList<Location> sim2 = null;;
                if (demo) {
                    locations = runSimulation();
                    sim2 = runSimulation2();
                }
                else {

                }
                long minTime = ShortestPath.getMinimumTime();

                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                intent.putExtra("minTime", minTime);
                intent.putExtra("path", locations);
                intent.putExtra("sim2", sim2);
                //intent.putExtra("OGList", og);
                startActivity(intent);

            }
        });
    }



    @Override
    public void onTaskEndLocInteraction(boolean checked, TaskFragment taskFragment) {
        if (checked) {
            if (endLocCheckBox != null) {
                endLocCheckBox.setChecked(false);
                endLocCheckBox = taskFragment.getEndLocCB();
                endLocation = taskFragment.getLocation();
            }
            endLocation = taskFragment.getLocation();
        }
    }



    public void sortTasks() {
        if (tasks != null && priorityTasks != null && taskFragments != null) {
            for (TaskFragment taskFragment : taskFragments) {
                if (taskFragment.isPriority()) {
                    priorityTasks.add(taskFragment.getLocation());
                }
                else if (!taskFragment.isEndLocation()){
                    tasks.add(taskFragment.getLocation());
                }
                else {
                    endLocation = taskFragment.getLocation();
                }
            }
        }
    }


    public ArrayList<Location> runSimulation() {
        final Location startLocation = (Location) getIntent().getExtras().getSerializable("base_loc");
        List<Location> locationList = new ArrayList<Location>();
        locationList.add(new Location("1235 Upper Village Dr", "Mississauga", "ON", "L5E3J6", 10));
        locationList.add(new Location("2800 Erin Centre Blvd", "Mississauga", "ON", "L5M6R5", 12));

        Location finalLoc = new Location("2840 Duncairn Dr", "Mississauga", "ON", "L5M5C6", 13);
        startLocation.setTimeLeft(System.currentTimeMillis());
        ShortestPath.getShortestPath(startLocation, locationList, finalLoc);
        ArrayList<Location> locations = ShortestPath.getFinalPath();
        return locations;
    }


    public ArrayList<Location> runSimulation2() {
        final Location startLocation = (Location) getIntent().getExtras().getSerializable("base_loc");
        List<Location> locationList = new ArrayList<Location>();
        locationList.add(new Location("1235 Upper Village Dr", "Mississauga", "ON", "L5E3J6", 10));
        locationList.add(new Location("2800 Erin Centre Blvd", "Mississauga", "ON", "L5M6R5", 12));
        Location finalLoc = new Location("2840 Duncairn Dr", "Mississauga", "ON", "L5M5C6", 13);
        int[] resultArr = ShortestPath.shortestPath(startLocation, locationList, finalLoc, System.currentTimeMillis());
        ArrayList<Location> result = new ArrayList<Location>();
        result.add(startLocation);
        for(int index : resultArr) {
            result.add(locationList.get(index));
        }
        result.add(finalLoc);
        return result;
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        formatDepartTime();
    }

    public void formatDepartTime() {
        StringBuilder display = new StringBuilder();

        if (hour == 12 || hour == 0) {
            display.append("12:");
        }
        else if (hour > 12) {
            display.append(hour % 12 + ":");
        }
        else {
            display.append(hour + ":");
        }

        if (minute < 10) {
            display.append(0);
        }
        display.append(minute + ":");

        if (hour >= 12) {
            display.append(" pm");
        }
        else {
            display.append(" am");
        }


        timeSetUp.setText(display.toString());
    }




}
