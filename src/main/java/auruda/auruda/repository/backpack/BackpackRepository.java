package auruda.auruda.repository.backpack;

import auruda.auruda.domain.Backpack;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 배낭 레파지토리
 */
public interface BackpackRepository extends JpaRepository<Backpack, Long>, BackpackRepositoryCustom {
}
