package auruda.auruda.repository.member;

import auruda.auruda.domain.Member;
import auruda.auruda.dto.member.SearchMemberCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *  회원 레파지토리 커스텀
 */
public interface MemberRepositoryCustom {

    Page<Member> findAllByCond(SearchMemberCondDto cond, Pageable pageable);

}
