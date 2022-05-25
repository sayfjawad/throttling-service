package nl.logius.osdbk.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PendingTasks {

    private String cpaId;
    private int amountOfPendingTasks;
}
