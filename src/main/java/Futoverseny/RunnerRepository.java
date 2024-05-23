package Futoverseny;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RunnerRepository extends JpaRepository<RunnerEntity,Long > {
    Object findByRunnerName(String runnerName);
}