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
import org.springframework.data.domain.Sort;
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
        //given
        long memberId = 1L;
        MemberDto.Patch patchDto = new MemberDto.Patch(
                "송호근","010-1111-2222", Member.MemberStatus.MEMBER_SLEEP
        );

        MemberDto.response response = new MemberDto.response(
                memberId,
                "lucky@cat.com",
                "송호근",
                "010-1111-2222",
                Member.MemberStatus.MEMBER_SLEEP,
                new Stamp()
        );

        patchDto.setMemberId(memberId);

        //해당 메서드가 실행되었을때 member(엔티티)를 반환하겠다는 의미
        given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

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
        //given
        long memberId = 1L;
        MemberDto.response response = new MemberDto.response(
                memberId,
                "lucky@cat.com",
                "김러키",
                "010-1234-1234",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
        //when
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

        Page<Member> pageMembers = new PageImpl<>(List.of(new Member(), new Member()));

        List<MemberDto.response> responses = List.of(
                new MemberDto.response(1L, "lucky@cat.com", "김러키", "010-1234-1234", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.response(2L, "luck@cat.com", "김럭키", "010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE, new Stamp())
        );
        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(pageMembers);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .param("page", "1")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(responses.size()));
    }


    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        doNothing().when(memberService).deleteMember(Mockito.anyLong());
        ResultActions actions = mockMvc.perform(delete("/v11/members/1"));
        actions.andExpect(status().isNoContent());
    }
}
