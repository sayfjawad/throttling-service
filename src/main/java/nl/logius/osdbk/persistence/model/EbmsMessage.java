package nl.logius.osdbk.persistence.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ebms_message")
@IdClass(EbmsMessageId.class)
@Data
@NoArgsConstructor
public class EbmsMessage {

    @Id
    @Column(name = "message_id")
    private String messageId;

    @Id
    @Column(name = "message_nr")
    private String messageNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_stamp")
    private Date timeStamp;

    @Column(name = "cpa_id")
    private String cpaId;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "ref_to_message_id")
    private String refToMessageId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_to_live")
    private Date timeToLive;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "persist_time")
    private Date persistTime;

    @Column(name = "from_party_id")
    private String fromPartyId;

    @Column(name = "from_role")
    private String fromRole;

    @Column(name = "to_party_id")
    private String toPartyId;

    @Column(name = "to_role")
    private String toRole;

    @Column(name = "service")
    private String service;

    @Column(name = "action")
    private String action;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate;

}
