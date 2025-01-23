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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private MemberMapper memberMapper;

    @Test
    void patchMemberTest() throws Exception {
//        given
        long memberId = 1;
        MemberDto.Patch patch = new MemberDto.Patch(memberId,"프레소","010-9012-3456", Member.MemberStatus.MEMBER_ACTIVE);
//        Member member = new Member("bana@rabbit.com","프레소", "010-9012-3456");
        MemberDto.response response = new MemberDto.response(memberId, "bana@google.com", "프레소","010-9012-3456", Member.MemberStatus.MEMBER_ACTIVE, new Stamp());
        String content = gson.toJson(response);

        given(memberService.updateMember(memberMapper.memberPatchToMember(patch))).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
//        when
        ResultActions actions = mockMvc.perform(
                patch("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
//        then
        actions.andExpect(status().isOk())
//                각 요소가 내가 patch로 보낸건지 확인하려고
                .andExpect(jsonPath("$.data.phone").value(patch.getPhone()))
                .andExpect(jsonPath("$.data.name").value(patch.getName()))
                .andExpect(jsonPath("$.data.memberStatus").value(patch.getMemberStatus().getStatus()));
    }

    @Test
    void getMemberTest() throws Exception {
//        given
        long memberId = 1L;
        MemberDto.response response = new MemberDto.response(memberId, "bana@google.com", "바나",
                "010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE, new Stamp());

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
//      when
        ResultActions actions = mockMvc.perform(
                get("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
//        then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));
    }
// 입력받은 page와 size만큼의 member를 보여주는가.
    @Test
    void getMembersTest() throws Exception {
//        given
//        controller가  List<MemberDto.response>를 반환한는가.
        List<MemberDto.response> responseMembers = new ArrayList<>();
        responseMembers.add(new MemberDto.response(1,"bana@rabbit.com","바니","010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()));
        responseMembers.add(new MemberDto.response(2,"bana@presso.com","바나","010-9012-3456", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()));
        responseMembers.add(new MemberDto.response(3,"bana@cat.com","바나나","010-7890-1234", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()));
//        paheMembers를 받았을 때,
        Page<Member> pageMembers = new PageImpl<>(List.of(new Member()));

        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(pageMembers);
        given(memberMapper.membersToMemberResponses(Mockito.anyList())).willReturn(responseMembers);
//        when
        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .param("page", "1")
                        .param("size","2")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
//        then
        );
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value(responseMembers.get(0).getName()))
                .andExpect(jsonPath("$.data[1].name").value(responseMembers.get(1).getName()));
    }

    @Test
    void deleteMemberTest() throws Exception {
//       given
        long memberId = 1;
//        given(memberService.deleteMember(Mockito.anyLong())).willReturn()
//        when
        ResultActions actions = mockMvc.perform(
                delete("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
//        then
        actions.andExpect(status().isNoContent());
    }
}
