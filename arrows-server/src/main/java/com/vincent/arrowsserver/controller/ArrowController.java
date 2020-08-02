package com.vincent.arrowsserver.controller;

import com.vincent.arrowsserver.model.Arrow;
import com.vincent.arrowsserver.model.Room;
import com.vincent.arrowsserver.model.User;
import com.vincent.arrowsserver.persistence.ArrowRepository;
import com.vincent.arrowsserver.persistence.RoomRepository;
import com.vincent.arrowsserver.persistence.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ArrowController {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArrowRepository arrowRepository;


    @PostMapping("/arrow")
    public Map<String,String> createArrow(@RequestParam String roomCode, @RequestParam String sender,
                                         @RequestParam String direction, @RequestParam String intensity) {
        Arrow arrow = new Arrow();
        HashMap<String, String> resp = new HashMap<>();
        Room room = roomRepository.findRoomsByRoomCode(roomCode).get(0);
        arrow.setDirection(direction);
        arrow.setRoomId(room.getId());
        arrow.setFromId(userRepository.findByUsername(sender).get().getId());
        arrow.setIntensity(Integer.parseInt(intensity));
        arrowRepository.save(arrow);
        User userSent = userRepository.findByUsername(sender).get();
        if (direction.equals("up")) {
            room.setNumUpvotes(room.getNumUpvotes() + 1);
            userSent.setNumberUpvotes(userSent.getNumberUpvotes() + 1);
        }
        else {
            room.setNumDownvotes(room.getNumDownvotes() + 1);
            userSent.setNumberDownvotes(userSent.getNumberDownvotes() + 1);
        }
        room.setMostRecentArrowId(arrow.getId());
        roomRepository.save(room);
        resp.put("result", "ok");
        return resp;
    }

    @PostMapping("/arrowInfo")
    public Arrow getArrowInfo(@RequestParam String id) {
        Optional<Arrow> arrowOptional = arrowRepository.findById(Long.parseLong(id));
        return arrowOptional.orElse(null);
    }


}