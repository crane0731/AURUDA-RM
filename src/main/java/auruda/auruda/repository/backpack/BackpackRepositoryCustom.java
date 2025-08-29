package auruda.auruda.repository.backpack;

import auruda.auruda.domain.Backpack;
import auruda.auruda.domain.Member;
import auruda.auruda.dto.backpack.SearchBackpackCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * 배낭 레파지토리 커스텀
 */
public interface BackpackRepositoryCustom {

    Page<Backpack> findAllByMemberAndCond(Member member, SearchBackpackCondDto cond, Pageable pageable);

}
