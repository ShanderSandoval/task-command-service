package yps.systems.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import yps.systems.ai.model.Task;
import yps.systems.ai.repository.ITaskRepository;

import java.util.Optional;

@RestController
@RequestMapping("/command/taskService")
public class TaskCommandController {

    private final ITaskRepository taskRepository;
    private final KafkaTemplate<String, Task> kafkaTemplate;

    @Value("${env.kafka.topicEvent}")
    private String kafkaTopicEvent;

    @Autowired
    public TaskCommandController(ITaskRepository taskRepository, KafkaTemplate<String, Task> kafkaTemplate) {
        this.taskRepository = taskRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody Task task) {
        Task saveTask = taskRepository.save(task);
        Message<Task> message = MessageBuilder
                .withPayload(saveTask)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "CREATE_TASK")
                .setHeader("source", "taskService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Task saved with ID: " + saveTask.getElementId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{elementId}")
    public ResponseEntity<String> deleteTask(@PathVariable String elementId) {
        Optional<Task> optionalTask = taskRepository.findById(elementId);
        if (optionalTask.isPresent()) {
            taskRepository.deleteById(elementId);
            Message<String> message = MessageBuilder
                    .withPayload(elementId)
                    .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                    .setHeader("eventType", "DELETE_TASK")
                    .setHeader("source", "taskService")
                    .build();
            kafkaTemplate.send(message);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Task not founded", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{elementId}")
    public ResponseEntity<String> updateTask(@PathVariable String elementId, @RequestBody Task task) {
        Optional<Task> optionalTask = taskRepository.findById(elementId);
        if (optionalTask.isPresent()) {
            task.setElementId(optionalTask.get().getElementId());
            taskRepository.save(task);
            Message<Task> message = MessageBuilder
                    .withPayload(task)
                    .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                    .setHeader("eventType", "UPDATE_TASK")
                    .setHeader("source", "taskService")
                    .build();
            kafkaTemplate.send(message);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Task not founded", HttpStatus.NOT_FOUND);
        }
    }

}
