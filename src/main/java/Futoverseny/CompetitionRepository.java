package Futoverseny;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<CompetitionEntity,Long > {
    Object findByCompName(String compName);
}