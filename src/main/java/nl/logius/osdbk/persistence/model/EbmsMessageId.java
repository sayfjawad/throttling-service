package nl.logius.osdbk.persistence.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class EbmsMessageId implements Serializable {

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "message_nr")
    private String messageNumber;
}
