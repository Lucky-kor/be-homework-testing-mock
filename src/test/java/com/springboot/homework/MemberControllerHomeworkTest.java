package com.springboot.homework;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private MemberMapper mapper;

    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        long memberId = 1L;
        MemberDto.Patch patch = new MemberDto.Patch(
                memberId,
                "호",
                "010-2409-1789",
                Member.MemberStatus.MEMBER_SLEEP
        );

        String content = gson.toJson(patch);

        MemberDto.response response = new MemberDto.response(
                memberId,
                "t@gmail.com",
                "호",
                "010-2409-1789",
                Member.MemberStatus.MEMBER_SLEEP,
                new Stamp()
        );

        given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class)))
                .willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class)))
                .willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.phone").value(response.getPhone()))
                .andExpect(jsonPath("$.data.memberStatus").value(response.getMemberStatus()));
    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        long memberId = 1L;
        MemberDto.response response = new MemberDto.response(
                memberId,
                "t@gmail.com",
                "택",
                "010-2401-5123",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));
    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        List<MemberDto.response> responses = List.of(
                new MemberDto.response(
                        1L,
                        "t@gmail.com",
                        "택",
                        "010-2401-5123",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                ),
                new MemberDto.response(
                        2L,
                        "t2@gmail.com",
                        "호",
                        "010-2401-5124",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                )
        );

        Page<Member> memberPage = new PageImpl<>(List.of(new Member(), new Member()));

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(memberPage);
        given(mapper.membersToMemberResponses(Mockito.anyList()))
                .willReturn(responses);


        // when
        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "2")
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2));
    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        long memberId = 1L;

        // void 일때는 doNothing
        doNothing().when(memberService).deleteMember(Mockito.anyLong());

        // when
        ResultActions deleteActions = mockMvc.perform(
                delete("/v11/members/" + memberId)
        );

        // then
        deleteActions.andExpect(status().isNoContent());
    }
}
