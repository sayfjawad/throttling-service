package nl.logius.osdbk.persistence.repository;

import nl.logius.osdbk.persistence.model.EbmsMessage;
import nl.logius.osdbk.persistence.model.EbmsMessageId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EbmsMessageRepository extends CrudRepository<EbmsMessage, EbmsMessageId> {

    @Query(value = "select COUNT(*) from ebms_message em where em.cpa_id = :cpaId and status = 10" , nativeQuery = true)
    Long getAmountOfRecordsReadyToBeSentForCpa(@Param("cpaId") String cpaId);

    @Query(value = "select COUNT(*) from ebms_message em where em.cpa_id = :cpaId and status in (11, 12, 13) and status_time BETWEEN now() - (interval '1s') and now()" , nativeQuery = true)
    Long getAmountOfRecordsAlreadySentInLastSecondForCpa(@Param("cpaId") String cpaId);

}
