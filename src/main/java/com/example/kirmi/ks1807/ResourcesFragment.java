package com.example.kirmi.ks1807;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResourcesFragment extends Fragment
{

    private TextView res1, res2, res3, res4, res5, res6, res70, res71, res8, resL1, resL2, resL3, resL4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_resourcesfrag, null);

        //Linking each text view to its respective link to show in a browser
        res1 = (TextView) view.findViewById(R.id.text_resq1);
        res1.setMovementMethod(LinkMovementMethod.getInstance());
        res1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(
                        Uri.parse("https://www.lifeline.org.au/get-help/online-services/crisis-chat"));
                startActivity(browserIntent);
            }
        });

        res2 = (TextView) view.findViewById(R.id.text_resq2);
        res2.setMovementMethod(LinkMovementMethod.getInstance());
        res2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(
                        "https://www.beyondblue.org.au/personal-best/pillar/in-focus/depression-vs-sadness"));
                startActivity(browserIntent);
            }
        });

        res3 = (TextView) view.findViewById(R.id.text_resq3);
        res3.setMovementMethod(LinkMovementMethod.getInstance());
        res3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(
                        "https://www.beyondblue.org.au/personal-best/pillar/wellbeing/why-listening-to-music-makes-you-feel-good"));
                startActivity(browserIntent);
            }
        });

        res4 = (TextView) view.findViewById(R.id.text_resq4);
        res4.setMovementMethod(LinkMovementMethod.getInstance());
        res4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(
                        "https://theconversation.com/sad-music-and-depression-does-it-help-66123"));
                startActivity(browserIntent);
            }
        });

        res5 = (TextView) view.findViewById(R.id.text_resq5);
        res5.setMovementMethod(LinkMovementMethod.getInstance());
        res5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(
                        "https://www.beyondblue.org.au/personal-best/pillar/supporting-yourself/exercise-your-way-to-good-mental-health"));
                startActivity(browserIntent);
            }
        });

        res6 = (TextView) view.findViewById(R.id.text_resq6);
        res6.setMovementMethod(LinkMovementMethod.getInstance());
        res6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(
                        "https://greatergood.berkeley.edu/article/item/three_ways_mindfulness_reduces_depression"));
                startActivity(browserIntent);
            }
        });

        res70 = (TextView) view.findViewById(R.id.text_resq70);
        res70.setMovementMethod(LinkMovementMethod.getInstance());
        res70.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(
                        "https://www.glamourmagazine.co.uk/gallery/celebrities-talking-about-depression-anxiety-and-mental-health"));
                startActivity(browserIntent);
            }
        });

        res71 = (TextView) view.findViewById(R.id.text_resq71);
        res71.setMovementMethod(LinkMovementMethod.getInstance());
        res71.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(""));
                startActivity(browserIntent);
            }
        });

        res8 = (TextView) view.findViewById(R.id.text_resq8);
        res8.setMovementMethod(LinkMovementMethod.getInstance());
        res8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://headspace.org.au/headspace-centres/"));
                startActivity(browserIntent);
            }
        });

        resL1 = (TextView) view.findViewById(R.id.text_reslink1);
        resL1.setMovementMethod(LinkMovementMethod.getInstance());
        resL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.lifeline.org.au/"));
                startActivity(browserIntent);
            }
        });

        resL2 = (TextView) view.findViewById(R.id.text_reslink2);
        resL2.setMovementMethod(LinkMovementMethod.getInstance());
        resL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.blackdoginstitute.org.au/"));
                startActivity(browserIntent);
            }
        });

        resL3 = (TextView) view.findViewById(R.id.text_reslink3);
        resL3.setMovementMethod(LinkMovementMethod.getInstance());
        resL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.beyondblue.org.au/"));
                startActivity(browserIntent);
            }
        });

        resL4 = (TextView) view.findViewById(R.id.text_reslink4);
        resL4.setMovementMethod(LinkMovementMethod.getInstance());
        resL4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://headspace.org.au/"));
                startActivity(browserIntent);
            }
        });
        return view;
    }

}
