package com.vincent.arrowsserver.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rooms")
@EntityListeners(AuditingEntityListener.class)
public class Room {
    public long getId() {
        return id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public long getMostRecentArrowId() {
        return mostRecentArrowId;
    }

    public void setMostRecentArrowId(long mostRecentArrowId) {
        this.mostRecentArrowId = mostRecentArrowId;
    }

    @Column
    public long mostRecentArrowId = 0;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date createdOn;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(long createdById) {
        this.createdById = createdById;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumUpvotes() {
        return numUpvotes;
    }

    public void setNumUpvotes(int numUpvotes) {
        this.numUpvotes = numUpvotes;
    }

    public int getNumDownvotes() {
        return numDownvotes;
    }

    public void setNumDownvotes(int numDownvotes) {
        this.numDownvotes = numDownvotes;
    }

    public int getCurrentListeners() {
        return currentListeners;
    }

    public void setCurrentListeners(int currentListeners) {
        this.currentListeners = currentListeners;
    }

    public int getPeakListeners() {
        return peakListeners;
    }

    public void setPeakListeners(int peakListeners) {
        this.peakListeners = peakListeners;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column
    private long createdById;


    @Column
    private String title;


    @Column
    private int numUpvotes = 0;

    @Column
    private int numDownvotes = 0;

    @Column
    private int currentListeners = 0;

    @Column
    private int peakListeners = 0;

    @Column
    private String roomCode;

    @Column
    private boolean active = true;

    public long getLastArrowId() {
        return lastArrowId;
    }

    public void setLastArrowId(long lastArrowId) {
        this.lastArrowId = lastArrowId;
    }

    @Column
    private long lastArrowId = 0;

    public void incrementListeners() {
        currentListeners++;
        if (currentListeners > peakListeners) peakListeners = currentListeners;
    }



    public Room() {
    }




}

