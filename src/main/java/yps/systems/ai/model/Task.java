package yps.systems.ai.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;

@Node("Task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class Task {

    @Id
    @GeneratedValue
    private String elementId;

    private String name;

    private String description;

    private String priority;

    private Boolean state;

    private LocalDate startDate;

    private LocalDate endDate;

}
