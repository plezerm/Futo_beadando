package Futoverseny;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<ResultEntity,Long > {
    List<ResultEntity> findByRunner(RunnerEntity runner);
    List<ResultEntity> findByCompetition(CompetitionEntity competition);

    List<ResultEntity> findByCompetition_CompId(int compId);
}