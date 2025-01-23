package com.springboot.homework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper memberMapper;

    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long memberId  = 1L;

        MemberDto.Post post = new MemberDto.Post(
                "asd@naver.com",
                "택",
                "010-1111-2222"
        );

        MemberDto.Patch patchDto = new MemberDto.Patch(
                memberId,
                "권택현",
                "010-6782-8932",
                Member.MemberStatus.MEMBER_ACTIVE
        );

        MemberDto.response response = new MemberDto.response(
                1L,
                post.getEmail(),
                "권택현",
                "010-6782-8932",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberMapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        // when

        String content = gson.toJson(patchDto);

        ResultActions patchActions = mockMvc.perform(
                patch("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        // then

        patchActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(patchDto.getName()))
                .andExpect(jsonPath("$.data.phone").value(patchDto.getPhone()));

    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long memberId = 1L;

        MemberDto.response response = new MemberDto.response(
                1L,
                "asd@naver.com",
                "택",
                "010-1111-2222",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );
        // new Member() 부분이 제일 중요함
        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());

        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
        // when

        ResultActions getActions = mockMvc.perform(
                get("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        getActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("택"))
                .andExpect(jsonPath("$.data.email").value("asd@naver.com"));
    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long memberIdOne = 1L;

        long memberIdTwo = 2L;

        Page<Member> pageMembers = new PageImpl<>(List.of(new Member(), new Member()));

        List<MemberDto.response> responses = List.of(
                new MemberDto.response(
                        memberIdOne,
                        "tjsk1999@naver.com",
                        "택현",
                        "010-6782-8932",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                ),
                new MemberDto.response(
                        memberIdTwo,
                        "asd@naver.com",
                        "택",
                        "010-1111-2222",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                )
        );

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageMembers);
        given(memberMapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        ResultActions getsActions = mockMvc.perform(
                get("/v11/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page","1")
                        .param("size", "10")
        );

        getsActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(responses.size()));
    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        long memberId = 1L;

        doNothing().when(memberService).deleteMember(Mockito.anyLong());

        ResultActions deleteActions = mockMvc.perform(
                delete("/v11/members/" + memberId)
        );

        deleteActions.andExpect(status().isNoContent());

    }
}
