package auruda.auruda.service.admin;

import auruda.auruda.domain.Member;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.member.MemberInfoResponseDto;
import auruda.auruda.dto.member.MemberListResponseDto;
import auruda.auruda.dto.member.SearchMemberCondDto;
import auruda.auruda.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 회원 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberService memberService;//회원 서비스


    /**
     * [서비스 로직]
     * 관리자 - 회원 삭제
     * @param id 회원 아이디 PK
     */
    @Transactional
    public void deleteMember(Long id){
        //회원 조회
        Member member = memberService.findById(id);

        //SOFT DELETE 처리
        member.softDelete();
    }

    /**
     * [서비스 로직]
     * 관리자 - 회원 정보 조회
     * @param id 회원 아이디 PK
     * @return MemberInfoResponseDto
     */
    public MemberInfoResponseDto findMemberInfo(Long id){

        //회원 조회
        Member member = memberService.findById(id);

        //응답 DTO 생성 + 반환
        return MemberInfoResponseDto.create(member);
    }


    /**
     * [서비스 로직]
     * 관리자 - 회원 목록 조회
     * @param cond 검색 조건 DTO
     * @param page 페이지 번호
     * @return PagedResponseDto<MemberListResponseDto>
     */
    public PagedResponseDto<MemberListResponseDto> findMemberList(SearchMemberCondDto cond, int page){

        //회원 조회
        Page<Member> pagedMember = memberService.findAllByCond(cond, page);

        //응답 DTO 리스트 생성
        List<MemberListResponseDto> content = pagedMember.getContent().stream().map(MemberListResponseDto::create).toList();

        //페이징 응답 DTO 생성 + 반환
        return createPagedResponseDto(content, pagedMember);
    }


    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<MemberListResponseDto> createPagedResponseDto(List<MemberListResponseDto> content, Page<Member> pagedMember) {
        return PagedResponseDto.<MemberListResponseDto>builder()
                .content(content)
                .page(pagedMember.getNumber())
                .size(pagedMember.getSize())
                .totalPages(pagedMember.getTotalPages())
                .totalElements(pagedMember.getTotalElements())
                .first(pagedMember.isFirst())
                .last(pagedMember.isLast())
                .build();
    }


}
