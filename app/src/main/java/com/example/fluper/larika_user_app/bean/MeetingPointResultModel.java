package com.example.fluper.larika_user_app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohit on 6/7/17.
 */

public class MeetingPointResultModel {
    List<MeetingPointModel>meetingPoint;
    private List<MeetingTimeModel> time;

    public List<MeetingTimeModel> getTime() {

        return time;
    }

    public void setTime(List<MeetingTimeModel> time) {
        this.time = time;
    }

    public List<MeetingPointModel> getMeetingPoint() {
        if(meetingPoint==null)
            meetingPoint=new ArrayList<>();
        return meetingPoint;
    }

    public void setMeetingPoint(List<MeetingPointModel> meetingPoint) {
        this.meetingPoint = meetingPoint;
    }
}
