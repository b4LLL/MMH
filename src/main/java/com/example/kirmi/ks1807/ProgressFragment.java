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
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
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
        graph.getViewport().setMinX(-10);
        graph.getViewport().setMaxX(0);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-10);
        graph.getViewport().setMaxY(10);

        graph.getViewport().setScalable(false);
        graph.getViewport().setScalableY(false);

        //Placing the points on the graph at the position with two different lines.
        // Mood
        series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(-10,4),
                new DataPoint(-8,-8),
                new DataPoint(-6,-5),
                new DataPoint(-4,0),
                new DataPoint(-2,4),
                new DataPoint(0,6),
        });

        // #Tracks
        series2 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(-10,6),
                new DataPoint(-8,4),
                new DataPoint(-6,5),
                new DataPoint(-4,0),
                new DataPoint(-2,4),
                new DataPoint(0,9),
        });

        //Adding the both lines on the graph.
        graph.addSeries(series);
        graph.addSeries(series2);
        //graph.getSecondScale().addSeries(series);
        //graph.getSecondScale().addSeries(series2);
        //graph.getSecondScale().setMinY(-10);
        //graph.getSecondScale().setMaxY(10);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        //Styling the line graph to differentiate between the two.

        //White is Mood
        series.setColor(Color.WHITE);
        series.setThickness(3);
        series.setDrawBackground(false);
        series.setDrawDataPoints(true);
        series.setTitle("Average Mood");

        //Blue is No of tracks
        series2.setColor(Color.BLUE);
        series2.setThickness(3);
        series2.setDrawBackground(false);
        series2.setDrawDataPoints(true);
        series2.setTitle("No of Tracks");

        //Displaying a legend for the graph.
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        return view;
    }
}
