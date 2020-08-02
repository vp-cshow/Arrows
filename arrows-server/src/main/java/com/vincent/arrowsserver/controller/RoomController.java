package com.vincent.arrowsserver.controller;


import com.vincent.arrowsserver.model.Room;
import com.vincent.arrowsserver.model.User;
import com.vincent.arrowsserver.persistence.RoomRepository;
import com.vincent.arrowsserver.persistence.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/room")
    public Map<String,String> createRoom(@RequestParam String roomTitle, @RequestParam String creator) {
        Map<String, String> response = new HashMap<>();
        boolean genNewCode = true;
        RandomString string = new RandomString(5);
        String code = null;
        while (genNewCode) {
            code = string.nextString();
            List<Room> rooms = roomRepository.findRoomsByRoomCode(code);
            if (rooms.size() == 0) break;
            for (Room room : rooms) {
                if (room.getRoomCode().equals(code) && room.isActive()) {
                    genNewCode = true;

                }
                else {
                    genNewCode = false;
                }
            }

        }
        Room room = new Room();
        room.setCreatedById(userRepository.findByUsername(creator).get().getId());
        room.setTitle(roomTitle);
        room.setRoomCode(code);
        room.setCreatedOn(new Date());
        roomRepository.save(room);
        response.put("roomCode", code);
        return response;
    }

    @PostMapping("/joinRoom")
    public Map<String,String> joinRoom(@RequestParam String roomCode, @RequestParam String sender) {
        Map<String, String> response = new HashMap<>();
        List<Room> rooms = roomRepository.findRoomsByRoomCode(roomCode);

        if(rooms.size() == 0) {
            response.put("result", "doesntExist");
        } else {
            List<Room> activeRooms = rooms.stream().filter(Room::isActive).collect(Collectors.toList());
            Room room = activeRooms.get(0);
            // Just return the first one
            response.put("result", "OK");
            response.put("title", room.getTitle());
            response.put("creator", userRepository.findById(room.getCreatedById())
                                                    .get() // we know the user exists
                                                    .getUsername());
            response.put("roomCode", roomCode);
            room.incrementListeners();
            roomRepository.save(room);
        }

        return response;
    }

    @PostMapping("/exitRoom")
    public Map<String, String> exitRoom(@RequestParam String roomCode, @RequestParam String sender) {
        Map<String, String> response = new HashMap<>();
        List<Room> rooms = roomRepository.findRoomsByRoomCode(roomCode);
        Room room = rooms.stream().filter(Room::isActive).findFirst().get();
        User user = userRepository.findByUsername(sender).get();
        if (user.getId() == room.getCreatedById()) {
            room.setActive(false);
        }
        room.setCurrentListeners(room.getCurrentListeners() - 1); // decrement
        roomRepository.save(room);
        response.put("result", "ok");
        return response;
    }

    @PostMapping("/roomUpdate")
    public Map<String, String> roomUpdate(@RequestParam String roomCode) {
        List<Room> rooms = roomRepository.findRoomsByRoomCode(roomCode);
        Room room = rooms.stream().filter(room1 -> roomCode.equals(room1.getRoomCode())).collect(Collectors.toList()).get(0);
        Map<String, String> response = new HashMap<>();
        response.put("result", "ok");
        response.put("listeners", String.valueOf(room.getCurrentListeners()));
        response.put("active", String.valueOf(room.isActive()));
        response.put("mostRecentArrow", String.valueOf(room.getMostRecentArrowId()));
        return response;
    }


    @GetMapping("/room/{name}")
    public List<Room> getUsersByName(@PathVariable(value = "name") String username)
    {
        User user =
                userRepository
                        .findByUsername(username)
                        .orElse(null);
        return roomRepository.findRoomsByCreatedById(user.getId());
    }

}
