package ru.tbank.education.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.education.Entity.Memory;

import java.util.List;
import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {


    List<Memory> findAllByTrip_Id(Long tripId);

    List<Memory> findAllByTrip_IdAndTrip_User_Id(Long tripId, Long userId);

    Optional<Memory> findByIdAndTrip_Id(Long id, Long tripId);

    //  фильтр по тегам внутри конкретной поездки
    @Query("""
        select m
        from Memory m
        join m.tags t
        where m.trip.user.id = :userId
          and m.trip.id = :tripId
          and t.id in :tagIds
        group by m
        having count(distinct t.id) = :tagCount
    """)
    List<Memory> findAllByUserIdTripIdAndAllTags(@Param("userId") Long userId,
                                                 @Param("tripId") Long tripId,
                                                 @Param("tagIds") List<Long> tagIds,
                                                 @Param("tagCount") long tagCount);

    @Modifying
    @Query(value = "DELETE FROM memory_tags WHERE tag_id = :tagId", nativeQuery = true)
    void detachTagEverywhere(@Param("tagId") Long tagId);
}
