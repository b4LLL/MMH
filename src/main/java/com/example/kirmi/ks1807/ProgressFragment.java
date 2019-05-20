package com.example.kirmi.ks1807;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ProgressFragment extends Fragment
{

    String UserID = "";
    LineGraphSeries<DataPoint> series, series2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_progressfrag, null);

        //Passing the userID for future need.
        UserID = Global.UserID;

        GraphView graph = (GraphView)view.findViewById(R.id.graph);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(8);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);

        //Placing the points on the graph at the position with two different lines.
        series = new LineGraphSeries<>(new DataPoint[]
                {
                new DataPoint(0,0),
                new DataPoint(2,5),
                new DataPoint(3,1),
                new DataPoint(5,6),
        });

        series2 = new LineGraphSeries<>(new DataPoint[]
                {
                new DataPoint(0,2),
                new DataPoint(2,3),
                new DataPoint(4,6),
                new DataPoint(7,7),
        });

        //Adding the both lines on the graph.
        //graph.addSeries(series);
        //graph.addSeries(series2);
        graph.getSecondScale().addSeries(series);
        graph.getSecondScale().addSeries(series2);
        graph.getSecondScale().setMinY(-10);
        graph.getSecondScale().setMaxY(10);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        //Styling the line graph to differentiate between the two.
        series.setColor(Color.WHITE);
        series.setThickness(2);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(90, 255, 255, 255));
        series.setDrawDataPoints(true);
        series.setTitle("Mood");

        series2.setColor(Color.BLUE);
        series2.setThickness(2);
        series2.setDrawBackground(true);
        series2.setBackgroundColor(Color.argb(90, 73, 130, 203));
        series2.setDrawDataPoints(true);
        series2.setTitle("No of Tracks");

        //Displaying a legend for the graph.
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        return view;
    }
}
