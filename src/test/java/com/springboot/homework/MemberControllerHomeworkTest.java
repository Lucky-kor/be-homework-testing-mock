package com.springboot.homework;

import com.google.gson.Gson;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.stamp.Stamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
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
        long memberId = 1L;

        MemberDto.Patch patch = new MemberDto.Patch();
        patch.setMemberId(memberId);
        patch.setName("홍길동");
        patch.setPhone("010-1010-0101");
        patch.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        String newContent = gson.toJson(patch);

        URI uri = UriComponentsBuilder.newInstance()
                .path("/v11/members/{memberId}")
                .buildAndExpand(memberId)
                .toUri();

        MemberDto.response responseDto = new MemberDto.response(
                memberId,
                "gogildong@naver.com",
                "홍길동",
                "010-1010-0101",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        // 1,2,3 기능 정의
        given(memberMapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(responseDto);

        ResultActions actions = mockMvc.perform(
                patch(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newContent));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(patch.getName()))
                .andExpect(jsonPath("$.data.phone").value(patch.getPhone()));

    }

    // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
    // TODO Mockito를 사용해야 합니다. ^^
    @Test
    void getMemberTest() throws Exception {
        long memberId = 1L;
        MemberDto.response response = new MemberDto.response(
                memberId,
                "lucky@cat.house",
                "러키킴",
                "010-0000-1110",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );
        URI uri = UriComponentsBuilder.newInstance()
                .path("/v11/members/{memberId}")
                .buildAndExpand(memberId)
                .toUri();

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        ResultActions actions = mockMvc.perform(
                get(uri)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));
    }

    // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
    // TODO Mockito를 사용해야 합니다. ^^
    @Test
    void getMembersTest() throws Exception {
//         given
//         1. request Dto 생성 -> List
//         2. responseDtos 생성 -> List
//         3. Mockito 활용하여 Mock객체 given

        List<MemberDto.response> responses = List.of(
                new MemberDto.response(1L, "lucky@naver.com", "luckyKim", "010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.response(2L, "latte@gmail.com", "latte", "010-0000-0101", Member.MemberStatus.MEMBER_ACTIVE, new Stamp())
        );

        Page<Member> page = new PageImpl<>(List.of(new Member(), new Member()));

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(page);
        given(memberMapper.membersToMemberResponses(Mockito.any())).willReturn(responses);

        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "10")
        );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(responses.size()));
    }


    // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
    // TODO Mockito를 사용해야 합니다. ^^
    @Test
    void deleteMemberTest() throws Exception {
        long memberId = 1L;

     doNothing().when(memberService).deleteMember(memberId);

        URI uri = UriComponentsBuilder.newInstance()
                .path("/v11/members/{memberId}")
                .buildAndExpand(memberId)
                .toUri();

        mockMvc.perform(
             delete(uri))
                     .andExpect(status().isNoContent());
    }
}
