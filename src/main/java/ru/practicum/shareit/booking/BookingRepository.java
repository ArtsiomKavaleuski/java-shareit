package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1")
    List<Booking> findAllByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and CURRENT TIMESTAMP between b.start and b.end ")
    List<Booking> findCurrentByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current timestamp ")
    List<Booking> findPastByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current timestamp ")
    List<Booking> findFutureByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'WAITING'")
    List<Booking> findWaitingByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'REJECTED'")
    List<Booking> findRejectedByBookerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findAllByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and CURRENT TIMESTAMP between b.start and b.end ")
    List<Booking> findCurrentByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current timestamp ")
    List<Booking> findPastByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current timestamp ")
    List<Booking> findFutureByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'WAITING'")
    List<Booking> findWaitingByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'REJECTED'")
    List<Booking> findRejectedByOwnerId(long id);
}


