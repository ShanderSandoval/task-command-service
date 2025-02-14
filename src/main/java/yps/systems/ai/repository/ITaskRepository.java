package yps.systems.ai.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import yps.systems.ai.model.Task;

@Repository
public interface ITaskRepository extends Neo4jRepository<Task, String> {
}
