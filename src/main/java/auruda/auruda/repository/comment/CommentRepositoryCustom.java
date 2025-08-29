package auruda.auruda.repository.comment;

import auruda.auruda.domain.Comment;
import auruda.auruda.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 댓글 레파지토리 커스텀
 */
public interface CommentRepositoryCustom {

    Page<Comment> findAllByMember(Member member, Pageable pageable);

}
