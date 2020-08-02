package com.vincent.arrowsserver.persistence;

import com.vincent.arrowsserver.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findRoomsByRoomCode(String code);
    List<Room> findRoomsByCreatedById(Long id);
}
