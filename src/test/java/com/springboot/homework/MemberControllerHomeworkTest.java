package com.springboot.homework;

import com.google.gson.Gson;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.stamp.Stamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerHomeworkTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    //@MockBean// 목빈으로 가짜 객체를 만듭니다.

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper memberMapper;

    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        //patch에 대한 정보
        long memberId = 1L;
        MemberDto.Patch patchDto = new MemberDto.Patch(1,"최용준","010-1111-2222", Member.MemberStatus.MEMBER_SLEEP);
        MemberDto.response response = new MemberDto.response(memberId, "lucky@cat.com", "최용준", "010-4444-2255", Member.MemberStatus.MEMBER_SLEEP, new Stamp());

        patchDto.setMemberId(memberId);

        given(memberMapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
        //해당 메서드가 실행되었을때 member(엔티티)를 반환하겠다는 의미

        String content = gson.toJson(patchDto);
        //when
        ResultActions actions = mockMvc.perform(
                patch("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        actions.andExpect(status().isOk())
                //변경 데이터 검증
                .andExpect(jsonPath("$.data.phone").value(response.getPhone()))
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                //기존 데이터 검증
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));
    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        //mokito -> post로 등록을 하지 않아도 됩니다!, 데이터 베이스에 접근을 하지 않으므로 고려 대상이 아닙니다.
        //1. post는 /v11/members/1 와 같은 /1 -> MemberId로 조회하는 기능을 구현해야 합니다.
        //2. long type으로 memberId를 선언 해주어야 합니다.
        //3. member -> memberId, email, name, phone, memberStatus, stamp

        //given
        long memberId = 1L; //1L 에서 L은 리터럴 값이 long 타입임을 나타내는 명시적인 선언입니다.
        Member member = new Member(); // Mock 데이터 생성
        member.setMemberId(memberId);
        member.setEmail("asd123@gmail.com");
        member.setName("홍길동");
        member.setPhone("010-4444-5555");
        member.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member.setStamp(new Stamp());

        MemberDto.response response = new MemberDto.response(1L, "asd123@gmail.com", "홍길동", "010-4444-5555", Member.MemberStatus.MEMBER_ACTIVE, new Stamp());
        //post

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then -> 검증
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));

    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        Page<Member> page = new PageImpl<>(List.of(new Member(), new Member()));
        List<MemberDto.response> responses = List.of(
                new MemberDto.response(1L, "asd123@gmail.com", "최용준", "010-1111-2222", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.response(2L, "asd44@gmail.com", "김리안", "010-6666-4444", Member.MemberStatus.MEMBER_SLEEP, new Stamp()),
                new MemberDto.response(3L, "abc3@gmail.com", "이양기", "010-7878-5768", Member.MemberStatus.MEMBER_ACTIVE, new Stamp())
        );

        // Mock 설정
        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(page);
        given(memberMapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        // when
        ResultActions getActions = mockMvc.perform(
                get("/v11/members")
                        .param("page", "1")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        getActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(responses.size())); //사이즈로 검증

//                .andExpect(jsonPath("$.data[0].memberId").value(1L))
//                .andExpect(jsonPath("$.data[0].name").value("최용준"))
//                .andExpect(jsonPath("$.data[0].email").value("asd123@gmail.com"))
//                .andExpect(jsonPath("$.data[1].memberId").value(2L))
//                .andExpect(jsonPath("$.data[1].name").value("김리안"))
//                .andExpect(jsonPath("$.data[1].email").value("asd44@gmail.com"))
//                .andExpect(jsonPath("$.data[2].memberId").value(3L))
//                .andExpect(jsonPath("$.data[2].name").value("이양기"))
//                .andExpect(jsonPath("$.data[2].email").value("abc3@gmail.com"));

    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        long memberId = 1L;
        MemberDto.Patch patch = new MemberDto.Patch(1L,"김리리","010-1234-5555", Member.MemberStatus.MEMBER_ACTIVE);
        patch.setMemberId(memberId);
        given(memberMapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());

        // DELETE 동작을 Mock 설정
        doNothing().when(memberService).deleteMember(memberId);

        ResultActions deleteActions = mockMvc.perform(
                delete("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // DELETE 요청 후 상태 코드 확인
        deleteActions.andExpect(status().isNoContent());

    }
}
